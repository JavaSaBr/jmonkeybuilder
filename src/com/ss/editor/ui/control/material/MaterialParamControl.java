package com.ss.editor.ui.control.material;

import com.jme3.material.Material;
import com.ss.editor.model.undo.EditorOperation;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;

import java.util.function.Consumer;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import rlib.logging.Logger;
import rlib.logging.LoggerManager;
import rlib.ui.util.FXUtils;

/**
 * Базовая реализация контрола для изменения параметра материала.
 *
 * @author Ronn
 */
public class MaterialParamControl extends HBox {

    protected static final Logger LOGGER = LoggerManager.getLogger(MaterialParamControl.class);

    /**
     * Обработчик внесения изменений.
     */
    private final Consumer<EditorOperation> changeHandler;

    /**
     * Текущий материал.
     */
    private final Material material;

    /**
     * Название параметра.
     */
    private final String parameterName;

    /**
     * Надпись с названием параметра.
     */
    private Label paramNameLabel;

    /**
     * Флаг игнорирования слушателей.
     */
    private boolean ignoreListeners;

    public MaterialParamControl(final Consumer<EditorOperation> changeHandler, final Material material, final String parameterName) {
        this.changeHandler = changeHandler;
        this.material = material;
        this.parameterName = parameterName;

        setIgnoreListeners(true);
        try {
            createComponents();
            reload();
        } finally {
            setIgnoreListeners(false);
        }

        FXUtils.addClassTo(this, CSSClasses.MATERIAL_PARAM_CONTROL);
    }

    /**
     * @param operation операция с внесением изменения.
     */
    protected void execute(final EditorOperation operation) {
        changeHandler.accept(operation);
    }

    /**
     * @return флаг игнорирования слушателей.
     */
    public boolean isIgnoreListeners() {
        return ignoreListeners;
    }

    /**
     * @param ignoreListeners флаг игнорирования слушателей.
     */
    public void setIgnoreListeners(final boolean ignoreListeners) {
        this.ignoreListeners = ignoreListeners;
    }

    /**
     * Создание компонентов контрола.
     */
    protected void createComponents() {

        paramNameLabel = new Label(getParameterName() + ":");
        paramNameLabel.setId(CSSIds.MATERIAL_PARAM_CONTROL_PARAM_NAME);

        FXUtils.addClassTo(paramNameLabel, CSSClasses.MAIN_FONT_13);
        FXUtils.addToPane(paramNameLabel, this);
    }

    /**
     * @return надпись с названием параметра.
     */
    protected Label getParamNameLabel() {
        return paramNameLabel;
    }

    /**
     * Инициализация контрола.
     */
    public void reload() {
    }

    /**
     * @return название параметра.
     */
    public String getParameterName() {
        return parameterName;
    }

    /**
     * @return текущий материал.
     */
    public Material getMaterial() {
        return material;
    }
}
