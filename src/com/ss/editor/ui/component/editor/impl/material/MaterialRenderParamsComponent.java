package com.ss.editor.ui.component.editor.impl.material;

import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.material.RenderState.FaceCullMode;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.EditorOperation;
import com.ss.editor.ui.component.editor.impl.material.operation.RenderStateOperation;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;

import java.util.function.Consumer;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;

import static java.lang.Float.parseFloat;
import static javafx.collections.FXCollections.observableArrayList;
import static javafx.geometry.Pos.CENTER_LEFT;

/**
 * Реализация компонента конфигурирования параметров рендера материала.
 *
 * @author Ronn
 */
public class MaterialRenderParamsComponent extends TitledPane {

    private static final ObservableList<FaceCullMode> FACE_CULL_MODES = observableArrayList(FaceCullMode.values());
    private static final ObservableList<BlendMode> BLEND_MODES = observableArrayList(BlendMode.values());

    private static final Insets ELEMENT_OFFSET = new Insets(3, 0, 0, 0);

    /**
     * Обработчик внесения изменений.
     */
    private final Consumer<EditorOperation> changeHandler;

    /**
     * Контейнер контролов различных параметров.
     */
    private final VBox container;

    /**
     * Выбор режима FaceCullMode.
     */
    private ComboBox<FaceCullMode> faceCullModeComboBox;

    /**
     * Выбор режима BlendMode.
     */
    private ComboBox<BlendMode> blendModeComboBox;

    /**
     * Установка factor для смещения.
     */
    private TextField polyOffsetFactorField;

    /**
     * Установка units для смещения.
     */
    private TextField polyOffsetUnitsField;

    /**
     * Активация записи в буффер глубины.
     */
    private CheckBox depthWriteCheckBox;

    /**
     * Активация записи результата фрагментского шейдера в буффер цвета.
     */
    private CheckBox colorWriteCheckBox;

    /**
     * Активация тестирования глубины для этого материала.
     */
    private CheckBox depthTestCheckBox;

    /**
     * Активация режима рендера wireframe.
     */
    private CheckBox wireframeCheckBox;

    /**
     * Текущий отображаемый материал.
     */
    private Material currentMaterial;

    /**
     * Флаг игнорирования слушателей.
     */
    private boolean ignoreListeners;

    public MaterialRenderParamsComponent(final Consumer<EditorOperation> changeHandler) {
        this.changeHandler = changeHandler;
        this.container = new VBox();
        setText(Messages.MATERIAL_FILE_EDITOR_RENDER_PARAMS_COMPONENT_TITLE);
        createContent();
        setContent(container);
        setAnimated(false);
    }

    /**
     * @return флаг игнорирования слушателей.
     */
    private boolean isIgnoreListeners() {
        return ignoreListeners;
    }

    /**
     * @param ignoreListeners флаг игнорирования слушателей.
     */
    private void setIgnoreListeners(final boolean ignoreListeners) {
        this.ignoreListeners = ignoreListeners;
    }

    /**
     * @return обработчик внесения изменений.
     */
    private Consumer<EditorOperation> getChangeHandler() {
        return changeHandler;
    }

    /**
     * Создание контролов.
     */
    private void createContent() {

        final VBox container = getContainer();

        final Label faceCullModeLabel = new Label(Messages.MATERIAL_RENDER_STATE_FACE_CULL_MODE + ":");
        faceCullModeLabel.setId(CSSIds.MATERIAL_PARAM_CONTROL_PARAM_NAME);

        faceCullModeComboBox = new ComboBox<>(FACE_CULL_MODES);
        faceCullModeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> processChange(newValue));

        final Label blendModeLabel = new Label(Messages.MATERIAL_RENDER_STATE_BLEND_MODE + ":");
        blendModeLabel.setId(CSSIds.MATERIAL_PARAM_CONTROL_PARAM_NAME);

        blendModeComboBox = new ComboBox<>(BLEND_MODES);
        blendModeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> processChange(newValue));

        final Label polyOffsetFactorLabel = new Label(Messages.MATERIAL_RENDER_STATE_POLY_OFFSET_FACTOR + ":");
        final Label polyOffsetUnitsLabel = new Label(Messages.MATERIAL_RENDER_STATE_POLY_OFFSET_UNITS + ":");

        polyOffsetFactorField = new TextField();
        polyOffsetFactorField.setId(CSSIds.MATERIAL_RENDER_STATE_POLY_OFFSET_FIELD);
        polyOffsetFactorField.textProperty().addListener((observable, oldValue, newValue) -> processChangeFactor(newValue));

        polyOffsetUnitsField = new TextField();
        polyOffsetUnitsField.setId(CSSIds.MATERIAL_RENDER_STATE_POLY_OFFSET_FIELD);
        polyOffsetUnitsField.textProperty().addListener((observable, oldValue, newValue) -> processChangeUnits(newValue));

        depthWriteCheckBox = new CheckBox(Messages.MATERIAL_RENDER_STATE_DEPTH_WRITE);
        depthWriteCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> pointChangeDepthWrite(newValue));

        colorWriteCheckBox = new CheckBox(Messages.MATERIAL_RENDER_STATE_COLOR_WRITE);
        colorWriteCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> processChangeColorWrite(newValue));

        depthTestCheckBox = new CheckBox(Messages.MATERIAL_RENDER_STATE_DEPTH_TEST);
        depthTestCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> processChangeDepthTest(newValue));

        wireframeCheckBox = new CheckBox(Messages.MATERIAL_RENDER_STATE_WIREFRAME);
        wireframeCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> processChangeWireframe(newValue));

        final HBox faceCullModeContainer = new HBox(faceCullModeLabel, faceCullModeComboBox);
        faceCullModeContainer.setAlignment(CENTER_LEFT);

        final HBox blendModeContainer = new HBox(blendModeLabel, blendModeComboBox);
        blendModeContainer.setAlignment(CENTER_LEFT);

        final HBox polyOffsetContainer = new HBox(polyOffsetFactorLabel, polyOffsetFactorField, polyOffsetUnitsLabel, polyOffsetUnitsField);
        polyOffsetContainer.setAlignment(CENTER_LEFT);

        FXUtils.addToPane(faceCullModeContainer, container);
        FXUtils.addToPane(blendModeContainer, container);
        FXUtils.addToPane(polyOffsetContainer, container);
        FXUtils.addToPane(depthWriteCheckBox, container);
        FXUtils.addToPane(colorWriteCheckBox, container);
        FXUtils.addToPane(depthTestCheckBox, container);
        FXUtils.addToPane(wireframeCheckBox, container);

        VBox.setMargin(faceCullModeContainer, ELEMENT_OFFSET);
        VBox.setMargin(blendModeContainer, ELEMENT_OFFSET);
        VBox.setMargin(polyOffsetContainer, ELEMENT_OFFSET);
        VBox.setMargin(depthWriteCheckBox, ELEMENT_OFFSET);
        VBox.setMargin(colorWriteCheckBox, ELEMENT_OFFSET);
        VBox.setMargin(depthTestCheckBox, ELEMENT_OFFSET);
        VBox.setMargin(wireframeCheckBox, ELEMENT_OFFSET);

        FXUtils.addClassTo(faceCullModeLabel, CSSClasses.MAIN_FONT_13);
        FXUtils.addClassTo(faceCullModeComboBox, CSSClasses.MAIN_FONT_13);
        FXUtils.addClassTo(blendModeLabel, CSSClasses.MAIN_FONT_13);
        FXUtils.addClassTo(blendModeComboBox, CSSClasses.MAIN_FONT_13);
        FXUtils.addClassTo(polyOffsetFactorLabel, CSSClasses.MAIN_FONT_13);
        FXUtils.addClassTo(polyOffsetUnitsLabel, CSSClasses.MAIN_FONT_13);
        FXUtils.addClassTo(polyOffsetFactorField, CSSClasses.MAIN_FONT_13);
        FXUtils.addClassTo(polyOffsetUnitsField, CSSClasses.MAIN_FONT_13);
        FXUtils.addClassTo(depthWriteCheckBox, CSSClasses.MAIN_FONT_13);
        FXUtils.addClassTo(colorWriteCheckBox, CSSClasses.MAIN_FONT_13);
        FXUtils.addClassTo(depthTestCheckBox, CSSClasses.MAIN_FONT_13);
        FXUtils.addClassTo(wireframeCheckBox, CSSClasses.MAIN_FONT_13);
    }

    /**
     * Обработка смены Wireframe.
     */
    private void processChangeWireframe(final Boolean newValue) {
        if (isIgnoreListeners()) return;

        final Material currentMaterial = getCurrentMaterial();
        final RenderState renderState = currentMaterial.getAdditionalRenderState();

        final Consumer<EditorOperation> changeHandler = getChangeHandler();
        changeHandler.accept(new RenderStateOperation<Boolean>(newValue, renderState.isWireframe()) {

            @Override
            protected void apply(final RenderState renderState, final Boolean value) {
                renderState.setWireframe(value);
            }
        });
    }

    /**
     * Обработка смены DepthTest.
     */
    private void processChangeDepthTest(final Boolean newValue) {
        if (isIgnoreListeners()) return;

        final Material currentMaterial = getCurrentMaterial();
        final RenderState renderState = currentMaterial.getAdditionalRenderState();

        final Consumer<EditorOperation> changeHandler = getChangeHandler();
        changeHandler.accept(new RenderStateOperation<Boolean>(newValue, renderState.isDepthTest()) {

            @Override
            protected void apply(final RenderState renderState, final Boolean value) {
                renderState.setDepthTest(value);
            }
        });
    }

    /**
     * Обработка смены ColorWrite.
     */
    private void processChangeColorWrite(final Boolean newValue) {
        if (isIgnoreListeners()) return;

        final Material currentMaterial = getCurrentMaterial();
        final RenderState renderState = currentMaterial.getAdditionalRenderState();

        final Consumer<EditorOperation> changeHandler = getChangeHandler();
        changeHandler.accept(new RenderStateOperation<Boolean>(newValue, renderState.isColorWrite()) {

            @Override
            protected void apply(final RenderState renderState, final Boolean value) {
                renderState.setColorWrite(value);
            }
        });
    }

    /**
     * Обработка смены DepthWrite.
     */
    private void pointChangeDepthWrite(final Boolean newValue) {
        if (isIgnoreListeners()) return;

        final Material currentMaterial = getCurrentMaterial();
        final RenderState renderState = currentMaterial.getAdditionalRenderState();

        final Consumer<EditorOperation> changeHandler = getChangeHandler();
        changeHandler.accept(new RenderStateOperation<Boolean>(newValue, renderState.isDepthWrite()) {

            @Override
            protected void apply(final RenderState renderState, final Boolean value) {
                renderState.setDepthWrite(value);
            }
        });
    }

    private void processChangeUnits(final String newUnits) {
        if (isIgnoreListeners()) return;

        try {

            final float polyOffsetUnits = parseFloat(newUnits);

            final Material currentMaterial = getCurrentMaterial();
            final RenderState renderState = currentMaterial.getAdditionalRenderState();

            final Consumer<EditorOperation> changeHandler = getChangeHandler();
            changeHandler.accept(new RenderStateOperation<Float>(polyOffsetUnits, renderState.getPolyOffsetUnits()) {

                @Override
                protected void apply(final RenderState renderState, final Float value) {
                    renderState.setPolyOffset(renderState.getPolyOffsetFactor(), value);
                }
            });

        } catch (final NumberFormatException ignored) {
        }
    }


    /**
     * Обработка смены PolyOffset Factor.
     */
    private void processChangeFactor(final String newFactor) {
        if (isIgnoreListeners()) return;

        try {

            final float polyOffsetFactor = parseFloat(newFactor);

            final Material currentMaterial = getCurrentMaterial();
            final RenderState renderState = currentMaterial.getAdditionalRenderState();

            final Consumer<EditorOperation> changeHandler = getChangeHandler();
            changeHandler.accept(new RenderStateOperation<Float>(polyOffsetFactor, renderState.getPolyOffsetFactor()) {

                @Override
                protected void apply(final RenderState renderState, final Float value) {
                    renderState.setPolyOffset(value, renderState.getPolyOffsetUnits());
                }
            });

        } catch (final NumberFormatException ignored) {
        }
    }

    /**
     * Обработка смены Blend Mode.
     */
    private void processChange(final BlendMode blendMode) {
        if (isIgnoreListeners()) return;

        final Material currentMaterial = getCurrentMaterial();
        final RenderState renderState = currentMaterial.getAdditionalRenderState();

        final Consumer<EditorOperation> changeHandler = getChangeHandler();
        changeHandler.accept(new RenderStateOperation<BlendMode>(blendMode, renderState.getBlendMode()) {

            @Override
            protected void apply(final RenderState renderState, final BlendMode value) {
                renderState.setBlendMode(value);
            }
        });
    }

    /**
     * Обработка смены FaceCull Mode.
     */
    private void processChange(final FaceCullMode faceCullMode) {
        if (isIgnoreListeners()) return;

        final Material currentMaterial = getCurrentMaterial();
        final RenderState renderState = currentMaterial.getAdditionalRenderState();

        final Consumer<EditorOperation> changeHandler = getChangeHandler();
        changeHandler.accept(new RenderStateOperation<FaceCullMode>(faceCullMode, renderState.getFaceCullMode()) {

            @Override
            protected void apply(final RenderState renderState, final FaceCullMode value) {
                renderState.setFaceCullMode(value);
            }
        });

    }

    /**
     * @return контейнер контролов различных параметров.
     */
    private VBox getContainer() {
        return container;
    }

    /**
     * @return активация записи результата фрагментского шейдера в буффер цвета.
     */
    private CheckBox getColorWriteCheckBox() {
        return colorWriteCheckBox;
    }

    /**
     * @return активация тестирования глубины для этого материала.
     */
    private CheckBox getDepthTestCheckBox() {
        return depthTestCheckBox;
    }

    /**
     * @return активация записи в буффер глубины.
     */
    private CheckBox getDepthWriteCheckBox() {
        return depthWriteCheckBox;
    }

    /**
     * @return активация режима рендера wireframe.
     */
    private CheckBox getWireframeCheckBox() {
        return wireframeCheckBox;
    }

    /**
     * @return выбор режима BlendMode.
     */
    private ComboBox<BlendMode> getBlendModeComboBox() {
        return blendModeComboBox;
    }

    /**
     * @return выбор режима FaceCullMode.
     */
    private ComboBox<FaceCullMode> getFaceCullModeComboBox() {
        return faceCullModeComboBox;
    }

    /**
     * @return установка factor для смещения.
     */
    private TextField getPolyOffsetFactorField() {
        return polyOffsetFactorField;
    }

    /**
     * @return установка units для смещения.
     */
    private TextField getPolyOffsetUnitsField() {
        return polyOffsetUnitsField;
    }

    /**
     * Построение настроек для материала.
     */
    public void buildFor(final Material material) {
        setCurrentMaterial(material);

        final RenderState renderState = material.getAdditionalRenderState();

        setIgnoreListeners(true);
        try {

            final ComboBox<FaceCullMode> faceCullModeComboBox = getFaceCullModeComboBox();
            faceCullModeComboBox.getSelectionModel().select(renderState.getFaceCullMode());

            final ComboBox<BlendMode> blendModeComboBox = getBlendModeComboBox();
            blendModeComboBox.getSelectionModel().select(renderState.getBlendMode());

            final TextField polyOffsetFactorField = getPolyOffsetFactorField();
            polyOffsetFactorField.setText(String.valueOf(renderState.getPolyOffsetFactor()));

            final TextField polyOffsetUnitsField = getPolyOffsetUnitsField();
            polyOffsetUnitsField.setText(String.valueOf(renderState.getPolyOffsetUnits()));

            final CheckBox depthTestCheckBox = getDepthTestCheckBox();
            depthTestCheckBox.setSelected(renderState.isDepthTest());

            final CheckBox depthWriteCheckBox = getDepthWriteCheckBox();
            depthWriteCheckBox.setSelected(renderState.isDepthWrite());

            final CheckBox colorWriteCheckBox = getColorWriteCheckBox();
            colorWriteCheckBox.setSelected(renderState.isColorWrite());

            final CheckBox wireframeCheckBox = getWireframeCheckBox();
            wireframeCheckBox.setSelected(renderState.isWireframe());

        } finally {
            setIgnoreListeners(false);
        }
    }

    /**
     * @return текущий отображаемый материал.
     */
    private Material getCurrentMaterial() {
        return currentMaterial;
    }

    /**
     * @param currentMaterial текущий отображаемый материал.
     */
    private void setCurrentMaterial(final Material currentMaterial) {
        this.currentMaterial = currentMaterial;
    }
}
