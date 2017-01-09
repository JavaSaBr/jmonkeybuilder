package com.ss.editor.ui.component.editor.impl.model;

import static com.ss.editor.util.EditorUtil.getAssetFile;
import static com.ss.editor.util.EditorUtil.toAssetPath;
import static com.ss.editor.util.MaterialUtils.updateMaterialIdNeed;

import com.jme3.asset.AssetManager;
import com.jme3.asset.MaterialKey;
import com.jme3.asset.ModelKey;
import com.jme3.asset.TextureKey;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.light.Light;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import com.jme3.util.SkyFactory.EnvMapType;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.control.transform.SceneEditorControl.TransformType;
import com.ss.editor.manager.WorkspaceManager;
import com.ss.editor.model.undo.EditorOperation;
import com.ss.editor.model.undo.EditorOperationControl;
import com.ss.editor.model.undo.UndoableEditor;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.workspace.Workspace;
import com.ss.editor.state.editor.impl.model.ModelEditorAppState;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.editor.EditorDescription;
import com.ss.editor.ui.component.editor.impl.AbstractFileEditor;
import com.ss.editor.ui.component.editor.state.impl.ModelFileEditorState;
import com.ss.editor.ui.component.split.pane.EditorToolSplitPane;
import com.ss.editor.ui.component.tab.EditorToolComponent;
import com.ss.editor.ui.control.model.property.ModelPropertyEditor;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.node.ModelNode;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.event.impl.FileChangedEvent;
import com.ss.editor.util.MaterialUtils;
import com.ss.editor.util.NodeUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import rlib.ui.util.FXUtils;
import rlib.util.FileUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;
import tonegod.emitter.filter.TonegodTranslucentBucketFilter;

/**
 * The implementation of the {@link AbstractFileEditor} for working with {@link Spatial}.
 *
 * @author JavaSaBr
 */
public class ModelFileEditor extends AbstractFileEditor<StackPane> implements UndoableEditor, ModelChangeConsumer {

    public static final String NO_FAST_SKY = Messages.MODEL_FILE_EDITOR_NO_SKY;

    public static final Insets LIGHT_BUTTON_OFFSET = new Insets(0, 0, 0, 4);
    public static final Insets FAST_SKY_LABEL_OFFSET = new Insets(0, 0, 0, 8);

    public static final EditorDescription DESCRIPTION = new EditorDescription();

    static {
        DESCRIPTION.setEditorName(Messages.MODEL_FILE_EDITOR_NAME);
        DESCRIPTION.setConstructor(ModelFileEditor::new);
        DESCRIPTION.setEditorId(ModelFileEditor.class.getSimpleName());
        DESCRIPTION.addExtension(FileExtensions.JME_OBJECT);
    }

    private static final Array<String> FAST_SKY_LIST = ArrayFactory.newArray(String.class);

    static {
        FAST_SKY_LIST.add(NO_FAST_SKY);
        FAST_SKY_LIST.add("graphics/textures/sky/studio.hdr");
        FAST_SKY_LIST.add("graphics/textures/sky/env1.hdr");
        FAST_SKY_LIST.add("graphics/textures/sky/env2.hdr");
        FAST_SKY_LIST.add("graphics/textures/sky/env3.hdr");
        FAST_SKY_LIST.add("graphics/textures/sky/env4.hdr");
        FAST_SKY_LIST.add("graphics/textures/sky/outside.hdr");
        FAST_SKY_LIST.add("graphics/textures/sky/inside.hdr");
    }

    /**
     * The 3D part of this editor.
     */
    private final ModelEditorAppState editorAppState;

    /**
     * The operation control.
     */
    private final EditorOperationControl operationControl;

    /**
     * The changes counter.
     */
    private final AtomicInteger changeCounter;

    /**
     * The state of this editor.
     */
    private ModelFileEditorState editorState;

    /**
     * The opened model.
     */
    private Spatial currentModel;

    /**
     * The selection handler.
     */
    private Consumer<Object> selectionHandler;

    /**
     * The model tree.
     */
    private ModelNodeTree modelNodeTree;

    /**
     * The model property editor.
     */
    private ModelPropertyEditor modelPropertyEditor;

    /**
     * The list of fast skies.
     */
    private ComboBox<String> fastSkyComboBox;

    /**
     * The light toggle.
     */
    private ToggleButton lightButton;

    /**
     * The selection toggle.
     */
    private ToggleButton selectionButton;

    /**
     * The grid toggle.
     */
    private ToggleButton gridButton;

    /**
     * The move tool toggle.
     */
    private ToggleButton moveToolButton;

    /**
     * The rotation tool toggle.
     */
    private ToggleButton rotationToolButton;

    /**
     * The scaling tool toggle.
     */
    private ToggleButton scaleToolButton;

    /**
     * The pane of editor area.
     */
    private Pane editorAreaPane;

    /**
     * The main split container.
     */
    private EditorToolSplitPane mainSplitContainer;

    /**
     * The flag of ignoring listeners.
     */
    private boolean ignoreListeners;

    public ModelFileEditor() {
        this.editorAppState = new ModelEditorAppState(this);
        this.operationControl = new EditorOperationControl(this);
        this.changeCounter = new AtomicInteger();
        addEditorState(editorAppState);
    }

    @Override
    public void incrementChange() {
        final int result = changeCounter.incrementAndGet();
        setDirty(result != 0);
    }

    @Override
    public void decrementChange() {
        final int result = changeCounter.decrementAndGet();
        setDirty(result != 0);
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

        final Path assetFile = Objects.requireNonNull(getAssetFile(file), "Not found asset file for " + file);
        final String assetPath = toAssetPath(assetFile);

        final Spatial currentModel = getCurrentModel();

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

        final Spatial currentModel = getCurrentModel();
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

    @NotNull
    @Override
    protected StackPane createRoot() {
        return new StackPane();
    }

    /**
     * @return the 3D part of this editor.
     */
    @NotNull
    private ModelEditorAppState getEditorAppState() {
        return editorAppState;
    }

    /**
     * @return the state of this editor.
     */
    @Nullable
    public ModelFileEditorState getEditorState() {
        return editorState;
    }

    /**
     * @return the list of fast skies.
     */
    @NotNull
    private ComboBox<String> getFastSkyComboBox() {
        return fastSkyComboBox;
    }

    /**
     * @return the model tree.
     */
    private ModelNodeTree getModelNodeTree() {
        return modelNodeTree;
    }

    /**
     * @return the model property editor.
     */
    private ModelPropertyEditor getModelPropertyEditor() {
        return modelPropertyEditor;
    }

    @Override
    public void openFile(@NotNull final Path file) {
        super.openFile(file);

        final Path assetFile = getAssetFile(file);

        Objects.requireNonNull(assetFile, "Asset file for " + file + " can't be null.");

        final ModelKey modelKey = new ModelKey(toAssetPath(assetFile));

        final AssetManager assetManager = EDITOR.getAssetManager();
        assetManager.deleteFromCache(modelKey);

        final Spatial model = assetManager.loadAsset(modelKey);

        MaterialUtils.cleanUpMaterialParams(model);

        final ModelEditorAppState editorState = getEditorAppState();
        editorState.openModel(model);

        handleObjects(model);

        setCurrentModel(model);
        setIgnoreListeners(true);
        try {

            final ComboBox<String> fastSkyComboBox = getFastSkyComboBox();
            fastSkyComboBox.getSelectionModel().select(FAST_SKY_LIST.first());

            final ModelNodeTree modelNodeTree = getModelNodeTree();
            modelNodeTree.fill(model);

        } finally {
            setIgnoreListeners(false);
        }

        EXECUTOR_MANAGER.addFXTask(this::loadState);
    }

    /**
     * Load the saved state.
     */
    protected void loadState() {

        final WorkspaceManager workspaceManager = WorkspaceManager.getInstance();
        final Workspace currentWorkspace = Objects.requireNonNull(workspaceManager.getCurrentWorkspace(),
                "Current workspace can't be null.");

        editorState = currentWorkspace.getEditorState(getEditFile(), ModelFileEditorState::new);
        mainSplitContainer.updateFor(editorState);
        fastSkyComboBox.getSelectionModel().select(editorState.getSkyType());
        lightButton.setSelected(editorState.isEnableLight());
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

        final ModelEditorAppState editorAppState = getEditorAppState();
        final Vector3f cameraLocation = editorState.getCameraLocation();

        final float hRotation = editorState.getCameraHRotation();
        final float vRotation = editorState.getCameraVRotation();
        final float tDistance = editorState.getCameraTDistance();

        EXECUTOR_MANAGER.addEditorThreadTask(() -> editorAppState.updateCamera(cameraLocation, hRotation, vRotation, tDistance));
    }

    @Override
    public void notifyChangedCamera(@NotNull final Vector3f cameraLocation, final float hRotation,
                                    final float vRotation, final float targetDistance) {

        final ModelFileEditorState editorState = getEditorState();
        if (editorState == null) return;

        editorState.setCameraHRotation(hRotation);
        editorState.setCameraVRotation(vRotation);
        editorState.setCameraTDistance(targetDistance);
        editorState.setCameraLocation(cameraLocation);
    }

    /**
     * Handle the model.
     */
    private void handleObjects(@NotNull final Spatial model) {

        final ModelEditorAppState editorState = getEditorAppState();
        final Array<Geometry> geometries = ArrayFactory.newArray(Geometry.class);

        NodeUtils.addGeometry(model, geometries);

        if (!geometries.isEmpty()) {
            geometries.forEach(geometry -> {
                if (geometry.getUserData(ModelNodeTree.USER_DATA_IS_SKY) == Boolean.TRUE) {
                    editorState.addCustomSky(geometry);
                }
            });
        }

        final Array<Light> lights = ArrayFactory.newArray(Light.class);

        NodeUtils.addLight(model, lights);

        if (!lights.isEmpty()) {
            lights.forEach(editorState::addLight);
        }
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
    public void redo() {
        final EditorOperationControl operationControl = getOperationControl();
        operationControl.redo();
    }

    /**
     * Undo the last operation.
     */
    public void undo() {
        final EditorOperationControl operationControl = getOperationControl();
        operationControl.undo();
    }

    @NotNull
    @Override
    public EditorDescription getDescription() {
        return DESCRIPTION;
    }

    /**
     * @return true if needs to ignore events.
     */
    private boolean isIgnoreListeners() {
        return ignoreListeners;
    }

    /**
     * @param ignoreListeners true if needs to ignore events.
     */
    private void setIgnoreListeners(final boolean ignoreListeners) {
        this.ignoreListeners = ignoreListeners;
    }

    /**
     * @param currentModel the opened model.
     */
    private void setCurrentModel(@NotNull final Spatial currentModel) {
        this.currentModel = currentModel;
    }

    @NotNull
    @Override
    public Spatial getCurrentModel() {
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
    public void notifyAddedChild(@NotNull final Node parent, @NotNull final Spatial added, final int index) {

        final ModelEditorAppState editorState = getEditorAppState();
        final boolean isSky = added.getUserData(ModelNodeTree.USER_DATA_IS_SKY) == Boolean.TRUE;

        if (isSky) {
            editorState.addCustomSky(added);
            editorState.updateLightProbe();
        }

        final ModelNodeTree modelNodeTree = getModelNodeTree();
        modelNodeTree.notifyAdded(parent, added, index);
    }

    @Override
    public void notifyAddedChild(@NotNull final Object parent, @NotNull final Object added, final int index) {
        final ModelNodeTree modelNodeTree = getModelNodeTree();
        modelNodeTree.notifyAdded(parent, added, index);
    }

    @Override
    public void notifyAddedControl(@NotNull final Spatial spatial, @NotNull final Control control, final int index) {
        final ModelNodeTree modelNodeTree = getModelNodeTree();
        modelNodeTree.notifyAdded(spatial, control, index);
    }

    @Override
    public void notifyRemovedControl(@NotNull final Spatial spatial, @NotNull final Control control) {
        final ModelNodeTree modelNodeTree = getModelNodeTree();
        modelNodeTree.notifyRemoved(spatial, control);
    }

    @Override
    public void notifyAddedLight(@NotNull final Node parent, @NotNull final Light added, final int index) {

        final ModelEditorAppState editorState = getEditorAppState();
        editorState.addLight(added);

        final ModelNodeTree modelNodeTree = getModelNodeTree();
        modelNodeTree.notifyAdded(parent, added, index);
    }

    @Override
    public void notifyRemovedChild(@NotNull final Node parent, @NotNull final Spatial removed) {

        final ModelEditorAppState editorState = getEditorAppState();
        final boolean isSky = removed.getUserData(ModelNodeTree.USER_DATA_IS_SKY) == Boolean.TRUE;

        if (isSky) {
            editorState.removeCustomSky(removed);
            editorState.updateLightProbe();
        }

        final ModelNodeTree modelNodeTree = getModelNodeTree();
        modelNodeTree.notifyRemoved(parent, removed);
    }

    @Override
    public void notifyRemovedChild(@NotNull final Object parent, @NotNull final Object removed) {
        final ModelNodeTree modelNodeTree = getModelNodeTree();
        modelNodeTree.notifyRemoved(parent, removed);
    }

    @Override
    public void notifyRemovedLight(@NotNull final Node parent, @NotNull final Light removed) {

        final ModelEditorAppState editorState = getEditorAppState();
        editorState.removeLight(removed);

        final ModelNodeTree modelNodeTree = getModelNodeTree();
        modelNodeTree.notifyRemoved(parent, removed);
    }

    @Override
    public void notifyReplaced(@NotNull final Node parent, @NotNull final Spatial oldChild, @NotNull final Spatial newChild) {

        final ModelEditorAppState editorState = getEditorAppState();
        final Spatial currentModel = getCurrentModel();

        if (currentModel == oldChild) {
            setCurrentModel(newChild);
            editorState.openModel(newChild);
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

    @Override
    public void doSave() {

        final Path editFile = getEditFile();
        final Spatial currentModel = getCurrentModel();

        final BinaryExporter exporter = BinaryExporter.getInstance();

        try (final OutputStream out = Files.newOutputStream(editFile)) {
            exporter.save(currentModel, out);
        } catch (final IOException e) {
            LOGGER.warning(this, e);
        }

        setDirty(false);
    }

    @Override
    protected void createContent(@NotNull final StackPane root) {
        this.selectionHandler = this::processSelectFromTree;

        editorAreaPane = new Pane();

        modelNodeTree = new ModelNodeTree(selectionHandler, this);
        modelPropertyEditor = new ModelPropertyEditor(this);

        final SplitPane parameterSplitContainer = new SplitPane(modelNodeTree, modelPropertyEditor);
        parameterSplitContainer.setId(CSSIds.FILE_EDITOR_TOOL_SPLIT_PANE);
        parameterSplitContainer.prefHeightProperty().bind(root.heightProperty());
        parameterSplitContainer.prefWidthProperty().bind(root.widthProperty());

        mainSplitContainer = new EditorToolSplitPane(JFX_APPLICATION.getScene(), root);
        mainSplitContainer.setId(CSSIds.FILE_EDITOR_MAIN_SPLIT_PANE);

        final EditorToolComponent editorToolComponent = new EditorToolComponent(mainSplitContainer, 1);
        editorToolComponent.prefHeightProperty().bind(root.heightProperty());
        editorToolComponent.addComponent(parameterSplitContainer, Messages.MODEL_FILE_EDITOR_TOOL_OBJECTS);

        mainSplitContainer.initFor(editorToolComponent, editorAreaPane);

        FXUtils.addToPane(mainSplitContainer, root);

        root.heightProperty().addListener((observableValue, oldValue, newValue) -> calcVSplitSize(parameterSplitContainer));
    }

    @Override
    public boolean isInside(final double sceneX, final double sceneY) {
        final Point2D point2D = editorAreaPane.sceneToLocal(sceneX, sceneY);
        return editorAreaPane.contains(point2D);
    }

    private static void calcVSplitSize(@NotNull final SplitPane splitContainer) {
        splitContainer.setDividerPosition(0, 0.3);
    }

    /**
     * @return the operation control.
     */
    @NotNull
    private EditorOperationControl getOperationControl() {
        return operationControl;
    }

    /**
     * Handle the selected object.
     */
    public void notifySelected(@Nullable final Object object) {
        final ModelNodeTree modelNodeTree = getModelNodeTree();
        modelNodeTree.select(object);
    }

    /**
     * Handle the selected object from the Tree.
     */
    public void processSelectFromTree(@Nullable final Object object) {

        Object parent = null;
        Object element = null;

        if (object instanceof ModelNode<?>) {
            final ModelNode modelNode = (ModelNode) object;
            final ModelNode parentNode = modelNode.getParent();
            parent = parentNode == null ? null : parentNode.getElement();
            element = modelNode.getElement();
        }

        Spatial spatial = null;

        if (element instanceof Spatial) {
            spatial = (Spatial) element;
            parent = spatial.getParent();
        }

        final Array<Spatial> spatials = ArrayFactory.newArray(Spatial.class);
        if (spatial != null) spatials.add(spatial);

        final ModelEditorAppState editorState = getEditorAppState();
        editorState.updateSelection(spatials);

        final ModelPropertyEditor modelPropertyEditor = getModelPropertyEditor();
        modelPropertyEditor.buildFor(element, parent);
    }

    @Override
    protected boolean needToolbar() {
        return true;
    }

    @Override
    protected void createToolbar(@NotNull final HBox container) {
        FXUtils.addToPane(createSaveAction(), container);

        lightButton = new ToggleButton();
        lightButton.setGraphic(new ImageView(Icons.LIGHT_16));
        lightButton.setSelected(true);
        lightButton.selectedProperty().addListener((observable, oldValue, newValue) -> changeLight(newValue));

        selectionButton = new ToggleButton();
        selectionButton.setGraphic(new ImageView(Icons.CUBE_16));
        selectionButton.setSelected(true);
        selectionButton.selectedProperty().addListener((observable, oldValue, newValue) -> changeSelectionVisible(newValue));

        gridButton = new ToggleButton();
        gridButton.setGraphic(new ImageView(Icons.PLANE_16));
        gridButton.setSelected(true);
        gridButton.selectedProperty().addListener((observable, oldValue, newValue) -> changeGridVisible(newValue));

        moveToolButton = new ToggleButton();
        moveToolButton.setGraphic(new ImageView(Icons.MOVE_16));
        moveToolButton.setSelected(true);
        moveToolButton.selectedProperty().addListener((observable, oldValue, newValue) -> updateTransformTool(TransformType.MOVE_TOOL, newValue));

        rotationToolButton = new ToggleButton();
        rotationToolButton.setGraphic(new ImageView(Icons.ROTATION_16));
        rotationToolButton.selectedProperty().addListener((observable, oldValue, newValue) -> updateTransformTool(TransformType.ROTATE_TOOL, newValue));

        scaleToolButton = new ToggleButton();
        scaleToolButton.setGraphic(new ImageView(Icons.SCALE_16));
        scaleToolButton.selectedProperty().addListener((observable, oldValue, newValue) -> updateTransformTool(TransformType.SCALE_TOOL, newValue));

        final Label fastSkyLabel = new Label(Messages.MODEL_FILE_EDITOR_FAST_SKY + ":");

        fastSkyComboBox = new ComboBox<>();
        fastSkyComboBox.setId(CSSIds.MATERIAL_FILE_EDITOR_TOOLBAR_BOX);
        fastSkyComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> changeFastSky(newValue));

        final ObservableList<String> skyItems = fastSkyComboBox.getItems();

        FAST_SKY_LIST.forEach(skyItems::add);

        FXUtils.addClassTo(lightButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(lightButton, CSSClasses.FILE_EDITOR_TOOLBAR_BUTTON);
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
        FXUtils.addClassTo(fastSkyLabel, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(fastSkyComboBox, CSSClasses.SPECIAL_FONT_13);

        FXUtils.addToPane(lightButton, container);
        FXUtils.addToPane(selectionButton, container);
        FXUtils.addToPane(gridButton, container);
        FXUtils.addToPane(moveToolButton, container);
        FXUtils.addToPane(rotationToolButton, container);
        FXUtils.addToPane(scaleToolButton, container);
        FXUtils.addToPane(fastSkyLabel, container);
        FXUtils.addToPane(fastSkyComboBox, container);

        HBox.setMargin(lightButton, LIGHT_BUTTON_OFFSET);
        HBox.setMargin(fastSkyLabel, FAST_SKY_LABEL_OFFSET);
    }

    /**
     * @return the scaling tool toggle.
     */
    @NotNull
    private ToggleButton getScaleToolButton() {
        return scaleToolButton;
    }

    /**
     * @return the move tool toggle.
     */
    @NotNull
    private ToggleButton getMoveToolButton() {
        return moveToolButton;
    }

    /**
     * @return the rotation tool toggle.
     */
    @NotNull
    private ToggleButton getRotationToolButton() {
        return rotationToolButton;
    }

    /**
     * Switch transformation mode.
     */
    private void updateTransformTool(final TransformType transformType, final Boolean newValue) {
        if (newValue != Boolean.TRUE) return;

        final ToggleButton scaleToolButton = getScaleToolButton();
        final ToggleButton moveToolButton = getMoveToolButton();
        final ToggleButton rotationToolButton = getRotationToolButton();

        final ModelEditorAppState editorAppState = getEditorAppState();
        final ModelFileEditorState editorState = getEditorState();

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

    /**
     * Handle changing select visibility.
     */
    private void changeSelectionVisible(@NotNull final Boolean newValue) {
        if (isIgnoreListeners()) return;

        final ModelEditorAppState editorAppState = getEditorAppState();
        editorAppState.updateShowSelection(newValue);

        final ModelFileEditorState editorState = getEditorState();
        if (editorState != null) editorState.setEnableSelection(newValue);
    }

    /**
     * Handle changing grid visibility.
     */
    private void changeGridVisible(@NotNull final Boolean newValue) {
        if (isIgnoreListeners()) return;

        final ModelEditorAppState editorAppState = getEditorAppState();
        editorAppState.updateShowGrid(newValue);

        final ModelFileEditorState editorState = getEditorState();
        if (editorState != null) editorState.setEnableGrid(newValue);
    }

    /**
     * Handle changing a sky.
     */
    private void changeFastSky(@NotNull final String newSky) {
        if (isIgnoreListeners()) return;

        final ModelEditorAppState editorAppState = getEditorAppState();

        if (NO_FAST_SKY.equals(newSky)) {
            editorAppState.changeFastSky(null);
            final ModelFileEditorState editorState = getEditorState();
            if (editorState != null) editorState.setSkyType(0);
            return;
        }

        final AssetManager assetManager = EDITOR.getAssetManager();

        final TextureKey key = new TextureKey(newSky, true);
        key.setGenerateMips(false);

        final Texture texture = assetManager.loadTexture(key);
        final Spatial newFastSky = SkyFactory.createSky(assetManager, texture, EnvMapType.EquirectMap);

        editorAppState.changeFastSky(newFastSky);

        final SingleSelectionModel<String> selectionModel = fastSkyComboBox.getSelectionModel();
        final int selectedIndex = selectionModel.getSelectedIndex();

        final ModelFileEditorState editorState = getEditorState();
        if (editorState != null) editorState.setSkyType(selectedIndex);
    }

    /**
     * Handle changing camera light visibility.
     */
    private void changeLight(@NotNull final Boolean newValue) {
        if (isIgnoreListeners()) return;

        final ModelEditorAppState editorAppState = getEditorAppState();
        editorAppState.updateLightEnabled(newValue);

        if (editorState != null) editorState.setEnableLight(newValue);
    }

    /**
     * Notify about transformed the object.
     */
    public void notifyTransformed(final Spatial spatial) {
        EXECUTOR_MANAGER.addFXTask(() -> notifyTransformedImpl(spatial));
    }

    /**
     * Notify about transformed the object.
     */
    private void notifyTransformedImpl(final Spatial spatial) {
        final ModelPropertyEditor modelPropertyEditor = getModelPropertyEditor();
        modelPropertyEditor.syncFor(spatial);
    }

    @Override
    public String toString() {
        return "ModelFileEditor{" +
                "operationControl=" + operationControl +
                ", changeCounter=" + changeCounter +
                ", currentModel=" + currentModel +
                ", ignoreListeners=" + ignoreListeners +
                "} " + super.toString();
    }
}
