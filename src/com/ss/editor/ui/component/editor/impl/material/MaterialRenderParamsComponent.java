package com.ss.editor.ui.component.editor.impl.material;

import static java.util.Objects.requireNonNull;
import static javafx.collections.FXCollections.observableArrayList;
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
import com.ss.rlib.ui.control.input.FloatTextField;
import com.ss.rlib.ui.util.FXUtils;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * The component for editing material other properties.
 *
 * @author JavaSaBr
 */
public class MaterialRenderParamsComponent extends VBox {

    @NotNull
    private static final ObservableList<FaceCullMode> FACE_CULL_MODES = observableArrayList(FaceCullMode.values());

    @NotNull
    private static final ObservableList<BlendMode> BLEND_MODES = observableArrayList(BlendMode.values());

    /**
     * The changes handler.
     */
    @NotNull
    private final Consumer<EditorOperation> changeHandler;

    /**
     * The combo box to choose FaceCullMode.
     */
    @Nullable
    private ComboBox<FaceCullMode> faceCullModeComboBox;

    /**
     * The combo box to choose BlendMode.
     */
    @Nullable
    private ComboBox<BlendMode> blendModeComboBox;

    /**
     * The poly offset factor field.
     */
    @Nullable
    private FloatTextField polyOffsetFactorField;

    /**
     * The poly offset units field.
     */
    @Nullable
    private FloatTextField polyOffsetUnitsField;

    /**
     * The depth write check box.
     */
    @Nullable
    private CheckBox depthWriteCheckBox;

    /**
     * The color write check box.
     */
    @Nullable
    private CheckBox colorWriteCheckBox;

    /**
     * The depth test check box.
     */
    @Nullable
    private CheckBox depthTestCheckBox;

    /**
     * The wireframe check box.
     */
    @Nullable
    private CheckBox wireframeCheckBox;

    /**
     * The current material.
     */
    @Nullable
    private Material currentMaterial;

    /**
     * The flag of change ignoring.
     */
    private boolean ignoreListeners;

    /**
     * Instantiates a new Material render params component.
     *
     * @param changeHandler the change handler
     */
    MaterialRenderParamsComponent(@NotNull final Consumer<EditorOperation> changeHandler) {
        this.changeHandler = changeHandler;
        createControls();
        FXUtils.addClassTo(this, CSSClasses.MATERIAL_FILE_EDITOR_PROPERTIES_COMPONENT);
    }

    /**
     * @return true if need to ignore changes.
     */
    private boolean isIgnoreListeners() {
        return ignoreListeners;
    }

    /**
     * @param ignoreListeners true if need to ignore changes.
     */
    private void setIgnoreListeners(final boolean ignoreListeners) {
        this.ignoreListeners = ignoreListeners;
    }

    /**
     * @return the changes handler.
     */
    @NotNull
    private Consumer<EditorOperation> getChangeHandler() {
        return changeHandler;
    }

    /**
     * Create controls.
     */
    private void createControls() {

        final VBox container = new VBox();

        final Label faceCullModeLabel = new Label(Messages.MATERIAL_RENDER_STATE_FACE_CULL_MODE + ":");
        faceCullModeLabel.prefWidthProperty().bind(widthProperty().multiply(MaterialParamControl.LABEL_PERCENT_WIDTH2));

        faceCullModeComboBox = new ComboBox<>(FACE_CULL_MODES);
        faceCullModeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> processChange(newValue));
        faceCullModeComboBox.prefWidthProperty().bind(widthProperty().multiply(MaterialParamControl.CONTROL_PERCENT_WIDTH2));

        final Label blendModeLabel = new Label(Messages.MATERIAL_RENDER_STATE_BLEND_MODE + ":");
        blendModeLabel.prefWidthProperty().bind(widthProperty().multiply(MaterialParamControl.LABEL_PERCENT_WIDTH2));

        blendModeComboBox = new ComboBox<>(BLEND_MODES);
        blendModeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> processChange(newValue));
        blendModeComboBox.prefWidthProperty().bind(widthProperty().multiply(MaterialParamControl.CONTROL_PERCENT_WIDTH2));

        final Label polyOffsetFactorLabel = new Label(Messages.MATERIAL_RENDER_STATE_POLY_OFFSET_FACTOR + ":");
        polyOffsetFactorLabel.prefWidthProperty().bind(widthProperty().multiply(MaterialParamControl.LABEL_PERCENT_WIDTH2));

        polyOffsetFactorField = new FloatTextField();
        polyOffsetFactorField.addChangeListener((observable, oldValue, newValue) -> processChangeFactor(newValue));
        polyOffsetFactorField.prefWidthProperty().bind(widthProperty().multiply(MaterialParamControl.CONTROL_PERCENT_WIDTH2));
        polyOffsetFactorField.setScrollPower(5F);

        final Label polyOffsetUnitsLabel = new Label(Messages.MATERIAL_RENDER_STATE_POLY_OFFSET_UNITS + ":");
        polyOffsetUnitsLabel.prefWidthProperty().bind(widthProperty().multiply(MaterialParamControl.LABEL_PERCENT_WIDTH2));

        polyOffsetUnitsField = new FloatTextField();
        polyOffsetUnitsField.addChangeListener((observable, oldValue, newValue) -> processChangeUnits(newValue));
        polyOffsetUnitsField.prefWidthProperty().bind(widthProperty().multiply(MaterialParamControl.CONTROL_PERCENT_WIDTH2));
        polyOffsetUnitsField.setScrollPower(5F);

        final Label depthWriteLabel = new Label(Messages.MATERIAL_RENDER_STATE_DEPTH_WRITE + ":");
        depthWriteLabel.prefWidthProperty().bind(widthProperty().multiply(MaterialParamControl.LABEL_PERCENT_WIDTH2));

        depthWriteCheckBox = new CheckBox();
        depthWriteCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> pointChangeDepthWrite(newValue));
        depthWriteCheckBox.prefWidthProperty().bind(widthProperty().multiply(MaterialParamControl.CONTROL_PERCENT_WIDTH2));

        final Label colorWriteLabel = new Label(Messages.MATERIAL_RENDER_STATE_COLOR_WRITE + ":");
        colorWriteLabel.prefWidthProperty().bind(widthProperty().multiply(MaterialParamControl.LABEL_PERCENT_WIDTH2));

        colorWriteCheckBox = new CheckBox();
        colorWriteCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> processChangeColorWrite(newValue));
        colorWriteCheckBox.prefWidthProperty().bind(widthProperty().multiply(MaterialParamControl.CONTROL_PERCENT_WIDTH2));

        final Label depthTestLabel = new Label(Messages.MATERIAL_RENDER_STATE_DEPTH_TEST + ":");
        depthTestLabel.prefWidthProperty().bind(widthProperty().multiply(MaterialParamControl.LABEL_PERCENT_WIDTH2));

        depthTestCheckBox = new CheckBox();
        depthTestCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> processChangeDepthTest(newValue));
        depthTestCheckBox.prefWidthProperty().bind(widthProperty().multiply(MaterialParamControl.CONTROL_PERCENT_WIDTH2));

        final Label wireframeLabel = new Label(Messages.MATERIAL_RENDER_STATE_WIREFRAME + ":");
        wireframeLabel.prefWidthProperty().bind(widthProperty().multiply(MaterialParamControl.LABEL_PERCENT_WIDTH2));

        wireframeCheckBox = new CheckBox();
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

        FXUtils.addClassesTo(faceCullModeContainer, blendModeContainer, polyOffsetFactorContainer,
                polyOffsetUnitsContainer, depthWriteContainer, colorWriteContainer, depthTestContainer,
                wireframeContainer, CSSClasses.MATERIAL_FILE_EDITOR_PARAM_CONTROL);

        FXUtils.addClassesTo(faceCullModeLabel, blendModeLabel, polyOffsetFactorLabel, polyOffsetUnitsLabel,
                depthWriteLabel, colorWriteLabel, depthTestLabel, wireframeLabel,
                CSSClasses.MATERIAL_FILE_EDITOR_PARAM_CONTROL_NAME);

        FXUtils.addClassesTo(faceCullModeComboBox, colorWriteCheckBox, polyOffsetFactorField, polyOffsetUnitsField,
                CSSClasses.MATERIAL_FILE_EDITOR_PARAM_CONTROL_COMBO_BOX);

        FXUtils.addClassesTo(depthWriteCheckBox, colorWriteCheckBox, depthTestCheckBox, wireframeCheckBox,
                CSSClasses.MATERIAL_FILE_EDITOR_PARAM_CONTROL_NAME);
    }

    /**
     * Handle changing wireframe flag.
     */
    private void processChangeWireframe(@Nullable final Boolean newValue) {
        if (isIgnoreListeners()) return;

        final Material currentMaterial = getCurrentMaterial();
        final RenderState renderState = currentMaterial.getAdditionalRenderState();

        final Consumer<EditorOperation> changeHandler = getChangeHandler();
        changeHandler.accept(new RenderStateOperation<>(newValue, renderState.isWireframe(), RenderState::setWireframe));
    }

    /**
     * Handle changing depth test flag.
     */
    private void processChangeDepthTest(@Nullable final Boolean newValue) {
        if (isIgnoreListeners()) return;

        final Material currentMaterial = getCurrentMaterial();
        final RenderState renderState = currentMaterial.getAdditionalRenderState();

        final Consumer<EditorOperation> changeHandler = getChangeHandler();
        changeHandler.accept(new RenderStateOperation<>(newValue, renderState.isDepthTest(), RenderState::setDepthTest));
    }

    /**
     * Handle changing color write flag.
     */
    private void processChangeColorWrite(@Nullable final Boolean newValue) {
        if (isIgnoreListeners()) return;

        final Material currentMaterial = getCurrentMaterial();
        final RenderState renderState = currentMaterial.getAdditionalRenderState();

        final Consumer<EditorOperation> changeHandler = getChangeHandler();
        changeHandler.accept(new RenderStateOperation<>(newValue, renderState.isColorWrite(), RenderState::setColorWrite));
    }

    /**
     * Handle changing depth write flag.
     */
    private void pointChangeDepthWrite(@Nullable final Boolean newValue) {
        if (isIgnoreListeners()) return;

        final Material currentMaterial = getCurrentMaterial();
        final RenderState renderState = currentMaterial.getAdditionalRenderState();

        final Consumer<EditorOperation> changeHandler = getChangeHandler();
        changeHandler.accept(new RenderStateOperation<>(newValue, renderState.isDepthWrite(), RenderState::setDepthWrite));
    }

    /**
     * Handle changing poly offset units.
     */
    private void processChangeUnits(@NotNull final Float newUnits) {
        if (isIgnoreListeners()) return;

        final Material currentMaterial = getCurrentMaterial();
        final RenderState renderState = currentMaterial.getAdditionalRenderState();

        final Consumer<EditorOperation> changeHandler = getChangeHandler();
        changeHandler.accept(new RenderStateOperation<>(newUnits, renderState.getPolyOffsetUnits(),
                (state, value) -> state.setPolyOffset(state.getPolyOffsetFactor(), value)));
    }

    /**
     * Handle changing poly offset factor.
     */
    private void processChangeFactor(@NotNull final Float newFactor) {
        if (isIgnoreListeners()) return;

        final Material currentMaterial = getCurrentMaterial();
        final RenderState renderState = currentMaterial.getAdditionalRenderState();

        final Consumer<EditorOperation> changeHandler = getChangeHandler();
        changeHandler.accept(new RenderStateOperation<>(newFactor, renderState.getPolyOffsetFactor(),
                (state, value) -> state.setPolyOffset(value, state.getPolyOffsetUnits())));

    }

    /**
     * Handle changing blend mode.
     */
    private void processChange(@NotNull final BlendMode blendMode) {
        if (isIgnoreListeners()) return;

        final Material currentMaterial = getCurrentMaterial();
        final RenderState renderState = currentMaterial.getAdditionalRenderState();

        final Consumer<EditorOperation> changeHandler = getChangeHandler();
        changeHandler.accept(new RenderStateOperation<>(blendMode, renderState.getBlendMode(), RenderState::setBlendMode));
    }

    /**
     * Handle changing face cull mode.
     */
    private void processChange(@NotNull final FaceCullMode faceCullMode) {
        if (isIgnoreListeners()) return;

        final Material currentMaterial = getCurrentMaterial();
        final RenderState renderState = currentMaterial.getAdditionalRenderState();

        final Consumer<EditorOperation> changeHandler = getChangeHandler();
        changeHandler.accept(new RenderStateOperation<>(faceCullMode, renderState.getFaceCullMode(), RenderState::setFaceCullMode));
    }

    /**
     * @return the color write check box.
     */
    @NotNull
    private CheckBox getColorWriteCheckBox() {
        return requireNonNull(colorWriteCheckBox);
    }

    /**
     * @return the depth test check box.
     */
    @NotNull
    private CheckBox getDepthTestCheckBox() {
        return requireNonNull(depthTestCheckBox);
    }

    /**
     * @return the depth write check box.
     */
    @NotNull
    private CheckBox getDepthWriteCheckBox() {
        return requireNonNull(depthWriteCheckBox);
    }

    /**
     * @return the wireframe check box.
     */
    @NotNull
    private CheckBox getWireframeCheckBox() {
        return requireNonNull(wireframeCheckBox);
    }

    /**
     * @return the combo box to choose BlendMode.
     */
    @NotNull
    private ComboBox<BlendMode> getBlendModeComboBox() {
        return requireNonNull(blendModeComboBox);
    }

    /**
     * @return the combo box to choose FaceCullMode.
     */
    @NotNull
    private ComboBox<FaceCullMode> getFaceCullModeComboBox() {
        return requireNonNull(faceCullModeComboBox);
    }

    /**
     * @return the poly offset factor field.
     */
    @NotNull
    private FloatTextField getPolyOffsetFactorField() {
        return requireNonNull(polyOffsetFactorField);
    }

    /**
     * @return the poly offset units field.
     */
    @NotNull
    private FloatTextField getPolyOffsetUnitsField() {
        return requireNonNull(polyOffsetUnitsField);
    }

    /**
     * Update settings for a material.
     *
     * @param material the material
     */
    public void buildFor(@NotNull final Material material) {
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
     * @return the current material.
     */
    @NotNull
    private Material getCurrentMaterial() {
        return requireNonNull(currentMaterial);
    }

    /**
     * @param currentMaterial the current material.
     */
    private void setCurrentMaterial(@NotNull final Material currentMaterial) {
        this.currentMaterial = currentMaterial;
    }
}
