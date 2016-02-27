package com.ss.editor.ui.component.editor.impl.material;

import com.jme3.material.Material;
import com.jme3.material.MaterialDef;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

import static com.ss.editor.Messages.MATERIAL_OTHER_COMPONENT_TITLE;

/**
 * Реализация компонента конфигурирования параметров рендера материала.
 *
 * @author Ronn
 */
public class MaterialRenderParamsComponent extends TitledPane {

    /**
     * Контейнер контролов различных параметров.
     */
    private final VBox container;

    /**
     * Текущий отображаемый материал.
     */
    private Material currentMaterial;

    public MaterialRenderParamsComponent() {
        this.container = new VBox();
        setText(MATERIAL_OTHER_COMPONENT_TITLE);
        setContent(container);
    }

    /**
     * @return контейнер контролов различных параметров.
     */
    private VBox getContainer() {
        return container;
    }

    /**
     * Построение настроек для материала.
     */
    public void buildFor(final Material material) {
        setCurrentMaterial(material);

        final VBox container = getContainer();
        final ObservableList<Node> children = container.getChildren();
        children.clear();

        final MaterialDef materialDef = material.getMaterialDef();


    }

    /**
     * @param currentMaterial текущий отображаемый материал.
     */
    private void setCurrentMaterial(final Material currentMaterial) {
        this.currentMaterial = currentMaterial;
    }
}
