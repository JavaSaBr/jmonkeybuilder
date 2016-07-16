package com.ss.editor.ui.control.model.property;

import com.jme3.light.Light;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.operation.LightPropertyOperation;
import com.ss.editor.ui.css.CSSIds;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import rlib.ui.util.FXUtils;
import rlib.util.array.ArrayFactory;

import static java.lang.Float.parseFloat;

/**
 * Реализация контрола по редактированию позиции источника света.
 *
 * @author Ronn
 */
public class PositionLightPropertyControl<T extends Light> extends ModelPropertyControl<T, Vector3f> {

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

    public PositionLightPropertyControl(final Vector3f element, final String paramName, final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer);
    }

    @Override
    protected void createComponents(final HBox container) {
        super.createComponents(container);

        final Label xLabel = new Label("x:");
        xLabel.setId(CSSIds.MODEL_PARAM_CONTROL_NUMBER_LABEL);

        xField = new TextField();
        xField.setId(CSSIds.MODEL_PARAM_CONTROL_VECTOR3F_FIELD);
        xField.setOnScroll(this::processScroll);
        xField.setOnKeyReleased(this::updateVector);

        final Label yLabel = new Label("y:");
        yLabel.setId(CSSIds.MODEL_PARAM_CONTROL_NUMBER_LABEL);

        yFiled = new TextField();
        yFiled.setId(CSSIds.MODEL_PARAM_CONTROL_VECTOR3F_FIELD);
        yFiled.setOnScroll(this::processScroll);
        yFiled.setOnKeyReleased(this::updateVector);

        final Label zLabel = new Label("z:");
        zLabel.setId(CSSIds.MODEL_PARAM_CONTROL_NUMBER_LABEL);

        zField = new TextField();
        zField.setId(CSSIds.MODEL_PARAM_CONTROL_VECTOR3F_FIELD);
        zField.setOnScroll(this::processScroll);
        zField.setOnKeyReleased(this::updateVector);

        FXUtils.addToPane(xLabel, container);
        FXUtils.addToPane(xField, container);
        FXUtils.addToPane(yLabel, container);
        FXUtils.addToPane(yFiled, container);
        FXUtils.addToPane(zLabel, container);
        FXUtils.addToPane(zField, container);
    }

    /**
     * Процесс скролирования значения.
     */
    private void processScroll(final ScrollEvent event) {
        if (!event.isControlDown()) return;

        final TextField source = (TextField) event.getSource();
        final String text = source.getText();

        float value = 0;

        try {
            value = parseFloat(text);
        } catch (final NumberFormatException e) {
            return;
        }

        long longValue = (long) (value * 1000);
        longValue += event.getDeltaY() * 25;

        source.setText(String.valueOf(longValue / 1000F));
        updateVector(null);
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

        final Vector3f element = getPropertyValue();

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
    private void updateVector(final KeyEvent event) {
        if (isIgnoreListener() || (event != null && event.getCode() != KeyCode.ENTER)) return;

        final TextField xField = getXField();

        float x = 0;

        try {
            x = parseFloat(xField.getText());
        } catch (final NumberFormatException e) {
            return;
        }

        final TextField yFiled = getYFiled();

        float y = 0;

        try {
            y = parseFloat(yFiled.getText());
        } catch (final NumberFormatException e) {
            return;
        }

        final TextField zField = getZField();

        float z = 0;

        try {
            z = parseFloat(zField.getText());
        } catch (final NumberFormatException e) {
            return;
        }

        final Quaternion rotation = new Quaternion();
        rotation.fromAngles(ArrayFactory.toFloatArray(x, y, z));

        final Vector3f oldValue = getPropertyValue();
        final Vector3f newValue = new Vector3f(x, y, z);

        changed(newValue, oldValue.clone());
    }

    @Override
    protected void changed(final Vector3f newValue, final Vector3f oldValue) {

        final ModelChangeConsumer modelChangeConsumer = getModelChangeConsumer();
        final T editObject = getEditObject();

        final LightPropertyOperation<T, Vector3f> operation = new LightPropertyOperation<>(editObject, getPropertyName(), newValue, oldValue);
        operation.setApplyHandler(getApplyHandler());

        modelChangeConsumer.execute(operation);
    }
}
