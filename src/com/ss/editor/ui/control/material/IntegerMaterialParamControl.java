package com.ss.editor.ui.control.material;

import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.ss.editor.model.undo.EditorOperation;
import com.ss.editor.ui.control.material.operation.IntegerMaterialParamOperation;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;

import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import rlib.ui.util.FXUtils;

/**
 * Реализация контрола для установки целочисленного значения.
 *
 * @author Ronn
 */
public class IntegerMaterialParamControl extends MaterialParamControl {

    public static final Insets ELEMENT_OFFSET = new Insets(0, 0, 0, 3);

    /**
     * Контрол для установки целочисленного значения.
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
            spinner.increment();
        } else {
            spinner.decrement();
        }
    }

    /**
     * Процесс обновления целочисленного значения.
     */
    private void processChange(final Integer newValue) {

        if (isIgnoreListeners()) {
            return;
        }

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
