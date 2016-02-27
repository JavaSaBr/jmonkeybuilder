package com.ss.editor.ui.component.editor.impl.material;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.ss.editor.state.editor.impl.material.MaterialEditorState;
import com.ss.editor.state.editor.impl.material.MaterialEditorState.ModelType;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.editor.EditorDescription;
import com.ss.editor.ui.component.editor.impl.AbstractFileEditor;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.util.EditorUtil;

import java.nio.file.Path;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;

import static com.ss.editor.ui.css.CSSIds.MATERIAL_EDITOR_PARAMETER_CONTAINER;
import static javafx.geometry.Pos.TOP_RIGHT;

/**
 * Реализация редактора для редактирования материалов.
 *
 * @author Ronn
 */
public class MaterialEditor extends AbstractFileEditor<StackPane> {

    public static final EditorDescription DESCRIPTION = new EditorDescription();

    static {
        DESCRIPTION.setConstructor(MaterialEditor::new);
        DESCRIPTION.setEditorName("MaterialEditor");
        DESCRIPTION.addExtension("j3m");
    }

    /**
     * 3D часть редактора.
     */
    private final MaterialEditorState editorState;

    /**
     * Компонент для редактирования текстур.
     */
    private MaterialTexturesComponent materialTexturesComponent;

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

    public MaterialEditor() {
        this.editorState = new MaterialEditorState();
        addEditorState(editorState);
    }

    @Override
    protected StackPane createRoot() {
        return new StackPane();
    }

    @Override
    protected void createContent(final StackPane root) {
        root.setAlignment(TOP_RIGHT);

        final Accordion accordion = new Accordion();

        final VBox parameterContainer = new VBox();
        parameterContainer.setId(MATERIAL_EDITOR_PARAMETER_CONTAINER);

        materialTexturesComponent = new MaterialTexturesComponent();

        final ObservableList<TitledPane> panes = accordion.getPanes();
        panes.add(materialTexturesComponent);

        FXUtils.addToPane(accordion, parameterContainer);
        FXUtils.addToPane(parameterContainer, root);
    }

    /**
     * @return компонент для редактирования текстур.
     */
    private MaterialTexturesComponent getMaterialTexturesComponent() {
        return materialTexturesComponent;
    }

    @Override
    public void openFile(final Path file) {
        super.openFile(file);

        final Path assetFile = EditorUtil.getAssetFile(file);
        final AssetManager assetManager = EDITOR.getAssetManager();
        final Material material = assetManager.loadMaterial(assetFile.toString());

        final MaterialEditorState editorState = getEditorState();
        editorState.changeMode(ModelType.BOX);
        editorState.updateMaterial(material);

        final ToggleButton cubeButton = getCubeButton();
        cubeButton.setSelected(true);

        final MaterialTexturesComponent materialTexturesComponent = getMaterialTexturesComponent();
        materialTexturesComponent.buildFor(material);
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

        FXUtils.addToPane(createSaveAction(), container);
        FXUtils.addToPane(cubeButton, container);
        FXUtils.addToPane(sphereButton, container);
        FXUtils.addToPane(planeButton, container);
        FXUtils.addToPane(lightButton, container);

        FXUtils.addClassTo(cubeButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(cubeButton, CSSClasses.FILE_EDITOR_TOOLBAR_BUTTON);
        FXUtils.addClassTo(sphereButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(sphereButton, CSSClasses.FILE_EDITOR_TOOLBAR_BUTTON);
        FXUtils.addClassTo(planeButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(planeButton, CSSClasses.FILE_EDITOR_TOOLBAR_BUTTON);
        FXUtils.addClassTo(lightButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(lightButton, CSSClasses.FILE_EDITOR_TOOLBAR_BUTTON);

        HBox.setMargin(cubeButton, new Insets(0, 0, 0, 3));
        HBox.setMargin(lightButton, new Insets(0, 0, 0, 6));
    }

    /**
     * Обновление активности света камеры.
     */
    private void changeLight(final Boolean newValue) {
        final MaterialEditorState editorState = getEditorState();
        editorState.updateLightEnabled(newValue);
    }

    /**
     *
     * @return кнопка активации модели куба.
     */
    private ToggleButton getCubeButton() {
        return cubeButton;
    }

    /**
     *
     * @return кнопка активации модели плоскости.
     */
    private ToggleButton getPlaneButton() {
        return planeButton;
    }

    /**
     *
     * @return кнопка активации модели сферы.
     */
    private ToggleButton getSphereButton() {
        return sphereButton;
    }

    /**
     * Обработка смены режима модели.
     */
    private void changeModelType(final ToggleButton button, final Boolean newValue) {

        if(newValue == Boolean.FALSE) {
            return;
        }

        final MaterialEditorState editorState = getEditorState();

        final ToggleButton cubeButton = getCubeButton();
        final ToggleButton sphereButton = getSphereButton();
        final ToggleButton planeButton = getPlaneButton();

        if(button == cubeButton) {
            sphereButton.setSelected(false);
            planeButton.setSelected(false);
            sphereButton.setDisable(false);
            planeButton.setDisable(false);
            cubeButton.setDisable(true);
            editorState.changeMode(ModelType.BOX);
        } else if(button == sphereButton) {
            cubeButton.setSelected(false);
            planeButton.setSelected(false);
            cubeButton.setDisable(false);
            planeButton.setDisable(false);
            sphereButton.setDisable(true);
            editorState.changeMode(ModelType.SPHERE);
        } else if(button == planeButton) {
            sphereButton.setSelected(false);
            cubeButton.setSelected(false);
            sphereButton.setDisable(false);
            cubeButton.setDisable(false);
            planeButton.setDisable(true);
            editorState.changeMode(ModelType.QUAD);
        }
    }

    /**
     * @param currentMaterial текущий редактируемый материал.
     */
    private void setCurrentMaterial(final Material currentMaterial) {
        this.currentMaterial = currentMaterial;
    }

    /**
     * @return текущий редактируемый материал.
     */
    private Material getCurrentMaterial() {
        return currentMaterial;
    }

    /**
     * @return 3D часть редактора.
     */
    private MaterialEditorState getEditorState() {
        return editorState;
    }
}
