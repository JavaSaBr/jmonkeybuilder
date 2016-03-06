package com.ss.editor.ui.component.editor.impl.material;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.MaterialDef;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.manager.ResourceManager;
import com.ss.editor.serializer.MaterialSerializer;
import com.ss.editor.state.editor.impl.material.MaterialEditorState;
import com.ss.editor.state.editor.impl.material.MaterialEditorState.ModelType;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.editor.EditorDescription;
import com.ss.editor.ui.component.editor.impl.AbstractFileEditor;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.util.EditorUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Accordion;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;
import rlib.util.FileUtils;
import rlib.util.StringUtils;
import rlib.util.array.Array;

import static com.ss.editor.Messages.MATERIAL_EDITOR_NAME;

/**
 * Реализация редактора для редактирования материалов.
 *
 * @author Ronn
 */
public class MaterialFileEditor extends AbstractFileEditor<StackPane> {

    public static final EditorDescription DESCRIPTION = new EditorDescription();

    static {
        DESCRIPTION.setConstructor(MaterialFileEditor::new);
        DESCRIPTION.setEditorName(MATERIAL_EDITOR_NAME);
        DESCRIPTION.addExtension(FileExtensions.JME_MATERIAL);
    }

    private static final ResourceManager RESOURCE_MANAGER = ResourceManager.getInstance();

    private static final Insets SMALL_OFFSET = new Insets(0, 0, 0, 3);
    private static final Insets BIG_OFFSET = new Insets(0, 0, 0, 6);

    /**
     * 3D часть редактора.
     */
    private final MaterialEditorState editorState;

    /**
     * Компонент для редактирования текстур.
     */
    private MaterialTexturesComponent materialTexturesComponent;

    /**
     * Компонетн для редактирования цветов.
     */
    private MaterialColorsComponent materialColorsComponent;

    /**
     * Компонент для редактирования различных параметров.
     */
    private MaterialOtherParamsComponent materialOtherParamsComponent;

    /**
     * Компонент для редактирования настроек рендера материала.
     */
    private MaterialRenderParamsComponent materialRenderParamsComponent;

    /**
     * Текущий редактируемый материал.
     */
    private Material currentMaterial;

    /**
     * Кнопка активации модели куба.
     */
    private ToggleButton cubeButton;

    /**
     * Кнопка активации модели сферы.
     */
    private ToggleButton sphereButton;

    /**
     * Кнопка активации модели плоскости.
     */
    private ToggleButton planeButton;

    /**
     * Кнопка активации света камеры.
     */
    private ToggleButton lightButton;

    /**
     * Список доступных типов материалов.
     */
    private ComboBox<String> materialDefinitionBox;

    /**
     * Обработчик внесенич изменений.
     */
    private Runnable changeHandler;

    /**
     * Оригинальный материал.
     */
    private String original;

    /**
     * Игнорировать ли слушателей.
     */
    private boolean ignoreListeners;

    public MaterialFileEditor() {
        this.editorState = new MaterialEditorState();
        addEditorState(editorState);
    }

    /**
     * @param ignoreListeners игнорировать ли слушателей.
     */
    private void setIgnoreListeners(boolean ignoreListeners) {
        this.ignoreListeners = ignoreListeners;
    }

    /**
     * @return игнорировать ли слушателей.
     */
    private boolean isIgnoreListeners() {
        return ignoreListeners;
    }

    /**
     * Обработка внесения изменений.
     */
    private void handleChanges() {

        final Material currentMaterial = getCurrentMaterial();

        final String original = getOriginal();
        final String content = MaterialSerializer.serializeToString(currentMaterial);

        final boolean dirty = !StringUtils.equals(original, content);

        setDirty(dirty);
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

        setOriginal(content);
        setDirty(false);

        notifyFileChanged();
    }

    /**
     * @return обработчик внесенич изменений.
     */
    private Runnable getChangeHandler() {
        return changeHandler;
    }

    @Override
    protected StackPane createRoot() {
        return new StackPane();
    }

    @Override
    protected void createContent(final StackPane root) {
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

        FXUtils.bindFixedHeight(accordion, parameterContainer.heightProperty());
    }

    /**
     * @return компонент для редактирования текстур.
     */
    private MaterialTexturesComponent getMaterialTexturesComponent() {
        return materialTexturesComponent;
    }

    /**
     * @return компонетн для редактирования цветов.
     */
    private MaterialColorsComponent getMaterialColorsComponent() {
        return materialColorsComponent;
    }

    /**
     * @return компонент для редактирования различных параметров.
     */
    private MaterialOtherParamsComponent getMaterialOtherParamsComponent() {
        return materialOtherParamsComponent;
    }

    /**
     * @return компонент для редактирования настроек рендера материала.
     */
    private MaterialRenderParamsComponent getMaterialRenderParamsComponent() {
        return materialRenderParamsComponent;
    }

    @Override
    public void openFile(final Path file) {
        super.openFile(file);

        final Path assetFile = EditorUtil.getAssetFile(file);
        final String assetPath = EditorUtil.toAssetPath(assetFile);

        final AssetManager assetManager = EDITOR.getAssetManager();
        assetManager.clearCache();

        final Material material = assetManager.loadMaterial(assetPath);

        final MaterialEditorState editorState = getEditorState();
        editorState.changeMode(ModelType.BOX);

        setOriginal(new String(FileUtils.getContent(file)));

        reload(material);
    }

    /**
     * Загрузка материала.
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

    /**
     * @return список доступных типов материалов.
     */
    private ComboBox<String> getMaterialDefinitionBox() {
        return materialDefinitionBox;
    }

    @Override
    protected boolean needToolbar() {
        return true;
    }

    @Override
    protected void createToolbar(final HBox container) {

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
        lightButton.setGraphic(new ImageView(Icons.LIGHT_24));
        lightButton.setSelected(true);
        lightButton.selectedProperty().addListener((observable, oldValue, newValue) -> changeLight(newValue));

        final Label materialDefinitionLabel = new Label(Messages.MATERIAL_EDITOR_MATERIAL_TYPE_LABEL + ":");
        materialDefinitionLabel.setId(CSSIds.MATERIAL_FILE_EDITOR_TOOLBAR_LABEL);

        materialDefinitionBox = new ComboBox<>();
        materialDefinitionBox.setId(CSSIds.MATERIAL_FILE_EDITOR_TOOLBAR_BOX);
        materialDefinitionBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> changeType(newValue));

        FXUtils.addToPane(createSaveAction(), container);
        FXUtils.addToPane(cubeButton, container);
        FXUtils.addToPane(sphereButton, container);
        FXUtils.addToPane(planeButton, container);
        FXUtils.addToPane(lightButton, container);
        FXUtils.addToPane(materialDefinitionLabel, container);
        FXUtils.addToPane(materialDefinitionBox, container);

        FXUtils.addClassTo(cubeButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(cubeButton, CSSClasses.FILE_EDITOR_TOOLBAR_BUTTON);
        FXUtils.addClassTo(sphereButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(sphereButton, CSSClasses.FILE_EDITOR_TOOLBAR_BUTTON);
        FXUtils.addClassTo(planeButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(planeButton, CSSClasses.FILE_EDITOR_TOOLBAR_BUTTON);
        FXUtils.addClassTo(lightButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(lightButton, CSSClasses.FILE_EDITOR_TOOLBAR_BUTTON);
        FXUtils.addClassTo(materialDefinitionLabel, CSSClasses.MAIN_FONT_13);
        FXUtils.addClassTo(materialDefinitionBox, CSSClasses.MAIN_FONT_13);

        HBox.setMargin(cubeButton, SMALL_OFFSET);
        HBox.setMargin(sphereButton, SMALL_OFFSET);
        HBox.setMargin(planeButton, SMALL_OFFSET);
        HBox.setMargin(lightButton, BIG_OFFSET);
        HBox.setMargin(materialDefinitionLabel, BIG_OFFSET);
    }

    /**
     * Обработка смены типа материала.
     */
    private void changeType(final String newType) {

        if (isIgnoreListeners()) {
            return;
        }

        processChangeTypeImpl(newType);
    }

    /**
     * Процесс смены типа материала.
     */
    private void processChangeTypeImpl(final String newType) {

        final AssetManager assetManager = EDITOR.getAssetManager();
        final Material newMaterial = new Material(assetManager, newType);
        newMaterial.getAdditionalRenderState();

        reload(newMaterial);

        final Runnable changeHandler = getChangeHandler();
        changeHandler.run();
    }

    /**
     * Обновление активности света камеры.
     */
    private void changeLight(final Boolean newValue) {
        final MaterialEditorState editorState = getEditorState();
        editorState.updateLightEnabled(newValue);
    }

    /**
     * @return кнопка активации модели куба.
     */
    private ToggleButton getCubeButton() {
        return cubeButton;
    }

    /**
     * @return кнопка активации модели плоскости.
     */
    private ToggleButton getPlaneButton() {
        return planeButton;
    }

    /**
     * @return кнопка активации модели сферы.
     */
    private ToggleButton getSphereButton() {
        return sphereButton;
    }

    /**
     * Обработка смены режима модели.
     */
    private void changeModelType(final ToggleButton button, final Boolean newValue) {

        if (newValue == Boolean.FALSE) {
            return;
        }

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

    /**
     * @return текущий редактируемый материал.
     */
    private Material getCurrentMaterial() {
        return currentMaterial;
    }

    /**
     * @param currentMaterial текущий редактируемый материал.
     */
    private void setCurrentMaterial(final Material currentMaterial) {
        this.currentMaterial = currentMaterial;
    }

    /**
     * @return 3D часть редактора.
     */
    private MaterialEditorState getEditorState() {
        return editorState;
    }

    /**
     * @param original оригинальный материал.
     */
    private void setOriginal(String original) {
        this.original = original;
    }

    /**
     * @return оригинальный материал.
     */
    private String getOriginal() {
        return original;
    }
}
