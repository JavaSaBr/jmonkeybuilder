package com.ss.editor.ui.control.model.property;

import com.jme3.scene.Spatial;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.operation.ModelPropertyOperation;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.util.GeomUtils;

import java.util.function.BiConsumer;
import java.util.function.Function;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;

/**
 * Базовая реализация по изменению свойства модели.
 *
 * @author Ronn
 */
public class ModelPropertyControl<D, T> extends VBox {

    protected static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    /**
     * Название параметра.
     */
    private final String propertyName;

    /**
     * Потребитель изменений модели.
     */
    private final ModelChangeConsumer modelChangeConsumer;

    /**
     * Редактируемый объект.
     */
    private D editObject;

    /**
     * Значение свойства объекта.
     */
    private T propertyValue;

    /**
     * Обработчик приминения изменений.
     */
    private BiConsumer<D, T> applyHandler;

    /**
     * Обработчик синхронизации данных.
     */
    private Function<D, T> syncHandler;

    /**
     * Надпись с названием параметра.
     */
    private Label propertyNameLabel;

    /**
     * Игнорировать ли слушатели.
     */
    private boolean ignoreListener;

    public ModelPropertyControl(final T propertyValue, final String propertyName, final ModelChangeConsumer modelChangeConsumer) {
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
     * @param editObject редактируемый объект.
     */
    public void setEditObject(final D editObject) {
        this.editObject = editObject;
    }

    /**
     * @param applyHandler обработчик приминения изменений.
     */
    public void setApplyHandler(final BiConsumer<D, T> applyHandler) {
        this.applyHandler = applyHandler;
    }

    /**
     * @return обработчик синхронизации данных.
     */
    protected Function<D, T> getSyncHandler() {
        return syncHandler;
    }

    /**
     * @param syncHandler обработчик синхронизации данных.
     */
    public void setSyncHandler(final Function<D, T> syncHandler) {
        this.syncHandler = syncHandler;
    }

    /**
     * @return редактируемый объект.
     */
    protected D getEditObject() {
        return editObject;
    }

    /**
     * Инициализация данных.
     */
    protected void reload() {
    }

    /**
     * Синхронизирование данных.
     */
    public void sync() {
        setIgnoreListener(true);
        try {

            final Function<D, T> syncHandler = getSyncHandler();

            if (syncHandler != null) {
                setPropertyValue(syncHandler.apply(getEditObject()));
            }

            reload();

        } finally {
            setIgnoreListener(false);
        }
    }

    /**
     * Создание компонентов контрола.
     */
    protected void createComponents() {
        setAlignment(isSingleRow() ? Pos.CENTER_LEFT : Pos.CENTER);

        final HBox container = new HBox();
        container.setAlignment(isSingleRow() ? Pos.CENTER_LEFT : Pos.CENTER);

        propertyNameLabel = new Label(getPropertyName() + ":");

        if (isSingleRow()) {
            propertyNameLabel.setId(CSSIds.MODEL_PARAM_CONTROL_PARAM_NAME_SINGLE_ROW);
        } else {
            propertyNameLabel.setId(CSSIds.MODEL_PARAM_CONTROL_PARAM_NAME);
            propertyNameLabel.prefWidthProperty().bind(widthProperty());
        }

        FXUtils.addClassTo(propertyNameLabel, CSSClasses.MAIN_FONT_13);
        FXUtils.addToPane(propertyNameLabel, isSingleRow() ? container : this);

        createComponents(container);

        FXUtils.addToPane(container, this);
    }

    /**
     * @return контрол в одну сторку ли.
     */
    protected boolean isSingleRow() {
        return false;
    }

    /**
     * Создание компонентов для внесения изменений.
     */
    protected void createComponents(final HBox container) {
    }

    /**
     * @return название параметра.
     */
    protected String getPropertyName() {
        return propertyName;
    }

    /**
     * @return потребитель изменений модели.
     */
    protected ModelChangeConsumer getModelChangeConsumer() {
        return modelChangeConsumer;
    }

    /**
     * Уведомить о измнении чего-то.
     */
    protected void changed(final T newValue, final T oldValue) {

        final ModelChangeConsumer modelChangeConsumer = getModelChangeConsumer();
        final Spatial currentModel = modelChangeConsumer.getCurrentModel();

        final D editObject = getEditObject();
        final int index = GeomUtils.getIndex(currentModel, editObject);

        final ModelPropertyOperation<D, T> operation = new ModelPropertyOperation<>(index, getPropertyName(), newValue, oldValue);
        operation.setApplyHandler(getApplyHandler());

        modelChangeConsumer.execute(operation);
    }

    /**
     * @return редактируемый элемент.
     */
    public T getPropertyValue() {
        return propertyValue;
    }

    /**
     * @return обработчик приминения изменений.
     */
    protected BiConsumer<D, T> getApplyHandler() {
        return applyHandler;
    }

    /**
     * @param propertyValue редактируемый элемент.
     */
    protected void setPropertyValue(T propertyValue) {
        this.propertyValue = propertyValue;
    }

    /**
     * @param ignoreListener игнорировать ли слушатели.
     */
    public void setIgnoreListener(final boolean ignoreListener) {
        this.ignoreListener = ignoreListener;
    }

    /**
     * @return игнорировать ли слушатели.
     */
    public boolean isIgnoreListener() {
        return ignoreListener;
    }
}
