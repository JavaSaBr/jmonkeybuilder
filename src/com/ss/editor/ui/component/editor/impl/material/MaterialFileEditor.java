package com.ss.editor.ui.component.editor.impl.material;

import static com.ss.editor.Messages.MATERIAL_EDITOR_NAME;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.MaterialDef;
import com.jme3.renderer.queue.RenderQueue;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.manager.ResourceManager;
import com.ss.editor.model.undo.EditorOperation;
import com.ss.editor.model.undo.EditorOperationControl;
import com.ss.editor.model.undo.UndoableEditor;
import com.ss.editor.model.undo.editor.MaterialChangeConsumer;
import com.ss.editor.serializer.MaterialSerializer;
import com.ss.editor.state.editor.impl.material.MaterialEditorState;
import com.ss.editor.state.editor.impl.material.MaterialEditorState.ModelType;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.editor.EditorDescription;
import com.ss.editor.ui.component.editor.impl.AbstractFileEditor;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.event.impl.FileChangedEvent;
import com.ss.editor.util.EditorUtil;
import com.ss.editor.util.MaterialUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Accordion;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;
import rlib.util.array.Array;

/**
 * The implementation of the Editor for editing materials.
 *
 * @author JavaSaBr.
 */
public class MaterialFileEditor extends AbstractFileEditor<StackPane> implements UndoableEditor, MaterialChangeConsumer {

    public static final EditorDescription DESCRIPTION = new EditorDescription();

    static {
        DESCRIPTION.setConstructor(MaterialFileEditor::new);
        DESCRIPTION.setEditorName(MATERIAL_EDITOR_NAME);
        DESCRIPTION.setEditorId(MaterialFileEditor.class.getName());
        DESCRIPTION.addExtension(FileExtensions.JME_MATERIAL);
    }

    private static final ResourceManager RESOURCE_MANAGER = ResourceManager.getInstance();

    private static final RenderQueue.Bucket[] BUCKETS = RenderQueue.Bucket.values();

    private static final Insets SMALL_OFFSET = new Insets(0, 0, 0, 3);
    private static final Insets BIG_OFFSET = new Insets(0, 0, 0, 6);

    /**
     * The handler of file changing.
     */
    private final EventHandler<Event> fileChangedHandler;

    /**
     * 3D part of this editor.
     */
    private final MaterialEditorState editorState;

    /**
     * The operation control.
     */
    private final EditorOperationControl operationControl;

    /**
     * The changes counter.
     */
    private final AtomicInteger changeCounter;

    /**
     * The textures editor.
     */
    private MaterialTexturesComponent materialTexturesComponent;

    /**
     * The colors editor.
     */
    private MaterialColorsComponent materialColorsComponent;

    /**
     * The other parameters editor.
     */
    private MaterialOtherParamsComponent materialOtherParamsComponent;

    /**
     * The render settings editor.
     */
    private MaterialRenderParamsComponent materialRenderParamsComponent;

    /**
     * The current editing material.
     */
    private Material currentMaterial;

    /**
     * The button for using a cube.
     */
    private ToggleButton cubeButton;

    /**
     * The button for using a sphere.
     */
    private ToggleButton sphereButton;

    /**
     * The button for using a plane.
     */
    private ToggleButton planeButton;

    /**
     * The button for using a light.
     */
    private ToggleButton lightButton;

    /**
     * The list of RenderQueue.Bucket.
     */
    private ComboBox<RenderQueue.Bucket> bucketComboBox;

    /**
     * The list of material definitions.
     */
    private ComboBox<String> materialDefinitionBox;

    /**
     * The change handler.
     */
    private Consumer<EditorOperation> changeHandler;

    /**
     * The flag for ignoring listeners.
     */
    private boolean ignoreListeners;

    public MaterialFileEditor() {
        this.editorState = new MaterialEditorState(this);
        this.fileChangedHandler = event -> processChangedFile((FileChangedEvent) event);
        this.operationControl = new EditorOperationControl(this);
        this.changeCounter = new AtomicInteger();
        addEditorState(editorState);
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

    /**
     * Handle changed file.
     */
    private void processChangedFile(final FileChangedEvent event) {

        final Path file = event.getFile();
        if (!MaterialUtils.isShaderFile(file)) return;

        final Material currentMaterial = getCurrentMaterial();
        if (!MaterialUtils.containsShader(currentMaterial, file)) return;

        final AssetManager assetManager = EDITOR.getAssetManager();
        assetManager.clearCache();

        final Material newMaterial = assetManager.loadMaterial(currentMaterial.getAssetName());

        MaterialUtils.updateTo(newMaterial, currentMaterial);

        reload(newMaterial);

        notifyFileChanged();
    }

    /**
     * @param ignoreListeners the flag for ignoring listeners.
     */
    private void setIgnoreListeners(final boolean ignoreListeners) {
        this.ignoreListeners = ignoreListeners;
    }

    /**
     * @return the flag for ignoring listeners.
     */
    private boolean isIgnoreListeners() {
        return ignoreListeners;
    }

    /**
     * @return the operation control.
     */
    private EditorOperationControl getOperationControl() {
        return operationControl;
    }

    /**
     * Execute the operation.
     */
    private void handleChanges(final EditorOperation operation) {
        final EditorOperationControl operationControl = getOperationControl();
        operationControl.execute(operation);
    }

    @Override
    public void doSave() {
        super.doSave();

        final Material currentMaterial = getCurrentMaterial();
        final String content = MaterialSerializer.serializeToString(currentMaterial);

        try (final PrintWriter out = new PrintWriter(Files.newOutputStream(getEditFile()))) {
            out.print(content);
        } catch (final IOException e) {
            LOGGER.warning(this, e);
        }

        setDirty(false);
        notifyFileChanged();
    }

    @NotNull
    @Override
    protected StackPane createRoot() {
        return new StackPane();
    }

    @Override
    protected void processKeyReleased(@NotNull final KeyEvent event) {
        super.processKeyReleased(event);

        if (!event.isControlDown()) return;

        final KeyCode code = event.getCode();

        if (code == KeyCode.S && isDirty()) {
            doSave();
        } else if (code == KeyCode.Z) {
            undo();
        } else if (code == KeyCode.Y) {
            redo();
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

    @Override
    protected void createContent(@NotNull final StackPane root) {
        root.setAlignment(Pos.TOP_RIGHT);

        final Accordion accordion = new Accordion();

        final VBox parameterContainer = new VBox();
        parameterContainer.setId(CSSIds.MATERIAL_FILE_EDITOR_PARAMETER_CONTAINER);

        changeHandler = this::handleChanges;

        materialTexturesComponent = new MaterialTexturesComponent(changeHandler);
        materialColorsComponent = new MaterialColorsComponent(changeHandler);
        materialOtherParamsComponent = new MaterialOtherParamsComponent(changeHandler);
        materialRenderParamsComponent = new MaterialRenderParamsComponent(changeHandler);

        final ObservableList<TitledPane> panes = accordion.getPanes();
        panes.add(materialTexturesComponent);
        panes.add(materialColorsComponent);
        panes.add(materialOtherParamsComponent);
        panes.add(materialRenderParamsComponent);

        FXUtils.addToPane(accordion, parameterContainer);
        FXUtils.addToPane(parameterContainer, root);

        accordion.setExpandedPane(materialTexturesComponent);
        accordion.prefHeightProperty().bind(parameterContainer.heightProperty());
    }

    /**
     * @return the textures editor.
     */
    private MaterialTexturesComponent getMaterialTexturesComponent() {
        return materialTexturesComponent;
    }

    /**
     * @return the colors editor.
     */
    private MaterialColorsComponent getMaterialColorsComponent() {
        return materialColorsComponent;
    }

    /**
     * @return the other parameters editor.
     */
    private MaterialOtherParamsComponent getMaterialOtherParamsComponent() {
        return materialOtherParamsComponent;
    }

    /**
     * @return the render settings editor.
     */
    private MaterialRenderParamsComponent getMaterialRenderParamsComponent() {
        return materialRenderParamsComponent;
    }

    /**
     * @return the handler of file changing.
     */
    private EventHandler<Event> getFileChangedHandler() {
        return fileChangedHandler;
    }

    @Override
    public void openFile(@NotNull final Path file) {
        super.openFile(file);

        final Path assetFile = EditorUtil.getAssetFile(file);
        final String assetPath = EditorUtil.toAssetPath(assetFile);

        final AssetManager assetManager = EDITOR.getAssetManager();
        assetManager.clearCache();

        final Material material = assetManager.loadMaterial(assetPath);

        final MaterialEditorState editorState = getEditorState();
        editorState.changeMode(ModelType.BOX);

        reload(material);

        FX_EVENT_MANAGER.addEventHandler(FileChangedEvent.EVENT_TYPE, getFileChangedHandler());
    }

    /**
     * Reload the material.
     */
    private void reload(final Material material) {
        setCurrentMaterial(material);

        setIgnoreListeners(true);
        try {

            final MaterialEditorState editorState = getEditorState();
            editorState.updateMaterial(material);

            final ToggleButton cubeButton = getCubeButton();
            cubeButton.setSelected(true);

            final MaterialTexturesComponent materialTexturesComponent = getMaterialTexturesComponent();
            materialTexturesComponent.buildFor(material);

            final MaterialColorsComponent materialColorsComponent = getMaterialColorsComponent();
            materialColorsComponent.buildFor(material);

            final MaterialOtherParamsComponent materialOtherParamsComponent = getMaterialOtherParamsComponent();
            materialOtherParamsComponent.buildFor(material);

            final MaterialRenderParamsComponent materialRenderParamsComponent = getMaterialRenderParamsComponent();
            materialRenderParamsComponent.buildFor(material);

            final ComboBox<String> materialDefinitionBox = getMaterialDefinitionBox();
            final ObservableList<String> items = materialDefinitionBox.getItems();

            final Array<String> availableMaterialDefinitions = RESOURCE_MANAGER.getAvailableMaterialDefinitions();
            availableMaterialDefinitions.forEach(items::add);

            final MaterialDef materialDef = material.getMaterialDef();
            materialDefinitionBox.getSelectionModel().select(materialDef.getAssetName());

        } finally {
            setIgnoreListeners(false);
        }
    }

    @Override
    public void notifyClosed() {
        FX_EVENT_MANAGER.removeEventHandler(FileChangedEvent.EVENT_TYPE, getFileChangedHandler());
    }

    /**
     * @return the list of material definitions.
     */
    private ComboBox<String> getMaterialDefinitionBox() {
        return materialDefinitionBox;
    }

    @Override
    protected boolean needToolbar() {
        return true;
    }

    @Override
    protected void createToolbar(@NotNull final HBox container) {

        cubeButton = new ToggleButton();
        cubeButton.setGraphic(new ImageView(Icons.CUBE_16));
        cubeButton.selectedProperty().addListener((observable, oldValue, newValue) -> changeModelType(cubeButton, newValue));

        sphereButton = new ToggleButton();
        sphereButton.setGraphic(new ImageView(Icons.SPHERE_16));
        sphereButton.selectedProperty().addListener((observable, oldValue, newValue) -> changeModelType(sphereButton, newValue));

        planeButton = new ToggleButton();
        planeButton.setGraphic(new ImageView(Icons.PLANE_16));
        planeButton.selectedProperty().addListener((observable, oldValue, newValue) -> changeModelType(planeButton, newValue));

        lightButton = new ToggleButton();
        lightButton.setGraphic(new ImageView(Icons.LIGHT_16));
        lightButton.setSelected(true);
        lightButton.selectedProperty().addListener((observable, oldValue, newValue) -> changeLight(newValue));

        final Label materialDefinitionLabel = new Label(Messages.MATERIAL_EDITOR_MATERIAL_TYPE_LABEL + ":");
        materialDefinitionLabel.setId(CSSIds.MATERIAL_FILE_EDITOR_TOOLBAR_LABEL);

        materialDefinitionBox = new ComboBox<>();
        materialDefinitionBox.setId(CSSIds.MATERIAL_FILE_EDITOR_TOOLBAR_BOX);
        materialDefinitionBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> changeType(newValue));

        final Label bucketLabel = new Label(Messages.MATERIAL_FILE_EDITOR_BUCKET_TYPE_LABEL + ":");
        bucketLabel.setId(CSSIds.MATERIAL_FILE_EDITOR_TOOLBAR_LABEL);

        bucketComboBox = new ComboBox<>(FXCollections.observableArrayList(BUCKETS));
        bucketComboBox.setId(CSSIds.MATERIAL_FILE_EDITOR_TOOLBAR_SMALL_BOX);
        bucketComboBox.getSelectionModel().select(RenderQueue.Bucket.Inherit);
        bucketComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> changeBucketType(newValue));

        FXUtils.addToPane(createSaveAction(), container);
        FXUtils.addToPane(cubeButton, container);
        FXUtils.addToPane(sphereButton, container);
        FXUtils.addToPane(planeButton, container);
        FXUtils.addToPane(lightButton, container);
        FXUtils.addToPane(materialDefinitionLabel, container);
        FXUtils.addToPane(materialDefinitionBox, container);
        FXUtils.addToPane(bucketLabel, container);
        FXUtils.addToPane(bucketComboBox, container);

        FXUtils.addClassTo(cubeButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(cubeButton, CSSClasses.FILE_EDITOR_TOOLBAR_BUTTON);
        FXUtils.addClassTo(sphereButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(sphereButton, CSSClasses.FILE_EDITOR_TOOLBAR_BUTTON);
        FXUtils.addClassTo(planeButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(planeButton, CSSClasses.FILE_EDITOR_TOOLBAR_BUTTON);
        FXUtils.addClassTo(lightButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(lightButton, CSSClasses.FILE_EDITOR_TOOLBAR_BUTTON);
        FXUtils.addClassTo(materialDefinitionLabel, CSSClasses.MAIN_FONT_12);
        FXUtils.addClassTo(materialDefinitionBox, CSSClasses.MAIN_FONT_12);
        FXUtils.addClassTo(bucketLabel, CSSClasses.MAIN_FONT_12);
        FXUtils.addClassTo(bucketComboBox, CSSClasses.MAIN_FONT_12);

        HBox.setMargin(cubeButton, SMALL_OFFSET);
        HBox.setMargin(sphereButton, SMALL_OFFSET);
        HBox.setMargin(planeButton, SMALL_OFFSET);
        HBox.setMargin(lightButton, BIG_OFFSET);
        HBox.setMargin(materialDefinitionLabel, BIG_OFFSET);
        HBox.setMargin(bucketLabel, BIG_OFFSET);
    }

    /**
     * Handle changing the bucket type.
     */
    private void changeBucketType(final RenderQueue.Bucket newValue) {
        final MaterialEditorState editorState = getEditorState();
        editorState.changeBucketType(newValue);
    }

    /**
     * Handle changing the type.
     */
    private void changeType(final String newType) {
        if (isIgnoreListeners()) return;
        processChangeTypeImpl(newType);
    }

    /**
     * Handle changing the type.
     */
    private void processChangeTypeImpl(final String newType) {

        final AssetManager assetManager = EDITOR.getAssetManager();
        assetManager.clearCache();

        final Material newMaterial = new Material(assetManager, newType);

        MaterialUtils.migrateTo(newMaterial, getCurrentMaterial());

        final EditorOperationControl operationControl = getOperationControl();
        operationControl.clear();

        incrementChange();

        reload(newMaterial);
    }

    /**
     * Handle changing the light enabling.
     */
    private void changeLight(final Boolean newValue) {
        final MaterialEditorState editorState = getEditorState();
        editorState.updateLightEnabled(newValue);
    }

    /**
     * @return the button for using a cube.
     */
    private ToggleButton getCubeButton() {
        return cubeButton;
    }

    /**
     * @return the button for using a plane.
     */
    private ToggleButton getPlaneButton() {
        return planeButton;
    }

    /**
     * @return the button for using a sphere.
     */
    private ToggleButton getSphereButton() {
        return sphereButton;
    }

    /**
     * Handle changing model type.
     */
    private void changeModelType(final ToggleButton button, final Boolean newValue) {
        if (newValue == Boolean.FALSE) return;

        final MaterialEditorState editorState = getEditorState();

        final ToggleButton cubeButton = getCubeButton();
        final ToggleButton sphereButton = getSphereButton();
        final ToggleButton planeButton = getPlaneButton();

        if (button == cubeButton) {
            sphereButton.setSelected(false);
            planeButton.setSelected(false);
            sphereButton.setDisable(false);
            planeButton.setDisable(false);
            cubeButton.setDisable(true);
            editorState.changeMode(ModelType.BOX);
        } else if (button == sphereButton) {
            cubeButton.setSelected(false);
            planeButton.setSelected(false);
            cubeButton.setDisable(false);
            planeButton.setDisable(false);
            sphereButton.setDisable(true);
            editorState.changeMode(ModelType.SPHERE);
        } else if (button == planeButton) {
            sphereButton.setSelected(false);
            cubeButton.setSelected(false);
            sphereButton.setDisable(false);
            cubeButton.setDisable(false);
            planeButton.setDisable(true);
            editorState.changeMode(ModelType.QUAD);
        }
    }

    @Override
    public Material getCurrentMaterial() {
        return currentMaterial;
    }

    @Override
    public void notifyChangeParam(final String paramName) {

        final MaterialOtherParamsComponent otherParamsComponent = getMaterialOtherParamsComponent();
        otherParamsComponent.updateParam(paramName);

        final MaterialColorsComponent colorsComponent = getMaterialColorsComponent();
        colorsComponent.updateParam(paramName);

        final MaterialTexturesComponent texturesComponent = getMaterialTexturesComponent();
        texturesComponent.updateParam(paramName);
    }

    @Override
    public void notifyChangedRenderState() {
        final MaterialRenderParamsComponent renderParamsComponent = getMaterialRenderParamsComponent();
        renderParamsComponent.buildFor(getCurrentMaterial());
    }

    /**
     * @param currentMaterial the current editing material.
     */
    private void setCurrentMaterial(final Material currentMaterial) {
        this.currentMaterial = currentMaterial;
    }

    /**
     * @return 3D part of this editor.
     */
    private MaterialEditorState getEditorState() {
        return editorState;
    }

    @NotNull
    @Override
    public EditorDescription getDescription() {
        return DESCRIPTION;
    }
}
