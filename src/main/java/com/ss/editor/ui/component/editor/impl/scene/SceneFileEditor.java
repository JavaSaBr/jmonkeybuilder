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
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.extension.property.EditableProperty;
import com.ss.editor.extension.scene.SceneLayer;
import com.ss.editor.extension.scene.SceneNode;
import com.ss.editor.extension.scene.ScenePresentable;
import com.ss.editor.extension.scene.app.state.EditableSceneAppState;
import com.ss.editor.extension.scene.app.state.SceneAppState;
import com.ss.editor.extension.scene.filter.EditableSceneFilter;
import com.ss.editor.extension.scene.filter.SceneFilter;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.state.editor.impl.scene.SceneEditor3DState;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.editor.EditorDescription;
import com.ss.editor.ui.component.editor.impl.AbstractFileEditor;
import com.ss.editor.ui.component.editor.state.EditorState;
import com.ss.editor.ui.component.editor.state.impl.EditorSceneEditorState;
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
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * The implementation of the {@link AbstractFileEditor} for working with {@link SceneNode}.
 *
 * @author JavaSaBr
 */
public class SceneFileEditor extends
        AbstractSceneFileEditor<SceneNode, SceneEditor3DState, EditorSceneEditorState> implements SceneChangeConsumer {

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

    @Override
    @FXThread
    protected @NotNull SceneEditor3DState create3DEditorState() {
        return new SceneEditor3DState(this);
    }

    @Override
    @FXThread
    protected @Nullable Supplier<EditorState> getEditorStateFactory() {
        return EditorSceneEditorState::new;
    }

    @Override
    @FXThread
    protected void doOpenFile(@NotNull final Path file) throws IOException {
        super.doOpenFile(file);

        final Path assetFile = notNull(getAssetFile(file), "Asset file for " + file + " can't be null.");
        final ModelKey modelKey = new ModelKey(toAssetPath(assetFile));

        final AssetManager assetManager = JME_APPLICATION.getAssetManager();

        Spatial loadedScene = assetManager.loadAsset(modelKey);

        final SceneNode model = (SceneNode) loadedScene;
        model.depthFirstTraversal(this::updateVisibility);

        MaterialUtils.cleanUpMaterialParams(model);

        final SceneEditor3DState editor3DState = getEditor3DState();
        editor3DState.openModel(model);

        handleAddedObject(model);

        setCurrentModel(model);
        setIgnoreListeners(true);
        try {
            refreshTree();
        } finally {
            setIgnoreListeners(false);
        }
    }

    @Override
    @FXThread
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

    @FXThread
    private void updateVisibility(@NotNull final Spatial spatial) {
        final SceneLayer layer = SceneLayer.getLayer(spatial);
        if (layer != null) spatial.setVisible(layer.isShowed());
    }

    /**
     * @return the list with app states.
     */
    @FXThread
    private @NotNull AppStateList getAppStateList() {
        return notNull(appStateList);
    }

    /**
     * @return the list with filters.
     */
    @FXThread
    private @NotNull FilterList getFilterList() {
        return notNull(filterList);
    }

    /**
     * @return the tree with layers.
     */
    @FXThread
    private @NotNull LayerNodeTree getLayerNodeTree() {
        return notNull(layerNodeTree);
    }

    /**
     * @return the container of property editor in layers tool.
     */
    @FXThread
    private @NotNull VBox getPropertyEditorLayersContainer() {
        return notNull(propertyEditorLayersContainer);
    }

    /**
     * @return the container of property editor in filters tool.
     */
    @FXThread
    private @NotNull VBox getPropertyEditorFiltersContainer() {
        return notNull(propertyEditorFiltersContainer);
    }

    /**
     * @return the container of property editor in app states tool.
     */
    @FXThread
    private @NotNull VBox getPropertyEditorAppStateContainer() {
        return notNull(propertyEditorAppStateContainer);
    }

    @Override
    @FXThread
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
    @FXThread
    private void changeLight(@NotNull final Boolean newValue) {
        if (isIgnoreListeners()) return;

        final SceneEditor3DState editor3DState = getEditor3DState();
        editor3DState.updateLightShowed(newValue);

        if (editorState != null) editorState.setShowedLight(newValue);
    }

    /**
     * Handle changing audio models visibility.
     */
    @FXThread
    private void changeAudio(@NotNull final Boolean newValue) {
        if (isIgnoreListeners()) return;

        final SceneEditor3DState editor3DState = getEditor3DState();
        editor3DState.updateAudioShowed(newValue);

        if (editorState != null) editorState.setShowedAudio(newValue);
    }

    @Override
    @FXThread
    protected void createContent(@NotNull final StackPane root) {

        appStateList = new AppStateList(this::selectAppStateFromList, this);
        propertyEditorAppStateContainer = new VBox();

        filterList = new FilterList(this::selectFilterFromList, this);
        propertyEditorFiltersContainer = new VBox();

        layerNodeTree = new LayerNodeTree(this::selectNodeFromLayersTree, this);
        propertyEditorLayersContainer = new VBox();

        super.createContent(root);

        FXUtils.addClassTo(layerNodeTree.getTreeView(), CSSClasses.TRANSPARENT_TREE_VIEW);
    }

    @Override
    @FXThread
    protected void createToolComponents(@NotNull final EditorToolComponent container, @NotNull final StackPane root) {
        super.createToolComponents(container, root);

        container.addComponent(buildSplitComponent(getLayerNodeTree(), getPropertyEditorLayersContainer(), root),
                Messages.SCENE_FILE_EDITOR_TOOL_LAYERS);
        container.addComponent(buildSplitComponent(getAppStateList(), getPropertyEditorAppStateContainer(), root),
                Messages.SCENE_FILE_EDITOR_TOOL_APP_STATES);
        container.addComponent(buildSplitComponent(getFilterList(), getPropertyEditorFiltersContainer(), root),
                Messages.SCENE_FILE_EDITOR_TOOL_FILTERS);
    }

    @Override
    @FXThread
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
                modelPropertyEditor.rebuild();
                break;
            }
            case APP_STATES_TOOL: {
                FXUtils.addToPane(modelPropertyEditor, appStateContainer);
                modelPropertyEditor.rebuild();
                break;
            }
            case FILTERS_TOOL: {
                FXUtils.addToPane(modelPropertyEditor, filtersContainer);
                modelPropertyEditor.rebuild();
                break;
            }
        }
    }

    /**
     * @return the light toggle.
     */
    @FXThread
    private @NotNull ToggleButton getLightButton() {
        return notNull(lightButton);
    }

    /**
     * @return the audio toggle.
     */
    @FXThread
    private @NotNull ToggleButton getAudioButton() {
        return notNull(audioButton);
    }

    @Override
    @FXThread
    protected void loadState() {
        super.loadState();

        final EditorSceneEditorState editorState = notNull(getEditorState());
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
    private void selectFilterFromList(@Nullable final EditableSceneFilter sceneFilter) {
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
    @FXThread
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

    @Override
    @FromAnyThread
    public @NotNull EditorDescription getDescription() {
        return DESCRIPTION;
    }

    /**
     * @param needSyncSelection true if need sync selection.
     */
    @FXThread
    private void setNeedSyncSelection(final boolean needSyncSelection) {
        this.needSyncSelection = needSyncSelection;
    }

    /**
     * @return true if need sync selection.
     */
    @FXThread
    private boolean isNeedSyncSelection() {
        return needSyncSelection;
    }

    @Override
    @FXThread
    protected void updateSelection(@Nullable Spatial spatial) {

        if (spatial instanceof SceneNode || spatial instanceof SceneLayer) {
            spatial = null;
        }

        super.updateSelection(spatial);
    }

    @Override
    @FXThread
    protected void handleAddedObject(@NotNull final Spatial model) {
        super.handleAddedObject(model);

        if (!(model instanceof SceneNode)) {
            return;
        }

        final SceneNode sceneNode = (SceneNode) model;
        final SceneEditor3DState editor3DState = getEditor3DState();

        sceneNode.getFilters().stream()
                .filter(ScenePresentable.class::isInstance)
                .forEach(filter -> editor3DState.addPresentable((ScenePresentable) filter));
        sceneNode.getAppStates().stream()
                .filter(ScenePresentable.class::isInstance)
                .forEach(state -> editor3DState.addPresentable((ScenePresentable) state));
    }

    @Override
    @FXThread
    protected void handleRemovedObject(@NotNull final Spatial model) {
        super.handleRemovedObject(model);

        if (!(model instanceof SceneNode)) {
            return;
        }

        final SceneNode sceneNode = (SceneNode) model;
        final SceneEditor3DState editor3DState = getEditor3DState();

        sceneNode.getFilters().stream()
                .filter(ScenePresentable.class::isInstance)
                .forEach(filter -> editor3DState.removePresentable((ScenePresentable) filter));
        sceneNode.getAppStates().stream()
                .filter(ScenePresentable.class::isInstance)
                .forEach(state -> editor3DState.removePresentable((ScenePresentable) state));
    }

    @Override
    @FXThread
    public void notifyFXAddedChild(@NotNull final Object parent, @NotNull final Object added, final int index,
                                   final boolean needSelect) {
        super.notifyFXAddedChild(parent, added, index, needSelect);

        final LayerNodeTree layerNodeTree = getLayerNodeTree();

        if (parent instanceof LayersRoot) {
            layerNodeTree.notifyAdded(parent, added, index);
        } else if (added instanceof Spatial) {
            layerNodeTree.notifyAdded((Spatial) added);
        }

        EXECUTOR_MANAGER.addJMETask(() -> getCurrentModel().notifyAdded(added));
    }

    @Override
    @FXThread
    public void notifyFXRemovedChild(@NotNull final Object parent, @NotNull final Object removed) {
        super.notifyFXRemovedChild(parent, removed);

        final LayerNodeTree layerNodeTree = getLayerNodeTree();

        if (parent instanceof LayersRoot) {
            layerNodeTree.notifyRemoved(parent, removed);
        } else if (removed instanceof Spatial) {
            layerNodeTree.notifyRemoved((Spatial) removed);
        }

        EXECUTOR_MANAGER.addJMETask(() -> getCurrentModel().notifyRemoved(removed));
    }

    @Override
    @FXThread
    public void notifyFXChangeProperty(@Nullable final Object parent, @NotNull final Object object,
                                       @NotNull final String propertyName) {
        super.notifyFXChangeProperty(parent, object, propertyName);

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
    @FXThread
    public void notifyAddedAppState(@NotNull final SceneAppState appState) {

        final SceneEditor3DState editor3DState = getEditor3DState();
        editor3DState.addAppState(appState);

        if (appState instanceof ScenePresentable) {
            editor3DState.addPresentable((ScenePresentable) appState);
        }

        getAppStateList().fill(getCurrentModel());
    }

    @Override
    @FXThread
    public void notifyRemovedAppState(@NotNull final SceneAppState appState) {

        final SceneEditor3DState editor3DState = getEditor3DState();
        editor3DState.removeAppState(appState);

        if (appState instanceof ScenePresentable) {
            editor3DState.removePresentable((ScenePresentable) appState);
        }

        getAppStateList().fill(getCurrentModel());
    }

    @Override
    @FXThread
    public void notifyChangedAppState(@NotNull final SceneAppState appState) {
        getAppStateList().fill(getCurrentModel());
    }

    @Override
    @FXThread
    public void notifyAddedFilter(@NotNull final SceneFilter sceneFilter) {

        final SceneEditor3DState editor3DState = getEditor3DState();
        editor3DState.addFilter(sceneFilter);

        if (sceneFilter instanceof ScenePresentable) {
            editor3DState.addPresentable((ScenePresentable) sceneFilter);
        }

        getFilterList().fill(getCurrentModel());
    }

    @Override
    @FXThread
    public void notifyRemovedFilter(@NotNull final SceneFilter sceneFilter) {

        final SceneEditor3DState editor3DState = getEditor3DState();
        editor3DState.removeFilter(sceneFilter);

        if (sceneFilter instanceof ScenePresentable) {
            editor3DState.removePresentable((ScenePresentable) sceneFilter);
        }

        getFilterList().fill(getCurrentModel());
    }

    @Override
    @FXThread
    public void notifyChangedFilter(@NotNull final SceneFilter sceneFilter) {
        getFilterList().fill(getCurrentModel());
    }

    @Override
    @FXThread
    public void notifyHided() {
        super.notifyHided();
        EXECUTOR_MANAGER.addJMETask(JME_APPLICATION::enableLightProbe);
    }

    @Override
    @FXThread
    public void notifyShowed() {
        super.notifyShowed();
        EXECUTOR_MANAGER.addJMETask(JME_APPLICATION::disableLightProbe);
    }

    @Override
    public String toString() {
        return "SceneFileEditor{} " + super.toString();
    }
}
