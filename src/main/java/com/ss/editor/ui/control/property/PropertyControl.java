package com.ss.editor.ui.control.property;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.Editor;
import com.ss.editor.JFXApplication;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.manager.JavaFXImageManager;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.component.asset.tree.context.menu.action.DeleteFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.NewFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.RenameFileAction;
import com.ss.editor.ui.control.UpdatableControl;
import com.ss.editor.ui.control.property.operation.PropertyOperation;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.util.UIUtils;
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
 * @param <C> the type of a change consumer
 * @param <D> the type of an editing object
 * @param <T> the type of an editing property
 * @author JavaSaBr
 */
public class PropertyControl<C extends ChangeConsumer, D, T> extends VBox implements UpdatableControl {

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
    protected static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();

    /**
     * The javaFX part of this editor.
     */
    @NotNull
    protected static final JFXApplication JFX_APPLICATION = JFXApplication.getInstance();

    /**
     * The executor manager.
     */
    @NotNull
    protected static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    /**
     * The image preview manager.
     */
    @NotNull
    protected static final JavaFXImageManager IMAGE_MANAGER = JavaFXImageManager.getInstance();

    /**
     * The editor.
     */
    @NotNull
    protected static final Editor EDITOR = Editor.getInstance();

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

    public PropertyControl(@Nullable final T propertyValue, @NotNull final String propertyName,
                           @NotNull final C changeConsumer) {
        this(propertyValue, propertyName, changeConsumer, null);
    }

    public PropertyControl(@Nullable final T propertyValue, @NotNull final String propertyName,
                           @NotNull final C changeConsumer,
                           @Nullable final SixObjectConsumer<C, D, String, T, T, BiConsumer<D, T>> changeHandler) {
        this.propertyName = propertyName;
        this.changeConsumer = changeConsumer;
        this.changeHandler = changeHandler == null ? newChangeHandler() : changeHandler;

        setOnKeyReleased(UIUtils::consumeIfIsNotHotKey);
        setOnKeyPressed(UIUtils::consumeIfIsNotHotKey);
        setPropertyValue(propertyValue);
        createComponents();
        setIgnoreListener(true);
        try {
            reload();
        } finally {
            setIgnoreListener(false);
        }

        FXUtils.addClassTo(this, CSSClasses.ABSTRACT_PARAM_CONTROL);
    }

    /**
     * New change handler six object consumer.
     *
     * @return the six object consumer
     */
    @FromAnyThread
    public @NotNull SixObjectConsumer<@NotNull C, @NotNull D, @NotNull String, @Nullable T, @Nullable T, @NotNull BiConsumer<D, T>> newChangeHandler() {
        return (changeConsumer, object, propName, newValue, oldValue, handler) -> {

            final PropertyOperation<ChangeConsumer, D, T> operation = new PropertyOperation<>(object, propName, newValue, oldValue);
            operation.setApplyHandler(handler);

            changeConsumer.execute(operation);
        };
    }

    /**
     * Sets edit object.
     *
     * @param editObject the edit object.
     */
    @FXThread
    public void setEditObject(@NotNull final D editObject) {
        this.editObject = editObject;
    }

    /**
     * Sets edit object.
     *
     * @param editObject the edit object.
     * @param needReload the true if need to reload.
     */
    @FXThread
    public void setEditObject(@NotNull final D editObject, final boolean needReload) {
        setEditObject(editObject);
        if (!needReload) return;
        setIgnoreListener(true);
        try {
            reload();
        } finally {
            setIgnoreListener(false);
        }
    }

    /**
     * Sets apply handler.
     *
     * @param applyHandler the handler for handling new value.
     */
    @FXThread
    public void setApplyHandler(@NotNull final BiConsumer<D, T> applyHandler) {
        this.applyHandler = applyHandler;
    }

    /**
     * Gets sync handler.
     *
     * @return the handler for getting actual value.
     */
    @FromAnyThread
    protected @Nullable Function<D, T> getSyncHandler() {
        return syncHandler;
    }

    /**
     * Sets sync handler.
     *
     * @param syncHandler the handler for getting actual value.
     */
    @FXThread
    public void setSyncHandler(@Nullable final Function<D, T> syncHandler) {
        this.syncHandler = syncHandler;
    }

    /**
     * Gets edit object.
     *
     * @return the edit object.
     */
    @FromAnyThread
    protected @NotNull D getEditObject() {
        return notNull(editObject);
    }

    /**
     * Has edit object boolean.
     *
     * @return true if this control has an edit object.
     */
    @FromAnyThread
    protected boolean hasEditObject() {
        return editObject != null;
    }

    /**
     * Initializing control.
     */
    @FXThread
    protected void reload() {
    }

    /**
     * Synchronize value from the edit object.
     */
    @Override
    @FXThread
    public void sync() {
        setIgnoreListener(true);
        try {

            final Function<D, T> syncHandler = getSyncHandler();
            final T currentValue = getPropertyValue();

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
    @FXThread
    protected void createComponents() {
        setAlignment(isSingleRow() ? Pos.CENTER_RIGHT : Pos.CENTER);

        final HBox container = new HBox();
        container.setAlignment(isSingleRow() ? Pos.CENTER_RIGHT : Pos.CENTER);

        propertyNameLabel = new Label(getPropertyName() + ":");

        if (isSingleRow()) {
            propertyNameLabel.maxWidthProperty().bind(widthProperty().multiply(1F - CONTROL_WIDTH_PERCENT));
        }

        FXUtils.addClassTo(container, CSSClasses.DEF_HBOX);
        FXUtils.addClassTo(propertyNameLabel, isSingleRow() ? CSSClasses.ABSTRACT_PARAM_CONTROL_PARAM_NAME_SINGLE_ROW :
                CSSClasses.ABSTRACT_PARAM_CONTROL_PARAM_NAME);

        FXUtils.addToPane(propertyNameLabel, isSingleRow() ? container : this);

        createComponents(container);

        FXUtils.addToPane(container, this);
    }

    /**
     * Is single row boolean.
     *
     * @return true if this control is single row.
     */
    @FromAnyThread
    protected boolean isSingleRow() {
        return false;
    }

    /**
     * Create components of this control.
     *
     * @param container the container
     */
    @FXThread
    protected void createComponents(@NotNull final HBox container) {
    }

    /**
     * Gets property name.
     *
     * @return the name of the property.
     */
    @FromAnyThread
    protected @NotNull String getPropertyName() {
        return propertyName;
    }

    /**
     * Gets change consumer.
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
    @FXThread
    protected void changed(@Nullable final T newValue, @Nullable final T oldValue) {
        changeHandler.accept(getChangeConsumer(), getEditObject(), getPropertyName(), newValue, oldValue, getApplyHandler());
    }

    /**
     * Gets property value.
     *
     * @return the value of the property.
     */
    @FXThread
    public @Nullable T getPropertyValue() {
        return propertyValue;
    }

    /**
     * Gets apply handler.
     *
     * @return the handler for handling new value.
     */
    @FromAnyThread
    protected @NotNull BiConsumer<D, T> getApplyHandler() {
        return notNull(applyHandler);
    }

    /**
     * Sets property value.
     *
     * @param propertyValue the value of the property.
     */
    @FXThread
    protected void setPropertyValue(@Nullable final T propertyValue) {
        this.propertyValue = propertyValue;
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

    @Override
    public String toString() {
        return "PropertyControl{" +
                "propertyName='" + propertyName + '\'' +
                ", propertyValue=" + propertyValue +
                "} " + super.toString();
    }
}
