package com.ss.editor.ui.component.editor.impl.scene;

import static com.ss.editor.util.EditorUtil.getAssetFile;
import static com.ss.editor.util.EditorUtil.toAssetPath;
import static com.ss.editor.util.MaterialUtils.updateMaterialIdNeed;
import static java.util.Objects.requireNonNull;
import static rlib.util.ClassUtils.unsafeCast;

import com.jme3.asset.AssetManager;
import com.jme3.asset.MaterialKey;
import com.jme3.asset.ModelKey;
import com.jme3.audio.AudioNode;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.light.Light;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.control.transform.SceneEditorControl.TransformType;
import com.ss.editor.manager.WorkspaceManager;
import com.ss.editor.model.undo.EditorOperation;
import com.ss.editor.model.undo.EditorOperationControl;
import com.ss.editor.model.undo.UndoableEditor;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.workspace.Workspace;
import com.ss.editor.scene.EditorAudioNode;
import com.ss.editor.scene.EditorLightNode;
import com.ss.editor.state.editor.impl.scene.AbstractSceneEditorAppState;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.editor.impl.AbstractFileEditor;
import com.ss.editor.ui.component.editor.state.EditorState;
import com.ss.editor.ui.component.editor.state.impl.AbstractModelFileEditorState;
import com.ss.editor.ui.component.split.pane.EditorToolSplitPane;
import com.ss.editor.ui.component.tab.EditorToolComponent;
import com.ss.editor.ui.control.model.property.ModelPropertyEditor;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.operation.AddChildOperation;
import com.ss.editor.ui.control.tree.node.ModelNode;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.event.impl.FileChangedEvent;
import com.ss.editor.util.MaterialUtils;
import com.ss.editor.util.NodeUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.geometry.Point2D;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import rlib.ui.util.FXUtils;
import rlib.util.FileUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;
import tonegod.emitter.filter.TonegodTranslucentBucketFilter;

/**
 * The base implementation of a model file editor.
 *
 * @author JavaSaBr
 */
public abstract class AbstractSceneFileEditor<IM extends AbstractSceneFileEditor, M extends Spatial,
        MA extends AbstractSceneEditorAppState<IM, M>, ES extends AbstractModelFileEditorState>
        extends AbstractFileEditor<StackPane> implements UndoableEditor, ModelChangeConsumer {

    /**
     * The 3D part of this editor.
     */
    @NotNull
    protected final MA editorAppState;

    /**
     * The operation control.
     */
    @NotNull
    protected final EditorOperationControl operationControl;

    /**
     * The changes counter.
     */
    @NotNull
    protected final AtomicInteger changeCounter;

    /**
     * The opened model.
     */
    protected M currentModel;

    /**
     * The selection handler.
     */
    protected Consumer<Object> selectionNodeHandler;

    /**
     * The model tree.
     */
    protected ModelNodeTree modelNodeTree;

    /**
     * The model property editor.
     */
    protected ModelPropertyEditor modelPropertyEditor;

    /**
     * The state of this editor.
     */
    protected ES editorState;

    /**
     * The main split container.
     */
    protected EditorToolSplitPane mainSplitContainer;

    /**
     * The editor tool component.
     */
    protected EditorToolComponent editorToolComponent;

    /**
     * The pane of editor area.
     */
    protected Pane editorAreaPane;

    /**
     * The selection toggle.
     */
    protected ToggleButton selectionButton;

    /**
     * The grid toggle.
     */
    protected ToggleButton gridButton;

    /**
     * The move tool toggle.
     */
    protected ToggleButton moveToolButton;

    /**
     * The rotation tool toggle.
     */
    protected ToggleButton rotationToolButton;

    /**
     * The scaling tool toggle.
     */
    protected ToggleButton scaleToolButton;

    /**
     * The flag of ignoring listeners.
     */
    private boolean ignoreListeners;

    public AbstractSceneFileEditor() {
        this.editorAppState = createEditorAppState();
        this.operationControl = new EditorOperationControl(this);
        this.changeCounter = new AtomicInteger();
        addEditorState(editorAppState);
    }

    @NotNull
    protected abstract MA createEditorAppState();

    @NotNull
    @Override
    protected StackPane createRoot() {
        return new StackPane();
    }

    /**
     * @return the 3D part of this editor.
     */
    @NotNull
    protected MA getEditorAppState() {
        return editorAppState;
    }

    /**
     * @return the state of this editor.
     */
    @Nullable
    public ES getEditorState() {
        return editorState;
    }

    /**
     * @return the model tree.
     */
    protected ModelNodeTree getModelNodeTree() {
        return modelNodeTree;
    }

    /**
     * @return the model property editor.
     */
    protected ModelPropertyEditor getModelPropertyEditor() {
        return modelPropertyEditor;
    }

    @Override
    protected void processChangedFile(@NotNull final FileChangedEvent event) {

        final Path file = event.getFile();
        final String extension = FileUtils.getExtension(file);

        if (extension.endsWith(FileExtensions.JME_MATERIAL)) {
            EXECUTOR_MANAGER.addEditorThreadTask(() -> updateMaterial(file));
        } else if (MaterialUtils.isShaderFile(file) || MaterialUtils.isTextureFile(file)) {
            EXECUTOR_MANAGER.addEditorThreadTask(() -> updateMaterials(file));
        }
    }

    /**
     * Updating a material from the file.
     */
    private void updateMaterial(@NotNull final Path file) {

        final Path assetFile = requireNonNull(getAssetFile(file), "Not found asset file for " + file);
        final String assetPath = toAssetPath(assetFile);

        final M currentModel = getCurrentModel();

        final Array<Geometry> geometries = ArrayFactory.newArray(Geometry.class);
        NodeUtils.addGeometryWithMaterial(currentModel, geometries, assetPath);
        if (geometries.isEmpty()) return;

        final MaterialKey materialKey = new MaterialKey(assetPath);

        final AssetManager assetManager = EDITOR.getAssetManager();
        assetManager.deleteFromCache(materialKey);

        final Material material = assetManager.loadMaterial(assetPath);
        geometries.forEach(geometry -> geometry.setMaterial(material));

        final TonegodTranslucentBucketFilter translucentBucketFilter = EDITOR.getTranslucentBucketFilter();
        translucentBucketFilter.refresh();
    }

    /**
     * Updating materials.
     */
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
     * Handle the model.
     */
    protected void handleObjects(@NotNull final Spatial model) {

        final MA editorState = getEditorAppState();
        final Array<Light> lights = ArrayFactory.newArray(Light.class);
        final Array<AudioNode> audioNodes = ArrayFactory.newArray(AudioNode.class);

        NodeUtils.addLight(model, lights);
        NodeUtils.addAudioNodes(model, audioNodes);

        lights.forEach(editorState, (light, state) -> state.addLight(light));
        audioNodes.forEach(editorState, (audioNode, state) -> state.addAudioNode(audioNode));
    }

    /**
     * Load the saved state.
     */
    protected void loadState() {

        final WorkspaceManager workspaceManager = WorkspaceManager.getInstance();
        final Workspace currentWorkspace = requireNonNull(workspaceManager.getCurrentWorkspace(),
                "Current workspace can't be null.");

        editorState = currentWorkspace.getEditorState(getEditFile(), getStateConstructor());
        mainSplitContainer.updateFor(editorState);
        gridButton.setSelected(editorState.isEnableGrid());
        selectionButton.setSelected(editorState.isEnableSelection());

        final TransformType transformType = TransformType.valueOf(editorState.getTransformationType());

        switch (transformType) {
            case MOVE_TOOL:
                moveToolButton.setSelected(true);
                break;
            case ROTATE_TOOL:
                rotationToolButton.setSelected(true);
                break;
            case SCALE_TOOL:
                scaleToolButton.setSelected(true);
                break;
            default:
                break;
        }

        final MA editorAppState = getEditorAppState();
        final Vector3f cameraLocation = editorState.getCameraLocation();

        final float hRotation = editorState.getCameraHRotation();
        final float vRotation = editorState.getCameraVRotation();
        final float tDistance = editorState.getCameraTDistance();

        EXECUTOR_MANAGER.addEditorThreadTask(() -> editorAppState.updateCamera(cameraLocation, hRotation, vRotation, tDistance));
    }

    @Override
    public void notifyChangedCamera(@NotNull final Vector3f cameraLocation, final float hRotation,
                                    final float vRotation, final float targetDistance) {

        final ES editorState = getEditorState();
        if (editorState == null) return;

        editorState.setCameraHRotation(hRotation);
        editorState.setCameraVRotation(vRotation);
        editorState.setCameraTDistance(targetDistance);
        editorState.setCameraLocation(cameraLocation);
    }

    @Override
    protected void processKeyReleased(@NotNull final KeyEvent event) {
        super.processKeyReleased(event);
        if (!event.isControlDown()) return;

        final KeyCode code = event.getCode();

        if (code == KeyCode.Z) {
            undo();
            event.consume();
        } else if (code == KeyCode.Y) {
            redo();
            event.consume();
        }
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
     * @return true if needs to ignore events.
     */
    protected boolean isIgnoreListeners() {
        return ignoreListeners;
    }

    /**
     * @param ignoreListeners true if needs to ignore events.
     */
    protected void setIgnoreListeners(final boolean ignoreListeners) {
        this.ignoreListeners = ignoreListeners;
    }

    /**
     * @param currentModel the opened model.
     */
    protected void setCurrentModel(@NotNull final M currentModel) {
        this.currentModel = currentModel;
    }

    @NotNull
    @Override
    public M getCurrentModel() {
        return currentModel;
    }

    @Override
    public void notifyChangeProperty(@Nullable final Object parent, @NotNull final Object object, @NotNull final String propertyName) {

        final ModelPropertyEditor modelPropertyEditor = getModelPropertyEditor();
        modelPropertyEditor.syncFor(object);

        final ModelNodeTree modelNodeTree = getModelNodeTree();
        modelNodeTree.notifyChanged(parent, object);
    }

    @Override
    public void notifyChangePropertyCount(@Nullable final Object parent, @NotNull final Object object) {
        final ModelPropertyEditor modelPropertyEditor = getModelPropertyEditor();
        modelPropertyEditor.rebuildFor(object, parent);
    }

    @Override
    public void notifyAddedChild(@NotNull final Object parent, @NotNull final Object added, final int index) {

        final MA editorAppState = getEditorAppState();
        final ModelNodeTree modelNodeTree = getModelNodeTree();
        modelNodeTree.notifyAdded(parent, added, index);

        if (added instanceof Light) {
            editorAppState.addLight((Light) added);
        } else if (added instanceof AudioNode) {
            editorAppState.addAudioNode((AudioNode) added);
        }
    }

    @Override
    public void notifyRemovedChild(@NotNull final Object parent, @NotNull final Object removed) {

        final MA editorAppState = getEditorAppState();
        final ModelNodeTree modelNodeTree = getModelNodeTree();
        modelNodeTree.notifyRemoved(parent, removed);

        if (removed instanceof Light) {
            editorAppState.removeLight((Light) removed);
        } else if (removed instanceof AudioNode) {
            editorAppState.removeAudioNode((AudioNode) removed);
        }
    }

    @Override
    public void notifyReplaced(@NotNull final Node parent, @NotNull final Spatial oldChild, @NotNull final Spatial newChild) {

        final MA editorAppState = getEditorAppState();
        final Spatial currentModel = getCurrentModel();

        if (currentModel == oldChild) {
            setCurrentModel(unsafeCast(newChild));
            editorAppState.openModel(unsafeCast(newChild));
        }

        final ModelNodeTree modelNodeTree = getModelNodeTree();
        modelNodeTree.notifyReplace(parent, oldChild, newChild);
    }

    @Override
    public void notifyReplaced(@NotNull final Object parent, @Nullable final Object oldChild, @Nullable final Object newChild) {
        final ModelNodeTree modelNodeTree = getModelNodeTree();
        modelNodeTree.notifyReplace(parent, oldChild, newChild);
    }

    @Override
    public void notifyMoved(@NotNull final Node prevParent, @NotNull final Node newParent, @NotNull final Spatial child, int index) {
        final ModelNodeTree modelNodeTree = getModelNodeTree();
        modelNodeTree.notifyMoved(prevParent, newParent, child, index);
    }

    @Override
    public void execute(@NotNull final EditorOperation operation) {
        final EditorOperationControl operationControl = getOperationControl();
        operationControl.execute(operation);
    }

    /**
     * @return the operation control.
     */
    @NotNull
    protected EditorOperationControl getOperationControl() {
        return operationControl;
    }

    /**
     * Handle the selected object.
     */
    @FXThread
    public void notifySelected(@Nullable Object object) {

        if (object instanceof EditorLightNode) {
            object = ((EditorLightNode) object).getLight();
        }

        if (object instanceof EditorAudioNode) {
            object = ((EditorAudioNode) object).getAudioNode();
        }

        final ModelNodeTree modelNodeTree = getModelNodeTree();
        modelNodeTree.select(object);
    }

    @NotNull
    protected abstract Supplier<EditorState> getStateConstructor();

    /**
     * Handle the selected object from the Tree.
     */
    @FXThread
    public void selectNodeFromTree(@Nullable final Object object) {

        Object parent = null;
        Object element = null;

        if (object instanceof ModelNode<?>) {
            final ModelNode modelNode = (ModelNode) object;
            final ModelNode parentNode = modelNode.getParent();
            parent = parentNode == null ? null : parentNode.getElement();
            element = modelNode.getElement();
        }

        Spatial spatial = null;

        if (element instanceof AudioNode) {
            spatial = getEditorAppState().getAudioNode((AudioNode) element);
        } else if (element instanceof Spatial) {
            spatial = (Spatial) element;
            parent = spatial.getParent();
        } else if (element instanceof Light) {
            spatial = getEditorAppState().getLightNode((Light) element);
        }

        if (spatial != null && !spatial.isVisible()) {
            spatial = null;
        }

        updateSelection(spatial);

        final ModelPropertyEditor modelPropertyEditor = getModelPropertyEditor();
        modelPropertyEditor.buildFor(element, parent);
    }

    /**
     * Update selection to 3D state.
     *
     * @param spatial the new selected object.
     */
    protected void updateSelection(@Nullable final Spatial spatial) {

        //FIXME
        final Array<Spatial> selection = ArrayFactory.newArray(Spatial.class);
        if (spatial != null) selection.add(spatial);

        final MA editorAppState = getEditorAppState();
        editorAppState.updateSelection(selection);
    }

    @Override
    public boolean isInside(final double sceneX, final double sceneY) {
        final Point2D point2D = editorAreaPane.sceneToLocal(sceneX, sceneY);
        return editorAreaPane.contains(point2D);
    }

    @Override
    public void doSave() {

        final Path editFile = getEditFile();
        final M currentModel = getCurrentModel();

        final BinaryExporter exporter = BinaryExporter.getInstance();

        try (final OutputStream out = Files.newOutputStream(editFile)) {
            exporter.save(currentModel, out);
        } catch (final IOException e) {
            LOGGER.warning(this, e);
        }

        setDirty(false);
    }

    @Override
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
    protected ToggleButton getScaleToolButton() {
        return scaleToolButton;
    }

    /**
     * @return the move tool toggle.
     */
    @NotNull
    protected ToggleButton getMoveToolButton() {
        return moveToolButton;
    }

    /**
     * @return the rotation tool toggle.
     */
    @NotNull
    protected ToggleButton getRotationToolButton() {
        return rotationToolButton;
    }

    /**
     * Switch transformation mode.
     */
    private void updateTransformTool(@NotNull final TransformType transformType, @NotNull final Boolean newValue) {
        if (newValue != Boolean.TRUE) return;

        final ToggleButton scaleToolButton = getScaleToolButton();
        final ToggleButton moveToolButton = getMoveToolButton();
        final ToggleButton rotationToolButton = getRotationToolButton();

        final MA editorAppState = getEditorAppState();
        final ES editorState = getEditorState();

        if (transformType == TransformType.MOVE_TOOL) {
            rotationToolButton.setSelected(false);
            scaleToolButton.setSelected(false);
            editorAppState.setTransformType(transformType);
        } else if (transformType == TransformType.ROTATE_TOOL) {
            moveToolButton.setSelected(false);
            scaleToolButton.setSelected(false);
            editorAppState.setTransformType(transformType);
        } else if (transformType == TransformType.SCALE_TOOL) {
            rotationToolButton.setSelected(false);
            moveToolButton.setSelected(false);
            editorAppState.setTransformType(transformType);
        }

        if (editorState != null) {
            editorState.setTransformationType(transformType.ordinal());
        }
    }

    @Override
    protected void createToolbar(@NotNull final HBox container) {
        FXUtils.addToPane(createSaveAction(), container);

        selectionButton = new ToggleButton();
        selectionButton.setGraphic(new ImageView(Icons.CUBE_16));
        selectionButton.setSelected(true);
        selectionButton.selectedProperty().addListener((observable, oldValue, newValue) ->
                changeSelectionVisible(newValue));

        gridButton = new ToggleButton();
        gridButton.setGraphic(new ImageView(Icons.PLANE_16));
        gridButton.setSelected(true);
        gridButton.selectedProperty().addListener((observable, oldValue, newValue) ->
                changeGridVisible(newValue));

        moveToolButton = new ToggleButton();
        moveToolButton.setGraphic(new ImageView(Icons.MOVE_16));
        moveToolButton.setSelected(true);
        moveToolButton.selectedProperty().addListener((observable, oldValue, newValue) ->
                updateTransformTool(TransformType.MOVE_TOOL, newValue));

        rotationToolButton = new ToggleButton();
        rotationToolButton.setGraphic(new ImageView(Icons.ROTATION_16));
        rotationToolButton.selectedProperty().addListener((observable, oldValue, newValue) ->
                updateTransformTool(TransformType.ROTATE_TOOL, newValue));

        scaleToolButton = new ToggleButton();
        scaleToolButton.setGraphic(new ImageView(Icons.SCALE_16));
        scaleToolButton.selectedProperty().addListener((observable, oldValue, newValue) ->
                updateTransformTool(TransformType.SCALE_TOOL, newValue));

        FXUtils.addClassTo(selectionButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(selectionButton, CSSClasses.FILE_EDITOR_TOOLBAR_BUTTON);
        FXUtils.addClassTo(gridButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(gridButton, CSSClasses.FILE_EDITOR_TOOLBAR_BUTTON);
        FXUtils.addClassTo(moveToolButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(moveToolButton, CSSClasses.FILE_EDITOR_TOOLBAR_BUTTON);
        FXUtils.addClassTo(rotationToolButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(rotationToolButton, CSSClasses.FILE_EDITOR_TOOLBAR_BUTTON);
        FXUtils.addClassTo(scaleToolButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(scaleToolButton, CSSClasses.FILE_EDITOR_TOOLBAR_BUTTON);

        FXUtils.addToPane(selectionButton, container);
        FXUtils.addToPane(gridButton, container);
        FXUtils.addToPane(moveToolButton, container);
        FXUtils.addToPane(rotationToolButton, container);
        FXUtils.addToPane(scaleToolButton, container);
    }

    @Override
    protected void createContent(@NotNull final StackPane root) {
        this.selectionNodeHandler = this::selectNodeFromTree;

        editorAreaPane = new Pane();
        editorAreaPane.setId(CSSIds.FILE_EDITOR_EDITOR_AREA);
        editorAreaPane.setOnDragOver(this::dragOver);
        editorAreaPane.setOnDragDropped(this::dragDropped);
        editorAreaPane.setOnDragExited(this::dragExited);

        modelNodeTree = new ModelNodeTree(selectionNodeHandler, this);
        modelPropertyEditor = new ModelPropertyEditor(this);

        final SplitPane modelSplitContainer = new SplitPane(modelNodeTree, modelPropertyEditor);
        modelSplitContainer.setId(CSSIds.FILE_EDITOR_TOOL_SPLIT_PANE);
        modelSplitContainer.prefHeightProperty().bind(root.heightProperty());
        modelSplitContainer.prefWidthProperty().bind(root.widthProperty());

        mainSplitContainer = new EditorToolSplitPane(JFX_APPLICATION.getScene(), root);
        mainSplitContainer.setId(CSSIds.FILE_EDITOR_MAIN_SPLIT_PANE);

        editorToolComponent = new EditorToolComponent(mainSplitContainer, 1);
        editorToolComponent.prefHeightProperty().bind(root.heightProperty());
        editorToolComponent.addComponent(modelSplitContainer, Messages.SCENE_FILE_EDITOR_TOOL_OBJECTS);

        mainSplitContainer.initFor(editorToolComponent, editorAreaPane);

        FXUtils.addToPane(mainSplitContainer, root);

        root.heightProperty().addListener((observableValue, oldValue, newValue) ->
                calcVSplitSize(modelSplitContainer));
    }

    protected void dragExited(@NotNull final DragEvent dragEvent) {
    }

    /**
     * Handle dropped files to editor.
     */
    protected void dragDropped(@NotNull final DragEvent dragEvent) {

        final Dragboard dragboard = dragEvent.getDragboard();
        final List<File> files = unsafeCast(dragboard.getContent(DataFormat.FILES));

        if (files == null || files.isEmpty()) {
            return;
        }

        final MA editorAppState = getEditorAppState();
        final M currentModel = getCurrentModel();

        for (final File file : files) {

            if (!file.getName().endsWith(FileExtensions.JME_OBJECT)) {
                continue;
            }

            final Path assetFile = requireNonNull(getAssetFile(file.toPath()), "Not found asset file for " + file);
            final String assetPath = toAssetPath(assetFile);

            final ModelKey modelKey = new ModelKey(assetPath);

            final AssetManager assetManager = EDITOR.getAssetManager();
            assetManager.clearCache();

            final float sceneX = (float) dragEvent.getSceneX();
            final float sceneY = (float) dragEvent.getSceneY();

            EXECUTOR_MANAGER.addEditorThreadTask(() -> {

                final Spatial loadedModel = assetManager.loadModel(modelKey);
                loadedModel.setLocalTranslation(editorAppState.getCurrentCursorPosOnScene(sceneX, sceneY));

                execute(new AddChildOperation(loadedModel, (Node) currentModel));
            });
        }
    }

    /**
     * Handle drag over.
     */
    protected void dragOver(@NotNull final DragEvent dragEvent) {

        final Dragboard dragboard = dragEvent.getDragboard();
        final List<File> files = unsafeCast(dragboard.getContent(DataFormat.FILES));

        if (files == null || files.isEmpty()) {
            return;
        }

        final Set<TransferMode> transferModes = dragboard.getTransferModes();
        final boolean isCopy = transferModes.contains(TransferMode.COPY);

        dragEvent.acceptTransferModes(isCopy ? TransferMode.COPY : TransferMode.MOVE);
        dragEvent.consume();
    }

    protected static void calcVSplitSize(@NotNull final SplitPane splitContainer) {
        splitContainer.setDividerPosition(0, 0.3);
    }

    /**
     * Handle changing select visibility.
     */
    private void changeSelectionVisible(@NotNull final Boolean newValue) {
        if (isIgnoreListeners()) return;

        final MA editorAppState = getEditorAppState();
        editorAppState.updateShowSelection(newValue);

        final ES editorState = getEditorState();
        if (editorState != null) editorState.setEnableSelection(newValue);
    }

    /**
     * Handle changing grid visibility.
     */
    private void changeGridVisible(@NotNull final Boolean newValue) {
        if (isIgnoreListeners()) return;

        final MA editorAppState = getEditorAppState();
        editorAppState.updateShowGrid(newValue);

        final ES editorState = getEditorState();
        if (editorState != null) editorState.setEnableGrid(newValue);
    }

    /**
     * Notify about transformed the object.
     */
    @FromAnyThread
    public void notifyTransformed(@NotNull final Spatial spatial) {
        EXECUTOR_MANAGER.addFXTask(() -> notifyTransformedImpl(spatial));
    }

    /**
     * Notify about transformed the object.
     */
    private void notifyTransformedImpl(@NotNull final Spatial spatial) {

        Object toUpdate = spatial;

        if (spatial instanceof EditorLightNode) {
            toUpdate = ((EditorLightNode) spatial).getLight();
        } else if (spatial instanceof EditorAudioNode) {
            toUpdate = ((EditorAudioNode) spatial).getAudioNode();
        }

        final ModelPropertyEditor modelPropertyEditor = getModelPropertyEditor();
        modelPropertyEditor.syncFor(toUpdate);
    }
}

