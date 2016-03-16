package com.ss.editor.ui.control.model.property;

import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;

import java.util.function.Consumer;

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
public class ModelPropertyControl<T> extends VBox {

    protected static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    /**
     * Обработчик внесения изменений.
     */
    private final Runnable changeHandler;

    /**
     * Название параметра.
     */
    private final String paramName;

    /**
     * Редактируемый элемент.
     */
    private T element;

    /**
     * Обработчик приминения изменений.
     */
    private Consumer<T> applyHandler;

    /**
     * Обработчик синхронизации данных.
     */
    private Consumer<T> syncHandler;

    /**
     * Надпись с названием параметра.
     */
    private Label paramNameLabel;

    /**
     * Игнорировать ли слушатели.
     */
    private boolean ignoreListener;

    public ModelPropertyControl(final Runnable changeHandler, final T element, final String paramName) {
        this.changeHandler = changeHandler;
        this.element = element;
        this.paramName = paramName;

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
     * @param applyHandler обработчик приминения изменений.
     */
    public void setApplyHandler(final Consumer<T> applyHandler) {
        this.applyHandler = applyHandler;
    }

    /**
     * @param syncHandler обработчик синхронизации данных.
     */
    public void setSyncHandler(final Consumer<T> syncHandler) {
        this.syncHandler = syncHandler;
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

            if (syncHandler != null) {
                syncHandler.accept(getElement());
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

        paramNameLabel = new Label(getParamName() + ":");

        if (isSingleRow()) {
            paramNameLabel.setId(CSSIds.MODEL_PARAM_CONTROL_PARAM_NAME_SINGLE_ROW);
        } else {
            paramNameLabel.setId(CSSIds.MODEL_PARAM_CONTROL_PARAM_NAME);
            paramNameLabel.prefWidthProperty().bind(widthProperty());
        }

        FXUtils.addClassTo(paramNameLabel, CSSClasses.MAIN_FONT_13);
        FXUtils.addToPane(paramNameLabel, isSingleRow() ? container : this);

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
    private String getParamName() {
        return paramName;
    }

    /**
     * Уведомить о измнении чего-то.
     */
    protected void changed() {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> applyHandler.accept(element));
        changeHandler.run();
    }

    /**
     * @return редактируемый элемент.
     */
    public T getElement() {
        return element;
    }

    /**
     * @param element редактируемый элемент.
     */
    protected void setElement(T element) {
        this.element = element;
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
