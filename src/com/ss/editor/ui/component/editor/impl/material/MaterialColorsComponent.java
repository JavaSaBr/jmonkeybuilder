package com.ss.editor.ui.component.editor.impl.material;

import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.material.MaterialDef;
import com.jme3.shader.VarType;
import com.ss.editor.ui.control.material.Texture2DMaterialParamControl;

import java.util.Collection;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;

import static com.ss.editor.Messages.MATERIAL_TEXTURES_COMPONENT_TITLE;

/**
 * Реализация компонента конфигурирования цветов материала.
 *
 * @author Ronn
 */
public class MaterialColorsComponent extends TitledPane {

    public static final Insets CONTROL_OFFSET = new Insets(3, 0, 0, 0);

    /**
     * Контейнер контролов для изменения текстур.
     */
    private final VBox container;

    /**
     * Текущий отображаемый материал.
     */
    private Material currentMaterial;

    public MaterialColorsComponent() {
        this.container = new VBox();
        setText(MATERIAL_TEXTURES_COMPONENT_TITLE);
        setContent(container);
    }

    /**
     * @return контейнер контролов для изменения текстур.
     */
    private VBox getContainer() {
        return container;
    }

    /**
     * Построение настроек текстур для материала.
     */
    public void buildFor(final Material material) {
        setCurrentMaterial(material);

        final VBox container = getContainer();
        final ObservableList<Node> children = container.getChildren();
        children.clear();

        final MaterialDef materialDef = material.getMaterialDef();

        final Collection<MatParam> materialParams = materialDef.getMaterialParams();
        materialParams.forEach(matParam -> buildFor(matParam, material));
    }

    private void buildFor(final MatParam matParam, final Material material) {

        final VarType varType = matParam.getVarType();

        if(varType == VarType.Texture2D) {

            final Texture2DMaterialParamControl control = new Texture2DMaterialParamControl(material, matParam.getName());

            FXUtils.addToPane(control, getContainer());

            VBox.setMargin(control, CONTROL_OFFSET);
        }
    }

    /**
     * @param currentMaterial текущий отображаемый материал.
     */
    private void setCurrentMaterial(final Material currentMaterial) {
        this.currentMaterial = currentMaterial;
    }
}
