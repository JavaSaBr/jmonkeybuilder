package com.ss.editor.ui.control.property.impl;

import com.jme3.math.Vector3f;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.util.UiUtils;
import com.ss.rlib.fx.util.FxControlUtils;
import com.ss.rlib.fx.util.FxUtils;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link PropertyControl} to edit {@link com.jme3.math.Vector3f} values.
 *
 * @param <C> the type of a change consumer.
 * @param <D> the type of an editing object.
 * @author JavaSaBr.
 */
public class Vector3fSingleRowPropertyControl<C extends ChangeConsumer, D> extends Vector3fPropertyControl<C, D> {

    /**
     * The field container.
     */
    @NotNull
    private final HBox fieldContainer;

    public Vector3fSingleRowPropertyControl(
            @Nullable Vector3f propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer
    ) {
        super(propertyValue, propertyName, changeConsumer);
        this.fieldContainer = new HBox();
    }

    @Override
    @FxThread
    public void changeControlWidthPercent(double controlWidthPercent) {
        super.changeControlWidthPercent(controlWidthPercent);

        FxUtils.rebindPrefWidth(fieldContainer,
                widthProperty().multiply(controlWidthPercent));
    }

    @Override
    @FxThread
    protected void createControls(@NotNull HBox container) {

        fieldContainer.prefWidthProperty()
            .bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));

        xField.setOnKeyReleased(this::keyReleased);
        xField.setScrollPower(10F);
        xField.prefWidthProperty().
                bind(fieldContainer.widthProperty().multiply(0.33));

        yField.setOnKeyReleased(this::keyReleased);
        yField.setScrollPower(10F);
        yField.prefWidthProperty()
                .bind(fieldContainer.widthProperty().multiply(0.33));

        zField.setOnKeyReleased(this::keyReleased);
        zField.setScrollPower(10F);
        zField.prefWidthProperty()
                .bind(fieldContainer.widthProperty().multiply(0.33));

        FxControlUtils.onValueChange(xField, this::changeValue);
        FxControlUtils.onValueChange(yField, this::changeValue);
        FxControlUtils.onValueChange(zField, this::changeValue);

        FxUtils.addClass(fieldContainer,
                        CssClasses.DEF_HBOX,
                        CssClasses.TEXT_INPUT_CONTAINER,
                        CssClasses.ABSTRACT_PARAM_CONTROL_SHORT_INPUT_CONTAINER)
                .addClass(xField, yField, zField,
                        CssClasses.TRANSPARENT_TEXT_FIELD);

        FxUtils.addChild(fieldContainer, xField, yField, zField)
                .addChild(container, fieldContainer);

        UiUtils.addFocusBinding(fieldContainer, xField, yField, zField)
            .addListener((observable, oldValue, newValue) -> applyOnLostFocus(newValue));
    }

    @Override
    @FromAnyThread
    protected boolean isSingleRow() {
        return true;
    }
}
