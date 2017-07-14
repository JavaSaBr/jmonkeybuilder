package com.ss.editor.ui.control.property;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.UpdatableControl;
import com.ss.editor.ui.css.CSSClasses;
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

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * The base implementation of the property control.
 *
 * @param <C> the type of a change consumer
 * @param <D> the type of an editing object
 * @param <T> the type of an editing property
 * @author JavaSaBr
 */
public abstract class AbstractPropertyControl<C extends ChangeConsumer, D, T> extends VBox implements UpdatableControl {

    /**
     * The constant LOGGER.
     */
    @NotNull
    protected static final Logger LOGGER = LoggerManager.getLogger(AbstractPropertyControl.class);

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
     * The constant EXECUTOR_MANAGER.
     */
    @NotNull
    protected static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

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

    /**
     * Instantiates a new Abstract property control.
     *
     * @param propertyValue  the property value
     * @param propertyName   the property name
     * @param changeConsumer the change consumer
     * @param changeHandler  the change handler
     */
    public AbstractPropertyControl(@Nullable final T propertyValue, @NotNull final String propertyName,
                                   @NotNull final C changeConsumer,
                                   @NotNull final SixObjectConsumer<C, D, String, T, T, BiConsumer<D, T>> changeHandler) {
        this.propertyValue = propertyValue;
        this.propertyName = propertyName;
        this.changeConsumer = changeConsumer;
        this.changeHandler = changeHandler;

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
    @Nullable
    protected Function<D, T> getSyncHandler() {
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
    @NotNull
    protected D getEditObject() {
        return notNull(editObject);
    }

    /**
     * Has edit object boolean.
     *
     * @return true if this control has an edit object.
     */
    protected boolean hasEditObject() {
        return editObject != null;
    }

    /**
     * Initializing control.
     */
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
            if (syncHandler != null) setPropertyValue(syncHandler.apply(getEditObject()));

            reload();

        } finally {
            setIgnoreListener(false);
        }
    }

    /**
     * Create this control.
     */
    protected void createComponents() {
        setAlignment(isSingleRow() ? Pos.CENTER_RIGHT : Pos.CENTER);

        final HBox container = new HBox();
        container.setAlignment(isSingleRow() ? Pos.CENTER_RIGHT : Pos.CENTER);

        propertyNameLabel = new Label(getPropertyName() + ":");

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
    protected boolean isSingleRow() {
        return false;
    }

    /**
     * Create components of this control.
     *
     * @param container the container
     */
    protected void createComponents(@NotNull final HBox container) {
    }

    /**
     * Gets property name.
     *
     * @return the name of the property.
     */
    @NotNull
    protected String getPropertyName() {
        return propertyName;
    }

    /**
     * Gets change consumer.
     *
     * @return the consumer of changes.
     */
    @NotNull
    protected C getChangeConsumer() {
        return changeConsumer;
    }

    /**
     * Apply new value to the edit object.
     *
     * @param newValue the new value
     * @param oldValue the old value
     */
    protected void changed(@Nullable final T newValue, @Nullable final T oldValue) {
        changeHandler.accept(getChangeConsumer(), getEditObject(), getPropertyName(), newValue, oldValue, getApplyHandler());
    }

    /**
     * Gets property value.
     *
     * @return the value of the property.
     */
    @Nullable
    @FXThread
    public T getPropertyValue() {
        return propertyValue;
    }

    /**
     * Gets apply handler.
     *
     * @return the handler for handling new value.
     */
    @NotNull
    protected BiConsumer<D, T> getApplyHandler() {
        return notNull(applyHandler);
    }

    /**
     * Sets property value.
     *
     * @param propertyValue the value of the property.
     */
    protected void setPropertyValue(@Nullable final T propertyValue) {
        this.propertyValue = propertyValue;
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

    @Override
    public String toString() {
        return "AbstractPropertyControl{" +
                "propertyName='" + propertyName + '\'' +
                ", propertyValue=" + propertyValue +
                "} " + super.toString();
    }
}
