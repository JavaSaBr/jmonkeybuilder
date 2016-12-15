package com.ss.editor.ui.control.material;

import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.ss.editor.model.undo.EditorOperation;
import com.ss.editor.ui.control.material.operation.IntegerMaterialParamOperation;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;

import java.util.function.Consumer;

import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.input.ScrollEvent;
import rlib.ui.util.FXUtils;

/**
 * The base implementation of control for editing integer material parameter.
 *
 * @author JavaSaBr
 */
public class IntegerMaterialParamControl extends MaterialParamControl {

    /**
     * The integer spinner.
     */
    private Spinner<Integer> spinner;

    public IntegerMaterialParamControl(final Consumer<EditorOperation> changeHandler, final Material material, final String parameterName) {
        super(changeHandler, material, parameterName);
    }

    @Override
    protected void createComponents() {
        super.createComponents();

        final SpinnerValueFactory<Integer> valueFactory = new IntegerSpinnerValueFactory(-500, 500, 0, 1);

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
            spinner.increment();
        } else {
            spinner.decrement();
        }
    }

    /**
     * Update a value.
     */
    private void processChange(final Integer newValue) {
        if (isIgnoreListeners()) return;

        final String parameterName = getParameterName();
        final Material material = getMaterial();
        final MatParam param = material.getParam(parameterName);
        final Integer oldValue = param == null ? null : (Integer) param.getValue();

        execute(new IntegerMaterialParamOperation(parameterName, newValue, oldValue));
    }

    @Override
    public void reload() {
        super.reload();

        final Material material = getMaterial();
        final MatParam param = material.getParam(getParameterName());
        final SpinnerValueFactory<Integer> valueFactory = spinner.getValueFactory();

        if (param == null) {
            valueFactory.setValue(0);
            return;
        }

        valueFactory.setValue((Integer) param.getValue());
    }
}
