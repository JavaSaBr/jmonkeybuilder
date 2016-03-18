package com.ss.editor.ui.control.material;

import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.EditorOperation;
import com.ss.editor.ui.control.material.operation.FloatMaterialParamOperation;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;

import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import rlib.ui.util.FXUtils;

/**
 * Реализация контрола для установки дробных значения.
 *
 * @author Ronn
 */
public class FloatMaterialParamControl extends MaterialParamControl {

    public static final Insets ELEMENT_OFFSET = new Insets(0, 0, 0, 3);

    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    /**
     * Контрол для установки дробных значения.
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

        FXUtils.addToPane(spinner, this);
        FXUtils.addClassTo(spinner, CSSClasses.MAIN_FONT_13);
        FXUtils.bindFixedWidth(getParamNameLabel(), widthProperty().subtract(90));

        HBox.setMargin(spinner, ELEMENT_OFFSET);
    }

    /**
     * Процесс скролирования значения.
     */
    private void processScroll(final ScrollEvent event) {

        if (!event.isControlDown()) {
            return;
        }

        final double deltaY = event.getDeltaY();

        if (deltaY > 0) {
            spinner.increment(10);
        } else {
            spinner.decrement(10);
        }
    }

    /**
     * Процесс обновления дробных значения.
     */
    private void processChange(final Double newValue) {

        if (isIgnoreListeners()) {
            return;
        }

        final Float newFValue = newValue == null? null : newValue.floatValue();
        final String parameterName = getParameterName();
        final Material material = getMaterial();
        final MatParam param = material.getParam(parameterName);
        final Float oldValue = param == null? null : (Float) param.getValue();

        execute(new FloatMaterialParamOperation(parameterName, newFValue, oldValue));
    }

    @Override
    protected void reload() {
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
