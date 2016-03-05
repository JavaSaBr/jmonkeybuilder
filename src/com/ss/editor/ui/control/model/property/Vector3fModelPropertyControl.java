package com.ss.editor.ui.control.model.property;

import com.jme3.math.Vector3f;
import com.ss.editor.ui.css.CSSIds;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import rlib.ui.util.FXUtils;

/**
 * Реализация контрола по редактированию вектора.
 *
 * @author Ronn
 */
public class Vector3fModelPropertyControl extends ModelPropertyControl<Vector3f> {

    /**
     * Поле X.
     */
    private TextField xField;

    /**
     * Поле Y.
     */
    private TextField yFiled;

    /**
     * Поле Z.
     */
    private TextField zField;

    public Vector3fModelPropertyControl(final Runnable changeHandler, final Vector3f element, final String paramName) {
        super(changeHandler, element, paramName);
    }

    @Override
    protected void createComponents(final HBox container) {
        super.createComponents(container);

        final Label xLabel = new Label("x:");
        xLabel.setId(CSSIds.MODEL_PARAM_CONTROL_NUMBER_LABEL);

        xField = new TextField();
        xField.setId(CSSIds.MODEL_PARAM_CONTROL_NUMBER_FIELD);
        xField.textProperty().addListener((observable, oldValue, newValue) -> updateVector());

        final Label yLabel = new Label("y:");
        yLabel.setId(CSSIds.MODEL_PARAM_CONTROL_NUMBER_LABEL);

        yFiled = new TextField();
        yFiled.setId(CSSIds.MODEL_PARAM_CONTROL_NUMBER_FIELD);
        yFiled.textProperty().addListener((observable, oldValue, newValue) -> updateVector());

        final Label zLabel = new Label("z:");
        zLabel.setId(CSSIds.MODEL_PARAM_CONTROL_NUMBER_LABEL);

        zField = new TextField();
        zField.setId(CSSIds.MODEL_PARAM_CONTROL_NUMBER_FIELD);
        zField.textProperty().addListener((observable, oldValue, newValue) -> updateVector());

        FXUtils.addToPane(xLabel, container);
        FXUtils.addToPane(xField, container);
        FXUtils.addToPane(yLabel, container);
        FXUtils.addToPane(yFiled, container);
        FXUtils.addToPane(zLabel, container);
        FXUtils.addToPane(zField, container);
    }

    /**
     * @return поле X.
     */
    private TextField getXField() {
        return xField;
    }

    /**
     * @return поле Y.
     */
    private TextField getYFiled() {
        return yFiled;
    }

    /**
     * @return поле Z.
     */
    private TextField getZField() {
        return zField;
    }

    @Override
    protected void reload() {

        final Vector3f element = getElement();

        final TextField xField = getXField();
        xField.setText(String.valueOf(element.getX()));

        final TextField yFiled = getYFiled();
        yFiled.setText(String.valueOf(element.getY()));

        final TextField zField = getZField();
        zField.setText(String.valueOf(element.getZ()));
    }

    /**
     * Обновление вектора.
     */
    private void updateVector() {

        if (isIgnoreListener()) {
            return;
        }

        final TextField xField = getXField();

        float x = 0;

        try {
            x = Float.parseFloat(xField.getText());
        } catch (final NumberFormatException e) {
            return;
        }

        final TextField yFiled = getYFiled();

        float y = 0;

        try {
            y = Float.parseFloat(yFiled.getText());
        } catch (final NumberFormatException e) {
            return;
        }

        final TextField zField = getZField();

        float z = 0;

        try {
            z = Float.parseFloat(zField.getText());
        } catch (final NumberFormatException e) {
            return;
        }

        final Vector3f element = getElement();
        element.set(x, y, z);

        changed();
    }
}
