package com.ss.editor.ui.control.property.impl;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.math.Vector3f;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.AbstractPropertyControl;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.util.UIUtils;
import com.ss.rlib.function.SixObjectConsumer;
import com.ss.rlib.ui.control.input.FloatTextField;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

/**
 * The implementation of the {@link AbstractPropertyControl} to edit {@link Vector3f} values.
 *
 * @param <C> the type of a {@link ChangeConsumer}
 * @param <T> the type of an editing object.
 * @author JavaSaBr
 */
public abstract class AbstractVector3fPropertyControl<C extends ChangeConsumer, T> extends
        AbstractPropertyControl<C, T, Vector3f> {

    /**
     * The field X.
     */
    @Nullable
    private FloatTextField xField;

    /**
     * The field Y.
     */
    @Nullable
    private FloatTextField yField;

    /**
     * The field Z.
     */
    @Nullable
    private FloatTextField zField;

    /**
     * Instantiates a new Abstract vector 3 f property control.
     *
     * @param propertyValue  the property value
     * @param propertyName   the property name
     * @param changeConsumer the change consumer
     * @param changeHandler  the change handler
     */
    public AbstractVector3fPropertyControl(@Nullable final Vector3f propertyValue, @NotNull final String propertyName,
                                           @NotNull final C changeConsumer,
                                           @NotNull final SixObjectConsumer<C, T, String, Vector3f, Vector3f, BiConsumer<T, Vector3f>> changeHandler) {
        super(propertyValue, propertyName, changeConsumer, changeHandler);
    }

    @Override
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        final Label xLabel = new Label("x:");

        xField = new FloatTextField();
        xField.setOnKeyReleased(this::updateVector);
        xField.addChangeListener((observable, oldValue, newValue) -> updateVector(null));
        xField.prefWidthProperty().bind(widthProperty().divide(3));
        xField.setScrollPower(getScrollPower());

        final Label yLabel = new Label("y:");

        yField = new FloatTextField();
        yField.setOnKeyReleased(this::updateVector);
        yField.addChangeListener((observable, oldValue, newValue) -> updateVector(null));
        yField.prefWidthProperty().bind(widthProperty().divide(3));
        yField.setScrollPower(getScrollPower());

        final Label zLabel = new Label("z:");

        zField = new FloatTextField();
        zField.setOnKeyReleased(this::updateVector);
        zField.addChangeListener((observable, oldValue, newValue) -> updateVector(null));
        zField.prefWidthProperty().bind(widthProperty().divide(3));
        zField.setScrollPower(getScrollPower());

        FXUtils.addToPane(xLabel, container);
        FXUtils.addToPane(xField, container);
        FXUtils.addToPane(yLabel, container);
        FXUtils.addToPane(yField, container);
        FXUtils.addToPane(zLabel, container);
        FXUtils.addToPane(zField, container);

        FXUtils.addClassTo(xLabel, yLabel, zLabel, CSSClasses.ABSTRACT_PARAM_CONTROL_NUMBER_LABEL);
        FXUtils.addClassesTo(container, CSSClasses.DEF_HBOX, CSSClasses.TEXT_INPUT_CONTAINER,
                CSSClasses.ABSTRACT_PARAM_CONTROL_MULTI_VALUE_CONTAINER);
        FXUtils.addClassesTo(xField, yField, zField, CSSClasses.ABSTRACT_PARAM_CONTROL_VECTOR3F_FIELD,
                CSSClasses.TRANSPARENT_TEXT_FIELD);
    }

    /**
     * Gets scroll power.
     *
     * @return the scroll power.
     */
    protected float getScrollPower() {
        return 10F;
    }

    /**
     * Gets x field.
     *
     * @return the field X.
     */
    @NotNull
    protected FloatTextField getXField() {
        return notNull(xField);
    }

    /**
     * Gets y filed.
     *
     * @return the field Y.
     */
    @NotNull
    protected FloatTextField getYFiled() {
        return notNull(yField);
    }

    /**
     * Gets z field.
     *
     * @return the field Z.
     */
    @NotNull
    protected FloatTextField getZField() {
        return notNull(zField);
    }

    @Override
    protected void reload() {

        final Vector3f vector = getPropertyValue() == null ? Vector3f.ZERO : getPropertyValue();

        final FloatTextField xField = getXField();
        xField.setValue(vector.getX());
        xField.positionCaret(xField.getText().length());

        final FloatTextField yFiled = getYFiled();
        yFiled.setValue(vector.getY());
        yFiled.positionCaret(xField.getText().length());

        final FloatTextField zField = getZField();
        zField.setValue(vector.getZ());
        zField.positionCaret(xField.getText().length());
    }

    /**
     * Update the vector.
     *
     * @param event the event
     */
    protected void updateVector(@Nullable final KeyEvent event) {
        UIUtils.consumeIfIsNotHotKey(event);

        if (isIgnoreListener() || (event != null && event.getCode() != KeyCode.ENTER)) return;

        final FloatTextField xField = getXField();
        final float x = xField.getValue();

        final FloatTextField yFiled = getYFiled();
        final float y = yFiled.getValue();

        final FloatTextField zField = getZField();
        final float z = zField.getValue();

        final Vector3f oldValue = getPropertyValue() == null ? Vector3f.ZERO : getPropertyValue();
        final Vector3f newValue = new Vector3f();
        newValue.set(x, y, z);

        changed(newValue, oldValue.clone());
    }
}
