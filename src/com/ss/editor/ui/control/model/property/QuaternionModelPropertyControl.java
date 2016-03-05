package com.ss.editor.ui.control.model.property;

import com.jme3.math.Quaternion;
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
public class QuaternionModelPropertyControl extends ModelPropertyControl<Quaternion> {

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

    /**
     * Поле W.
     */
    private TextField wField;

    public QuaternionModelPropertyControl(final Runnable changeHandler, final Quaternion element, final String paramName) {
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

        final Label wLabel = new Label("w:");
        wLabel.setId(CSSIds.MODEL_PARAM_CONTROL_NUMBER_LABEL);

        wField = new TextField();
        wField.setId(CSSIds.MODEL_PARAM_CONTROL_NUMBER_FIELD);
        wField.textProperty().addListener((observable, oldValue, newValue) -> updateVector());

        FXUtils.addToPane(xLabel, container);
        FXUtils.addToPane(xField, container);
        FXUtils.addToPane(yLabel, container);
        FXUtils.addToPane(yFiled, container);
        FXUtils.addToPane(zLabel, container);
        FXUtils.addToPane(zField, container);
        FXUtils.addToPane(wLabel, container);
        FXUtils.addToPane(wField, container);
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

    /**
     * @return поле W.
     */
    private TextField getWField() {
        return wField;
    }

    @Override
    protected void reload() {

        final Quaternion element = getElement();

        final TextField xField = getXField();
        xField.setText(String.valueOf(element.getX()));

        final TextField yFiled = getYFiled();
        yFiled.setText(String.valueOf(element.getY()));

        final TextField zField = getZField();
        zField.setText(String.valueOf(element.getZ()));

        final TextField wField = getWField();
        wField.setText(String.valueOf(element.getW()));
    }

    /**
     * Обновление вектора.
     */
    private void updateVector() {

        if(isIgnoreListener()) {
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

        final TextField wField = getWField();

        float w = 0;

        try {
            w = Float.parseFloat(wField.getText());
        } catch (final NumberFormatException e) {
            return;
        }

        final Quaternion element = getElement();
        element.set(x, y, z, w);

        changed();
    }
}
