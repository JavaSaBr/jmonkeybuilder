package com.ss.editor.ui.control.model.property;

import com.jme3.scene.Spatial;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.component.editor.impl.model.ModelFileEditor;
import com.ss.editor.ui.control.UpdatableControl;
import com.ss.editor.ui.control.model.property.operation.ModelPropertyOperation;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.util.GeomUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import rlib.logging.Logger;
import rlib.logging.LoggerManager;
import rlib.ui.util.FXUtils;

/**
 * The base implementation of the property control for the {@link ModelFileEditor}.
 *
 * @author JavaSaBr
 */
public class ModelPropertyControl<D, T> extends VBox implements UpdatableControl {

    protected static final Logger LOGGER = LoggerManager.getLogger(ModelPropertyControl.class);

    protected static final double CONTROL_WIDTH_PERCENT = 0.4;

    protected static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    /**
     * The name of the property.
     */
    private final String propertyName;

    /**
     * The consumer of changes.
     */
    private final ModelChangeConsumer modelChangeConsumer;

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

    public ModelPropertyControl(@Nullable final T propertyValue, @NotNull final String propertyName,
                                @NotNull final ModelChangeConsumer modelChangeConsumer) {
        this.propertyValue = propertyValue;
        this.propertyName = propertyName;
        this.modelChangeConsumer = modelChangeConsumer;

        createComponents();
        setIgnoreListener(true);
        try {
            reload();
        } finally {
            setIgnoreListener(false);
        }

        FXUtils.addClassTo(this, CSSClasses.MODEL_PARAM_CONTROL);
    }

    /**
     * @param editObject the edit object.
     */
    @FXThread
    public void setEditObject(@NotNull final D editObject) {
        this.editObject = editObject;
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
    protected D getEditObject() {
        return editObject;
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
            propertyNameLabel.setId(CSSIds.MODEL_PARAM_CONTROL_PARAM_NAME_SINGLE_ROW);
        } else {
            propertyNameLabel.setId(CSSIds.MODEL_PARAM_CONTROL_PARAM_NAME);
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
    protected String getPropertyName() {
        return propertyName;
    }

    /**
     * @return the consumer of changes.
     */
    protected ModelChangeConsumer getModelChangeConsumer() {
        return modelChangeConsumer;
    }

    /**
     * Apply new value to the edit object.
     */
    protected void changed(@Nullable final T newValue, @Nullable final T oldValue) {

        final ModelChangeConsumer modelChangeConsumer = getModelChangeConsumer();
        final Spatial currentModel = modelChangeConsumer.getCurrentModel();

        final D editObject = getEditObject();
        final int index = GeomUtils.getIndex(currentModel, editObject);

        final ModelPropertyOperation<D, T> operation = new ModelPropertyOperation<>(index, getPropertyName(), newValue, oldValue);
        operation.setApplyHandler(getApplyHandler());

        modelChangeConsumer.execute(operation);
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
        return "ModelPropertyControl{" +
                "propertyName='" + propertyName + '\'' +
                ", propertyValue=" + propertyValue +
                "} " + super.toString();
    }
}
