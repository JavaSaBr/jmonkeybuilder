package com.ss.editor.ui.component.editor.impl.material;

import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.material.MaterialDef;
import com.ss.editor.model.undo.EditorOperation;
import com.ss.editor.ui.control.material.MaterialParamControl;
import com.ss.editor.ui.css.CSSIds;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import rlib.util.StringUtils;

/**
 * The component for editing material properties.
 *
 * @author JavaSaBr
 */
public abstract class AbstractMaterialPropertiesComponent extends VBox {

    /**
     * The changes handler.
     */
    private final Consumer<EditorOperation> changeHandler;

    public AbstractMaterialPropertiesComponent(@NotNull final Consumer<EditorOperation> changeHandler) {
        setId(CSSIds.MATERIAL_FILE_EDITOR_PROPERTIES_COMPONENT);
        this.changeHandler = changeHandler;
    }

    /**
     * @return the changes handler.
     */
    @NotNull
    protected Consumer<EditorOperation> getChangeHandler() {
        return changeHandler;
    }

    /**
     * Build property controls for the material.
     */
    public void buildFor(@NotNull final Material material) {

        final MaterialDef materialDef = material.getMaterialDef();
        final ObservableList<Node> children = getChildren();
        children.clear();

        final List<MatParam> materialParams = new ArrayList<>(materialDef.getMaterialParams());

        Collections.sort(materialParams, (first, second) -> StringUtils.compareIgnoreCase(first.getName(), second.getName()));

        materialParams.forEach(matParam -> buildFor(matParam, material));
    }

    /**
     * Build a control dor editing the material parameter.
     */
    protected void buildFor(@NotNull final MatParam matParam, @NotNull final Material material) {
    }

    /**
     * Update a value of material parameter.
     *
     * @param paramName the parameter name.
     */
    public void updateParam(@NotNull final String paramName) {

        final ObservableList<Node> children = getChildren();
        children.forEach(node -> {
            if (!(node instanceof MaterialParamControl)) return;

            final MaterialParamControl control = (MaterialParamControl) node;
            if (!StringUtils.equals(control.getParameterName(), paramName)) return;

            control.setIgnoreListeners(true);
            try {
                control.reload();
            } finally {
                control.setIgnoreListeners(false);
            }
        });
    }
}
