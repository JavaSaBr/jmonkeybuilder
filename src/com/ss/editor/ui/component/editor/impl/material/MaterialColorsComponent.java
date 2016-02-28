package com.ss.editor.ui.component.editor.impl.material;

import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.material.MaterialDef;
import com.jme3.shader.VarType;
import com.ss.editor.Messages;
import com.ss.editor.ui.control.material.ColorMaterialParamControl;

import java.util.Collection;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;

/**
 * Реализация компонента конфигурирования цветов материала.
 *
 * @author Ronn
 */
public class MaterialColorsComponent extends TitledPane {

    public static final Insets CONTROL_OFFSET = new Insets(3, 0, 0, 0);

    /**
     * Обработчик внесения изменений.
     */
    private final Runnable changeHandler;

    /**
     * Контейнер контролов для изменения цветов.
     */
    private final VBox container;

    public MaterialColorsComponent(final Runnable changeHandler) {
        this.changeHandler = changeHandler;
        this.container = new VBox();
        setText(Messages.MATERIAL_COLORS_COMPONENT_TITLE);
        setContent(container);
    }

    /**
     * @return обработчик внесения изменений.
     */
    private Runnable getChangeHandler() {
        return changeHandler;
    }

    /**
     * @return контейнер контролов для изменения цветов.
     */
    private VBox getContainer() {
        return container;
    }

    /**
     * Построение настроек цветов для материала.
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

        final VarType varType = matParam.getVarType();

        if (varType != VarType.Vector4) {
            return;
        }

        final ColorMaterialParamControl control = new ColorMaterialParamControl(getChangeHandler(), material, matParam.getName());

        FXUtils.addToPane(control, getContainer());

        VBox.setMargin(control, CONTROL_OFFSET);
    }
}
