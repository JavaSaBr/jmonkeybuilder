package com.ss.editor.ui.dialog.factory.control;

import com.jme3.math.Vector3f;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.dialog.factory.PropertyDefinition;
import com.ss.editor.ui.util.UIUtils;
import com.ss.rlib.ui.control.input.FloatTextField;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.VarTable;
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

    protected Vector3fPropertyEditorControl(@NotNull final VarTable vars, @NotNull final PropertyDefinition definition,
                                            @NotNull final Runnable validationCallback) {
        super(vars, definition, validationCallback);
    }

    @Override
    protected void createComponents() {
        super.createComponents();

        final GridPane gridPane = new GridPane();
        gridPane.prefWidthProperty().bind(widthProperty().multiply(0.5F));

        xField = new FloatTextField();
        xField.setOnKeyReleased(UIUtils::consumeIfIsNotHotKey);
        xField.addChangeListener((observable, oldValue, newValue) -> change());
        xField.prefWidthProperty().bind(gridPane.widthProperty().divide(3));

        yField = new FloatTextField();
        yField.setOnKeyReleased(UIUtils::consumeIfIsNotHotKey);
        yField.addChangeListener((observable, oldValue, newValue) -> change());
        yField.prefWidthProperty().bind(gridPane.widthProperty().divide(3));

        zField = new FloatTextField();
        zField.setOnKeyReleased(UIUtils::consumeIfIsNotHotKey);
        zField.addChangeListener((observable, oldValue, newValue) -> change());
        zField.prefWidthProperty().bind(gridPane.widthProperty().divide(3));

        gridPane.add(xField, 0, 0);
        gridPane.add(yField, 1, 0);
        gridPane.add(zField, 2, 0);

        FXUtils.addClassesTo(gridPane, CSSClasses.DEF_GRID_PANE, CSSClasses.TEXT_INPUT_CONTAINER);
        FXUtils.addClassesTo(xField, yField, zField, CSSClasses.ABSTRACT_PARAM_CONTROL_VECTOR3F_FIELD,
                CSSClasses.TRANSPARENT_TEXT_FIELD);

        FXUtils.addToPane(gridPane, this);
    }

    /**
     * @return the field X.
     */
    @NotNull
    private FloatTextField getXField() {
        return xField;
    }

    /**
     * @return the field Y.
     */
    @NotNull
    private FloatTextField getYField() {
        return yField;
    }

    /**
     * @return the field Z.
     */
    @NotNull
    private FloatTextField getZField() {
        return zField;
    }

    @Override
    protected void reload() {
        super.reload();

        final Vector3f value = getPropertyValue();

        final FloatTextField xField = getXField();
        xField.setValue(value == null ? 0 : value.getX());

        final FloatTextField yField = getYField();
        yField.setValue(value == null ? 0 : value.getY());

        final FloatTextField zField = getZField();
        zField.setValue(value == null ? 0 : value.getZ());
    }

    @Override
    protected void change() {

        final FloatTextField xField = getXField();
        final FloatTextField yField = getYField();
        final FloatTextField zField = getZField();

        setPropertyValue(new Vector3f(xField.getValue(), yField.getValue(), zField.getValue()));

        super.change();
    }
}
