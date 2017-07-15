package com.ss.editor.ui.component.editor.impl.scene;

import static com.ss.editor.util.EditorUtil.getAssetFile;
import static com.ss.editor.util.EditorUtil.toAssetPath;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.asset.AssetManager;
import com.jme3.asset.ModelKey;
import com.jme3.scene.Spatial;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.extension.property.EditableProperty;
import com.ss.editor.extension.scene.SceneLayer;
import com.ss.editor.extension.scene.SceneNode;
import com.ss.editor.extension.scene.app.state.EditableSceneAppState;
import com.ss.editor.extension.scene.app.state.SceneAppState;
import com.ss.editor.extension.scene.filter.EditableSceneFilter;
import com.ss.editor.extension.scene.filter.SceneFilter;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.state.editor.impl.scene.SceneEditorAppState;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.editor.EditorDescription;
import com.ss.editor.ui.component.editor.impl.AbstractFileEditor;
import com.ss.editor.ui.component.editor.state.EditorState;
import com.ss.editor.ui.component.editor.state.impl.SceneFileEditorState;
import com.ss.editor.ui.component.tab.EditorToolComponent;
import com.ss.editor.ui.control.app.state.list.AppStateList;
import com.ss.editor.ui.control.filter.list.FilterList;
import com.ss.editor.ui.control.layer.LayerNodeTree;
import com.ss.editor.ui.control.layer.LayersRoot;
import com.ss.editor.ui.control.model.property.ModelPropertyEditor;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.util.DynamicIconSupport;
import com.ss.editor.util.MaterialUtils;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * The implementation of the {@link AbstractFileEditor} for working with {@link SceneNode}.
 *
 * @author JavaSaBr
 */
public class SceneFileEditor extends
        AbstractSceneFileEditor<SceneFileEditor, SceneNode, SceneEditorAppState, SceneFileEditorState> implements
        SceneChangeConsumer {

    private static final int LAYERS_TOOL = 3;
    private static final int APP_STATES_TOOL = 4;
    private static final int FILTERS_TOOL = 5;

    /**
     * The constant DESCRIPTION.
     */
    @NotNull
    public static final EditorDescription DESCRIPTION = new EditorDescription();

    static {
        DESCRIPTION.setEditorName(Messages.SCENE_FILE_EDITOR_NAME);
        DESCRIPTION.setConstructor(SceneFileEditor::new);
        DESCRIPTION.setEditorId(SceneFileEditor.class.getSimpleName());
        DESCRIPTION.addExtension(FileExtensions.JME_SCENE);
    }

    /**
     * The list with app states.
     */
    @Nullable
    private AppStateList appStateList;

    /**
     * The list with filters.
     */
    @Nullable
    private FilterList filterList;

    /**
     * The tree with layers.
     */
    @Nullable
    private LayerNodeTree layerNodeTree;

    /**
     * The light toggle.
     */
    @Nullable
    private ToggleButton lightButton;

    /**
     * The audio toggle.
     */
    @Nullable
    private ToggleButton audioButton;

    /**
     * The container of property editor in app states tool.
     */
    @Nullable
    private VBox propertyEditorAppStateContainer;

    /**
     * The container of property editor in filters tool.
     */
    @Nullable
    private VBox propertyEditorFiltersContainer;

    /**
     * The container of property editor in layers tool.
     */
    @Nullable
    private VBox propertyEditorLayersContainer;

    /**
     * The flag of sync selection.
     */
    private boolean needSyncSelection;

    private SceneFileEditor() {
        setNeedSyncSelection(true);
    }

    @NotNull
    @Override
    protected SceneEditorAppState createEditorAppState() {
        return new SceneEditorAppState(this);
    }

    @FXThread
    @Override
    public void openFile(@NotNull final Path file) {
        super.openFile(file);

        final Path assetFile = notNull(getAssetFile(file), "Asset file for " + file + " can't be null.");
        final ModelKey modelKey = new ModelKey(toAssetPath(assetFile));

        final AssetManager assetManager = EDITOR.getAssetManager();

        Spatial loadedScene = assetManager.loadAsset(modelKey);


        final SceneNode model = (SceneNode) loadedScene;
        model.depthFirstTraversal(this::updateVisibility);

        MaterialUtils.cleanUpMaterialParams(model);

        final SceneEditorAppState editorState = getEditorAppState();
        editorState.openModel(model);

        handleAddedObject(model);

        setCurrentModel(model);
        setIgnoreListeners(true);
        try {
            refreshTree();
        } finally {
            setIgnoreListeners(false);
        }

        EXECUTOR_MANAGER.addFXTask(this::loadState);
    }

    @Override
    protected void refreshTree() {
        super.refreshTree();

        final SceneNode model = getCurrentModel();

        final AppStateList appStateList = getAppStateList();
        appStateList.fill(model);

        final FilterList filterList = getFilterList();
        filterList.fill(model);

        final LayerNodeTree layerNodeTree = getLayerNodeTree();
        layerNodeTree.fill(new LayersRoot(this));
    }

    private void updateVisibility(@NotNull final Spatial spatial) {
        final SceneLayer layer = SceneLayer.getLayer(spatial);
        if (layer != null) spatial.setVisible(layer.isShowed());
    }

    /**
     * @return the list with app states.
     */
    @NotNull
    private AppStateList getAppStateList() {
        return notNull(appStateList);
    }

    /**
     * @return the list with filters.
     */
    @NotNull
    private FilterList getFilterList() {
        return notNull(filterList);
    }

    /**
     * @return the tree with layers.
     */
    @NotNull
    private LayerNodeTree getLayerNodeTree() {
        return notNull(layerNodeTree);
    }

    /**
     * @return the container of property editor in layers tool.
     */
    @NotNull
    private VBox getPropertyEditorLayersContainer() {
        return notNull(propertyEditorLayersContainer);
    }

    /**
     * @return the container of property editor in filters tool.
     */
    @NotNull
    private VBox getPropertyEditorFiltersContainer() {
        return notNull(propertyEditorFiltersContainer);
    }

    /**
     * @return the container of property editor in app states tool.
     */
    @NotNull
    private VBox getPropertyEditorAppStateContainer() {
        return notNull(propertyEditorAppStateContainer);
    }

    @Override
    protected void createToolbar(@NotNull final HBox container) {
        super.createToolbar(container);

        lightButton = new ToggleButton();
        lightButton.setTooltip(new Tooltip(Messages.SCENE_FILE_EDITOR_ACTION_SHOW_LIGHTS));
        lightButton.setGraphic(new ImageView(Icons.LIGHT_16));
        lightButton.setSelected(true);
        lightButton.selectedProperty().addListener((observable, oldValue, newValue) -> changeLight(newValue));

        audioButton = new ToggleButton();
        audioButton.setTooltip(new Tooltip(Messages.SCENE_FILE_EDITOR_ACTION_SHOW_AUDIO));
        audioButton.setGraphic(new ImageView(Icons.AUDIO_16));
        audioButton.setSelected(true);
        audioButton.selectedProperty().addListener((observable, oldValue, newValue) -> changeAudio(newValue));

        DynamicIconSupport.addSupport(lightButton, audioButton);

        FXUtils.addClassesTo(lightButton, audioButton, CSSClasses.FILE_EDITOR_TOOLBAR_BUTTON);

        FXUtils.addToPane(lightButton, container);
        FXUtils.addToPane(audioButton, container);
    }

    /**
     * Handle changing light models visibility.
     */
    private void changeLight(@NotNull final Boolean newValue) {
        if (isIgnoreListeners()) return;

        final SceneEditorAppState editorAppState = getEditorAppState();
        editorAppState.updateLightShowed(newValue);

        if (editorState != null) editorState.setShowedLight(newValue);
    }

    /**
     * Handle changing audio models visibility.
     */
    private void changeAudio(@NotNull final Boolean newValue) {
        if (isIgnoreListeners()) return;

        final SceneEditorAppState editorAppState = getEditorAppState();
        editorAppState.updateAudioShowed(newValue);

        if (editorState != null) editorState.setShowedAudio(newValue);
    }

    @Override
    protected void createContent(@NotNull final StackPane root) {
        super.createContent(root);

        appStateList = new AppStateList(this::selectAppStateFromList, this);
        propertyEditorAppStateContainer = new VBox();

        filterList = new FilterList(this::selectFilterFromList, this);
        propertyEditorFiltersContainer = new VBox();

        layerNodeTree = new LayerNodeTree(this::selectNodeFromLayersTree, this);
        propertyEditorLayersContainer = new VBox();
        
        final SplitPane appStateSplitContainer = new SplitPane(appStateList, propertyEditorAppStateContainer);
        appStateSplitContainer.prefHeightProperty().bind(root.heightProperty());
        appStateSplitContainer.prefWidthProperty().bind(root.widthProperty());

        final SplitPane filtersSplitContainer = new SplitPane(filterList, propertyEditorFiltersContainer);
        filtersSplitContainer.prefHeightProperty().bind(root.heightProperty());
        filtersSplitContainer.prefWidthProperty().bind(root.widthProperty());

        final SplitPane layersSplitContainer = new SplitPane(layerNodeTree, propertyEditorLayersContainer);
        layersSplitContainer.prefHeightProperty().bind(root.heightProperty());
        layersSplitContainer.prefWidthProperty().bind(root.widthProperty());

        final EditorToolComponent editorToolComponent = getEditorToolComponent();
        editorToolComponent.addComponent(layersSplitContainer, Messages.SCENE_FILE_EDITOR_TOOL_LAYERS);
        editorToolComponent.addComponent(appStateSplitContainer, Messages.SCENE_FILE_EDITOR_TOOL_APP_STATES);
        editorToolComponent.addComponent(filtersSplitContainer, Messages.SCENE_FILE_EDITOR_TOOL_FILTERS);

        FXUtils.addClassTo(layerNodeTree.getTreeView(), CSSClasses.TRANSPARENT_TREE_VIEW);
        FXUtils.addClassTo(filtersSplitContainer, appStateSplitContainer, layersSplitContainer,
                CSSClasses.FILE_EDITOR_TOOL_SPLIT_PANE);

        root.heightProperty().addListener((observableValue, oldValue, newValue) -> calcVSplitSize(appStateSplitContainer));
        root.heightProperty().addListener((observableValue, oldValue, newValue) -> calcVSplitSize(filtersSplitContainer));
        root.heightProperty().addListener((observableValue, oldValue, newValue) -> calcVSplitSize(layersSplitContainer));
    }

    @Override
    protected void processChangeTool(@Nullable final Number oldValue, @NotNull final Number newValue) {
        super.processChangeTool(oldValue, newValue);

        final int newIndex = newValue.intValue();
        if (newIndex < 2) return;

        final ModelPropertyEditor modelPropertyEditor = getModelPropertyEditor();
        final VBox appStateContainer = getPropertyEditorAppStateContainer();
        final VBox filtersContainer = getPropertyEditorFiltersContainer();
        final VBox layersContainer = getPropertyEditorLayersContainer();

        switch (newIndex) {
            case LAYERS_TOOL: {
                FXUtils.addToPane(modelPropertyEditor, layersContainer);
                break;
            }
            case APP_STATES_TOOL: {
                FXUtils.addToPane(modelPropertyEditor, appStateContainer);
                break;
            }
            case FILTERS_TOOL: {
                FXUtils.addToPane(modelPropertyEditor, filtersContainer);
                break;
            }
        }
    }

    /**
     * @return the light toggle.
     */
    @NotNull
    private ToggleButton getLightButton() {
        return notNull(lightButton);
    }

    /**
     * @return the audio toggle.
     */
    @NotNull
    private ToggleButton getAudioButton() {
        return notNull(audioButton);
    }

    @Override
    protected void loadState() {
        super.loadState();

        final SceneFileEditorState editorState = notNull(getEditorState());
        getLightButton().setSelected(editorState.isShowedLight());
        getAudioButton().setSelected(editorState.isShowedAudio());
    }

    /**
     * Handle the selected app state from the list.
     */
    @FXThread
    private void selectAppStateFromList(@Nullable final EditableSceneAppState appState) {
        if (!isNeedSyncSelection()) return;

        setNeedSyncSelection(false);
        try {

            final ModelNodeTree modelNodeTree = getModelNodeTree();
            modelNodeTree.select(null);

            final LayerNodeTree layerNodeTree = getLayerNodeTree();
            layerNodeTree.select(null);

            final FilterList filterList = getFilterList();
            filterList.clearSelection();

            super.selectNodeFromTree(appState);

        } finally {
            setNeedSyncSelection(true);
        }
    }

    /**
     * Handle the selected filter from the list.
     */
    @FXThread
    private void selectFilterFromList(@Nullable final EditableSceneFilter<?> sceneFilter) {
        if (!isNeedSyncSelection()) return;

        setNeedSyncSelection(false);
        try {

            final ModelNodeTree modelNodeTree = getModelNodeTree();
            modelNodeTree.select(null);

            final LayerNodeTree layerNodeTree = getLayerNodeTree();
            layerNodeTree.select(null);

            final AppStateList appStateList = getAppStateList();
            appStateList.clearSelection();

            super.selectNodeFromTree(sceneFilter);

        } finally {
            setNeedSyncSelection(true);
        }
    }

    /**
     * Handle the selected object from the layers tree.
     */
    @FXThread
    private void selectNodeFromLayersTree(@Nullable final Object object) {
        if (!isNeedSyncSelection()) return;

        setNeedSyncSelection(false);
        try {

            final ModelNodeTree modelNodeTree = getModelNodeTree();
            modelNodeTree.select(object);

            final AppStateList appStateList = getAppStateList();
            appStateList.clearSelection();

            final FilterList filterList = getFilterList();
            filterList.clearSelection();

            super.selectNodeFromTree(object);

        } finally {
            setNeedSyncSelection(true);
        }
    }

    @Override
    public void selectNodeFromTree(@Nullable final Object object) {
        if (!isNeedSyncSelection()) return;

        setNeedSyncSelection(false);
        try {

            final LayerNodeTree layerNodeTree = getLayerNodeTree();
            layerNodeTree.select(object);

            final AppStateList appStateList = getAppStateList();
            appStateList.clearSelection();

            final FilterList filterList = getFilterList();
            filterList.clearSelection();

            super.selectNodeFromTree(object);

        } finally {
            setNeedSyncSelection(true);
        }
    }

    @NotNull
    @Override
    public EditorDescription getDescription() {
        return DESCRIPTION;
    }

    @NotNull
    @Override
    protected Supplier<EditorState> getStateConstructor() {
        return SceneFileEditorState::new;
    }

    /**
     * @param needSyncSelection true if need sync selection.
     */
    private void setNeedSyncSelection(final boolean needSyncSelection) {
        this.needSyncSelection = needSyncSelection;
    }

    /**
     * @return true if need sync selection.
     */
    private boolean isNeedSyncSelection() {
        return needSyncSelection;
    }

    @Override
    protected void updateSelection(@Nullable Spatial spatial) {

        if (spatial instanceof SceneNode || spatial instanceof SceneLayer) {
            spatial = null;
        }

        super.updateSelection(spatial);
    }

    @Override
    public void notifyAddedChild(@NotNull final Object parent, @NotNull final Object added, final int index) {
        super.notifyAddedChild(parent, added, index);

        final LayerNodeTree layerNodeTree = getLayerNodeTree();

        if (parent instanceof LayersRoot) {
            layerNodeTree.notifyAdded(parent, added, index);
        } else if (added instanceof Spatial) {
            layerNodeTree.notifyAdded((Spatial) added);
        }

        EXECUTOR_MANAGER.addEditorThreadTask(() -> getCurrentModel().notifyAdded(added));
    }

    @Override
    public void notifyRemovedChild(@NotNull final Object parent, @NotNull final Object removed) {
        super.notifyRemovedChild(parent, removed);

        final LayerNodeTree layerNodeTree = getLayerNodeTree();

        if (parent instanceof LayersRoot) {
            layerNodeTree.notifyRemoved(parent, removed);
        } else if (removed instanceof Spatial) {
            layerNodeTree.notifyRemoved(null, removed);
        }

        EXECUTOR_MANAGER.addEditorThreadTask(() -> getCurrentModel().notifyRemoved(removed));
    }

    @Override
    public void notifyChangeProperty(@Nullable final Object parent, @NotNull final Object object,
                                     @NotNull final String propertyName) {
        super.notifyChangeProperty(parent, object, propertyName);

        if (object instanceof Spatial && Objects.equals(propertyName, SceneLayer.KEY)) {

            final Spatial spatial = (Spatial) object;
            final SceneLayer layer = SceneLayer.getLayer(spatial);

            if (layer == null) {
                spatial.setVisible(true);
            } else {
                spatial.setVisible(layer.isShowed());
            }

            final LayerNodeTree layerNodeTree = getLayerNodeTree();
            layerNodeTree.notifyChangedLayer(spatial, layer);
        }

        final LayerNodeTree layerNodeTree = getLayerNodeTree();
        layerNodeTree.notifyChanged(null, object);

        if (!(object instanceof EditableProperty)) {
            return;
        }

        final EditableProperty<?, ?> property = (EditableProperty<?, ?>) object;
        final Object editObject = property.getObject();

        final ModelPropertyEditor modelPropertyEditor = getModelPropertyEditor();
        modelPropertyEditor.syncFor(editObject);
    }

    @Override
    public void notifyAddedAppState(@NotNull final SceneAppState appState) {
        getEditorAppState().addAppState(appState);
        getAppStateList().fill(getCurrentModel());
    }

    @Override
    public void notifyRemovedAppState(@NotNull final SceneAppState appState) {
        getEditorAppState().removeAppState(appState);
        getAppStateList().fill(getCurrentModel());
    }

    @Override
    public void notifyChangedAppState(@NotNull final SceneAppState appState) {
        getAppStateList().fill(getCurrentModel());
    }

    @Override
    public void notifyAddedFilter(@NotNull final SceneFilter<?> sceneFilter) {
        getFilterList().fill(getCurrentModel());
        getEditorAppState().addFilter(sceneFilter);
    }

    @Override
    public void notifyRemovedFilter(@NotNull final SceneFilter<?> sceneFilter) {
        getFilterList().fill(getCurrentModel());
        getEditorAppState().removeFilter(sceneFilter);
    }

    @Override
    public void notifyChangedFilter(@NotNull final SceneFilter<?> sceneFilter) {
        getFilterList().fill(getCurrentModel());
    }

    @Override
    public String toString() {
        return "SceneFileEditor{} " + super.toString();
    }
}
