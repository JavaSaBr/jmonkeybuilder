package com.ss.editor.ui.control.material;

import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;

import javafx.geometry.Insets;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.layout.HBox;
import rlib.ui.util.FXUtils;

/**
 * Реализация контрола для установки целочисленного значения.
 *
 * @author Ronn
 */
public class IntegerMaterialParamControl extends MaterialParamControl {

    public static final Insets ELEMENT_OFFSET = new Insets(0, 0, 0, 3);

    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    /**
     * Контрол для установки целочисленного значения.
     */
    private Spinner<Integer> spinner;

    public IntegerMaterialParamControl(final Runnable changeHandler, final Material material, final String parameterName) {
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
        spinner.valueProperty().addListener((observable, oldValue, newValue) -> processChange(newValue));

        FXUtils.addToPane(spinner, this);
        FXUtils.addClassTo(spinner, CSSClasses.MAIN_FONT_13);
        FXUtils.bindFixedWidth(getParamNameLabel(), widthProperty().subtract(90));

        HBox.setMargin(spinner, ELEMENT_OFFSET);
    }

    /**
     * Процесс обновления целочисленного значения.
     */
    private void processChange(final Integer newValue) {

        if (isIgnoreListeners()) {
            return;
        }

        EXECUTOR_MANAGER.addEditorThreadTask(() -> processChangeImpl(newValue));
    }

    /**
     * Процесс изменения целочисленного значения.
     */
    private void processChangeImpl(final Integer newValue) {

        final Material material = getMaterial();

        if (newValue == null) {
            material.clearParam(getParameterName());
        } else {
            material.setInt(getParameterName(), newValue);
        }

        EXECUTOR_MANAGER.addFXTask(() -> {
            changed();
            setIgnoreListeners(true);
            try {
                reload();
            } finally {
                setIgnoreListeners(false);
            }
        });
    }

    @Override
    protected void reload() {
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
