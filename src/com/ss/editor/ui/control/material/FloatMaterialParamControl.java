package com.ss.editor.ui.control.material;

import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.ss.editor.model.undo.EditorOperation;
import com.ss.editor.ui.control.material.operation.FloatMaterialParamOperation;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;

import java.util.function.Consumer;

import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory;
import javafx.scene.input.ScrollEvent;
import rlib.ui.util.FXUtils;

/**
 * The base implementation of control for editing float material parameter.
 *
 * @author JavaSaBr
 */
public class FloatMaterialParamControl extends MaterialParamControl {

    /**
     * The float spinner.
     */
    private Spinner<Double> spinner;

    public FloatMaterialParamControl(final Consumer<EditorOperation> changeHandler, final Material material, final String parameterName) {
        super(changeHandler, material, parameterName);
    }

    @Override
    protected void createComponents() {
        super.createComponents();

        final SpinnerValueFactory<Double> valueFactory = new DoubleSpinnerValueFactory(-500, 500, 0, 0.01);

        spinner = new Spinner<>();
        spinner.setId(CSSIds.MATERIAL_PARAM_CONTROL_SPINNER);
        spinner.setValueFactory(valueFactory);
        spinner.setEditable(true);
        spinner.setOnScroll(this::processScroll);
        spinner.valueProperty().addListener((observable, oldValue, newValue) -> processChange(newValue));
        spinner.prefWidthProperty().bind(widthProperty().multiply(CONTROL_PERCENT_WIDTH2));

        FXUtils.addToPane(spinner, this);
        FXUtils.addClassTo(spinner, CSSClasses.SPECIAL_FONT_13);
    }

    @Override
    protected double getLabelPercentWidth() {
        return LABEL_PERCENT_WIDTH2;
    }

    /**
     * The process of scrolling value.
     */
    private void processScroll(final ScrollEvent event) {
        if (!event.isControlDown()) return;

        final double deltaY = event.getDeltaY();

        if (deltaY > 0) {
            spinner.increment(10);
        } else {
            spinner.decrement(10);
        }
    }

    /**
     * Update a value.
     */
    private void processChange(final Double newValue) {
        if (isIgnoreListeners()) return;

        final Float newFValue = newValue == null ? null : newValue.floatValue();
        final String parameterName = getParameterName();
        final Material material = getMaterial();
        final MatParam param = material.getParam(parameterName);
        final Float oldValue = param == null ? null : (Float) param.getValue();

        execute(new FloatMaterialParamOperation(parameterName, newFValue, oldValue));
    }

    @Override
    public void reload() {
        super.reload();

        final Material material = getMaterial();
        final MatParam param = material.getParam(getParameterName());
        final SpinnerValueFactory<Double> valueFactory = spinner.getValueFactory();

        if (param == null) {
            valueFactory.setValue(0D);
            return;
        }

        final Float value = (Float) param.getValue();
        valueFactory.setValue(value.doubleValue());
    }
}
