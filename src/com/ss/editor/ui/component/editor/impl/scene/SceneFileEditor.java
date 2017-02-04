package com.ss.editor.ui.component.editor.impl.scene;

import static com.ss.editor.util.EditorUtil.getAssetFile;
import static com.ss.editor.util.EditorUtil.toAssetPath;
import static java.util.Objects.requireNonNull;
import com.jme3.asset.AssetManager;
import com.jme3.asset.ModelKey;
import com.jme3.scene.Spatial;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.state.editor.impl.scene.SceneEditorAppState;
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
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.util.MaterialUtils;
import com.ss.extension.property.EditableProperty;
import com.ss.extension.scene.SceneLayer;
import com.ss.extension.scene.SceneNode;
import com.ss.extension.scene.app.state.EditableSceneAppState;
import com.ss.extension.scene.app.state.SceneAppState;
import com.ss.extension.scene.filter.EditableSceneFilter;
import com.ss.extension.scene.filter.SceneFilter;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rlib.ui.util.FXUtils;

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
     * The property container of app states.
     */
    @Nullable
    private VBox appStatePropertyContainer;

    /**
     * The property container of filters.
     */
    @Nullable
    private VBox filterPropertyContainer;

    /**
     * The property container from layers tree.
     */
    @Nullable
    private VBox layerTreePropertyContainer;

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

    @Override
    public void openFile(@NotNull final Path file) {
        super.openFile(file);

        final Path assetFile = requireNonNull(getAssetFile(file), "Asset file for " + file + " can't be null.");
        final ModelKey modelKey = new ModelKey(toAssetPath(assetFile));

        final AssetManager assetManager = EDITOR.getAssetManager();
        final SceneNode model = (SceneNode) assetManager.loadAsset(modelKey);
        model.depthFirstTraversal(this::updateVisibility);

        MaterialUtils.cleanUpMaterialParams(model);

        final SceneEditorAppState editorState = getEditorAppState();
        editorState.openModel(model);

        handleAddedObject(model);

        setCurrentModel(model);
        setIgnoreListeners(true);
        try {

            final ModelNodeTree modelNodeTree = getModelNodeTree();
            modelNodeTree.fill(model);

            final AppStateList appStateList = getAppStateList();
            appStateList.fill(model);

            final FilterList filterList = getFilterList();
            filterList.fill(model);

            final LayerNodeTree layerNodeTree = getLayerNodeTree();
            layerNodeTree.fill(new LayersRoot(this));

        } finally {
            setIgnoreListeners(false);
        }

        EXECUTOR_MANAGER.addFXTask(this::loadState);
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
        return requireNonNull(appStateList);
    }

    /**
     * @return the list with filters.
     */
    @NotNull
    private FilterList getFilterList() {
        return requireNonNull(filterList);
    }

    /**
     * @return the tree with layers.
     */
    @NotNull
    private LayerNodeTree getLayerNodeTree() {
        return requireNonNull(layerNodeTree);
    }

    /**
     * @return the property container from layers tree.
     */
    @NotNull
    private VBox getLayerTreePropertyContainer() {
        return requireNonNull(layerTreePropertyContainer);
    }

    /**
     * @return the property container of filters.
     */
    @NotNull
    private VBox getFilterPropertyContainer() {
        return requireNonNull(filterPropertyContainer);
    }

    /**
     * @return the property container of app states.
     */
    @NotNull
    private VBox getAppStatePropertyContainer() {
        return requireNonNull(appStatePropertyContainer);
    }

    @Override
    protected void createContent(@NotNull final StackPane root) {
        super.createContent(root);

        appStateList = new AppStateList(this::selectAppStateFromList, this);
        appStatePropertyContainer = new VBox();

        filterList = new FilterList(this::selectFilterFromList, this);
        filterPropertyContainer = new VBox();

        layerNodeTree = new LayerNodeTree(this::selectNodeFromLayersTree, this);
        layerTreePropertyContainer = new VBox();

        final SplitPane appStateSplitContainer = new SplitPane(appStateList, appStatePropertyContainer);
        appStateSplitContainer.setId(CSSIds.FILE_EDITOR_TOOL_SPLIT_PANE);
        appStateSplitContainer.prefHeightProperty().bind(root.heightProperty());
        appStateSplitContainer.prefWidthProperty().bind(root.widthProperty());

        final SplitPane filtersSplitContainer = new SplitPane(filterList, filterPropertyContainer);
        filtersSplitContainer.setId(CSSIds.FILE_EDITOR_TOOL_SPLIT_PANE);
        filtersSplitContainer.prefHeightProperty().bind(root.heightProperty());
        filtersSplitContainer.prefWidthProperty().bind(root.widthProperty());

        final SplitPane layersSplitContainer = new SplitPane(layerNodeTree, layerTreePropertyContainer);
        layersSplitContainer.setId(CSSIds.FILE_EDITOR_TOOL_SPLIT_PANE);
        layersSplitContainer.prefHeightProperty().bind(root.heightProperty());
        layersSplitContainer.prefWidthProperty().bind(root.widthProperty());

        final EditorToolComponent editorToolComponent = getEditorToolComponent();
        editorToolComponent.addComponent(layersSplitContainer, Messages.SCENE_FILE_EDITOR_TOOL_LAYERS);
        editorToolComponent.addComponent(appStateSplitContainer, Messages.SCENE_FILE_EDITOR_TOOL_APP_STATES);
        editorToolComponent.addComponent(filtersSplitContainer, Messages.SCENE_FILE_EDITOR_TOOL_FILTERS);

        root.heightProperty().addListener((observableValue, oldValue, newValue) -> calcVSplitSize(appStateSplitContainer));
        root.heightProperty().addListener((observableValue, oldValue, newValue) -> calcVSplitSize(filtersSplitContainer));
        root.heightProperty().addListener((observableValue, oldValue, newValue) -> calcVSplitSize(layersSplitContainer));
    }

    @Override
    protected void processChangeTool(@NotNull final Number newValue) {
        super.processChangeTool(newValue);

        if (newValue.intValue() < 1) return;

        final ModelPropertyEditor modelPropertyEditor = getModelPropertyEditor();
        final VBox appStatePropertyContainer = getAppStatePropertyContainer();
        final VBox filterPropertyContainer = getFilterPropertyContainer();
        final VBox layerTreePropertyContainer = getLayerTreePropertyContainer();

        switch (newValue.intValue()) {
            case 1: {
                FXUtils.addToPane(modelPropertyEditor, layerTreePropertyContainer);
                break;
            }
            case 2: {
                FXUtils.addToPane(modelPropertyEditor, appStatePropertyContainer);
                break;
            }
            case 3: {
                FXUtils.addToPane(modelPropertyEditor, filterPropertyContainer);
                break;
            }
        }
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

        } finally {
            setNeedSyncSelection(true);
        }

        final ModelPropertyEditor modelPropertyEditor = getModelPropertyEditor();
        modelPropertyEditor.buildFor(appState, null);
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

            final ModelPropertyEditor modelPropertyEditor = getModelPropertyEditor();
            modelPropertyEditor.buildFor(sceneFilter, null);

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

            final ModelPropertyEditor modelPropertyEditor = getModelPropertyEditor();
            modelPropertyEditor.buildFor(object, null);

        } finally {
            setNeedSyncSelection(true);
        }
    }

    @Override
    public void selectNodeFromTree(@Nullable final Object object) {
        super.selectNodeFromTree(object);

        if (!isNeedSyncSelection()) return;

        setNeedSyncSelection(false);
        try {

            final LayerNodeTree layerNodeTree = getLayerNodeTree();
            layerNodeTree.select(object);

            final AppStateList appStateList = getAppStateList();
            appStateList.clearSelection();

            final FilterList filterList = getFilterList();
            filterList.clearSelection();

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

        if (parent instanceof LayersRoot) {
            getLayerNodeTree().notifyAdded(parent, added, index);
        }

        EXECUTOR_MANAGER.addEditorThreadTask(() -> getCurrentModel().notifyAdded(added));
    }

    @Override
    public void notifyRemovedChild(@NotNull final Object parent, @NotNull final Object removed) {
        super.notifyRemovedChild(parent, removed);

        if (parent instanceof LayersRoot) {
            getLayerNodeTree().notifyRemoved(parent, removed);
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
