package com.ss.editor.ui.component.editor.impl.scene;

import static com.ss.editor.util.EditorUtil.getAssetFile;
import static com.ss.editor.util.EditorUtil.toAssetPath;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.asset.AssetManager;
import com.jme3.asset.ModelKey;
import com.jme3.scene.Spatial;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.extension.property.EditableProperty;
import com.ss.editor.extension.scene.SceneLayer;
import com.ss.editor.extension.scene.SceneNode;
import com.ss.editor.extension.scene.ScenePresentable;
import com.ss.editor.extension.scene.app.state.EditableSceneAppState;
import com.ss.editor.extension.scene.app.state.SceneAppState;
import com.ss.editor.extension.scene.filter.EditableSceneFilter;
import com.ss.editor.extension.scene.filter.SceneFilter;
import com.ss.editor.model.node.layer.LayersRoot;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.part3d.editor.impl.scene.SceneEditor3DPart;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.editor.EditorDescription;
import com.ss.editor.ui.component.editor.impl.AbstractFileEditor;
import com.ss.editor.ui.component.editor.state.EditorState;
import com.ss.editor.ui.component.editor.state.impl.EditorSceneEditorState;
import com.ss.editor.ui.component.tab.EditorToolComponent;
import com.ss.editor.ui.control.app.state.list.AppStateList;
import com.ss.editor.ui.control.filter.list.FilterList;
import com.ss.editor.ui.control.layer.LayerNodeTree;
import com.ss.editor.ui.control.model.ModelNodeTree;
import com.ss.editor.ui.control.model.ModelPropertyEditor;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.util.DynamicIconSupport;
import com.ss.editor.util.EditorUtil;
import com.ss.editor.util.MaterialUtils;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.array.Array;
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
public class SceneFileEditor extends AbstractSceneFileEditor<SceneNode, SceneEditor3DPart, EditorSceneEditorState>
        implements SceneChangeConsumer {

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
    @FxThread
    protected @NotNull SceneEditor3DPart create3DEditorPart() {
        return new SceneEditor3DPart(this);
    }

    @Override
    @FxThread
    protected @Nullable Supplier<EditorState> getEditorStateFactory() {
        return EditorSceneEditorState::new;
    }

    @Override
    @FxThread
    protected void doOpenFile(@NotNull final Path file) throws IOException {
        super.doOpenFile(file);

        final Path assetFile = notNull(getAssetFile(file), "Asset file for " + file + " can't be null.");
        final ModelKey modelKey = new ModelKey(toAssetPath(assetFile));

        final AssetManager assetManager = EditorUtil.getAssetManager();

        Spatial loadedScene = assetManager.loadAsset(modelKey);

        final SceneNode model = (SceneNode) loadedScene;
        model.depthFirstTraversal(this::updateVisibility);

        MaterialUtils.cleanUpMaterialParams(model);

        final SceneEditor3DPart editor3DState = getEditor3DPart();
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
    @FxThread
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

    @FxThread
    private void updateVisibility(@NotNull final Spatial spatial) {
        final SceneLayer layer = SceneLayer.getLayer(spatial);
        if (layer != null) {
            spatial.setVisible(layer.isShowed());
        }
    }

    /**
     * Get the list with app states.
     *
     * @return the list with app states.
     */
    @FxThread
    private @NotNull AppStateList getAppStateList() {
        return notNull(appStateList);
    }

    /**
     * Get the list with filters.
     *
     * @return the list with filters.
     */
    @FxThread
    private @NotNull FilterList getFilterList() {
        return notNull(filterList);
    }

    /**
     * Get the tree with layers.
     *
     * @return the tree with layers.
     */
    @FxThread
    private @NotNull LayerNodeTree getLayerNodeTree() {
        return notNull(layerNodeTree);
    }

    /**
     * Get the container of property editor in layers tool.
     *
     * @return the container of property editor in layers tool.
     */
    @FxThread
    private @NotNull VBox getPropertyEditorLayersContainer() {
        return notNull(propertyEditorLayersContainer);
    }

    /**
     * Get the container of property editor in filters tool.
     *
     * @return the container of property editor in filters tool.
     */
    @FxThread
    private @NotNull VBox getPropertyEditorFiltersContainer() {
        return notNull(propertyEditorFiltersContainer);
    }

    @Override
    @FxThread
    protected boolean isNeedToOpenObjectsTool(final int current) {
        return !(current == OBJECTS_TOOL || current == LAYERS_TOOL || current == SCRIPTING_TOOL);
    }

    /**
     * Get the container of property editor in app states tool.
     *
     * @return the container of property editor in app states tool.
     */
    @FxThread
    private @NotNull VBox getPropertyEditorAppStateContainer() {
        return notNull(propertyEditorAppStateContainer);
    }

    @Override
    @FxThread
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

        FXUtils.addClassesTo(lightButton, audioButton, CssClasses.FILE_EDITOR_TOOLBAR_BUTTON);

        FXUtils.addToPane(lightButton, container);
        FXUtils.addToPane(audioButton, container);
    }

    /**
     * Handle changing light models visibility.
     */
    @FxThread
    private void changeLight(@NotNull final Boolean newValue) {

        if (isIgnoreListeners()) {
            return;
        }

        final SceneEditor3DPart editor3DPart = getEditor3DPart();
        editor3DPart.updateLightShowed(newValue);

        if (editorState != null) {
            editorState.setShowedLight(newValue);
        }
    }

    /**
     * Handle changing audio models visibility.
     */
    @FxThread
    private void changeAudio(@NotNull final Boolean newValue) {

        if (isIgnoreListeners()) {
            return;
        }

        final SceneEditor3DPart editor3DPart = getEditor3DPart();
        editor3DPart.updateAudioShowed(newValue);

        if (editorState != null) {
            editorState.setShowedAudio(newValue);
        }
    }

    @Override
    @FxThread
    protected void createContent(@NotNull final StackPane root) {

        appStateList = new AppStateList(this::selectAppStateFromList, this);
        propertyEditorAppStateContainer = new VBox();

        filterList = new FilterList(this::selectFilterFromList, this);
        propertyEditorFiltersContainer = new VBox();

        layerNodeTree = new LayerNodeTree(this::selectNodeFromLayersTree, this);
        propertyEditorLayersContainer = new VBox();

        super.createContent(root);

        FXUtils.addClassTo(layerNodeTree.getTreeView(), CssClasses.TRANSPARENT_TREE_VIEW);
    }

    @Override
    @FxThread
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
    @FxThread
    protected void processChangeTool(@Nullable final Number oldValue, @NotNull final Number newValue) {
        super.processChangeTool(oldValue, newValue);

        final int newIndex = newValue.intValue();
        if (newIndex < 2) {
            return;
        }

        final ModelPropertyEditor modelPropertyEditor = getModelPropertyEditor();
        final VBox appStateContainer = getPropertyEditorAppStateContainer();
        final VBox filtersContainer = getPropertyEditorFiltersContainer();
        final VBox layersContainer = getPropertyEditorLayersContainer();

        switch (newIndex) {
            case LAYERS_TOOL: {
                final LayerNodeTree layerNodeTree = getLayerNodeTree();
                final TreeNode<?> selected = layerNodeTree.getSelected();
                FXUtils.addToPane(modelPropertyEditor, layersContainer);
                selectNodeFromLayersTree(selected);
                break;
            }
            case APP_STATES_TOOL: {
                final AppStateList appStateList = getAppStateList();
                FXUtils.addToPane(modelPropertyEditor, appStateContainer);
                selectAppStateFromList(appStateList.getSelected());
                break;
            }
            case FILTERS_TOOL: {
                final FilterList filterList = getFilterList();
                FXUtils.addToPane(modelPropertyEditor, filtersContainer);
                selectFilterFromList(filterList.getSelected());
                break;
            }
        }
    }

    /**
     * Get the light toggle.
     *
     * @return the light toggle.
     */
    @FxThread
    private @NotNull ToggleButton getLightButton() {
        return notNull(lightButton);
    }

    /**
     * Get the audio toggle.
     *
     * @return the audio toggle.
     */
    @FxThread
    private @NotNull ToggleButton getAudioButton() {
        return notNull(audioButton);
    }

    @Override
    @FxThread
    protected void loadState() {
        super.loadState();

        final EditorSceneEditorState editorState = notNull(getEditorState());
        getLightButton().setSelected(editorState.isShowedLight());
        getAudioButton().setSelected(editorState.isShowedAudio());
    }

    /**
     * Handle the selected app state from the list.
     */
    @FxThread
    private void selectAppStateFromList(@Nullable final EditableSceneAppState appState) {

        if (!isNeedSyncSelection()) {
            return;
        }

        setNeedSyncSelection(false);
        try {
            super.selectNodeFromTree(appState);
        } finally {
            setNeedSyncSelection(true);
        }
    }

    /**
     * Handle the selected filter from the list.
     */
    @FxThread
    private void selectFilterFromList(@Nullable final EditableSceneFilter sceneFilter) {

        if (!isNeedSyncSelection()) {
            return;
        }

        setNeedSyncSelection(false);
        try {
            super.selectNodeFromTree(sceneFilter);
        } finally {
            setNeedSyncSelection(true);
        }
    }

    /**
     * Handle the selected object from the layers tree.
     */
    @FxThread
    private void selectNodeFromLayersTree(@Nullable final Object object) {

        if (!isNeedSyncSelection()) {
            return;
        }

        setNeedSyncSelection(false);
        try {

            final ModelNodeTree modelNodeTree = getModelNodeTree();
            modelNodeTree.selectSingle(object);

            selectNodeFromTree(object);

        } finally {
            setNeedSyncSelection(true);
        }
    }

    @Override
    @FxThread
    public void selectNodesFromTree(@NotNull final Array<?> objects) {

        if (!isNeedSyncSelection()) {
            super.selectNodesFromTree(objects);
            return;
        }

        setNeedSyncSelection(false);
        try {

            final LayerNodeTree layerNodeTree = getLayerNodeTree();
            layerNodeTree.selects(objects);

            super.selectNodesFromTree(objects);

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
     * Return true if need sync selection.
     *
     * @param needSyncSelection true if need sync selection.
     */
    @FxThread
    private void setNeedSyncSelection(final boolean needSyncSelection) {
        this.needSyncSelection = needSyncSelection;
    }

    /**
     * Return true if need sync selection.
     *
     * @return true if need sync selection.
     */
    @FxThread
    private boolean isNeedSyncSelection() {
        return needSyncSelection;
    }

    @Override
    protected boolean canSelect(@NotNull final Spatial spatial) {
        return !(spatial instanceof SceneNode) && !(spatial instanceof SceneLayer) && super.canSelect(spatial);
    }

    @Override
    @FxThread
    protected void handleAddedObject(@NotNull final Spatial model) {
        super.handleAddedObject(model);

        if (!(model instanceof SceneNode)) {
            return;
        }

        final SceneNode sceneNode = (SceneNode) model;
        final SceneEditor3DPart editor3DState = getEditor3DPart();

        sceneNode.getFilters().stream()
                .filter(ScenePresentable.class::isInstance)
                .forEach(filter -> editor3DState.addPresentable((ScenePresentable) filter));
        sceneNode.getAppStates().stream()
                .filter(ScenePresentable.class::isInstance)
                .forEach(state -> editor3DState.addPresentable((ScenePresentable) state));
    }

    @Override
    @FxThread
    protected void handleRemovedObject(@NotNull final Spatial model) {
        super.handleRemovedObject(model);

        if (!(model instanceof SceneNode)) {
            return;
        }

        final SceneNode sceneNode = (SceneNode) model;
        final SceneEditor3DPart editor3DState = getEditor3DPart();

        sceneNode.getFilters().stream()
                .filter(ScenePresentable.class::isInstance)
                .forEach(filter -> editor3DState.removePresentable((ScenePresentable) filter));
        sceneNode.getAppStates().stream()
                .filter(ScenePresentable.class::isInstance)
                .forEach(state -> editor3DState.removePresentable((ScenePresentable) state));
    }

    @Override
    @FxThread
    public void notifyFxAddedChild(@NotNull final Object parent, @NotNull final Object added, final int index,
                                   final boolean needSelect) {
        super.notifyFxAddedChild(parent, added, index, needSelect);

        final LayerNodeTree layerNodeTree = getLayerNodeTree();

        if (parent instanceof LayersRoot) {
            layerNodeTree.notifyAdded(parent, added, index);
        } else if (added instanceof Spatial) {
            layerNodeTree.notifyAdded((Spatial) added);
        }

        EXECUTOR_MANAGER.addJmeTask(() -> getCurrentModel().notifyAdded(added));
    }

    @Override
    @FxThread
    public void notifyFxRemovedChild(@NotNull final Object parent, @NotNull final Object removed) {
        super.notifyFxRemovedChild(parent, removed);

        final LayerNodeTree layerNodeTree = getLayerNodeTree();

        if (parent instanceof LayersRoot) {
            layerNodeTree.notifyRemoved(parent, removed);
        } else if (removed instanceof Spatial) {
            layerNodeTree.notifyRemoved((Spatial) removed);
        }

        EXECUTOR_MANAGER.addJmeTask(() -> getCurrentModel().notifyRemoved(removed));
    }

    @Override
    @FxThread
    public void notifyFxChangeProperty(@Nullable final Object parent, @NotNull final Object object,
                                       @NotNull final String propertyName) {
        super.notifyFxChangeProperty(parent, object, propertyName);

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
    @FxThread
    public void notifyAddedAppState(@NotNull final SceneAppState appState) {

        final SceneEditor3DPart editor3DPart = getEditor3DPart();
        editor3DPart.addAppState(appState);

        if (appState instanceof ScenePresentable) {
            editor3DPart.addPresentable((ScenePresentable) appState);
        }

        getAppStateList().fill(getCurrentModel());
    }

    @Override
    @FxThread
    public void notifyRemovedAppState(@NotNull final SceneAppState appState) {

        final SceneEditor3DPart editor3DPart = getEditor3DPart();
        editor3DPart.removeAppState(appState);

        if (appState instanceof ScenePresentable) {
            editor3DPart.removePresentable((ScenePresentable) appState);
        }

        getAppStateList().fill(getCurrentModel());
    }

    @Override
    @FxThread
    public void notifyChangedAppState(@NotNull final SceneAppState appState) {
        getAppStateList().fill(getCurrentModel());
    }

    @Override
    @FxThread
    public void notifyAddedFilter(@NotNull final SceneFilter sceneFilter) {

        final SceneEditor3DPart editor3DPart = getEditor3DPart();
        editor3DPart.addFilter(sceneFilter);

        if (sceneFilter instanceof ScenePresentable) {
            editor3DPart.addPresentable((ScenePresentable) sceneFilter);
        }

        getFilterList().fill(getCurrentModel());
    }

    @Override
    @FxThread
    public void notifyRemovedFilter(@NotNull final SceneFilter sceneFilter) {

        final SceneEditor3DPart editor3DPart = getEditor3DPart();
        editor3DPart.removeFilter(sceneFilter);

        if (sceneFilter instanceof ScenePresentable) {
            editor3DPart.removePresentable((ScenePresentable) sceneFilter);
        }

        getFilterList().fill(getCurrentModel());
    }

    @Override
    @FxThread
    public void notifyChangedFilter(@NotNull final SceneFilter sceneFilter) {
        getFilterList().fill(getCurrentModel());
    }

    @Override
    @FxThread
    public void notifyHided() {
        super.notifyHided();
        EXECUTOR_MANAGER.addJmeTask(EditorUtil::enableGlobalLightProbe);
    }

    @Override
    @FxThread
    public void notifyShowed() {
        super.notifyShowed();
        EXECUTOR_MANAGER.addJmeTask(EditorUtil::disableGlobalLightProbe);
    }

    @Override
    public String toString() {
        return "SceneFileEditor{} " + super.toString();
    }
}
