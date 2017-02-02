package com.ss.editor.ui.component.editor.impl.material;

import static java.lang.Float.parseFloat;
import static javafx.collections.FXCollections.observableArrayList;
import static javafx.geometry.Pos.CENTER;
import static javafx.geometry.Pos.CENTER_LEFT;

import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.material.RenderState.FaceCullMode;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.EditorOperation;
import com.ss.editor.ui.component.editor.impl.material.operation.RenderStateOperation;
import com.ss.editor.ui.control.material.MaterialParamControl;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;

import java.util.function.Consumer;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import rlib.ui.util.FXUtils;

/**
 * The component for editing material other properties.
 *
 * @author JavaSaBr
 */
public class MaterialRenderParamsComponent extends VBox {

    private static final ObservableList<FaceCullMode> FACE_CULL_MODES = observableArrayList(FaceCullMode.values());
    private static final ObservableList<BlendMode> BLEND_MODES = observableArrayList(BlendMode.values());

    /**
     * The changes handler.
     */
    private final Consumer<EditorOperation> changeHandler;

    /**
     * The combo box to choose FaceCullMode.
     */
    private ComboBox<FaceCullMode> faceCullModeComboBox;

    /**
     * The combo box to choose BlendMode.
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

    //FIXME переделать на использование пропертей
    MaterialRenderParamsComponent(@NotNull final Consumer<EditorOperation> changeHandler) {
        setId(CSSIds.MATERIAL_FILE_EDITOR_PROPERTIES_COMPONENT);
        this.changeHandler = changeHandler;
        createContent();
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

        final VBox container = new VBox();

        final Label faceCullModeLabel = new Label(Messages.MATERIAL_RENDER_STATE_FACE_CULL_MODE + ":");
        faceCullModeLabel.setId(CSSIds.MATERIAL_PARAM_CONTROL_PARAM_NAME);
        faceCullModeLabel.prefWidthProperty().bind(widthProperty().multiply(MaterialParamControl.LABEL_PERCENT_WIDTH2));

        faceCullModeComboBox = new ComboBox<>(FACE_CULL_MODES);
        faceCullModeComboBox.setId(CSSIds.MATERIAL_PARAM_CONTROL_COMBO_BOX);
        faceCullModeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> processChange(newValue));
        faceCullModeComboBox.prefWidthProperty().bind(widthProperty().multiply(MaterialParamControl.CONTROL_PERCENT_WIDTH2));

        final Label blendModeLabel = new Label(Messages.MATERIAL_RENDER_STATE_BLEND_MODE + ":");
        blendModeLabel.setId(CSSIds.MATERIAL_PARAM_CONTROL_PARAM_NAME);
        blendModeLabel.prefWidthProperty().bind(widthProperty().multiply(MaterialParamControl.LABEL_PERCENT_WIDTH2));

        blendModeComboBox = new ComboBox<>(BLEND_MODES);
        blendModeComboBox.setId(CSSIds.MATERIAL_PARAM_CONTROL_COMBO_BOX);
        blendModeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> processChange(newValue));
        blendModeComboBox.prefWidthProperty().bind(widthProperty().multiply(MaterialParamControl.CONTROL_PERCENT_WIDTH2));

        final Label polyOffsetFactorLabel = new Label(Messages.MATERIAL_RENDER_STATE_POLY_OFFSET_FACTOR + ":");
        polyOffsetFactorLabel.setId(CSSIds.MATERIAL_PARAM_CONTROL_PARAM_NAME);
        polyOffsetFactorLabel.prefWidthProperty().bind(widthProperty().multiply(MaterialParamControl.LABEL_PERCENT_WIDTH2));

        polyOffsetFactorField = new TextField();
        polyOffsetFactorField.setId(CSSIds.MATERIAL_RENDER_STATE_POLY_OFFSET_FIELD);
        polyOffsetFactorField.textProperty().addListener((observable, oldValue, newValue) -> processChangeFactor(newValue));
        polyOffsetFactorField.prefWidthProperty().bind(widthProperty().multiply(MaterialParamControl.CONTROL_PERCENT_WIDTH2));

        final Label polyOffsetUnitsLabel = new Label(Messages.MATERIAL_RENDER_STATE_POLY_OFFSET_UNITS + ":");
        polyOffsetUnitsLabel.setId(CSSIds.MATERIAL_PARAM_CONTROL_PARAM_NAME);
        polyOffsetUnitsLabel.prefWidthProperty().bind(widthProperty().multiply(MaterialParamControl.LABEL_PERCENT_WIDTH2));

        polyOffsetUnitsField = new TextField();
        polyOffsetUnitsField.setId(CSSIds.MATERIAL_RENDER_STATE_POLY_OFFSET_FIELD);
        polyOffsetUnitsField.textProperty().addListener((observable, oldValue, newValue) -> processChangeUnits(newValue));
        polyOffsetUnitsField.prefWidthProperty().bind(widthProperty().multiply(MaterialParamControl.CONTROL_PERCENT_WIDTH2));

        final Label depthWriteLabel = new Label(Messages.MATERIAL_RENDER_STATE_DEPTH_WRITE + ":");
        depthWriteLabel.setId(CSSIds.MATERIAL_PARAM_CONTROL_PARAM_NAME);
        depthWriteLabel.prefWidthProperty().bind(widthProperty().multiply(MaterialParamControl.LABEL_PERCENT_WIDTH2));

        depthWriteCheckBox = new CheckBox();
        depthWriteCheckBox.setId(CSSIds.MATERIAL_PARAM_CONTROL_CHECKBOX);
        depthWriteCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> pointChangeDepthWrite(newValue));
        depthWriteCheckBox.prefWidthProperty().bind(widthProperty().multiply(MaterialParamControl.CONTROL_PERCENT_WIDTH2));

        final Label colorWriteLabel = new Label(Messages.MATERIAL_RENDER_STATE_COLOR_WRITE + ":");
        colorWriteLabel.setId(CSSIds.MATERIAL_PARAM_CONTROL_PARAM_NAME);
        colorWriteLabel.prefWidthProperty().bind(widthProperty().multiply(MaterialParamControl.LABEL_PERCENT_WIDTH2));

        colorWriteCheckBox = new CheckBox();
        colorWriteCheckBox.setId(CSSIds.MATERIAL_PARAM_CONTROL_CHECKBOX);
        colorWriteCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> processChangeColorWrite(newValue));
        colorWriteCheckBox.prefWidthProperty().bind(widthProperty().multiply(MaterialParamControl.CONTROL_PERCENT_WIDTH2));

        final Label depthTestLabel = new Label(Messages.MATERIAL_RENDER_STATE_DEPTH_TEST + ":");
        depthTestLabel.setId(CSSIds.MATERIAL_PARAM_CONTROL_PARAM_NAME);
        depthTestLabel.prefWidthProperty().bind(widthProperty().multiply(MaterialParamControl.LABEL_PERCENT_WIDTH2));

        depthTestCheckBox = new CheckBox();
        depthTestCheckBox.setId(CSSIds.MATERIAL_PARAM_CONTROL_CHECKBOX);
        depthTestCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> processChangeDepthTest(newValue));
        depthTestCheckBox.prefWidthProperty().bind(widthProperty().multiply(MaterialParamControl.CONTROL_PERCENT_WIDTH2));

        final Label wireframeLabel = new Label(Messages.MATERIAL_RENDER_STATE_WIREFRAME + ":");
        wireframeLabel.setId(CSSIds.MATERIAL_PARAM_CONTROL_PARAM_NAME);
        wireframeLabel.prefWidthProperty().bind(widthProperty().multiply(MaterialParamControl.LABEL_PERCENT_WIDTH2));

        wireframeCheckBox = new CheckBox();
        wireframeCheckBox.setId(CSSIds.MATERIAL_PARAM_CONTROL_CHECKBOX);
        wireframeCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> processChangeWireframe(newValue));
        wireframeCheckBox.prefWidthProperty().bind(widthProperty().multiply(MaterialParamControl.CONTROL_PERCENT_WIDTH2));

        final HBox faceCullModeContainer = new HBox(faceCullModeLabel, faceCullModeComboBox);
        faceCullModeContainer.setAlignment(CENTER_LEFT);

        final HBox blendModeContainer = new HBox(blendModeLabel, blendModeComboBox);
        blendModeContainer.setAlignment(CENTER_LEFT);

        final HBox polyOffsetFactorContainer = new HBox(polyOffsetFactorLabel, polyOffsetFactorField);
        polyOffsetFactorContainer.setAlignment(CENTER_LEFT);

        final HBox polyOffsetUnitsContainer = new HBox(polyOffsetUnitsLabel, polyOffsetUnitsField);
        polyOffsetUnitsContainer.setAlignment(CENTER_LEFT);

        final HBox depthWriteContainer = new HBox(depthWriteLabel, depthWriteCheckBox);
        depthWriteContainer.setAlignment(CENTER_LEFT);

        final HBox colorWriteContainer = new HBox(colorWriteLabel, colorWriteCheckBox);
        colorWriteContainer.setAlignment(CENTER_LEFT);

        final HBox depthTestContainer = new HBox(depthTestLabel, depthTestCheckBox);
        depthTestContainer.setAlignment(CENTER_LEFT);

        final HBox wireframeContainer = new HBox(wireframeLabel, wireframeCheckBox);
        wireframeContainer.setAlignment(CENTER_LEFT);

        FXUtils.addToPane(faceCullModeContainer, container);
        FXUtils.addToPane(blendModeContainer, container);
        FXUtils.addToPane(polyOffsetFactorContainer, container);
        FXUtils.addToPane(polyOffsetUnitsContainer, container);
        FXUtils.addToPane(depthWriteContainer, container);
        FXUtils.addToPane(colorWriteContainer, container);
        FXUtils.addToPane(depthTestContainer, container);
        FXUtils.addToPane(wireframeContainer, container);
        FXUtils.addToPane(container, this);

        container.setPadding(new Insets(0, 6, 0, 0));

        FXUtils.addClassTo(faceCullModeLabel, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(faceCullModeComboBox, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(blendModeLabel, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(blendModeComboBox, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(polyOffsetFactorLabel, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(polyOffsetUnitsLabel, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(polyOffsetFactorField, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(polyOffsetUnitsField, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(depthWriteLabel, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(colorWriteLabel, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(depthTestLabel, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(wireframeLabel, CSSClasses.SPECIAL_FONT_13);
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
