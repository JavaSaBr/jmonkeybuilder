package com.ss.editor.ui.control.property;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.component.asset.tree.context.menu.action.DeleteFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.NewFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.RenameFileAction;
import com.ss.editor.ui.control.UpdatableControl;
import com.ss.editor.ui.control.property.operation.PropertyOperation;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.util.UiUtils;
import com.ss.rlib.common.function.SixObjectConsumer;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.fx.util.FxUtils;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * The base implementation of the property control.
 *
 * @param <C> the type of a change consumer.
 * @param <D> the type of an editing object.
 * @param <T> the type of an editing property.
 * @author JavaSaBr
 */
public class PropertyControl<C extends ChangeConsumer, D, T> extends VBox implements UpdatableControl {

    protected static final Logger LOGGER = LoggerManager.getLogger(PropertyControl.class);

    /**
     * @param <C> the type of a change consumer.
     * @param <D> the type of an editing object.
     * @param <T> the type of an editing property.
     * @author JavaSaBr
     */
    public interface ChangeHandler<C, D, T> extends SixObjectConsumer<C, D, String, T,  T, BiConsumer<D, T>> {
    }

    protected static final Predicate<Class<?>> DEFAULT_ACTION_TESTER = type -> type == NewFileAction.class ||
            type == DeleteFileAction.class || type == RenameFileAction.class;

    public static final double CONTROL_WIDTH_PERCENT = 0.4;
    public static final double CONTROL_WIDTH_PERCENT_2 = 0.6;
    public static final double CONTROL_WIDTH_PERCENT_3 = 0.7;

    @FxThread
    public static void constructProperties(@NotNull Parent parent) {

        var children = parent.getChildrenUnmodifiable();
        children.stream()
                .filter(PropertyControl.class::isInstance)
                .map(node -> (PropertyControl<?, ?, ?>) node)
                .peek(PropertyControl::postConstruct)
                .forEach(PropertyControl::reload);
    }

    /**
     * The change handler.
     */
    @NotNull
    private final SixObjectConsumer<C, D, String, T, T, BiConsumer<D, T>> changeHandler;

    /**
     * The name of the property.
     */
    @NotNull
    private final String propertyName;

    /**
     * The consumer of changes.
     */
    @NotNull
    private final C changeConsumer;

    /**
     * The label of the property name.
     */
    @NotNull
    protected final Label propertyNameLabel;

    /**
     * The edit object.
     */
    @Nullable
    private D editObject;

    /**
     * The value of the property.
     */
    @Nullable
    private T propertyValue;

    /**
     * The handler for handling new value.
     */
    @Nullable
    private BiConsumer<D, T> applyHandler;

    /**
     * The handler for getting actual value.
     */
    @Nullable
    private Function<D, T> syncHandler;

    /**
     * The flag for ignoring listeners.
     */
    private boolean ignoreListener;

    public PropertyControl(@Nullable T propertyValue, @NotNull String propertyName, @NotNull C changeConsumer) {
        this(propertyValue, propertyName, changeConsumer, null);
    }

    public PropertyControl(
            @Nullable T propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer,
            @Nullable ChangeHandler<C, D, T> changeHandler
    ) {
        this.propertyName = propertyName;
        this.changeConsumer = changeConsumer;
        this.changeHandler = changeHandler == null ? newChangeHandler() : changeHandler;
        this.propertyNameLabel = new Label(getPropertyName() + ":");

        setOnKeyReleased(UiUtils::consumeIfIsNotHotKey);
        setOnKeyPressed(UiUtils::consumeIfIsNotHotKey);
        setPropertyValue(propertyValue);
    }

    /**
     * New change handler six object consumer.
     *
     * @return the six object consumer
     */
    @FromAnyThread
    protected @NotNull ChangeHandler<C, D, T> newChangeHandler() {
        return (changeConsumer, object, propName, newValue, oldValue, handler) -> {

            var operation = new PropertyOperation<ChangeConsumer, D, T>(object, propName, newValue, oldValue);
            operation.setApplyHandler(handler);

            changeConsumer.execute(operation);
        };
    }

    /**
     * Set the edit object.
     *
     * @param editObject the edit object.
     */
    @FxThread
    public void setEditObject(@NotNull D editObject) {
        this.editObject = editObject;
    }

    /**
     * Set the edit object.
     *
     * @param editObject the edit object.
     * @param needReload the true if need to reload.
     */
    @FxThread
    public void setEditObject(@NotNull D editObject, boolean needReload) {
        setEditObject(editObject);

        if (needReload) {
            reload();
        }
    }

    /**
     * Set the apply handler.
     *
     * @param applyHandler the apply handler.
     */
    @FxThread
    public void setApplyHandler(@NotNull BiConsumer<D, T> applyHandler) {
        this.applyHandler = applyHandler;
    }

    /**
     * Get the sync handler.
     *
     * @return the sync handler.
     */
    @FromAnyThread
    protected @Nullable Function<D, T> getSyncHandler() {
        return syncHandler;
    }

    /**
     * Set the sync handler.
     *
     * @param syncHandler the sync handler.
     */
    @FxThread
    public void setSyncHandler(@Nullable Function<D, T> syncHandler) {
        this.syncHandler = syncHandler;
    }

    /**
     * Get the edit object.
     *
     * @return the edit object.
     */
    @FromAnyThread
    protected @NotNull D getEditObject() {
        return notNull(editObject);
    }

    /**
     * Return true if this control has an edit object.
     *
     * @return true if this control has an edit object.
     */
    @FromAnyThread
    protected boolean hasEditObject() {
        return editObject != null;
    }

    /**
     * Reloading control.
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
     * Reloading control.
     */
    @FxThread
    protected void reloadImpl() {
    }

    /**
     * Return true if this control has not saved changes.
     *
     * @return true if this control has not saved changes.
     */
    @FxThread
    public boolean isDirty() {
        return true;
    }

    /**
     * Apply changes from control to the edited object on lost focus.
     *
     * @param focused the focused state.
     */
    protected void applyOnLostFocus(boolean focused) {
        if (!isIgnoreListener() && !focused && isDirty()) {
            apply();
        }
    }

    /**
     * Apply changes from control to the edit object.
     */
    @FxThread
    protected void apply() {
    }

    /**
     * Synchronize value from the edit object.
     */
    @Override
    @FxThread
    public void sync() {
        setIgnoreListener(true);
        try {

            var syncHandler = getSyncHandler();
            var currentValue = getPropertyValue();

            if (syncHandler != null) {
                setPropertyValue(syncHandler.apply(getEditObject()));
            }

            if (!Objects.equals(currentValue, getPropertyValue())) {
                reloadImpl();
            }

        } finally {
            setIgnoreListener(false);
        }
    }

    /**
     * Create all necessary things after constructor.
     */
    @FxThread
    public void postConstruct() {
        setAlignment(isSingleRow() ? Pos.CENTER_RIGHT : Pos.CENTER);

        var container = new HBox();
        container.setAlignment(isSingleRow() ? Pos.CENTER_RIGHT : Pos.CENTER);

        if (isSingleRow()) {
            propertyNameLabel.maxWidthProperty()
                    .bind(widthProperty().multiply(1F - CONTROL_WIDTH_PERCENT));
        }

        FxUtils.addClass(container, CssClasses.DEF_HBOX)
                .addClass(propertyNameLabel, getLabelCssClass());

        FxUtils.addChild(isSingleRow() ? container : this, propertyNameLabel);

        createControls(container);

        FxUtils.addChild(this, container);
    }

    @FromAnyThread
    protected @NotNull String getLabelCssClass() {
        return isSingleRow() ? CssClasses.ABSTRACT_PARAM_CONTROL_PARAM_NAME_SINGLE_ROW :
                CssClasses.ABSTRACT_PARAM_CONTROL_PARAM_NAME;
    }

    /**
     * Change control width percent.
     *
     * @param controlWidthPercent the control width percent.
     */
    @FxThread
    public void changeControlWidthPercent(double controlWidthPercent) {

        if (!isSingleRow()) {
            return;
        }

        propertyNameLabel.maxWidthProperty().unbind();
        propertyNameLabel.maxWidthProperty()
                .bind(widthProperty().multiply(1D - controlWidthPercent));
    }

    /**
     * Return true if this control is a single row.
     *
     * @return true if this control is a single row.
     */
    @FromAnyThread
    protected boolean isSingleRow() {
        return false;
    }

    /**
     * Create components of this control.
     *
     * @param container the container.
     */
    @FxThread
    protected void createControls(@NotNull HBox container) {
    }

    /**
     * Get the property name.
     *
     * @return the name of the property.
     */
    @FromAnyThread
    protected @NotNull String getPropertyName() {
        return propertyName;
    }

    /**
     * Get the change consumer.
     *
     * @return the consumer of changes.
     */
    @FromAnyThread
    protected @NotNull C getChangeConsumer() {
        return changeConsumer;
    }

    /**
     * Apply new value to the edit object.
     *
     * @param newValue the new value
     * @param oldValue the old value
     */
    @FxThread
    protected void changed(@Nullable T newValue, @Nullable T oldValue) {
        changeHandler.accept(getChangeConsumer(), getEditObject(), getPropertyName(),
                newValue, oldValue, getApplyHandler());
    }

    /**
     * Get the property value.
     *
     * @return the value of the property.
     */
    @FxThread
    public @Nullable T getPropertyValue() {
        return propertyValue;
    }

    /**
     * Get the property value.
     *
     * @return the value of the property.
     */
    @FxThread
    public @NotNull Optional<T> getPropertyValueOpt() {
        return Optional.ofNullable(getPropertyValue());
    }

    /**
     * Get the apply handler.
     *
     * @return the handler for handling new value.
     */
    @FromAnyThread
    protected @NotNull BiConsumer<D, T> getApplyHandler() {
        return notNull(applyHandler);
    }

    /**
     * Set the property value.
     *
     * @param propertyValue the value of the property.
     */
    @FxThread
    protected void setPropertyValue(@Nullable T propertyValue) {
        this.propertyValue = propertyValue;
    }

    /**
     * Set the ignore listener.
     *
     * @param ignoreListener the flag for ignoring listeners.
     */
    @FxThread
    protected void setIgnoreListener(boolean ignoreListener) {
        this.ignoreListener = ignoreListener;
    }

    /**
     * Return true if need to ignore listeners.
     *
     * @return true if need to ignore listeners.
     */
    @FxThread
    protected boolean isIgnoreListener() {
        return ignoreListener;
    }
}
