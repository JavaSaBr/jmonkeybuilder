package com.ss.editor.ui.component.editor.impl.material;

import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.material.MaterialDef;
import com.jme3.shader.VarType;
import com.ss.editor.ui.control.material.BooleanMaterialParamControl;
import com.ss.editor.ui.control.material.FloatMaterialParamControl;
import com.ss.editor.ui.control.material.IntegerMaterialParamControl;
import com.ss.editor.ui.control.material.MaterialParamControl;

import java.util.Collection;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;

import static com.ss.editor.Messages.MATERIAL_FILE_EDITOR_OTHER_COMPONENT_TITLE;

/**
 * Реализация компонента конфигурирования других параметров материала.
 *
 * @author Ronn
 */
public class MaterialOtherParamsComponent extends TitledPane {

    public static final Insets CONTROL_OFFSET = new Insets(3, 0, 0, 0);

    /**
     * Обрбаотчик внесения изменений.
     */
    private final Runnable changeHandler;

    /**
     * Контейнер контролов различных параметров.
     */
    private final VBox container;

    public MaterialOtherParamsComponent(final Runnable changeHandler) {
        this.changeHandler = changeHandler;
        this.container = new VBox();
        setText(MATERIAL_FILE_EDITOR_OTHER_COMPONENT_TITLE);
        setContent(container);
    }

    /**
     * @return контейнер контролов различных параметров.
     */
    private VBox getContainer() {
        return container;
    }

    /**
     * @return обрбаотчик внесения изменений.
     */
    private Runnable getChangeHandler() {
        return changeHandler;
    }

    /**
     * Построение настроек для материала.
     */
    public void buildFor(final Material material) {

        final VBox container = getContainer();
        final ObservableList<Node> children = container.getChildren();
        children.clear();

        final MaterialDef materialDef = material.getMaterialDef();

        final Collection<MatParam> materialParams = materialDef.getMaterialParams();
        materialParams.forEach(matParam -> buildFor(matParam, material));
    }

    /**
     * Построение контрола для параметра.
     */
    private void buildFor(final MatParam matParam, final Material material) {

        final Runnable changeHandler = getChangeHandler();
        final VarType varType = matParam.getVarType();

        MaterialParamControl control = null;

        if (varType == VarType.Boolean) {
            control = new BooleanMaterialParamControl(changeHandler, material, matParam.getName());
        } else if (varType == VarType.Int) {
            control = new IntegerMaterialParamControl(changeHandler, material, matParam.getName());
        } else if (varType == VarType.Float) {
            control = new FloatMaterialParamControl(changeHandler, material, matParam.getName());
        }

        if (control == null) {
            return;
        }

        FXUtils.addToPane(control, getContainer());

        VBox.setMargin(control, CONTROL_OFFSET);
    }
}
