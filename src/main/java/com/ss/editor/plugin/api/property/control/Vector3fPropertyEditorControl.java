package com.ss.editor.plugin.api.property.control;

import com.jme3.math.Vector3f;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.util.UiUtils;
import com.ss.rlib.common.util.VarTable;
import com.ss.rlib.fx.control.input.FloatTextField;
import com.ss.rlib.fx.util.FxControlUtils;
import com.ss.rlib.fx.util.FxUtils;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;

/**
 * The control to edit float values.
 *
 * @author JavaSaBr
 */
public class Vector3fPropertyEditorControl extends PropertyEditorControl<Vector3f> {

    /**
     * The field X.
     */
    @NotNull
    private final FloatTextField xField;

    /**
     * The field Y.
     */
    @NotNull
    private final FloatTextField yField;

    /**
     * The field Z.
     */
    @NotNull
    private final FloatTextField zField;

    protected Vector3fPropertyEditorControl(
            @NotNull VarTable vars,
            @NotNull PropertyDefinition definition,
            @NotNull Runnable validationCallback
    ) {
        super(vars, definition, validationCallback);
        this.xField = new FloatTextField();
        this.yField = new FloatTextField();
        this.zField = new FloatTextField();
    }

    @Override
    @FxThread
    public void postConstruct() {
        super.postConstruct();

        var resultWidth = widthProperty()
                .multiply(DEFAULT_FIELD_W_PERCENT);

        var gridPane = new GridPane();
        gridPane.prefWidthProperty()
                .bind(resultWidth);

        var fieldWidth = gridPane.widthProperty()
                .divide(3);

        xField.prefWidthProperty()
                .bind(fieldWidth);
        yField.prefWidthProperty()
                .bind(fieldWidth);
        zField.prefWidthProperty()
                .bind(fieldWidth);

        gridPane.add(xField, 0, 0);
        gridPane.add(yField, 1, 0);
        gridPane.add(zField, 2, 0);

        FxControlUtils.onValueChange(xField, this::changed);
        FxControlUtils.onValueChange(yField, this::changed);
        FxControlUtils.onValueChange(zField, this::changed);

        FxUtils.addClass(gridPane,
                        CssClasses.DEF_GRID_PANE, CssClasses.TEXT_INPUT_CONTAINER)
                .addClass(xField, yField, zField,
                        CssClasses.TRANSPARENT_TEXT_FIELD,
                        CssClasses.PROPERTY_CONTROL_VECTOR_3F_FIELD);

        FxUtils.addChild(this, gridPane);

        UiUtils.addFocusBinding(gridPane, xField, yField, zField);
    }

    @Override
    @FxThread
    protected void reloadImpl() {

        var value = getPropertyValue();

        xField.setValue(value == null ? 0 : value.getX());
        yField.setValue(value == null ? 0 : value.getY());
        zField.setValue(value == null ? 0 : value.getZ());

        super.reloadImpl();
    }

    @Override
    @FxThread
    protected void changedImpl() {

        setPropertyValue(new Vector3f(
                xField.getPrimitiveValue(),
                yField.getPrimitiveValue(),
                zField.getPrimitiveValue()));

        super.changedImpl();
    }
}
