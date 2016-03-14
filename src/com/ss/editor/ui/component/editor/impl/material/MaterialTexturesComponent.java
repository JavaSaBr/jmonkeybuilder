package com.ss.editor.ui.component.editor.impl.material;

import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.material.MaterialDef;
import com.jme3.shader.VarType;
import com.ss.editor.Messages;
import com.ss.editor.ui.control.material.Texture2DMaterialParamControl;

import java.util.Collection;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;

/**
 * Реализация компонента конфигурирования текстур материала.
 *
 * @author Ronn
 */
public class MaterialTexturesComponent extends TitledPane {

    public static final Insets CONTROL_OFFSET = new Insets(3, 0, 0, 0);

    /**
     * Обработчик внесения изменений.
     */
    private final Runnable changeHandler;

    /**
     * Контейнер контролов для изменения текстур.
     */
    private final VBox container;

    public MaterialTexturesComponent(final Runnable changeHandler) {
        this.changeHandler = changeHandler;
        this.container = new VBox();
        setText(Messages.MATERIAL_FILE_EDITOR_TEXTURES_COMPONENT_TITLE);
        setContent(container);
        setAnimated(false);
    }

    /**
     * @return контейнер контролов для изменения текстур.
     */
    private VBox getContainer() {
        return container;
    }

    /**
     * @return обработчик внесения изменений.
     */
    private Runnable getChangeHandler() {
        return changeHandler;
    }

    /**
     * Построение настроек текстур для материала.
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
     * Построение контрола для этого параметра.
     */
    private void buildFor(final MatParam matParam, final Material material) {

        final Runnable changeHandler = getChangeHandler();
        final VarType varType = matParam.getVarType();

        if (varType == VarType.Texture2D) {

            final Texture2DMaterialParamControl control = new Texture2DMaterialParamControl(changeHandler, material, matParam.getName());

            FXUtils.addToPane(control, getContainer());

            VBox.setMargin(control, CONTROL_OFFSET);
        }
    }
}
