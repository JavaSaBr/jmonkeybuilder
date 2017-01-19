package com.ss.editor.ui.control.property;

import com.ss.editor.annotation.FXThread;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.UpdatableControl;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import rlib.function.SixObjectConsumer;
import rlib.logging.Logger;
import rlib.logging.LoggerManager;
import rlib.ui.util.FXUtils;

/**
 * The base implementation of the property control.
 *
 * @author JavaSaBr
 */
public abstract class AbstractPropertyControl<C extends ChangeConsumer, D, T> extends VBox implements UpdatableControl {

    protected static final Logger LOGGER = LoggerManager.getLogger(AbstractPropertyControl.class);

    protected static final double CONTROL_WIDTH_PERCENT = 0.4;

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
    private D editObject;

    /**
     * The value of the property.
     */
    private T propertyValue;

    /**
     * The handler for handling new value.
     */
    private BiConsumer<D, T> applyHandler;

    /**
     * The handler for getting actual value.
     */
    private Function<D, T> syncHandler;

    /**
     * The label of the property name.
     */
    private Label propertyNameLabel;

    /**
     * The flag for ignoring listeners.
     */
    private boolean ignoreListener;

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
     * @param editObject the edit object.
     */
    @FXThread
    public void setEditObject(@NotNull final D editObject) {
        this.editObject = editObject;
    }

    /**
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
     * @param applyHandler the handler for handling new value.
     */
    @FXThread
    public void setApplyHandler(@NotNull final BiConsumer<D, T> applyHandler) {
        this.applyHandler = applyHandler;
    }

    /**
     * @return the handler for getting actual value.
     */
    @Nullable
    protected Function<D, T> getSyncHandler() {
        return syncHandler;
    }

    /**
     * @param syncHandler the handler for getting actual value.
     */
    @FXThread
    public void setSyncHandler(@Nullable final Function<D, T> syncHandler) {
        this.syncHandler = syncHandler;
    }

    /**
     * @return the edit object.
     */
    @NotNull
    protected D getEditObject() {
        return Objects.requireNonNull(editObject);
    }

    /**
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

        if (isSingleRow()) {
            propertyNameLabel.setId(CSSIds.ABSTRACT_PARAM_CONTROL_PARAM_NAME_SINGLE_ROW);
        } else {
            propertyNameLabel.setId(CSSIds.ABSTRACT_PARAM_CONTROL_PARAM_NAME);
        }

        FXUtils.addClassTo(propertyNameLabel, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addToPane(propertyNameLabel, isSingleRow() ? container : this);

        createComponents(container);

        FXUtils.addToPane(container, this);
    }

    /**
     * @return true if this control is single row.
     */
    protected boolean isSingleRow() {
        return false;
    }

    /**
     * Create components of this control.
     */
    protected void createComponents(@NotNull final HBox container) {
    }

    /**
     * @return the name of the property.
     */
    @NotNull
    protected String getPropertyName() {
        return propertyName;
    }

    /**
     * @return the consumer of changes.
     */
    @NotNull
    protected C getChangeConsumer() {
        return changeConsumer;
    }

    /**
     * Apply new value to the edit object.
     */
    protected void changed(@Nullable final T newValue, @Nullable final T oldValue) {
        changeHandler.accept(getChangeConsumer(), getEditObject(), getPropertyName(), newValue, oldValue, getApplyHandler());
    }

    /**
     * @return the value of the property.
     */
    @Nullable
    @FXThread
    public T getPropertyValue() {
        return propertyValue;
    }

    /**
     * @return the handler for handling new value.
     */
    @NotNull
    protected BiConsumer<D, T> getApplyHandler() {
        return applyHandler;
    }

    /**
     * @param propertyValue the value of the property.
     */
    protected void setPropertyValue(@Nullable final T propertyValue) {
        this.propertyValue = propertyValue;
    }

    /**
     * @param ignoreListener the flag for ignoring listeners.
     */
    protected void setIgnoreListener(final boolean ignoreListener) {
        this.ignoreListener = ignoreListener;
    }

    /**
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
