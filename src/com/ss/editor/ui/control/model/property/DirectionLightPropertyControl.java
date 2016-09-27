package com.ss.editor.ui.control.model.property;

import com.jme3.light.Light;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.operation.LightPropertyOperation;
import com.ss.editor.ui.css.CSSIds;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import rlib.ui.util.FXUtils;
import rlib.util.array.ArrayFactory;

import static java.lang.Float.parseFloat;
import static java.util.Objects.requireNonNull;

/**
 * The implementation of the {@link ModelPropertyControl} for editing direction's vector of the
 * {@link Light}.
 *
 * @author JavaSaBr
 */
public class DirectionLightPropertyControl<T extends Light> extends ModelPropertyControl<T, Vector3f> {

    /**
     * The field X.
     */
    private TextField xField;

    /**
     * The field Y.
     */
    private TextField yFiled;

    /**
     * The field Z.
     */
    private TextField zField;

    public DirectionLightPropertyControl(@NotNull final Vector3f element, @NotNull final String paramName, @NotNull final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer);
    }

    @Override
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        final Label xLabel = new Label("x:");
        xLabel.setId(CSSIds.MODEL_PARAM_CONTROL_NUMBER_LABEL);

        xField = new TextField();
        xField.setId(CSSIds.MODEL_PARAM_CONTROL_VECTOR3F_FIELD);
        xField.setOnScroll(this::processScroll);
        xField.textProperty().addListener((observable, oldValue, newValue) -> updateVector());

        final Label yLabel = new Label("y:");
        yLabel.setId(CSSIds.MODEL_PARAM_CONTROL_NUMBER_LABEL);

        yFiled = new TextField();
        yFiled.setId(CSSIds.MODEL_PARAM_CONTROL_VECTOR3F_FIELD);
        yFiled.setOnScroll(this::processScroll);
        yFiled.textProperty().addListener((observable, oldValue, newValue) -> updateVector());

        final Label zLabel = new Label("z:");
        zLabel.setId(CSSIds.MODEL_PARAM_CONTROL_NUMBER_LABEL);

        zField = new TextField();
        zField.setId(CSSIds.MODEL_PARAM_CONTROL_VECTOR3F_FIELD);
        zField.setOnScroll(this::processScroll);
        zField.textProperty().addListener((observable, oldValue, newValue) -> updateVector());

        FXUtils.addToPane(xLabel, container);
        FXUtils.addToPane(xField, container);
        FXUtils.addToPane(yLabel, container);
        FXUtils.addToPane(yFiled, container);
        FXUtils.addToPane(zLabel, container);
        FXUtils.addToPane(zField, container);
    }

    /**
     * The process of value's scrolling.
     */
    private void processScroll(final ScrollEvent event) {
        if (!event.isControlDown()) return;

        final TextField source = (TextField) event.getSource();
        final String text = source.getText();

        float value;

        try {
            value = parseFloat(text);
        } catch (final NumberFormatException e) {
            return;
        }

        long longValue = (long) (value * 1000);
        longValue += event.getDeltaY();

        source.setText(String.valueOf(longValue / 1000F));
    }

    /**
     * @return the field X.
     */
    private TextField getXField() {
        return xField;
    }

    /**
     * @return the field Y.
     */
    private TextField getYFiled() {
        return yFiled;
    }

    /**
     * @return the field Z.
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
     * Update the vector.
     */
    private void updateVector() {
        if (isIgnoreListener()) return;

        final TextField xField = getXField();
        final TextField yFiled = getYFiled();
        final TextField zField = getZField();

        float x;
        try {
            x = parseFloat(xField.getText());
        } catch (final NumberFormatException e) {
            return;
        }

        float y;
        try {
            y = parseFloat(yFiled.getText());
        } catch (final NumberFormatException e) {
            return;
        }

        float z;
        try {
            z = parseFloat(zField.getText());
        } catch (final NumberFormatException e) {
            return;
        }

        final Quaternion rotation = new Quaternion();
        rotation.fromAngles(ArrayFactory.toFloatArray(x, y, z));

        final Vector3f oldValue = requireNonNull(getPropertyValue());
        final Vector3f newValue = new Vector3f(x, y, z);
        newValue.normalizeLocal();

        changed(newValue, oldValue.clone());
    }

    @Override
    protected void changed(@Nullable final Vector3f newValue, @Nullable final Vector3f oldValue) {

        final T editObject = getEditObject();

        final LightPropertyOperation<T, Vector3f> operation = new LightPropertyOperation<>(editObject, getPropertyName(), newValue, oldValue);
        operation.setApplyHandler(getApplyHandler());

        final ModelChangeConsumer modelChangeConsumer = getModelChangeConsumer();
        modelChangeConsumer.execute(operation);
    }
}
