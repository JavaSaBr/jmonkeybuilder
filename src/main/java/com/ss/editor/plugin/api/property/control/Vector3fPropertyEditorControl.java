package com.ss.editor.plugin.api.property.control;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
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
import org.jetbrains.annotations.Nullable;

/**
 * The control to edit float values.
 *
 * @author JavaSaBr
 */
public class Vector3fPropertyEditorControl extends PropertyEditorControl<Vector3f> {

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

    protected Vector3fPropertyEditorControl(
            @NotNull VarTable vars,
            @NotNull PropertyDefinition definition,
            @NotNull Runnable validationCallback
    ) {
        super(vars, definition, validationCallback);
    }

    @Override
    @FxThread
    protected void postConstruct() {
        super.postConstruct();

        var resultWidth = widthProperty()
                .multiply(DEFAULT_FIELD_W_PERCENT);

        var gridPane = new GridPane();
        gridPane.prefWidthProperty()
                .bind(resultWidth);

        var fieldWidth = gridPane.widthProperty()
                .divide(3);

        xField = new FloatTextField();
        xField.prefWidthProperty().bind(fieldWidth);

        yField = new FloatTextField();
        yField.prefWidthProperty().bind(fieldWidth);

        zField = new FloatTextField();
        zField.prefWidthProperty().bind(fieldWidth);

        gridPane.add(xField, 0, 0);
        gridPane.add(yField, 1, 0);
        gridPane.add(zField, 2, 0);

        FxControlUtils.onValueChange(xField, this::change);
        FxControlUtils.onValueChange(yField, this::change);
        FxControlUtils.onValueChange(zField, this::change);

        FxUtils.addClass(gridPane,
                        CssClasses.DEF_GRID_PANE, CssClasses.TEXT_INPUT_CONTAINER)
                .addClass(xField, yField, zField,
                        CssClasses.TRANSPARENT_TEXT_FIELD,
                        CssClasses.PROPERTY_CONTROL_VECTOR_3F_FIELD);

        FxUtils.addChild(this, gridPane);

        UiUtils.addFocusBinding(gridPane, xField, yField, zField);
    }

    /**
     * Get the field X.
     *
     * @return the field X.
     */
    @FxThread
    private @NotNull FloatTextField getXField() {
        return notNull(xField);
    }

    /**
     * Get the field Y.
     *
     * @return the field Y.
     */
    @FxThread
    private @NotNull FloatTextField getYField() {
        return notNull(yField);
    }

    /**
     * Get the field Z.
     *
     * @return the field Z.
     */
    @FxThread
    private @NotNull FloatTextField getZField() {
        return notNull(zField);
    }

    @Override
    @FxThread
    public void reload() {
        super.reload();

        var value = getPropertyValue();

        var xField = getXField();
        xField.setValue(value == null ? 0 : value.getX());

        var yField = getYField();
        yField.setValue(value == null ? 0 : value.getY());

        var zField = getZField();
        zField.setValue(value == null ? 0 : value.getZ());
    }

    @Override
    @FxThread
    protected void changeImpl() {
        var xField = getXField();
        var yField = getYField();
        var zField = getZField();
        setPropertyValue(new Vector3f(xField.getValue(), yField.getValue(), zField.getValue()));
        super.changeImpl();
    }
}
