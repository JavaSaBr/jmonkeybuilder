package com.ss.editor.ui.control.property;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.manager.JavaFxImageManager;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.component.asset.tree.context.menu.action.DeleteFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.NewFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.RenameFileAction;
import com.ss.editor.ui.control.UpdatableControl;
import com.ss.editor.ui.control.property.operation.PropertyOperation;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.event.FxEventManager;
import com.ss.editor.ui.util.UiUtils;
import com.ss.rlib.function.SixObjectConsumer;
import com.ss.rlib.logging.Logger;
import com.ss.rlib.logging.LoggerManager;
import com.ss.rlib.ui.util.FXUtils;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
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

    /**
     * @param <C> the type of a change consumer.
     * @param <D> the type of an editing object.
     * @param <T> the type of an editing property.
     * @author JavaSaBr
     */
    public interface ChangeHandler<C, D, T> extends SixObjectConsumer<C, D, String, T,  T, BiConsumer<D, T>> {
    }

    /**
     * The constant LOGGER.
     */
    @NotNull
    protected static final Logger LOGGER = LoggerManager.getLogger(PropertyControl.class);

    /**
     * Default action tester.
     */
    @NotNull
    protected static final Predicate<Class<?>> DEFAULT_ACTION_TESTER = type -> type == NewFileAction.class ||
            type == DeleteFileAction.class || type == RenameFileAction.class;

    /**
     * The constant CONTROL_WIDTH_PERCENT.
     */
    public static final double CONTROL_WIDTH_PERCENT = 0.4;

    /**
     * The constant CONTROL_WIDTH_PERCENT_2.
     */
    public static final double CONTROL_WIDTH_PERCENT_2 = 0.6;

    /**
     * The constant CONTROL_WIDTH_PERCENT_3.
     */
    public static final double CONTROL_WIDTH_PERCENT_3 = 0.7;

    /**
     * The FX event manager.
     */
    @NotNull
    protected static final FxEventManager FX_EVENT_MANAGER = FxEventManager.getInstance();

    /**
     * The executor manager.
     */
    @NotNull
    protected static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    /**
     * The image preview manager.
     */
    @NotNull
    protected static final JavaFxImageManager IMAGE_MANAGER = JavaFxImageManager.getInstance();

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
     * The label of the property name.
     */
    @Nullable
    private Label propertyNameLabel;

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

        setOnKeyReleased(UiUtils::consumeIfIsNotHotKey);
        setOnKeyPressed(UiUtils::consumeIfIsNotHotKey);
        setPropertyValue(propertyValue);
        createComponents();
        setIgnoreListener(true);
        try {
            reload();
        } finally {
            setIgnoreListener(false);
        }

        FXUtils.addClassTo(this, CssClasses.ABSTRACT_PARAM_CONTROL);
    }

    /**
     * New change handler six object consumer.
     *
     * @return the six object consumer
     */
    @FromAnyThread
    public @NotNull ChangeHandler<C, D, T> newChangeHandler() {
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

        if (!needReload) {
            return;
        }

        setIgnoreListener(true);
        try {
            reload();
        } finally {
            setIgnoreListener(false);
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
    protected void reload() {
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
                reload();
            }

        } finally {
            setIgnoreListener(false);
        }
    }

    /**
     * Create this control.
     */
    @FxThread
    protected void createComponents() {
        setAlignment(isSingleRow() ? Pos.CENTER_RIGHT : Pos.CENTER);

        var container = new HBox();
        container.setAlignment(isSingleRow() ? Pos.CENTER_RIGHT : Pos.CENTER);

        propertyNameLabel = new Label(getPropertyName() + ":");

        if (isSingleRow()) {
            propertyNameLabel.maxWidthProperty().bind(widthProperty().multiply(1F - CONTROL_WIDTH_PERCENT));
        }

        FXUtils.addClassTo(container, CssClasses.DEF_HBOX);
        FXUtils.addClassTo(propertyNameLabel, isSingleRow() ? CssClasses.ABSTRACT_PARAM_CONTROL_PARAM_NAME_SINGLE_ROW :
                CssClasses.ABSTRACT_PARAM_CONTROL_PARAM_NAME);

        FXUtils.addToPane(propertyNameLabel, isSingleRow() ? container : this);

        createComponents(container);

        FXUtils.addToPane(container, this);
    }

    /**
     * Get the property name label.
     *
     * @return the property name label.
     */
    @FxThread
    protected @NotNull Label getPropertyNameLabel() {
        return propertyNameLabel;
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

        var propertyNameLabel = getPropertyNameLabel();
        propertyNameLabel.maxWidthProperty().unbind();
        propertyNameLabel.maxWidthProperty().bind(widthProperty().multiply(1D - controlWidthPercent));
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
    protected void createComponents(@NotNull final HBox container) {
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

    @Override
    public String toString() {
        return "PropertyControl{" +
                "propertyName='" + propertyName + '\'' +
                ", propertyValue=" + propertyValue +
                "} " + super.toString();
    }
}
