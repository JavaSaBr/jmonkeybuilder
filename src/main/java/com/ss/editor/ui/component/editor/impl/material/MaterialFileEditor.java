package com.ss.editor.ui.component.editor.impl.material;

import static com.jme3.renderer.queue.RenderQueue.Bucket.Inherit;
import static com.jme3.renderer.queue.RenderQueue.Bucket.values;
import static com.ss.editor.Messages.MATERIAL_EDITOR_NAME;
import static com.ss.editor.util.EditorUtil.getAssetFile;
import static com.ss.editor.util.EditorUtil.toAssetPath;
import static com.ss.editor.util.MaterialUtils.updateMaterialIdNeed;
import static com.ss.rlib.util.ObjectUtils.notNull;
import static javafx.collections.FXCollections.observableArrayList;
import com.jme3.asset.AssetManager;
import com.jme3.asset.MaterialKey;
import com.jme3.material.Material;
import com.jme3.material.MaterialDef;
import com.jme3.material.RenderState;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.manager.ResourceManager;
import com.ss.editor.model.undo.EditorOperationControl;
import com.ss.editor.model.undo.editor.MaterialChangeConsumer;
import com.ss.editor.plugin.api.editor.Base3DFileEditor;
import com.ss.editor.serializer.MaterialSerializer;
import com.ss.editor.state.editor.impl.material.MaterialEditor3DState;
import com.ss.editor.state.editor.impl.material.MaterialEditor3DState.ModelType;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.editor.EditorDescription;
import com.ss.editor.ui.component.editor.state.EditorState;
import com.ss.editor.ui.component.editor.state.impl.MaterialFileEditorState;
import com.ss.editor.ui.component.split.pane.EditorToolSplitPane;
import com.ss.editor.ui.component.tab.ScrollableEditorToolComponent;
import com.ss.editor.ui.control.material.Texture2DMaterialParamControl;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.event.impl.FileChangedEvent;
import com.ss.editor.ui.util.DynamicIconSupport;
import com.ss.editor.ui.util.UIUtils;
import com.ss.editor.util.MaterialUtils;
import com.ss.rlib.ui.util.FXUtils;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

/**
 * The implementation of the Editor to edit materials.
 *
 * @author JavaSaBr
 */
public class MaterialFileEditor extends Base3DFileEditor<MaterialEditor3DState, MaterialFileEditorState> implements
        MaterialChangeConsumer {

    /**
     * The constant DESCRIPTION.
     */
    @NotNull
    public static final EditorDescription DESCRIPTION = new EditorDescription();

    static {
        DESCRIPTION.setConstructor(MaterialFileEditor::new);
        DESCRIPTION.setEditorName(MATERIAL_EDITOR_NAME);
        DESCRIPTION.setEditorId(MaterialFileEditor.class.getSimpleName());
        DESCRIPTION.addExtension(FileExtensions.JME_MATERIAL);
    }

    /**
     * The default flag of enabling light.
     */
    public static final boolean DEFAULT_LIGHT_ENABLED = true;

    @NotNull
    private static final ResourceManager RESOURCE_MANAGER = ResourceManager.getInstance();

    @NotNull
    private static final ObservableList<RenderQueue.Bucket> BUCKETS = observableArrayList(values());

    /**
     * The textures editor.
     */
    @Nullable
    private MaterialTexturesComponent materialTexturesComponent;

    /**
     * The colors editor.
     */
    @Nullable
    private MaterialColorsComponent materialColorsComponent;

    /**
     * The other parameters editor.
     */
    @Nullable
    private MaterialOtherParamsComponent materialOtherParamsComponent;

    /**
     * The render settings editor.
     */
    @Nullable
    private MaterialRenderParamsComponent materialRenderParamsComponent;

    /**
     * The main split container.
     */
    @Nullable
    private EditorToolSplitPane mainSplitContainer;

    /**
     * Editor tool component.
     */
    @Nullable
    private ScrollableEditorToolComponent editorToolComponent;

    /**
     * The current editing material.
     */
    @Nullable
    private Material currentMaterial;

    /**
     * The button to use a cube.
     */
    @Nullable
    private ToggleButton cubeButton;

    /**
     * The button to use a sphere.
     */
    @Nullable
    private ToggleButton sphereButton;

    /**
     * The button to use a plane.
     */
    @Nullable
    private ToggleButton planeButton;

    /**
     * The button to use a light.
     */
    @Nullable
    private ToggleButton lightButton;

    /**
     * The list of RenderQueue.Bucket.
     */
    @Nullable
    private ComboBox<RenderQueue.Bucket> bucketComboBox;

    /**
     * The list of material definitions.
     */
    @Nullable
    private ComboBox<String> materialDefinitionBox;

    /**
     * The pane of editor area.
     */
    @Nullable
    private BorderPane editorAreaPane;

    private MaterialFileEditor() {
        super();
    }

    @Override
    @FXThread
    protected @NotNull MaterialEditor3DState create3DEditorState() {
        return new MaterialEditor3DState(this);
    }

    @Override
    @FXThread
    protected void processChangedFile(@NotNull final FileChangedEvent event) {
        super.processChangedFile(event);

        final Material currentMaterial = getCurrentMaterial();
        final Path file = event.getFile();

        EXECUTOR_MANAGER.addJMETask(() -> {
            final Material newMaterial = updateMaterialIdNeed(file, currentMaterial);
            if (newMaterial != null) {
                EXECUTOR_MANAGER.addFXTask(() -> reload(newMaterial));
            }
        });
    }

    @Override
    @BackgroundThread
    public void doSave(@NotNull final Path toStore) {
        super.doSave(toStore);

        final Material currentMaterial = getCurrentMaterial();
        final String content = MaterialSerializer.serializeToString(currentMaterial);

        try (final PrintWriter out = new PrintWriter(Files.newOutputStream(toStore))) {
            out.print(content);
        } catch (final IOException e) {
            LOGGER.warning(this, e);
        }
    }

    @Override
    @FXThread
    protected void handleExternalChanges() {
        super.handleExternalChanges();

        final Path assetFile = notNull(getAssetFile(getEditFile()));
        final MaterialKey materialKey = new MaterialKey(toAssetPath(assetFile));

        final AssetManager assetManager = EDITOR.getAssetManager();
        final Material material = assetManager.loadAsset(materialKey);

        reload(material);

        final EditorOperationControl operationControl = getOperationControl();
        operationControl.clear();
    }

    @Override
    @FXThread
    protected boolean handleKeyActionImpl(@NotNull final KeyCode keyCode, final boolean isPressed,
                                          final boolean isControlDown, final boolean isButtonMiddleDown) {

        if (isPressed && isControlDown && keyCode == KeyCode.Z) {
            undo();
            return true;
        } else if (isPressed && isControlDown && keyCode == KeyCode.Y) {
            redo();
            return true;
        } else if (isPressed && keyCode == KeyCode.C && !isControlDown && !isButtonMiddleDown) {
            final ToggleButton cubeButton = getCubeButton();
            cubeButton.setSelected(true);
            return true;
        } else if (isPressed && keyCode == KeyCode.S && !isControlDown && !isButtonMiddleDown) {
            final ToggleButton sphereButton = getSphereButton();
            sphereButton.setSelected(true);
            return true;
        } else if (isPressed && keyCode == KeyCode.P && !isControlDown && !isButtonMiddleDown) {
            final ToggleButton planeButton = getPlaneButton();
            planeButton.setSelected(true);
            return true;
        } else if (isPressed && keyCode == KeyCode.L && !isControlDown && !isButtonMiddleDown) {
            final ToggleButton lightButton = getLightButton();
            lightButton.setSelected(!lightButton.isSelected());
            return true;
        }

        return super.handleKeyActionImpl(keyCode, isPressed, isControlDown, isButtonMiddleDown);
    }

    @Override
    @FXThread
    public @Nullable BorderPane get3DArea() {
        return editorAreaPane;
    }

    @Override
    @FXThread
    protected void createContent(@NotNull final StackPane root) {
        editorAreaPane = new BorderPane();
        editorAreaPane.setOnMousePressed(event -> editorAreaPane.requestFocus());
        editorAreaPane.setOnDragOver(this::dragOver);
        editorAreaPane.setOnDragDropped(this::dragDropped);
        editorAreaPane.setOnKeyReleased(Event::consume);
        editorAreaPane.setOnKeyPressed(Event::consume);

        materialTexturesComponent = new MaterialTexturesComponent(this);
        materialColorsComponent = new MaterialColorsComponent(this);
        materialRenderParamsComponent = new MaterialRenderParamsComponent(this);
        materialOtherParamsComponent = new MaterialOtherParamsComponent(this);

        mainSplitContainer = new EditorToolSplitPane(JFX_APPLICATION.getScene(), root);

        editorToolComponent = new ScrollableEditorToolComponent(mainSplitContainer, 1);
        editorToolComponent.prefHeightProperty().bind(root.heightProperty());
        editorToolComponent.addComponent(materialTexturesComponent, Messages.MATERIAL_FILE_EDITOR_TEXTURES_COMPONENT_TITLE);
        editorToolComponent.addComponent(materialColorsComponent, Messages.MATERIAL_FILE_EDITOR_COLORS_COMPONENT_TITLE);
        editorToolComponent.addComponent(materialRenderParamsComponent, Messages.MATERIAL_FILE_EDITOR_RENDER_PARAMS_COMPONENT_TITLE);
        editorToolComponent.addComponent(materialOtherParamsComponent, Messages.MATERIAL_FILE_EDITOR_OTHER_COMPONENT_TITLE);
        editorToolComponent.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            final MaterialFileEditorState editorState = getEditorState();
            if (editorState != null) editorState.setOpenedTool(newValue.intValue());
        });

        mainSplitContainer.initFor(editorToolComponent, editorAreaPane);

        FXUtils.addToPane(mainSplitContainer, root);
        FXUtils.addClassTo(mainSplitContainer, CSSClasses.FILE_EDITOR_MAIN_SPLIT_PANE);
    }

    /**
     * Handle dropped texture.
     */
    private void dragDropped(@NotNull final DragEvent dragEvent) {
        UIUtils.handleDroppedFile(dragEvent, FileExtensions.TEXTURE_EXTENSIONS, this,
                dragEvent, this::applyTexture);
    }

    /**
     * Try to apply dropped texture.
     *
     * @param editor    the editor.
     * @param dragEvent the drag event.
     * @param path      the path to the texture.
     */
    private void applyTexture(@NotNull final MaterialFileEditor editor, @NotNull final DragEvent dragEvent,
                              @NotNull final Path path) {

        final String textureName = path.getFileName().toString();
        final int textureType = MaterialUtils.getPossibleTextureType(textureName);

        if (textureType == 0) {
            return;
        }

        final String[] paramNames = MaterialUtils.getPossibleParamNames(textureType);
        final MaterialTexturesComponent component = editor.materialTexturesComponent;

        for (final String paramName : paramNames) {
            final Texture2DMaterialParamControl control = component.findControl(paramName, Texture2DMaterialParamControl.class);
            if (control == null) {
                continue;
            }

            control.addTexture(path);
            break;
        }
    }

    /**
     * Handle drag objects.
     */
    private void dragOver(@NotNull final DragEvent dragEvent) {
        UIUtils.acceptIfHasFile(dragEvent, FileExtensions.TEXTURE_EXTENSIONS);
    }

    /**
     * @return the textures editor.
     */
    @FromAnyThread
    private @NotNull MaterialTexturesComponent getMaterialTexturesComponent() {
        return notNull(materialTexturesComponent);
    }

    /**
     * @return the colors editor.
     */
    @FromAnyThread
    private @NotNull MaterialColorsComponent getMaterialColorsComponent() {
        return notNull(materialColorsComponent);
    }

    /**
     * @return the other parameters editor.
     */
    @FromAnyThread
    private @NotNull MaterialOtherParamsComponent getMaterialOtherParamsComponent() {
        return notNull(materialOtherParamsComponent);
    }

    /**
     * @return the render settings editor.
     */
    @FromAnyThread
    private @NotNull MaterialRenderParamsComponent getMaterialRenderParamsComponent() {
        return notNull(materialRenderParamsComponent);
    }

    @Override
    @FXThread
    protected void doOpenFile(@NotNull final Path file) {
        super.doOpenFile(file);

        final Path assetFile = notNull(getAssetFile(file));
        final MaterialKey materialKey = new MaterialKey(toAssetPath(assetFile));

        final AssetManager assetManager = EDITOR.getAssetManager();
        final Material material = assetManager.loadAsset(materialKey);

        final MaterialEditor3DState editor3DState = getEditor3DState();
        editor3DState.changeMode(ModelType.BOX);

        reload(material);
    }

    @Override
    @FXThread
    protected void loadState() {
        super.loadState();

        switch (ModelType.valueOf(editorState.getModelType())) {
            case BOX:
                cubeButton.setSelected(true);
                break;
            case SPHERE:
                sphereButton.setSelected(true);
                break;
            case QUAD:
                planeButton.setSelected(true);
                break;
        }

        editorToolComponent.getSelectionModel().select(editorState.getOpenedTool());
        bucketComboBox.getSelectionModel().select(editorState.getBucketType());
        mainSplitContainer.updateFor(editorState);
        lightButton.setSelected(editorState.isLightEnable());

        final MaterialEditor3DState editor3DState = getEditor3DState();
        final Vector3f cameraLocation = editorState.getCameraLocation();

        final float hRotation = editorState.getCameraHRotation();
        final float vRotation = editorState.getCameraVRotation();
        final float tDistance = editorState.getCameraTDistance();
        final float cameraSpeed = editorState.getCameraSpeed();

        EXECUTOR_MANAGER.addJMETask(() -> editor3DState.updateCameraSettings(cameraLocation,
                hRotation, vRotation, tDistance, cameraSpeed));
    }

    @Override
    protected @Nullable Supplier<EditorState> getEditorStateFactory() {
        return MaterialFileEditorState::new;
    }

    /**
     * Reload the material.
     */
    @FXThread
    private void reload(@NotNull final Material material) {
        setCurrentMaterial(material);

        setIgnoreListeners(true);
        try {

            final MaterialEditor3DState editor3DState = getEditor3DState();
            editor3DState.updateMaterial(material);

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
            items.clear();
            items.addAll(RESOURCE_MANAGER.getAvailableMaterialDefinitions());

            final MaterialDef materialDef = material.getMaterialDef();
            materialDefinitionBox.getSelectionModel().select(materialDef.getAssetName());

        } finally {
            setIgnoreListeners(false);
        }
    }

    /**
     * @return the list of material definitions.
     */
    @FromAnyThread
    private @NotNull ComboBox<String> getMaterialDefinitionBox() {
        return notNull(materialDefinitionBox);
    }

    @Override
    @FXThread
    protected boolean needToolbar() {
        return true;
    }

    @Override
    @FXThread
    protected void createToolbar(@NotNull final HBox container) {

        cubeButton = new ToggleButton();
        cubeButton.setTooltip(new Tooltip(Messages.MATERIAL_FILE_EDITOR_ACTION_CUBE + " (C)"));
        cubeButton.setGraphic(new ImageView(Icons.CUBE_16));
        cubeButton.selectedProperty().addListener((observable, oldValue, newValue) ->
                changeModelType(ModelType.BOX, newValue));

        sphereButton = new ToggleButton();
        sphereButton.setTooltip(new Tooltip(Messages.MATERIAL_FILE_EDITOR_ACTION_SPHERE + " (S)"));
        sphereButton.setGraphic(new ImageView(Icons.SPHERE_16));
        sphereButton.selectedProperty().addListener((observable, oldValue, newValue) ->
                changeModelType(ModelType.SPHERE, newValue));

        planeButton = new ToggleButton();
        planeButton.setTooltip(new Tooltip(Messages.MATERIAL_FILE_EDITOR_ACTION_PLANE + " (P)"));
        planeButton.setGraphic(new ImageView(Icons.PLANE_16));
        planeButton.selectedProperty().addListener((observable, oldValue, newValue) ->
                changeModelType(ModelType.QUAD, newValue));

        lightButton = new ToggleButton();
        lightButton.setTooltip(new Tooltip(Messages.MATERIAL_FILE_EDITOR_ACTION_LIGHT + " (L)"));
        lightButton.setGraphic(new ImageView(Icons.LIGHT_16));
        lightButton.setSelected(DEFAULT_LIGHT_ENABLED);
        lightButton.selectedProperty().addListener((observable, oldValue, newValue) -> changeLight(newValue));

        final Label materialDefinitionLabel = new Label(Messages.MATERIAL_EDITOR_MATERIAL_TYPE_LABEL + ":");

        materialDefinitionBox = new ComboBox<>();
        materialDefinitionBox.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> changeType(newValue));

        final Label bucketLabel = new Label(Messages.MATERIAL_FILE_EDITOR_BUCKET_TYPE_LABEL + ":");

        bucketComboBox = new ComboBox<>(BUCKETS);
        bucketComboBox.getSelectionModel().select(Inherit);
        bucketComboBox.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> changeBucketType(newValue));

        FXUtils.addToPane(createSaveAction(), container);
        FXUtils.addToPane(cubeButton, container);
        FXUtils.addToPane(sphereButton, container);
        FXUtils.addToPane(planeButton, container);
        FXUtils.addToPane(lightButton, container);
        FXUtils.addToPane(materialDefinitionLabel, container);
        FXUtils.addToPane(materialDefinitionBox, container);
        FXUtils.addToPane(bucketLabel, container);
        FXUtils.addToPane(bucketComboBox, container);

        DynamicIconSupport.addSupport(cubeButton, sphereButton, planeButton, lightButton);

        FXUtils.addClassTo(materialDefinitionLabel, bucketLabel, CSSClasses.FILE_EDITOR_TOOLBAR_LABEL);
        FXUtils.addClassTo(materialDefinitionBox, bucketComboBox, CSSClasses.FILE_EDITOR_TOOLBAR_FIELD);
        FXUtils.addClassTo(cubeButton, sphereButton, planeButton, lightButton, CSSClasses.FILE_EDITOR_TOOLBAR_BUTTON);
    }

    /**
     * Handle changing the bucket type.
     */
    @FXThread
    private void changeBucketType(@NotNull final RenderQueue.Bucket newValue) {

        final MaterialEditor3DState editor3DState = getEditor3DState();
        editor3DState.changeBucketType(newValue);

        final MaterialFileEditorState editorState = getEditorState();
        if (editorState != null) editorState.setBucketType(newValue);
    }

    /**
     * Handle changing the type.
     */
    @FXThread
    private void changeType(@Nullable final String newType) {
        if (isIgnoreListeners()) return;
        processChangeTypeImpl(newType);
    }

    /**
     * Handle changing the type.
     */
    @FXThread
    private void processChangeTypeImpl(@Nullable final String newType) {
        if (newType == null) return;

        final AssetManager assetManager = EDITOR.getAssetManager();
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
    @FXThread
    private void changeLight(@NotNull final Boolean newValue) {

        final MaterialEditor3DState editor3DState = getEditor3DState();
        editor3DState.updateLightEnabled(newValue);

        final MaterialFileEditorState editorState = getEditorState();
        if (editorState != null) editorState.setLightEnable(newValue);
    }

    /**
     * @return the button to use a cube.
     */
    @FromAnyThread
    private @NotNull ToggleButton getCubeButton() {
        return notNull(cubeButton);
    }

    /**
     * @return the button to use a plane.
     */
    @FromAnyThread
    private @NotNull ToggleButton getPlaneButton() {
        return notNull(planeButton);
    }

    /**
     * @return the button to use a sphere.
     */
    @FromAnyThread
    private @NotNull ToggleButton getSphereButton() {
        return notNull(sphereButton);
    }

    /**
     * @return the button to use a light.
     */
    @FromAnyThread
    private @NotNull ToggleButton getLightButton() {
        return notNull(lightButton);
    }

    /**
     * Handle the changed model type.
     */
    @FXThread
    private void changeModelType(@NotNull final ModelType modelType, @NotNull final Boolean newValue) {
        if (newValue == Boolean.FALSE) return;

        final MaterialEditor3DState editor3DState = getEditor3DState();

        final ToggleButton cubeButton = getCubeButton();
        final ToggleButton sphereButton = getSphereButton();
        final ToggleButton planeButton = getPlaneButton();

        if (modelType == ModelType.BOX) {
            cubeButton.setMouseTransparent(true);
            sphereButton.setMouseTransparent(false);
            planeButton.setMouseTransparent(false);
            cubeButton.setSelected(true);
            sphereButton.setSelected(false);
            planeButton.setSelected(false);
            editor3DState.changeMode(modelType);
        } else if (modelType == ModelType.SPHERE) {
            cubeButton.setMouseTransparent(false);
            sphereButton.setMouseTransparent(true);
            planeButton.setMouseTransparent(false);
            cubeButton.setSelected(false);
            sphereButton.setSelected(true);
            planeButton.setSelected(false);
            editor3DState.changeMode(modelType);
        } else if (modelType == ModelType.QUAD) {
            cubeButton.setMouseTransparent(false);
            sphereButton.setMouseTransparent(false);
            planeButton.setMouseTransparent(true);
            sphereButton.setSelected(false);
            cubeButton.setSelected(false);
            planeButton.setSelected(true);
            editor3DState.changeMode(modelType);
        }

        final MaterialFileEditorState editorState = getEditorState();
        if (editorState != null) editorState.setModelType(modelType);
    }

    @Override
    @FromAnyThread
    public @NotNull Material getCurrentMaterial() {
        return notNull(currentMaterial);
    }

    @Override
    @FXThread
    public void notifyFXChangeProperty(@NotNull final Object object, @NotNull final String propertyName) {

        if (object instanceof RenderState) {
            final MaterialRenderParamsComponent renderParamsComponent = getMaterialRenderParamsComponent();
            renderParamsComponent.buildFor(getCurrentMaterial());
            return;
        }

        final MaterialOtherParamsComponent otherParamsComponent = getMaterialOtherParamsComponent();
        otherParamsComponent.updateParam(propertyName);

        final MaterialColorsComponent colorsComponent = getMaterialColorsComponent();
        colorsComponent.updateParam(propertyName);

        final MaterialTexturesComponent texturesComponent = getMaterialTexturesComponent();
        texturesComponent.updateParam(propertyName);
    }

    /**
     * @param currentMaterial the current editing material.
     */
    @FXThread
    private void setCurrentMaterial(@NotNull final Material currentMaterial) {
        this.currentMaterial = currentMaterial;
    }

    @Override
    @FromAnyThread
    public @NotNull EditorDescription getDescription() {
        return DESCRIPTION;
    }
}
