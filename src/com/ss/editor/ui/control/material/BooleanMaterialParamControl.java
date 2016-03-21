package com.ss.editor.ui.control.material;

import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.EditorOperation;
import com.ss.editor.ui.control.material.operation.BooleanMaterialParamOperation;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;

import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import rlib.ui.util.FXUtils;

/**
 * Реализация контрола для установки флага.
 *
 * @author Ronn
 */
public class BooleanMaterialParamControl extends MaterialParamControl {

    public static final Insets ELEMENT_OFFSET = new Insets(0, 0, 0, 3);

    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    /**
     * Контрол для установки флага.
     */
    private CheckBox checkBox;

    public BooleanMaterialParamControl(final Consumer<EditorOperation> changeHandler, final Material material, final String parameterName) {
        super(changeHandler, material, parameterName);
    }

    @Override
    protected void createComponents() {
        super.createComponents();

        checkBox = new CheckBox();
        checkBox.setId(CSSIds.MATERIAL_PARAM_CONTROL_CHECKBOX);
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> processChange(newValue));

        FXUtils.addToPane(checkBox, this);
        FXUtils.addClassTo(checkBox, CSSClasses.MAIN_FONT_13);
        FXUtils.bindFixedWidth(getParamNameLabel(), widthProperty().subtract(30));

        HBox.setMargin(checkBox, ELEMENT_OFFSET);
    }

    /**
     * Процесс обновления флага.
     */
    private void processChange(final Boolean newValue) {

        if (isIgnoreListeners()) {
            return;
        }

        final String parameterName = getParameterName();
        final Material material = getMaterial();
        final MatParam param = material.getParam(parameterName);
        final Boolean oldValue = param == null? null : (Boolean) param.getValue();

        execute(new BooleanMaterialParamOperation(parameterName, newValue, oldValue));
    }

    @Override
    public void reload() {
        super.reload();

        final Material material = getMaterial();
        final MatParam param = material.getParam(getParameterName());

        if (param == null) {
            checkBox.setSelected(false);
            return;
        }

        final Boolean value = (Boolean) param.getValue();

        checkBox.setSelected(value);
    }
}
