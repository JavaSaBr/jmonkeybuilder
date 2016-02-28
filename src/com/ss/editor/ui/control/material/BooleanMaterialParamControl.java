package com.ss.editor.ui.control.material;

import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.ui.css.CSSClasses;

import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import rlib.ui.util.FXUtils;

import static com.ss.editor.ui.css.CSSIds.MATERIAL_PARAM_CONTROL_CHECKBOX;

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

    public BooleanMaterialParamControl(final Runnable changeHandler, final Material material, final String parameterName) {
        super(changeHandler, material, parameterName);
    }

    @Override
    protected void createComponents() {
        super.createComponents();

        checkBox = new CheckBox();
        checkBox.setId(MATERIAL_PARAM_CONTROL_CHECKBOX);
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> processChange(newValue));

        FXUtils.addToPane(checkBox, this);

        HBox.setMargin(checkBox, ELEMENT_OFFSET);

        FXUtils.addClassTo(checkBox, CSSClasses.MAIN_FONT_13);
        FXUtils.bindFixedWidth(getParamNameLabel(), widthProperty().subtract(30));
    }

    /**
     * Процесс обновления флага.
     */
    private void processChange(final Boolean newValue) {

        if (isIgnoreListeners()) {
            return;
        }

        EXECUTOR_MANAGER.addEditorThreadTask(() -> processChangeImpl(newValue));
    }

    /**
     * Процесс изменения флага.
     */
    private void processChangeImpl(final Boolean newValue) {

        final Material material = getMaterial();
        material.setBoolean(getParameterName(), newValue);

        EXECUTOR_MANAGER.addFXTask(() -> {
            changed();
            setIgnoreListeners(true);
            reload();
            setIgnoreListeners(false);
        });
    }

    @Override
    protected void reload() {
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
