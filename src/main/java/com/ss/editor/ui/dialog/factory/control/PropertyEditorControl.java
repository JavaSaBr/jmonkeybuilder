package com.ss.editor.ui.dialog.factory.control;

import com.ss.editor.Editor;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.dialog.factory.PropertyDefinition;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.VarTable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The base implementation of an editor control to edit properties on the factory dialog.
 *
 * @author JavaSaBr
 */
public class PropertyEditorControl<T> extends HBox {

    @NotNull
    protected static final Editor EDITOR = Editor.getInstance();

    /**
     * The validation callback to call re-validating.
     */
    @NotNull
    private final Runnable validationCallback;

    /**
     * The table of all variables.
     */
    @NotNull
    private final VarTable vars;

    /**
     * The property id.
     */
    @NotNull
    private final String id;

    /**
     * The property name.
     */
    @NotNull
    private final String name;

    /**
     * The property name label.
     */
    @Nullable
    private Label propertyNameLabel;

    /**
     * The flag for ignoring listeners.
     */
    private boolean ignoreListener;

    protected PropertyEditorControl(@NotNull final VarTable vars, @NotNull final PropertyDefinition definition,
                                    @NotNull final Runnable validationCallback) {
        this.vars = vars;
        this.id = definition.getId();
        this.name = definition.getName();
        this.validationCallback = validationCallback;

        final Object defaultValue = definition.getDefaultValue();

        if (defaultValue != null) {
            vars.set(id, defaultValue);
        }

        createComponents();
        setIgnoreListener(true);
        try {
            reload();
        } finally {
            setIgnoreListener(false);
        }

        FXUtils.addClassTo(this, CSSClasses.ABSTRACT_PARAM_EDITOR_CONTROL);
    }

    protected void reload() {

    }

    protected void change() {
        validationCallback.run();
    }

    protected void createComponents() {
        setAlignment(Pos.CENTER_RIGHT);

        propertyNameLabel = new Label(getName() + ":");
        propertyNameLabel.prefWidthProperty().bind(widthProperty().multiply(0.5F));

        FXUtils.addClassTo(propertyNameLabel, CSSClasses.ABSTRACT_PARAM_CONTROL_PARAM_NAME_SINGLE_ROW);
        FXUtils.addToPane(propertyNameLabel, this);
    }

    /**
     * Gets a name of the property.
     *
     * @return the name of the property.
     */
    @NotNull
    protected String getName() {
        return name;
    }

    /**
     * Gets a current property value.
     *
     * @return the current property value.
     */
    @Nullable
    protected T getPropertyValue() {
        if (!vars.has(id)) return null;
        return vars.get(id);
    }

    /**
     * Sets a new current property value.
     *
     * @param propertyValue the new current property value.
     */
    @Nullable
    protected void setPropertyValue(@Nullable final T propertyValue) {
        if (propertyValue == null) {
            vars.clear(id);
        } else {
            vars.set(id, propertyValue);
        }
    }

    /**
     * Sets ignore listener.
     *
     * @param ignoreListener the flag for ignoring listeners.
     */
    protected void setIgnoreListener(final boolean ignoreListener) {
        this.ignoreListener = ignoreListener;
    }

    /**
     * Is ignore listener boolean.
     *
     * @return true if need to ignore listeners.
     */
    protected boolean isIgnoreListener() {
        return ignoreListener;
    }
}
