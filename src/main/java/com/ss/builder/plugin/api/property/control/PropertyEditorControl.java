package com.ss.builder.plugin.api.property.control;

import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.css.CssClasses;
import com.ss.builder.fx.dialog.AbstractSimpleEditorDialog;
import com.ss.builder.fx.util.UiUtils;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.editor.extension.property.EditablePropertyType;
import com.ss.builder.plugin.api.property.PropertyDefinition;
import com.ss.builder.fx.css.CssClasses;
import com.ss.builder.fx.dialog.AbstractSimpleEditorDialog;
import com.ss.builder.fx.util.UiUtils;
import com.ss.rlib.common.util.VarTable;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.fx.util.FxUtils;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

/**
 * The base implementation of an editor control to edit properties on the factory dialog.
 *
 * @author JavaSaBr
 */
public class PropertyEditorControl<T> extends HBox {

    public static final double DEFAULT_LABEL_W_PERCENT =
            AbstractSimpleEditorDialog.DEFAULT_LABEL_W_PERCENT;

    public static final double DEFAULT_FIELD_W_PERCENT =
            AbstractSimpleEditorDialog.DEFAULT_FIELD_W_PERCENT;

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
     * The property type.
     */
    @NotNull
    private final EditablePropertyType propertyType;

    /**
     * The default value.
     */
    @Nullable
    private final Object defaultValue;

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

    /**
     * The flag to mark this control that this control was constructed fully.
     */
    private boolean constructed;

    protected PropertyEditorControl(
            @NotNull VarTable vars,
            @NotNull PropertyDefinition definition,
            @NotNull Runnable validationCallback
    ) {
        this.vars = vars;
        this.id = definition.getId();
        this.name = definition.getName();
        this.propertyType = definition.getPropertyType();
        this.validationCallback = validationCallback;
        this.dependencies = definition.getDependencies();
        this.defaultValue = definition.getDefaultValue();

        var defaultValue = definition.getDefaultValue();

        if (defaultValue != null) {
            vars.set(id, defaultValue);
        }

        setOnKeyReleased(UiUtils::consumeIfIsNotHotKey);
        setOnKeyPressed(UiUtils::consumeIfIsNotHotKey);

        FxUtils.addClass(this, CssClasses.PROPERTY_EDITOR_CONTROL);
    }

    /**
     * Check dependency of this control.
     */
    @FxThread
    public void checkDependency() {

        var dependencies = getDependencies();

        if (dependencies.isEmpty()) {
            return;
        }

        setDisable(false);

        for (var dependency : dependencies) {

            if (!vars.has(dependency)) {
                setDisable(true);
                return;
            }

            var value = vars.get(dependency);

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

    /**
     * Get the property id.
     *
     * @return the property id.
     */
    @FxThread
    public @NotNull String getPropertyId() {
        return id;
    }

    /**
     * Get the default value.
     *
     * @return the default value.
     */
    @FxThread
    public @Nullable Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * Get the property type.
     *
     * @return the property type.
     */
    @FxThread
    public @NotNull EditablePropertyType getPropertyType() {
        return propertyType;
    }

    /**
     * Reload value of this control.
     */
    @FxThread
    public void reload() {
        setIgnoreListener(true);
        try {
            reloadImpl();
        } finally {
            setIgnoreListener(false);
        }
    }

    /**
     * Reload value of this control.
     */
    @FxThread
    protected void reloadImpl() {
    }

    @FxThread
    protected void changed() {
        if (!isIgnoreListener()) {
            changedImpl();
        }
    }

    @FxThread
    protected void changedImpl() {
        validationCallback.run();
    }

    @FxThread
    public void postConstruct() {

        if (constructed) {
            return;
        } else {
            constructed = true;
        }

        setAlignment(Pos.CENTER_RIGHT);

        propertyNameLabel = new Label(getName() + ":");
        propertyNameLabel.prefWidthProperty().bind(widthProperty().multiply(DEFAULT_LABEL_W_PERCENT));

        FxUtils.addClass(propertyNameLabel, CssClasses.ABSTRACT_PARAM_CONTROL_PARAM_NAME_SINGLE_ROW);
        FxUtils.addChild(this, propertyNameLabel);
    }

    /**
     * Get the name of the property.
     *
     * @return the name of the property.
     */
    @FromAnyThread
    protected @NotNull String getName() {
        return name;
    }

    /**
     * Get the current property value.
     *
     * @return the current property value.
     */
    @FromAnyThread
    protected @Nullable T getPropertyValue() {
        if (!vars.has(id)) return null;
        return vars.get(id);
    }

    /**
     * Get the current property value.
     *
     * @return the optional value of the current property value.
     */
    @FromAnyThread
    protected @NotNull Optional<T> getPropertyValueOpt() {
        if (!vars.has(id)) {
            return Optional.empty();
        } else {
            return Optional.of(vars.get(id));
        }
    }

    /**
     * Set the new current property value.
     *
     * @param propertyValue the new current property value.
     */
    @FxThread
    protected void setPropertyValue(@Nullable T propertyValue) {
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
    protected void setIgnoreListener(boolean ignoreListener) {
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

    /**
     * Check the current value with the default value.
     *
     * @return true if this property isn't equal to default value.
     */
    @FxThread
    public boolean isNotDefault() {
        var propertyValue = getPropertyValue();
        var defaultValue = getDefaultValue();
        return !Objects.equals(defaultValue, propertyValue);
    }
}
