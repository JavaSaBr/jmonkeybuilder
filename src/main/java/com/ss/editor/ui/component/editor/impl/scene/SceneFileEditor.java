package com.ss.editor.ui.component.editor.impl.scene;

import static com.ss.editor.util.EditorUtil.toAssetPath;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.asset.ModelKey;
import com.jme3.scene.Spatial;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.annotation.BackgroundThread;
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
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.node.layer.LayersRoot;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.model.undo.impl.RenameNodeOperation;
import com.ss.editor.part3d.editor.impl.scene.SceneEditor3dPart;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.editor.EditorDescriptor;
import com.ss.editor.ui.component.editor.impl.AbstractFileEditor;
import com.ss.editor.ui.component.editor.state.EditorState;
import com.ss.editor.ui.component.editor.state.impl.EditorSceneEditorState;
import com.ss.editor.ui.component.tab.EditorToolComponent;
import com.ss.editor.ui.control.app.state.list.AppStateList;
import com.ss.editor.ui.control.filter.list.FilterList;
import com.ss.editor.ui.control.layer.LayerNodeTree;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.util.DynamicIconSupport;
import com.ss.editor.util.EditorUtil;
import com.ss.editor.util.MaterialUtils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.fx.util.FxControlUtils;
import com.ss.rlib.fx.util.FxUtils;
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
public class SceneFileEditor extends AbstractSceneFileEditor<SceneNode, SceneEditor3dPart, EditorSceneEditorState>
        implements SceneChangeConsumer {

    public static final EditorDescriptor DESCRIPTOR = new EditorDescriptor(
            SceneFileEditor::new,
            Messages.SCENE_FILE_EDITOR_NAME,
            SceneFileEditor.class.getSimpleName(),
            FileExtensions.JME_SCENE
    );

    private static final int LAYERS_TOOL = 3;
    private static final int APP_STATES_TOOL = 4;
    private static final int FILTERS_TOOL = 5;

    /**
     * The list with app states.
     */
    @NotNull
    private final AppStateList appStateList;

    /**
     * The list with filters.
     */
    @NotNull
    private final FilterList filterList;

    /**
     * The tree with layers.
     */
    @NotNull
    private final LayerNodeTree layerNodeTree;

    /**
     * The light toggle.
     */
    @NotNull
    private final ToggleButton lightButton;

    /**
     * The audio toggle.
     */
    @NotNull
    private final ToggleButton audioButton;

    /**
     * The container of property editor in app states tool.
     */
    @NotNull
    private final VBox propertyEditorAppStateContainer;

    /**
     * The container of property editor in filters tool.
     */
    @NotNull
    private final VBox propertyEditorFiltersContainer;

    /**
     * The container of property editor in layers tool.
     */
    @NotNull
    private final VBox propertyEditorLayersContainer;

    /**
     * The flag of sync selection.
     */
    private boolean needSyncSelection;

    private SceneFileEditor() {
        setNeedSyncSelection(true);
        this.lightButton = new ToggleButton();
        this.audioButton = new ToggleButton();
        this.appStateList = new AppStateList(this::selectAppStateFromList, this);
        this.filterList = new FilterList(this::selectFilterFromList, this);
        this.layerNodeTree = new LayerNodeTree(this::selectNodeFromLayersTree, this);
        this.propertyEditorAppStateContainer = new VBox();
        this.propertyEditorFiltersContainer = new VBox();
        this.propertyEditorLayersContainer = new VBox();
    }

    @Override
    @FxThread
    protected @NotNull SceneEditor3dPart create3dEditorPart() {
        return new SceneEditor3dPart(this);
    }

    @Override
    @FxThread
    protected @Nullable Supplier<EditorState> getEditorStateFactory() {
        return EditorSceneEditorState::new;
    }

    @Override
    @BackgroundThread
    protected void doOpenFile(@NotNull Path file) throws IOException {
        super.doOpenFile(file);

        var assetFile = EditorUtil.requireAssetFile(file);
        var modelKey = new ModelKey(toAssetPath(assetFile));

        Spatial loadedScene = EditorUtil.getAssetManager()
                .loadAsset(modelKey);

        var model = (SceneNode) loadedScene;
        model.depthFirstTraversal(this::updateVisibility);

        MaterialUtils.cleanUpMaterialParams(model);

        editor3dPart.openModel(model);
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

        var model = getCurrentModel();

        appStateList.fill(model);
        filterList.fill(model);
        layerNodeTree.fill(new LayersRoot(this));
    }

    @FxThread
    private void updateVisibility(@NotNull Spatial spatial) {
        var layer = SceneLayer.getLayer(spatial);
        if (layer != null) {
            spatial.setVisible(layer.isShowed());
        }
    }

    @Override
    @FxThread
    protected boolean isNeedToOpenObjectsTool(int current) {
        return !(current == OBJECTS_TOOL || current == LAYERS_TOOL || current == SCRIPTING_TOOL);
    }

    @Override
    @FxThread
    protected void createToolbar(@NotNull final HBox container) {
        super.createToolbar(container);

        lightButton.setTooltip(new Tooltip(Messages.SCENE_FILE_EDITOR_ACTION_SHOW_LIGHTS));
        lightButton.setGraphic(new ImageView(Icons.LIGHT_16));
        lightButton.setSelected(true);

        audioButton.setTooltip(new Tooltip(Messages.SCENE_FILE_EDITOR_ACTION_SHOW_AUDIO));
        audioButton.setGraphic(new ImageView(Icons.AUDIO_16));
        audioButton.setSelected(true);

        FxControlUtils.onSelectedChange(lightButton, this::changeLight);
        FxControlUtils.onSelectedChange(audioButton, this::changeAudio);

        DynamicIconSupport.addSupport(lightButton, audioButton);

        FxUtils.addClass(lightButton, audioButton, CssClasses.FILE_EDITOR_TOOLBAR_BUTTON);
        FxUtils.addChild(container, lightButton, audioButton);
    }

    /**
     * Handle changing light models visibility.
     */
    @FxThread
    private void changeLight(@NotNull Boolean newValue) {

        if (isIgnoreListeners()) {
            return;
        }

        editor3dPart.updateLightShowed(newValue);

        if (editorState != null) {
            editorState.setShowedLight(newValue);
        }
    }

    /**
     * Handle changing audio models visibility.
     */
    @FxThread
    private void changeAudio(@NotNull Boolean newValue) {

        if (isIgnoreListeners()) {
            return;
        }

        editor3dPart.updateAudioShowed(newValue);

        if (editorState != null) {
            editorState.setShowedAudio(newValue);
        }
    }

    @Override
    @FxThread
    protected void createContent(@NotNull StackPane root) {
        super.createContent(root);

        FxUtils.addClass(layerNodeTree.getTreeView(),
                CssClasses.TRANSPARENT_TREE_VIEW);
    }

    @Override
    @FxThread
    protected void createToolComponents(@NotNull EditorToolComponent container, @NotNull StackPane root) {
        super.createToolComponents(container, root);

        container.addComponent(buildSplitComponent(layerNodeTree, propertyEditorLayersContainer, root),
                Messages.SCENE_FILE_EDITOR_TOOL_LAYERS);
        container.addComponent(buildSplitComponent(appStateList, propertyEditorAppStateContainer, root),
                Messages.SCENE_FILE_EDITOR_TOOL_APP_STATES);
        container.addComponent(buildSplitComponent(filterList, propertyEditorFiltersContainer, root),
                Messages.SCENE_FILE_EDITOR_TOOL_FILTERS);
    }

    @Override
    @FxThread
    protected void processChangeTool(@Nullable Number oldValue, @NotNull Number newValue) {
        super.processChangeTool(oldValue, newValue);

        var newIndex = newValue.intValue();
        if (newIndex < 2) {
            return;
        }

        switch (newIndex) {
            case LAYERS_TOOL: {
                var selected = layerNodeTree.getSelected();
                FxUtils.addChild(propertyEditorLayersContainer, modelPropertyEditor);
                selectNodeFromLayersTree(selected);
                break;
            }
            case APP_STATES_TOOL: {
                FxUtils.addChild(propertyEditorAppStateContainer, modelPropertyEditor);
                selectAppStateFromList(appStateList.getSelected());
                break;
            }
            case FILTERS_TOOL: {
                FxUtils.addChild(propertyEditorFiltersContainer, modelPropertyEditor);
                selectFilterFromList(filterList.getSelected());
                break;
            }
        }
    }

    @Override
    @FxThread
    protected void loadState() {
        super.loadState();

        var editorState = notNull(getEditorState());

        lightButton.setSelected(editorState.isShowedLight());
        audioButton.setSelected(editorState.isShowedAudio());
    }

    /**
     * Handle the selected app state from the list.
     */
    @FxThread
    private void selectAppStateFromList(@Nullable EditableSceneAppState appState) {

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
    private void selectFilterFromList(@Nullable EditableSceneFilter sceneFilter) {

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
    private void selectNodeFromLayersTree(@Nullable Object object) {

        if (!isNeedSyncSelection()) {
            return;
        }

        setNeedSyncSelection(false);
        try {
            modelNodeTree.selectSingle(object);
            selectNodeFromTree(object);
        } finally {
            setNeedSyncSelection(true);
        }
    }

    @Override
    @FxThread
    public void selectNodesFromTree(@NotNull Array<?> objects) {

        if (!isNeedSyncSelection()) {
            super.selectNodesFromTree(objects);
            return;
        }

        setNeedSyncSelection(false);
        try {
            layerNodeTree.selects(objects);
            super.selectNodesFromTree(objects);
        } finally {
            setNeedSyncSelection(true);
        }
    }

    @Override
    @FromAnyThread
    public @NotNull EditorDescriptor getDescriptor() {
        return DESCRIPTOR;
    }

    /**
     * Return true if need sync selection.
     *
     * @param needSyncSelection true if need sync selection.
     */
    @FxThread
    private void setNeedSyncSelection(boolean needSyncSelection) {
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
    protected boolean canSelect(@NotNull Spatial spatial) {
        return !(spatial instanceof SceneNode) && !(spatial instanceof SceneLayer) && super.canSelect(spatial);
    }

    @Override
    @FxThread
    protected void handleAddedObject(@NotNull Spatial model) {
        super.handleAddedObject(model);

        if (!(model instanceof SceneNode)) {
            return;
        }

        var sceneNode = (SceneNode) model;

        sceneNode.getFilters().stream()
                .filter(ScenePresentable.class::isInstance)
                .forEach(filter -> editor3dPart.addPresentable((ScenePresentable) filter));
        sceneNode.getAppStates().stream()
                .filter(ScenePresentable.class::isInstance)
                .forEach(state -> editor3dPart.addPresentable((ScenePresentable) state));
    }

    @Override
    @FxThread
    protected void handleRemovedObject(@NotNull Spatial model) {
        super.handleRemovedObject(model);

        if (!(model instanceof SceneNode)) {
            return;
        }

        var sceneNode = (SceneNode) model;

        sceneNode.getFilters().stream()
                .filter(ScenePresentable.class::isInstance)
                .forEach(filter -> editor3dPart.removePresentable((ScenePresentable) filter));
        sceneNode.getAppStates().stream()
                .filter(ScenePresentable.class::isInstance)
                .forEach(state -> editor3dPart.removePresentable((ScenePresentable) state));
    }

    @Override
    @FxThread
    public void notifyFxAddedChild(@NotNull Object parent, @NotNull Object added, int index, boolean needSelect) {
        super.notifyFxAddedChild(parent, added, index, needSelect);

        if (parent instanceof LayersRoot) {
            layerNodeTree.notifyAdded(parent, added, index);
        } else if (added instanceof Spatial) {
            layerNodeTree.notifyAdded((Spatial) added);
        }

        ExecutorManager.getInstance()
                .addJmeTask(() -> getCurrentModel().notifyAdded(added));
    }

    @Override
    @FxThread
    public void notifyFxRemovedChild(@NotNull Object parent, @NotNull Object removed) {
        super.notifyFxRemovedChild(parent, removed);

        if (parent instanceof LayersRoot) {
            layerNodeTree.notifyRemoved(parent, removed);
        } else if (removed instanceof Spatial) {
            layerNodeTree.notifyRemoved((Spatial) removed);
        }

        ExecutorManager.getInstance()
                .addJmeTask(() -> getCurrentModel().notifyRemoved(removed));
    }

    @Override
    @FxThread
    public void notifyFxChangeProperty(@Nullable Object parent, @NotNull Object object, @NotNull String propertyName) {
        super.notifyFxChangeProperty(parent, object, propertyName);

        if (object instanceof EditableProperty) {
            object = ((EditableProperty) object).getObject();
        }

        if (object instanceof Spatial && Objects.equals(propertyName, Messages.MODEL_PROPERTY_LAYER)) {

            var spatial = (Spatial) object;
            var layer = SceneLayer.getLayer(spatial);

            if (layer == null) {
                spatial.setVisible(true);
            } else {
                spatial.setVisible(layer.isShowed());
            }

            layerNodeTree.notifyChangedLayer(spatial, layer);
        }

        if (object instanceof EditableSceneAppState && RenameNodeOperation.PROPERTY_NAME.equals(propertyName)) {
            appStateList.refresh((EditableSceneAppState) object);
        }

        layerNodeTree.notifyChanged(null, object);
    }

    @Override
    @FxThread
    public void notifyAddedAppState(@NotNull SceneAppState appState) {

        editor3dPart.addAppState(appState);

        if (appState instanceof ScenePresentable) {
            editor3dPart.addPresentable((ScenePresentable) appState);
        }

        appStateList.fill(getCurrentModel());
    }

    @Override
    @FxThread
    public void notifyRemovedAppState(@NotNull SceneAppState appState) {

        editor3dPart.removeAppState(appState);

        if (appState instanceof ScenePresentable) {
            editor3dPart.removePresentable((ScenePresentable) appState);
        }

        appStateList.fill(getCurrentModel());
    }

    @Override
    @FxThread
    public void notifyChangedAppState(@NotNull SceneAppState appState) {
        appStateList.fill(getCurrentModel());
    }

    @Override
    @FxThread
    public void notifyAddedFilter(@NotNull SceneFilter sceneFilter) {

        editor3dPart.addFilter(sceneFilter);

        if (sceneFilter instanceof ScenePresentable) {
            editor3dPart.addPresentable((ScenePresentable) sceneFilter);
        }

        filterList.fill(getCurrentModel());
    }

    @Override
    @FxThread
    public void notifyRemovedFilter(@NotNull SceneFilter sceneFilter) {

        editor3dPart.removeFilter(sceneFilter);

        if (sceneFilter instanceof ScenePresentable) {
            editor3dPart.removePresentable((ScenePresentable) sceneFilter);
        }

        filterList.fill(getCurrentModel());
    }

    @Override
    @FxThread
    public void notifyChangedFilter(@NotNull SceneFilter sceneFilter) {
        filterList.fill(getCurrentModel());
    }

    @Override
    @FxThread
    public void notifyHided() {
        super.notifyHided();

        ExecutorManager.getInstance()
                .addJmeTask(EditorUtil::enableGlobalLightProbe);
    }

    @Override
    @FxThread
    public void notifyShowed() {
        super.notifyShowed();

        ExecutorManager.getInstance()
                .addJmeTask(EditorUtil::disableGlobalLightProbe);
    }

    @Override
    public String toString() {
        return "SceneFileEditor{} " + super.toString();
    }
}
