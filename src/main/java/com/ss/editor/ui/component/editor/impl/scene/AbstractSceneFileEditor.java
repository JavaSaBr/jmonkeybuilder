package com.ss.editor.ui.component.editor.impl.scene;

import static com.ss.editor.state.editor.impl.scene.AbstractSceneEditor3DState.LOADED_MODEL_KEY;
import static com.ss.editor.util.EditorUtil.*;
import static com.ss.editor.util.MaterialUtils.saveIfNeedTextures;
import static com.ss.editor.util.MaterialUtils.updateMaterialIdNeed;
import static com.ss.editor.util.NodeUtils.findParent;
import static com.ss.rlib.util.ClassUtils.unsafeCast;
import static com.ss.rlib.util.ObjectUtils.notNull;
import static javafx.collections.FXCollections.observableArrayList;
import com.jme3.asset.AssetManager;
import com.jme3.asset.MaterialKey;
import com.jme3.asset.ModelKey;
import com.jme3.audio.AudioNode;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.AssetLinkNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.texture.Texture;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.JMEThread;
import com.ss.editor.config.Config;
import com.ss.editor.control.transform.EditorTransformSupport.TransformType;
import com.ss.editor.control.transform.EditorTransformSupport.TransformationMode;
import com.ss.editor.extension.scene.SceneLayer;
import com.ss.editor.extension.scene.ScenePresentable;
import com.ss.editor.manager.WorkspaceManager;
import com.ss.editor.model.editor.ModelEditingProvider;
import com.ss.editor.model.undo.EditorOperation;
import com.ss.editor.model.undo.EditorOperationControl;
import com.ss.editor.model.undo.UndoableEditor;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.workspace.Workspace;
import com.ss.editor.scene.EditorAudioNode;
import com.ss.editor.scene.EditorLightNode;
import com.ss.editor.scene.EditorPresentableNode;
import com.ss.editor.scene.WrapperNode;
import com.ss.editor.state.editor.impl.Stats3DState;
import com.ss.editor.state.editor.impl.scene.AbstractSceneEditor3DState;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.container.ProcessingComponent;
import com.ss.editor.ui.component.editing.EditingComponent;
import com.ss.editor.ui.component.editing.EditingComponentContainer;
import com.ss.editor.ui.component.editing.terrain.TerrainEditingComponent;
import com.ss.editor.ui.component.editor.impl.AbstractFileEditor;
import com.ss.editor.ui.component.editor.scripting.EditorScriptingComponent;
import com.ss.editor.ui.component.editor.state.EditorState;
import com.ss.editor.ui.component.editor.state.impl.BaseEditorSceneEditorState;
import com.ss.editor.ui.component.split.pane.EditorToolSplitPane;
import com.ss.editor.ui.component.tab.EditorToolComponent;
import com.ss.editor.ui.control.model.property.ModelPropertyEditor;
import com.ss.editor.ui.control.model.property.operation.ModelPropertyOperation;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.operation.AddChildOperation;
import com.ss.editor.ui.control.model.tree.action.operation.RemoveChildOperation;
import com.ss.editor.ui.control.model.tree.action.operation.RemoveControlOperation;
import com.ss.editor.ui.control.model.tree.action.operation.RemoveLightOperation;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.event.impl.FileChangedEvent;
import com.ss.editor.ui.util.DynamicIconSupport;
import com.ss.editor.ui.util.UIUtils;
import com.ss.editor.util.LocalObjects;
import com.ss.editor.util.MaterialUtils;
import com.ss.editor.util.NodeUtils;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.FileUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tonegod.emitter.filter.TonegodTranslucentBucketFilter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * The base implementation of a model file editor.
 *
 * @param <IM> the type of {@link AbstractSceneFileEditor}
 * @param <M>  the type edited object.
 * @param <MA> the type of {@link AbstractSceneEditor3DState}
 * @param <ES> the type of an editor state.
 * @author JavaSaBr
 */
public abstract class AbstractSceneFileEditor<IM extends AbstractSceneFileEditor, M extends Spatial,
        MA extends AbstractSceneEditor3DState<IM, M>, ES extends BaseEditorSceneEditorState>
        extends AbstractFileEditor<StackPane> implements UndoableEditor, ModelChangeConsumer, ModelEditingProvider {

    private static final int OBJECTS_TOOL = 0;
    private static final int EDITING_TOOL = 1;

    @NotNull
    private static final Array<String> ACCEPTED_FILES = ArrayFactory.asArray(
            FileExtensions.JME_MATERIAL,
            FileExtensions.JME_OBJECT);

    @NotNull
    private static final ObservableList<TransformationMode> TRANSFORMATION_MODES = observableArrayList(TransformationMode.values());

    /**
     * The 3D part of this editor.
     */
    @NotNull
    private final MA editor3DState;

    /**
     * The stats app state.
     */
    @NotNull
    private final Stats3DState statsAppState;

    /**
     * The operation control.
     */
    @NotNull
    private final EditorOperationControl operationControl;

    /**
     * The changes counter.
     */
    @NotNull
    private final AtomicInteger changeCounter;

    /**
     * The opened model.
     */
    @Nullable
    private M currentModel;

    /**
     * The selection handler.
     */
    @Nullable
    private Consumer<Object> selectionNodeHandler;

    /**
     * The list of transform modes.
     */
    @Nullable
    private ComboBox<TransformationMode> transformModeComboBox;

    /**
     * The model tree.
     */
    @Nullable
    private ModelNodeTree modelNodeTree;

    /**
     * The model property editor.
     */
    @Nullable
    private ModelPropertyEditor modelPropertyEditor;

    /**
     * The container of editing components.
     */
    @Nullable
    private EditingComponentContainer editingComponentContainer;

    /**
     * The scripting component.
     */
    @Nullable
    private EditorScriptingComponent scriptingComponent;

    /**
     * The container of property editor in objects tool.
     */
    @Nullable
    private VBox propertyEditorObjectsContainer;

    /**
     * The container of model node tree in objects tool.
     */
    @Nullable
    private VBox modelNodeTreeObjectsContainer;

    /**
     * The container of model node tree in editing tool.
     */
    @Nullable
    private VBox modelNodeTreeEditingContainer;

    /**
     * The stats container.
     */
    @Nullable
    private VBox statsContainer;

    /**
     * The state of this editor.
     */
    @Nullable
    protected ES editorState;

    /**
     * The main split container.
     */
    @Nullable
    private EditorToolSplitPane mainSplitContainer;

    /**
     * The editor tool component.
     */
    @Nullable
    private EditorToolComponent editorToolComponent;

    /**
     * The pane of editor area.
     */
    @Nullable
    private StackPane editorAreaPane;

    /**
     * The pane of 3D editor area.
     */
    @Nullable
    private BorderPane editor3DArea;

    /**
     * The selection toggle.
     */
    @Nullable
    private ToggleButton selectionButton;

    /**
     * The grid toggle.
     */
    @Nullable
    private ToggleButton gridButton;

    /**
     * The statistics toggle.
     */
    @Nullable
    private ToggleButton statisticsButton;

    /**
     * The move tool toggle.
     */
    @Nullable
    private ToggleButton moveToolButton;

    /**
     * The rotation tool toggle.
     */
    @Nullable
    private ToggleButton rotationToolButton;

    /**
     * The scaling tool toggle.
     */
    @Nullable
    private ToggleButton scaleToolButton;

    /**
     * The flag of ignoring listeners.
     */
    private boolean ignoreListeners;

    /**
     * The flag of ignoring camera moving.
     */
    private boolean ignoreCameraMove;

    /**
     * Instantiates a new Abstract scene file editor.
     */
    public AbstractSceneFileEditor() {
        this.editor3DState = createEditor3DState();
        this.operationControl = new EditorOperationControl(this);
        this.changeCounter = new AtomicInteger();
        this.statsAppState = new Stats3DState(statsContainer);
        addEditorState(editor3DState);
        addEditorState(statsAppState);
        statsAppState.setEnabled(true);
        processChangeTool(-1, OBJECTS_TOOL);
    }

    /**
     * Create editor app state ma.
     *
     * @return the ma
     */
    @NotNull
    @FXThread
    protected abstract MA createEditor3DState();

    @NotNull
    @Override
    @FXThread
    protected StackPane createRoot() {
        return new StackPane();
    }

    @Override
    protected boolean needListenEventsFromPage() {
        return false;
    }

    /**
     * Gets editor app state.
     *
     * @return the 3D part of this editor.
     */
    @NotNull
    @FXThread
    protected MA getEditor3DState() {
        return editor3DState;
    }

    /**
     * Gets editor state.
     *
     * @return the state of this editor.
     */
    @Nullable
    @FXThread
    protected ES getEditorState() {
        return editorState;
    }

    /**
     * Gets model node tree.
     *
     * @return the model tree.
     */
    @NotNull
    @FXThread
    protected ModelNodeTree getModelNodeTree() {
        return notNull(modelNodeTree);
    }

    /**
     * Gets model property editor.
     *
     * @return the model property editor.
     */
    @NotNull
    @FXThread
    protected ModelPropertyEditor getModelPropertyEditor() {
        return notNull(modelPropertyEditor);
    }

    /**
     * @return the container of editing components.
     */
    @NotNull
    @FXThread
    private EditingComponentContainer getEditingComponentContainer() {
        return notNull(editingComponentContainer);
    }

    /**
     * @return the container of property editor in objects tool.
     */
    @NotNull
    @FXThread
    private VBox getPropertyEditorObjectsContainer() {
        return notNull(propertyEditorObjectsContainer);
    }

    /**
     * @return the container of model node tree in editing tool.
     */
    @NotNull
    @FXThread
    private VBox getModelNodeTreeEditingContainer() {
        return notNull(modelNodeTreeEditingContainer);
    }

    /**
     * @return the container of model node tree in objects tool.
     */
    @NotNull
    @FXThread
    private VBox getModelNodeTreeObjectsContainer() {
        return notNull(modelNodeTreeObjectsContainer);
    }

    @Override
    @FXThread
    protected void processChangedFile(@NotNull final FileChangedEvent event) {
        super.processChangedFile(event);

        final Path file = event.getFile();
        final String extension = FileUtils.getExtension(file);

        if (extension.endsWith(FileExtensions.JME_MATERIAL)) {
            EXECUTOR_MANAGER.addJMETask(() -> updateMaterial(file));
        } else if (MaterialUtils.isShaderFile(file) || MaterialUtils.isTextureFile(file)) {
            EXECUTOR_MANAGER.addJMETask(() -> updateMaterials(file));
        }
    }

    /**
     * Updating a material from the file.
     */
    @FXThread
    private void updateMaterial(@NotNull final Path file) {

        final Path assetFile = notNull(getAssetFile(file), "Not found asset file for " + file);
        final String assetPath = toAssetPath(assetFile);

        final M currentModel = getCurrentModel();

        final Array<Geometry> geometries = ArrayFactory.newArray(Geometry.class);
        NodeUtils.addGeometryWithMaterial(currentModel, geometries, assetPath);
        if (geometries.isEmpty()) return;

        final AssetManager assetManager = EDITOR.getAssetManager();
        final Material material = assetManager.loadMaterial(assetPath);
        geometries.forEach(geometry -> geometry.setMaterial(material));

        final TonegodTranslucentBucketFilter translucentBucketFilter = EDITOR.getTranslucentBucketFilter();
        translucentBucketFilter.refresh();
    }

    /**
     * Updating materials.
     */
    @FXThread
    private void updateMaterials(@NotNull final Path file) {

        final M currentModel = getCurrentModel();
        final AtomicInteger needRefresh = new AtomicInteger();

        NodeUtils.visitGeometry(currentModel, geometry -> {

            final Material material = geometry.getMaterial();
            final Material newMaterial = updateMaterialIdNeed(file, material);

            if (newMaterial != null) {
                geometry.setMaterial(newMaterial);
                needRefresh.incrementAndGet();
            }
        });

        if (needRefresh.get() < 1) {
            return;
        }

        final TonegodTranslucentBucketFilter translucentBucketFilter = EDITOR.getTranslucentBucketFilter();
        translucentBucketFilter.refresh();
    }

    /**
     * Handle a added model.
     *
     * @param model the model
     */
    @FXThread
    protected void handleAddedObject(@NotNull final Spatial model) {

        final MA editor3DState = getEditor3DState();
        final Array<Light> lights = ArrayFactory.newArray(Light.class);
        final Array<AudioNode> audioNodes = ArrayFactory.newArray(AudioNode.class);

        NodeUtils.addLight(model, lights);
        NodeUtils.addAudioNodes(model, audioNodes);

        lights.forEach(editor3DState, (light, state) -> state.addLight(light));
        audioNodes.forEach(editor3DState, (audioNode, state) -> state.addAudioNode(audioNode));
    }

    /**
     * Handle a removed model.T
     *
     * @param model the model
     */
    @FXThread
    protected void handleRemovedObject(@NotNull final Spatial model) {

        final MA editor3DState = getEditor3DState();
        final Array<Light> lights = ArrayFactory.newArray(Light.class);
        final Array<AudioNode> audioNodes = ArrayFactory.newArray(AudioNode.class);

        NodeUtils.addLight(model, lights);
        NodeUtils.addAudioNodes(model, audioNodes);

        lights.forEach(editor3DState, (light, state) -> state.removeLight(light));
        audioNodes.forEach(editor3DState, (audioNode, state) -> state.removeAudioNode(audioNode));
    }

    /**
     * Load the saved state.
     */
    @FXThread
    protected void loadState() {

        scriptingComponent.addVariable("root", getCurrentModel());
        scriptingComponent.addVariable("assetManager", EDITOR.getAssetManager());
        scriptingComponent.addImport(Spatial.class);
        scriptingComponent.addImport(Geometry.class);
        scriptingComponent.addImport(Control.class);
        scriptingComponent.addImport(Node.class);
        scriptingComponent.addImport(Light.class);
        scriptingComponent.addImport(DirectionalLight.class);
        scriptingComponent.addImport(PointLight.class);
        scriptingComponent.addImport(SpotLight.class);
        scriptingComponent.addImport(Material.class);
        scriptingComponent.addImport(Texture.class);
        scriptingComponent.setExampleCode("root.attachChild(\nnew Node(\"created from Groovy\"));");
        scriptingComponent.buildHeader();

        final WorkspaceManager workspaceManager = WorkspaceManager.getInstance();
        final Workspace currentWorkspace = notNull(workspaceManager.getCurrentWorkspace(),
                "Current workspace can't be null.");

        editorState = currentWorkspace.getEditorState(getEditFile(), getStateConstructor());
        mainSplitContainer.updateFor(editorState);
        gridButton.setSelected(editorState.isEnableGrid());
        statisticsButton.setSelected(editorState.isShowStatistics());
        selectionButton.setSelected(editorState.isEnableSelection());
        transformModeComboBox.getSelectionModel()
                .select(TransformationMode.valueOf(editorState.getTransformationMode()));

        final Array<EditingComponent> components = editingComponentContainer.getComponents();
        components.forEach(editorState, ProcessingComponent::loadState);

        final TransformType transformType = TransformType.valueOf(editorState.getTransformationType());

        switch (transformType) {
            case MOVE_TOOL: {
                moveToolButton.setSelected(true);
                break;
            }
            case ROTATE_TOOL: {
                rotationToolButton.setSelected(true);
                break;
            }
            case SCALE_TOOL: {
                scaleToolButton.setSelected(true);
                break;
            }
            default: {
                break;
            }
        }

        final MA editor3DState = getEditor3DState();
        final Vector3f cameraLocation = editorState.getCameraLocation();

        final float hRotation = editorState.getCameraHRotation();
        final float vRotation = editorState.getCameraVRotation();
        final float tDistance = editorState.getCameraTDistance();
        final float cameraSpeed = editorState.getCameraSpeed();

        EXECUTOR_MANAGER.addJMETask(() -> editor3DState.updateCameraSettings(cameraLocation, hRotation,
                vRotation, tDistance, cameraSpeed));
    }

    @Override
    @FXThread
    public void notifyChangedCameraSettings(@NotNull final Vector3f cameraLocation, final float hRotation,
                                            final float vRotation, final float targetDistance,
                                            final float cameraSpeed) {

        final ES editorState = getEditorState();
        if (editorState == null) return;

        editorState.setCameraHRotation(hRotation);
        editorState.setCameraVRotation(vRotation);
        editorState.setCameraTDistance(targetDistance);
        editorState.setCameraLocation(cameraLocation);
        editorState.setCameraSpeed(cameraSpeed);
    }

    @Override
    @FXThread
    protected boolean handleKeyActionImpl(@NotNull final KeyCode keyCode, final boolean isPressed,
                                          final boolean isControlDown, final boolean isButtonMiddleDown) {

        final MA editor3DState = getEditor3DState();

        if (editor3DState.isCameraMoving()) {
            return false;
        }

        if (isPressed && isControlDown && keyCode == KeyCode.Z) {
            undo();
            return true;
        } else if (isPressed && isControlDown && keyCode == KeyCode.Y) {
            redo();
            return true;
        } else if (isPressed && keyCode == KeyCode.G && !isControlDown && !isButtonMiddleDown) {
            final ToggleButton moveToolButton = getMoveToolButton();
            moveToolButton.setSelected(true);
            return true;
        } else if (isPressed && keyCode == KeyCode.R && !isControlDown && !isButtonMiddleDown) {
            final ToggleButton rotationToolButton = getRotationToolButton();
            rotationToolButton.setSelected(true);
            return true;
        } else if (isPressed && keyCode == KeyCode.S && !isControlDown && !isButtonMiddleDown) {
            final ToggleButton scaleToolButton = getScaleToolButton();
            scaleToolButton.setSelected(true);
            return true;
        } else if (isPressed && keyCode == KeyCode.DELETE) {

            final ModelNodeTree modelNodeTree = getModelNodeTree();
            final TreeNode<?> selected = modelNodeTree.getSelected();
            if (selected == null || !selected.canRemove()) return false;

            final Object element = selected.getElement();
            final TreeNode<?> parent = selected.getParent();
            final Object parentElement = parent == null ? null : parent.getElement();

            if (element instanceof Spatial) {
                final Spatial spatial = (Spatial) element;
                execute(new RemoveChildOperation(spatial, spatial.getParent()));
            } else if (element instanceof Light && parentElement instanceof Node) {
                final Light light = (Light) element;
                execute(new RemoveLightOperation(light, (Node) parentElement));
            } else if (element instanceof Control && parentElement instanceof Spatial) {
                final Control control = (Control) element;
                execute(new RemoveControlOperation(control, (Spatial) parentElement));
            }

            return true;
        }

        return super.handleKeyActionImpl(keyCode, isPressed, isControlDown, isButtonMiddleDown);
    }

    /**
     * Redo the last operation.
     */
    @FromAnyThread
    public void redo() {
        final EditorOperationControl operationControl = getOperationControl();
        operationControl.redo();
    }

    /**
     * Undo the last operation.
     */
    @FromAnyThread
    public void undo() {
        final EditorOperationControl operationControl = getOperationControl();
        operationControl.undo();
    }

    /**
     * Is ignore listeners boolean.
     *
     * @return true if needs to ignore events.
     */
    @FXThread
    protected boolean isIgnoreListeners() {
        return ignoreListeners;
    }

    /**
     * Sets ignore listeners.
     *
     * @param ignoreListeners true if needs to ignore events.
     */
    @FXThread
    protected void setIgnoreListeners(final boolean ignoreListeners) {
        this.ignoreListeners = ignoreListeners;
    }

    /**
     * @return true if need to ignore moving camera.
     */
    @FXThread
    private boolean isIgnoreCameraMove() {
        return ignoreCameraMove;
    }

    /**
     * @param ignoreCameraMove true if need to ignore moving camera.
     */
    @FXThread
    private void setIgnoreCameraMove(final boolean ignoreCameraMove) {
        this.ignoreCameraMove = ignoreCameraMove;
    }

    /**
     * Sets current model.
     *
     * @param currentModel the opened model.
     */
    @FXThread
    protected void setCurrentModel(@NotNull final M currentModel) {
        this.currentModel = currentModel;
    }

    @NotNull
    @Override
    @FXThread
    public M getCurrentModel() {
        return notNull(currentModel);
    }

    @Override
    @FXThread
    public void notifyFXChangeProperty(@Nullable final Object parent, @NotNull final Object object,
                                       @NotNull final String propertyName) {

        final ModelPropertyEditor modelPropertyEditor = getModelPropertyEditor();
        modelPropertyEditor.syncFor(object);

        final ModelNodeTree modelNodeTree = getModelNodeTree();
        modelNodeTree.notifyChanged(parent, object);

        final EditingComponentContainer editingComponentContainer = getEditingComponentContainer();
        editingComponentContainer.notifyChangeProperty(object, propertyName);
    }

    @Override
    @FXThread
    public void notifyJMEChangeProperty(@NotNull final Object object, @NotNull final String propertyName) {
        getEditor3DState().notifyPropertyChanged(object);
    }

    @Override
    @FXThread
    public void notifyFXChangePropertyCount(@NotNull final Object object) {
        final ModelPropertyEditor modelPropertyEditor = getModelPropertyEditor();
        modelPropertyEditor.rebuildFor(object, null);
    }

    @Override
    @FXThread
    public void notifyFXAddedChild(@NotNull final Object parent, @NotNull final Object added, final int index,
                                   final boolean needSelect) {

        final MA editor3DState = getEditor3DState();
        final ModelNodeTree modelNodeTree = getModelNodeTree();
        modelNodeTree.notifyAdded(parent, added, index);

        if (added instanceof Light) {
            editor3DState.addLight((Light) added);
        } else if (added instanceof AudioNode) {
            editor3DState.addAudioNode((AudioNode) added);
        } else if (added instanceof Spatial) {
            handleAddedObject((Spatial) added);
        }

        if (needSelect) {
            EXECUTOR_MANAGER.addJMETask(() -> EXECUTOR_MANAGER.addFXTask(() -> modelNodeTree.select(added)));
        }
    }

    @Override
    @FXThread
    public void notifyFXRemovedChild(@NotNull final Object parent, @NotNull final Object removed) {

        final MA editor3DState = getEditor3DState();
        final ModelNodeTree modelNodeTree = getModelNodeTree();
        modelNodeTree.notifyRemoved(parent, removed);

        if (removed instanceof Light) {
            editor3DState.removeLight((Light) removed);
        } else if (removed instanceof AudioNode) {
            editor3DState.removeAudioNode((AudioNode) removed);
        } else if (removed instanceof Spatial) {
            handleRemovedObject((Spatial) removed);
        }
    }

    @FXThread
    @Override
    public void notifyFXReplaced(@NotNull final Object parent, @Nullable final Object oldChild,
                                 @Nullable final Object newChild, final boolean needExpand,
                                 final boolean needDeepExpand) {

        final MA editor3DState = getEditor3DState();
        final Spatial currentModel = getCurrentModel();

        if (currentModel == oldChild && newChild != null) {
            setCurrentModel(unsafeCast(newChild));
            editor3DState.openModel(unsafeCast(newChild));
        }

        final ModelNodeTree modelNodeTree = getModelNodeTree();
        modelNodeTree.notifyReplace(parent, oldChild, newChild, needExpand, needDeepExpand);
    }

    @Override
    @FXThread
    public void notifyFXMoved(@NotNull final Object prevParent, @NotNull final Object newParent,
                              @NotNull final Object child, final int index, final boolean needSelect) {

        final ModelNodeTree modelNodeTree = getModelNodeTree();
        modelNodeTree.notifyMoved(prevParent, newParent, child, index);

        if (needSelect) {
            EXECUTOR_MANAGER.addJMETask(() -> EXECUTOR_MANAGER.addFXTask(() -> modelNodeTree.select(child)));
        }
    }

    @Override
    @FromAnyThread
    public void execute(@NotNull final EditorOperation operation) {
        final EditorOperationControl operationControl = getOperationControl();
        operationControl.execute(operation);
    }

    /**
     * @return the operation control.
     */
    @NotNull
    @FXThread
    private EditorOperationControl getOperationControl() {
        return operationControl;
    }

    /**
     * Handle the selected object.
     *
     * @param object the object
     */
    @FXThread
    public void notifySelected(@Nullable Object object) {

        if (object instanceof EditorLightNode) {
            object = ((EditorLightNode) object).getLight();
        }

        if (object instanceof EditorAudioNode) {
            object = ((EditorAudioNode) object).getAudioNode();
        }

        setIgnoreCameraMove(true);
        try {
            final ModelNodeTree modelNodeTree = getModelNodeTree();
            modelNodeTree.select(object);
        } finally {
            setIgnoreCameraMove(false);
        }
    }

    /**
     * Gets state constructor.
     *
     * @return the state constructor
     */
    @NotNull
    @FXThread
    protected abstract Supplier<EditorState> getStateConstructor();

    /**
     * Handle the selected object from the Tree.
     *
     * @param object the object
     */
    @FXThread
    public void selectNodeFromTree(@Nullable final Object object) {

        final MA editor3DState = getEditor3DState();

        Object parent = null;
        Object element;

        if (object instanceof TreeNode<?>) {
            final TreeNode treeNode = (TreeNode) object;
            final TreeNode parentNode = treeNode.getParent();
            parent = parentNode == null ? null : parentNode.getElement();
            element = treeNode.getElement();
        } else {
            element = object;
        }

        if (element instanceof SceneLayer) {
            element = null;
        }

        Spatial spatial = null;

        if (element instanceof AudioNode) {
            final EditorAudioNode audioNode = editor3DState.getAudioNode((AudioNode) element);
            spatial = audioNode == null ? null : audioNode.getEditedNode();
        } else if (element instanceof Spatial) {
            spatial = (Spatial) element;
            parent = spatial.getParent();
        } else if (element instanceof Light) {
            spatial = editor3DState.getLightNode((Light) element);
        } else if(object instanceof ScenePresentable) {
            final EditorPresentableNode presentableNode = editor3DState.getPresentableNode((ScenePresentable) object);
            spatial = presentableNode == null? null : presentableNode.getEditedNode();
        }

        if (spatial != null && !spatial.isVisible()) {
            spatial = null;
        }

        updateSelection(spatial);

        if (spatial != null && !isIgnoreCameraMove() && !isVisibleOnEditor(spatial)) {
            editor3DState.cameraLookAt(spatial.getWorldTranslation());
        }

        final ModelPropertyEditor modelPropertyEditor = getModelPropertyEditor();
        modelPropertyEditor.buildFor(element, parent);

        final EditingComponentContainer editingComponentContainer = getEditingComponentContainer();
        editingComponentContainer.showComponentFor(element);
    }

    @FXThread
    private boolean isVisibleOnEditor(@NotNull final Spatial spatial) {

        final MA editor3DState = getEditor3DState();
        final Camera camera = editor3DState.getCamera();

        final Vector3f position = spatial.getWorldTranslation();
        final Vector3f coordinates = camera.getScreenCoordinates(position, new Vector3f());

        boolean invisible = coordinates.getZ() < 0F || coordinates.getZ() > 1F;
        invisible = invisible || !isInside(coordinates.getX(), camera.getHeight() - coordinates.getY(), Event.class);

        return !invisible;
    }

    /**
     * Update selection to 3D state.
     *
     * @param spatial the new selected object.
     */
    @FXThread
    protected void updateSelection(@Nullable final Spatial spatial) {

        final Array<Spatial> selection = ArrayFactory.newArray(Spatial.class);
        if (spatial != null) selection.add(spatial);

        final MA editor3DState = getEditor3DState();
        editor3DState.updateSelection(selection);
    }

    /**
     * @return the editor are panel.
     */
    @NotNull
    @FXThread
    private Pane getEditorAreaPane() {
        return notNull(editorAreaPane);
    }

    @Override
    @FXThread
    public boolean isInside(final double sceneX, final double sceneY, @NotNull final Class<? extends Event> eventType) {

        final Pane page = eventType.isAssignableFrom(MouseEvent.class) ||
                eventType.isAssignableFrom(ScrollEvent.class) ?
                getEditorAreaPane() : getPage();

        final Point2D point2D = page.sceneToLocal(sceneX, sceneY);
        final boolean result = page.contains(point2D);

        if (LOGGER.isEnabledDebug()) {
            if (Config.DEV_DEBUG_JFX_KEY_INPUT && eventType.isAssignableFrom(KeyEvent.class)) {
                LOGGER.debug("Coords sceneX = " + sceneX + ", sceneY = " + sceneY + ", localX = " + point2D.getX() +
                        ", localY = " + point2D.getY() + " is inside " + result);
            } else if (Config.DEV_DEBUG_JFX_MOUSE_INPUT && eventType.isAssignableFrom(MouseEvent.class)) {
                LOGGER.debug("Coords sceneX = " + sceneX + ", sceneY = " + sceneY + ", localX = " + point2D.getX() +
                        ", localY = " + point2D.getY() + " is inside " + result);
            }
        }

        return result;
    }

    @Override
    @BackgroundThread
    public void doSave(@NotNull final Path toStore) {
        super.doSave(toStore);

        final M currentModel = getCurrentModel();

        NodeUtils.visitGeometry(currentModel, geometry -> saveIfNeedTextures(geometry.getMaterial()));

        final BinaryExporter exporter = BinaryExporter.getInstance();

        try (final OutputStream out = Files.newOutputStream(toStore)) {
            exporter.save(currentModel, out);
        } catch (final IOException e) {
            LOGGER.warning(this, e);
        }
    }

    @Override
    @FXThread
    protected boolean needToolbar() {
        return true;
    }

    @Override
    @FXThread
    public void incrementChange() {
        final int result = changeCounter.incrementAndGet();
        setDirty(result != 0);
    }

    @Override
    @FXThread
    public void decrementChange() {
        final int result = changeCounter.decrementAndGet();
        setDirty(result != 0);
    }

    /**
     * @return the scaling tool toggle.
     */
    @NotNull
    @FXThread
    private ToggleButton getScaleToolButton() {
        return notNull(scaleToolButton);
    }

    /**
     * @return the move tool toggle.
     */
    @NotNull
    @FXThread
    private ToggleButton getMoveToolButton() {
        return notNull(moveToolButton);
    }

    /**
     * @return the rotation tool toggle.
     */
    @NotNull
    @FXThread
    private ToggleButton getRotationToolButton() {
        return notNull(rotationToolButton);
    }

    /**
     * Switch transformation mode.
     */
    @FXThread
    private void changeTransformMode(@NotNull final TransformationMode transformationMode) {

        final MA editor3DState = getEditor3DState();
        editor3DState.setTransformMode(transformationMode);

        final ES editorState = getEditorState();

        if (editorState != null) {
            editorState.setTransformationMode(transformationMode.ordinal());
        }
    }

    /**
     * Switch transformation type.
     */
    @FXThread
    private void updateTransformTool(@NotNull final TransformType transformType, @NotNull final Boolean newValue) {

        final MA editor3DState = getEditor3DState();
        final ToggleButton scaleToolButton = getScaleToolButton();
        final ToggleButton moveToolButton = getMoveToolButton();
        final ToggleButton rotationToolButton = getRotationToolButton();

        if (newValue != Boolean.TRUE) {
            if (editor3DState.getTransformType() == transformType) {
                if (transformType == TransformType.MOVE_TOOL) {
                    moveToolButton.setSelected(true);
                } else if (transformType == TransformType.ROTATE_TOOL) {
                    rotationToolButton.setSelected(true);
                } else if (transformType == TransformType.SCALE_TOOL) {
                    scaleToolButton.setSelected(true);
                }
            }
            return;
        }

        final ES editorState = getEditorState();
        editor3DState.setTransformType(transformType);

        if (transformType == TransformType.MOVE_TOOL) {
            rotationToolButton.setSelected(false);
            scaleToolButton.setSelected(false);
        } else if (transformType == TransformType.ROTATE_TOOL) {
            moveToolButton.setSelected(false);
            scaleToolButton.setSelected(false);
        } else if (transformType == TransformType.SCALE_TOOL) {
            rotationToolButton.setSelected(false);
            moveToolButton.setSelected(false);
        }

        if (editorState != null) {
            editorState.setTransformationType(transformType.ordinal());
        }
    }

    @Override
    @FXThread
    protected void createToolbar(@NotNull final HBox container) {
        createActions(container);

        final Label transformModeLabel = new Label(Messages.MODEL_FILE_EDITOR_TRANSFORM_MODE + ":");

        transformModeComboBox = new ComboBox<>(TRANSFORMATION_MODES);
        transformModeComboBox.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> changeTransformMode(newValue));

        FXUtils.addToPane(transformModeLabel, container);
        FXUtils.addToPane(transformModeComboBox, container);
    }

    @FXThread
    protected void createActions(@NotNull final HBox container) {
        FXUtils.addToPane(createSaveAction(), container);

        selectionButton = new ToggleButton();
        selectionButton.setTooltip(new Tooltip(Messages.SCENE_FILE_EDITOR_ACTION_SELECTION));
        selectionButton.setGraphic(new ImageView(Icons.CUBE_16));
        selectionButton.setSelected(true);
        selectionButton.selectedProperty().addListener((observable, oldValue, newValue) ->
                changeSelectionVisible(newValue));

        gridButton = new ToggleButton();
        gridButton.setTooltip(new Tooltip(Messages.SCENE_FILE_EDITOR_ACTION_GRID));
        gridButton.setGraphic(new ImageView(Icons.PLANE_16));
        gridButton.setSelected(true);
        gridButton.selectedProperty().addListener((observable, oldValue, newValue) ->
                changeGridVisible(newValue));

        statisticsButton = new ToggleButton();
        statisticsButton.setTooltip(new Tooltip(Messages.SCENE_FILE_EDITOR_ACTION_STATISTICS));
        statisticsButton.setGraphic(new ImageView(Icons.STATISTICS_16));
        statisticsButton.setSelected(true);
        statisticsButton.selectedProperty().addListener((observable, oldValue, newValue) ->
                changeStatisticsVisible(newValue));

        moveToolButton = new ToggleButton();
        moveToolButton.setTooltip(new Tooltip(Messages.SCENE_FILE_EDITOR_ACTION_MOVE_TOOL + " (G)"));
        moveToolButton.setGraphic(new ImageView(Icons.MOVE_16));
        moveToolButton.setSelected(true);
        moveToolButton.selectedProperty().addListener((observable, oldValue, newValue) ->
                updateTransformTool(TransformType.MOVE_TOOL, newValue));

        rotationToolButton = new ToggleButton();
        rotationToolButton.setTooltip(new Tooltip(Messages.SCENE_FILE_EDITOR_ACTION_ROTATION_TOOL + " (R)"));
        rotationToolButton.setGraphic(new ImageView(Icons.ROTATION_16));
        rotationToolButton.selectedProperty().addListener((observable, oldValue, newValue) ->
                updateTransformTool(TransformType.ROTATE_TOOL, newValue));

        scaleToolButton = new ToggleButton();
        scaleToolButton.setTooltip(new Tooltip(Messages.SCENE_FILE_EDITOR_ACTION_SCALE_TOOL + " (S)"));
        scaleToolButton.setGraphic(new ImageView(Icons.SCALE_16));
        scaleToolButton.selectedProperty().addListener((observable, oldValue, newValue) ->
                updateTransformTool(TransformType.SCALE_TOOL, newValue));

        DynamicIconSupport.addSupport(selectionButton, gridButton, statisticsButton, moveToolButton, rotationToolButton,
                scaleToolButton);

        FXUtils.addClassesTo(selectionButton, gridButton, statisticsButton, moveToolButton, rotationToolButton,
                scaleToolButton, CSSClasses.FILE_EDITOR_TOOLBAR_BUTTON);

        FXUtils.addToPane(selectionButton, container);
        FXUtils.addToPane(gridButton, container);
        FXUtils.addToPane(statisticsButton, container);
        FXUtils.addToPane(moveToolButton, container);
        FXUtils.addToPane(rotationToolButton, container);
        FXUtils.addToPane(scaleToolButton, container);
    }

    @Nullable
    @Override
    @FXThread
    public BorderPane get3DArea() {
        return editor3DArea;
    }

    @Override
    @FXThread
    protected void createContent(@NotNull final StackPane root) {
        this.selectionNodeHandler = this::selectNodeFromTree;

        editorAreaPane = new StackPane();
        editorAreaPane.setOnDragOver(this::dragOver);
        editorAreaPane.setOnDragDropped(this::dragDropped);

        editor3DArea = new BorderPane();
        editor3DArea.setOnMousePressed(event -> editor3DArea.requestFocus());
        editor3DArea.setOnKeyReleased(Event::consume);
        editor3DArea.setOnKeyPressed(Event::consume);

        statsContainer = new VBox();
        statsContainer.setMouseTransparent(true);
        statsContainer.prefHeightProperty().bind(editorAreaPane.heightProperty());

        modelNodeTree = new ModelNodeTree(selectionNodeHandler, this);
        modelNodeTree.prefHeightProperty().bind(root.heightProperty());

        modelPropertyEditor = new ModelPropertyEditor(this);
        modelPropertyEditor.prefHeightProperty().bind(root.heightProperty());

        propertyEditorObjectsContainer = new VBox();
        modelNodeTreeEditingContainer = new VBox();
        modelNodeTreeObjectsContainer = new VBox();

        editingComponentContainer = new EditingComponentContainer(this, this);
        editingComponentContainer.addComponent(new TerrainEditingComponent());

        scriptingComponent = new EditorScriptingComponent(this::refreshTree);

        final SplitPane objectsSplitContainer = new SplitPane(modelNodeTreeObjectsContainer, propertyEditorObjectsContainer);
        objectsSplitContainer.prefHeightProperty().bind(root.heightProperty());
        objectsSplitContainer.prefWidthProperty().bind(root.widthProperty());

        final SplitPane editingSplitContainer = new SplitPane(modelNodeTreeEditingContainer, editingComponentContainer);
        editingSplitContainer.prefHeightProperty().bind(root.heightProperty());
        editingSplitContainer.prefWidthProperty().bind(root.widthProperty());

        mainSplitContainer = new EditorToolSplitPane(JFX_APPLICATION.getScene(), root);

        editorToolComponent = new EditorToolComponent(mainSplitContainer, 1);
        editorToolComponent.prefHeightProperty().bind(root.heightProperty());
        editorToolComponent.addComponent(objectsSplitContainer, Messages.SCENE_FILE_EDITOR_TOOL_OBJECTS);
        editorToolComponent.addComponent(editingSplitContainer, Messages.SCENE_FILE_EDITOR_TOOL_EDITING);
        editorToolComponent.addComponent(scriptingComponent, Messages.SCENE_FILE_EDITOR_TOOL_SCRIPTING);
        editorToolComponent.addChangeListener((observable, oldValue, newValue) -> processChangeTool(oldValue, newValue));

        mainSplitContainer.initFor(editorToolComponent, editorAreaPane);

        FXUtils.addToPane(mainSplitContainer, root);
        FXUtils.addToPane(editor3DArea, editorAreaPane);
        FXUtils.addToPane(statsContainer, editorAreaPane);
        FXUtils.addClassTo(editorAreaPane, CSSClasses.FILE_EDITOR_EDITOR_AREA);
        FXUtils.addClassTo(mainSplitContainer, CSSClasses.FILE_EDITOR_MAIN_SPLIT_PANE);
        FXUtils.addClassTo(objectsSplitContainer, editingSplitContainer, CSSClasses.FILE_EDITOR_TOOL_SPLIT_PANE);
        FXUtils.addClassTo(statsContainer, CSSClasses.SCENE_EDITOR_STATS_CONTAINER);
        FXUtils.addClassTo(modelNodeTree.getTreeView(), CSSClasses.TRANSPARENT_TREE_VIEW);

        root.heightProperty().addListener((observableValue, oldValue, newValue) -> calcVSplitSize(objectsSplitContainer));
        root.heightProperty().addListener((observableValue, oldValue, newValue) -> calcVSplitSize(editingSplitContainer));
    }

    /**
     * Refresh tree.
     */
    @FXThread
    protected void refreshTree() {

        final M currentModel = getCurrentModel();

        final ModelNodeTree modelNodeTree = getModelNodeTree();
        modelNodeTree.fill(currentModel);
    }

    /**
     * Gets editor tool component.
     *
     * @return the editor tool component.
     */
    @NotNull
    @FXThread
    protected EditorToolComponent getEditorToolComponent() {
        return notNull(editorToolComponent);
    }

    /**
     * Process change tool.
     *
     * @param oldValue the old value
     * @param newValue the new value
     */
    @FXThread
    protected void processChangeTool(@Nullable final Number oldValue, @NotNull final Number newValue) {

        final ModelNodeTree modelNodeTree = getModelNodeTree();
        final ModelPropertyEditor modelPropertyEditor = getModelPropertyEditor();
        final EditingComponentContainer editingComponentContainer = getEditingComponentContainer();

        final VBox propertyEditorParent = (VBox) modelPropertyEditor.getParent();
        final VBox modelNodeTreeParent = (VBox) modelNodeTree.getParent();

        if (propertyEditorParent != null) {
            FXUtils.removeFromParent(modelPropertyEditor, propertyEditorParent);
        }

        if (modelNodeTreeParent != null) {
            FXUtils.removeFromParent(modelNodeTree, modelNodeTreeParent);
        }

        final int oldIndex = oldValue == null ? -1 : oldValue.intValue();
        final int newIndex = newValue.intValue();

        final VBox propertyContainer = getPropertyEditorObjectsContainer();

        if (newIndex == OBJECTS_TOOL) {
            FXUtils.addToPane(modelPropertyEditor, propertyContainer);
            FXUtils.addToPane(modelNodeTree, getModelNodeTreeObjectsContainer());
            modelPropertyEditor.rebuild();
        } else if (newIndex == EDITING_TOOL) {
            FXUtils.addToPane(modelNodeTree, getModelNodeTreeEditingContainer());
            editingComponentContainer.notifyShowed();
        }

        if (oldIndex == EDITING_TOOL) {
            editingComponentContainer.notifyHided();
        }

        final MA editor3DState = getEditor3DState();
        editor3DState.changeEditingMode(newIndex == EDITING_TOOL);
    }

    /**
     * Handle dropped files to editor.
     */
    @FXThread
    private void dragDropped(@NotNull final DragEvent dragEvent) {

        UIUtils.handleDroppedFile(dragEvent, FileExtensions.JME_OBJECT, this,
                dragEvent, AbstractSceneFileEditor::addNewModel);

        UIUtils.handleDroppedFile(dragEvent, FileExtensions.JME_MATERIAL, this,
                dragEvent, AbstractSceneFileEditor::applyMaterial);
    }

    /**
     * Handle drag over.
     */
    @FXThread
    private void dragOver(@NotNull final DragEvent dragEvent) {
        UIUtils.acceptIfHasFile(dragEvent, ACCEPTED_FILES);
    }

    /**
     * Apply a new material from an asset tree.
     *
     * @param dragEvent the drag event.
     * @param file      the file.
     */
    @FXThread
    private void applyMaterial(@NotNull final DragEvent dragEvent, @NotNull final Path file) {

        final Path assetFile = notNull(getAssetFile(file), "Not found asset file for " + file);
        final String assetPath = toAssetPath(assetFile);

        final MA editor3DState = getEditor3DState();
        final MaterialKey materialKey = new MaterialKey(assetPath);
        final Camera camera = editor3DState.getCamera();

        final BorderPane area = get3DArea();
        final Point2D areaPoint = area.sceneToLocal(dragEvent.getSceneX(), dragEvent.getSceneY());

        EXECUTOR_MANAGER.addJMETask(() -> {

            final Geometry geometry = editor3DState.getGeometryByScreenPos((float) areaPoint.getX(),
                    camera.getHeight() - (float) areaPoint.getY());

            if (geometry == null) return;
            final Object linkNode = findParent(geometry, AssetLinkNode.class::isInstance);
            if (linkNode != null) return;

            final AssetManager assetManager = EDITOR.getAssetManager();
            final Material material = assetManager.loadAsset(materialKey);

            final ModelPropertyOperation<Geometry, Material> operation =
                    new ModelPropertyOperation<>(geometry, Messages.MODEL_PROPERTY_MATERIAL, material, geometry.getMaterial());

            operation.setApplyHandler(Geometry::setMaterial);

            execute(operation);
        });
    }

    /**
     * Add a new model from an asset tree.
     *
     * @param dragEvent the drag event.
     * @param file      the file.
     */
    @FXThread
    private void addNewModel(@NotNull final DragEvent dragEvent, @NotNull final Path file) {

        final M currentModel = getCurrentModel();
        if (!(currentModel instanceof Node)) return;

        final ModelNodeTree modelNodeTree = getModelNodeTree();
        final Object selected = modelNodeTree.getSelectedObject();

        final Node parent;

        if (selected instanceof Node && findParent((Spatial) selected, AssetLinkNode.class::isInstance) == null) {
            parent = (Node) selected;
        } else {
            parent = (Node) currentModel;
        }

        final Path assetFile = notNull(getAssetFile(file), "Not found asset file for " + file);
        final String assetPath = toAssetPath(assetFile);

        final MA editor3DState = getEditor3DState();
        final ModelKey modelKey = new ModelKey(assetPath);
        final Camera camera = editor3DState.getCamera();

        final BorderPane area = get3DArea();
        final Point2D areaPoint = area.sceneToLocal(dragEvent.getSceneX(), dragEvent.getSceneY());

        EXECUTOR_MANAGER.addJMETask(() -> {

            final SceneLayer defaultLayer = getDefaultLayer(this);
            final LocalObjects local = LocalObjects.get();

            final AssetManager assetManager = EDITOR.getAssetManager();
            final Spatial loadedModel = assetManager.loadModel(modelKey);

            final AssetLinkNode assetLinkNode = new AssetLinkNode(modelKey);
            assetLinkNode.attachLinkedChild(loadedModel, modelKey);
            assetLinkNode.setUserData(LOADED_MODEL_KEY, true);

            if (defaultLayer != null) {
                SceneLayer.setLayer(defaultLayer, assetLinkNode);
            }

            final Vector3f scenePoint = editor3DState.getScenePosByScreenPos((float) areaPoint.getX(),
                    camera.getHeight() - (float) areaPoint.getY());
            final Vector3f result = local.nextVector(scenePoint)
                    .subtractLocal(parent.getWorldTranslation());

            assetLinkNode.setLocalTranslation(result);

            execute(new AddChildOperation(assetLinkNode, parent, false));
        });
    }

    /**
     * Calc v split size.
     *
     * @param splitContainer the split container
     */
    @FXThread
    static void calcVSplitSize(@NotNull final SplitPane splitContainer) {
        splitContainer.setDividerPosition(0, 0.3);
    }

    /**
     * Handle changing select visibility.
     */
    @FXThread
    private void changeSelectionVisible(@NotNull final Boolean newValue) {
        if (isIgnoreListeners()) return;

        final MA editor3DState = getEditor3DState();
        editor3DState.updateShowSelection(newValue);

        final ES editorState = getEditorState();
        if (editorState != null) editorState.setEnableSelection(newValue);
    }

    /**
     * Handle changing grid visibility.
     */
    @FXThread
    private void changeGridVisible(@NotNull final Boolean newValue) {
        if (isIgnoreListeners()) return;

        final MA editor3DState = getEditor3DState();
        editor3DState.updateShowGrid(newValue);

        final ES editorState = getEditorState();
        if (editorState != null) editorState.setEnableGrid(newValue);
    }

    /**
     * Handle changing statistics visibility.
     */
    @FXThread
    private void changeStatisticsVisible(@NotNull final Boolean newValue) {
        if (isIgnoreListeners()) return;

        statsAppState.setEnabled(newValue);

        final ES editorState = getEditorState();
        if (editorState != null) editorState.setShowStatistics(newValue);
    }

    /**
     * Notify about transformed the object.
     *
     * @param spatial the spatial
     */
    @FromAnyThread
    public void notifyTransformed(@NotNull final Spatial spatial) {
        EXECUTOR_MANAGER.addFXTask(() -> notifyTransformedImpl(spatial));
    }

    /**
     * Notify about transformed the object.
     */
    @FXThread
    private void notifyTransformedImpl(@NotNull final Spatial spatial) {

        Object toUpdate = spatial;

        if (spatial instanceof WrapperNode) {
            toUpdate = ((WrapperNode) spatial).getWrappedObject();
        }

        final ModelPropertyEditor modelPropertyEditor = getModelPropertyEditor();
        modelPropertyEditor.syncFor(toUpdate);
    }

    @NotNull
    @Override
    @JMEThread
    public Node getCursorNode() {
        return getEditor3DState().getCursorNode();
    }

    @NotNull
    @Override
    @JMEThread
    public Node getMarkersNode() {
        return getEditor3DState().getMarkersNode();
    }
}

