package com.ss.editor.plugin.api.property.control;

import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.dialog.AbstractSimpleEditorDialog;
import com.ss.editor.ui.util.UiUtils;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.VarTable;
import com.ss.rlib.util.array.Array;
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
     * The dependencies.
     */
    @NotNull
    private final Array<String> dependencies;

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
        this.dependencies = definition.getDependencies();

        final Object defaultValue = definition.getDefaultValue();

        if (defaultValue != null) {
            vars.set(id, defaultValue);
        }

        setOnKeyReleased(UiUtils::consumeIfIsNotHotKey);
        setOnKeyPressed(UiUtils::consumeIfIsNotHotKey);
        createComponents();
        setIgnoreListener(true);
        try {
            reload();
        } finally {
            setIgnoreListener(false);
        }

        FXUtils.addClassTo(this, CssClasses.ABSTRACT_PARAM_EDITOR_CONTROL);
    }

    /**
     * Check dependency of this control.
     */
    @FxThread
    public void checkDependency() {

        final Array<String> dependencies = getDependencies();
        if (dependencies.isEmpty()) return;

        setDisable(false);

        for (final String dependency : dependencies) {

            if (!vars.has(dependency)) {
                setDisable(true);
                return;
            }

            final Object value = vars.get(dependency);

            if (value instanceof Boolean) {
                setDisable(!(Boolean) value);
            }

            if (isDisable()) {
                return;
            }
        }
    }

    /**
     * Get the dependencies.
     *
     * @return the dependencies.
     */
    @FxThread
    protected @NotNull Array<String> getDependencies() {
        return dependencies;
    }

    @FxThread
    protected void reload() {
    }

    @FxThread
    protected void change() {
        if (isIgnoreListener()) return;
        changeImpl();
    }

    @FxThread
    protected void changeImpl() {
        validationCallback.run();
    }

    @FxThread
    protected void createComponents() {
        setAlignment(Pos.CENTER_RIGHT);

        propertyNameLabel = new Label(getName() + ":");
        propertyNameLabel.prefWidthProperty().bind(widthProperty().multiply(DEFAULT_LABEL_W_PERCENT));

        FXUtils.addClassTo(propertyNameLabel, CssClasses.ABSTRACT_PARAM_CONTROL_PARAM_NAME_SINGLE_ROW);
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
    @FxThread
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
    @FxThread
    protected void setIgnoreListener(final boolean ignoreListener) {
        this.ignoreListener = ignoreListener;
    }

    /**
     * Is ignore listener boolean.
     *
     * @return true if need to ignore listeners.
     */
    @FxThread
    protected boolean isIgnoreListener() {
        return ignoreListener;
    }
}
