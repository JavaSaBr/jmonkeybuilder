package com.ss.editor.plugin.api.property.control;

import com.ss.editor.Editor;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.dialog.AbstractSimpleEditorDialog;
import com.ss.editor.ui.util.UIUtils;
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

    /**
     * The constant DEFAULT_LABEL_W_PERCENT.
     */
    public static final double DEFAULT_LABEL_W_PERCENT = AbstractSimpleEditorDialog.DEFAULT_LABEL_W_PERCENT;

    /**
     * The constant DEFAULT_FIELD_W_PERCENT.
     */
    public static final double DEFAULT_FIELD_W_PERCENT = AbstractSimpleEditorDialog.DEFAULT_FIELD_W_PERCENT;

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

        setOnKeyReleased(UIUtils::consumeIfIsNotHotKey);
        setOnKeyPressed(UIUtils::consumeIfIsNotHotKey);
        createComponents();
        setIgnoreListener(true);
        try {
            reload();
        } finally {
            setIgnoreListener(false);
        }

        FXUtils.addClassTo(this, CSSClasses.ABSTRACT_PARAM_EDITOR_CONTROL);
    }

    @FXThread
    protected void reload() {
    }

    @FXThread
    protected void change() {
        if (isIgnoreListener()) return;
        changeImpl();
    }

    @FXThread
    protected void changeImpl() {
        validationCallback.run();
    }

    @FXThread
    protected void createComponents() {
        setAlignment(Pos.CENTER_RIGHT);

        propertyNameLabel = new Label(getName() + ":");
        propertyNameLabel.prefWidthProperty().bind(widthProperty().multiply(DEFAULT_LABEL_W_PERCENT));

        FXUtils.addClassTo(propertyNameLabel, CSSClasses.ABSTRACT_PARAM_CONTROL_PARAM_NAME_SINGLE_ROW);
        FXUtils.addToPane(propertyNameLabel, this);
    }

    /**
     * Gets a name of the property.
     *
     * @return the name of the property.
     */
    @FromAnyThread
    protected @NotNull String getName() {
        return name;
    }

    /**
     * Gets a current property value.
     *
     * @return the current property value.
     */
    @FromAnyThread
    protected @Nullable T getPropertyValue() {
        if (!vars.has(id)) return null;
        return vars.get(id);
    }

    /**
     * Sets a new current property value.
     *
     * @param propertyValue the new current property value.
     */
    @FXThread
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
    @FXThread
    protected void setIgnoreListener(final boolean ignoreListener) {
        this.ignoreListener = ignoreListener;
    }

    /**
     * Is ignore listener boolean.
     *
     * @return true if need to ignore listeners.
     */
    @FXThread
    protected boolean isIgnoreListener() {
        return ignoreListener;
    }
}
