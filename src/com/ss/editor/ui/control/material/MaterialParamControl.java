package com.ss.editor.ui.control.material;

import com.jme3.material.Material;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import rlib.ui.util.FXUtils;

import static com.ss.editor.ui.css.CSSClasses.MAIN_FONT_13;
import static com.ss.editor.ui.css.CSSClasses.MATERIAL_PARAM_CONTROL;
import static com.ss.editor.ui.css.CSSIds.MATERIAL_PARAM_CONTROL_PARAM_NAME;

/**
 * Базовая реализация контрола для изменения параметра материала.
 *
 * @author Ronn
 */
public class MaterialParamControl extends HBox {

    /**
     * Свойство о том что было что-то изменено этим контролом.
     */
    private BooleanProperty changedProperty;

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

    public MaterialParamControl(final Material material, final String parameterName) {
        this.changedProperty = new SimpleBooleanProperty(this, "changed", false);
        this.material = material;
        this.parameterName = parameterName;

        setIgnoreListeners(true);
        createComponents();
        reload();
        setIgnoreListeners(false);

        FXUtils.addClassTo(this, MATERIAL_PARAM_CONTROL);
    }

    /**
     * @return свойство о том что было что-то изменено этим контролом.
     */
    public BooleanProperty changedProperty() {
        return changedProperty;
    }

    /**
     * Уведомить о измнении чего-то.
     */
    protected void changed() {
        this.changedProperty.setValue(true);
    }

    /**
     * @param ignoreListeners флаг игнорирования слушателей.
     */
    protected void setIgnoreListeners(boolean ignoreListeners) {
        this.ignoreListeners = ignoreListeners;
    }

    /**
     *
     * @return флаг игнорирования слушателей.
     */
    protected boolean isIgnoreListeners() {
        return ignoreListeners;
    }

    /**
     * Создание компонентов контрола.
     */
    protected void createComponents() {

        paramNameLabel = new Label(getParameterName() + ":");
        paramNameLabel.setId(MATERIAL_PARAM_CONTROL_PARAM_NAME);

        FXUtils.addClassTo(paramNameLabel, MAIN_FONT_13);
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
    protected void reload() {
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
