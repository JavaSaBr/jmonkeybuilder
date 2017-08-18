package com.ss.editor.ui.component.editor.impl.material;

import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.material.MaterialDef;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.material.MaterialParamControl;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.StringUtils;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * The component for editing material properties.
 *
 * @author JavaSaBr
 */
public abstract class AbstractMaterialPropertiesComponent extends VBox {

    /**
     * The change consumer.
     */
    @NotNull
    private final ChangeConsumer changeConsumer;

    public AbstractMaterialPropertiesComponent(@NotNull final ChangeConsumer changeConsumer) {
        this.changeConsumer = changeConsumer;
        FXUtils.addClassTo(this, CSSClasses.MATERIAL_FILE_EDITOR_PROPERTIES_COMPONENT);
    }

    /**
     * Gets change consumer.
     *
     * @return the changes consumer.
     */
    @NotNull
    protected ChangeConsumer getChangeConsumer() {
        return changeConsumer;
    }

    /**
     * Build property controls for the material.
     *
     * @param material the material
     */
    public void buildFor(@NotNull final Material material) {

        final MaterialDef materialDef = material.getMaterialDef();
        final ObservableList<Node> children = getChildren();
        children.clear();

        final List<MatParam> materialParams = new ArrayList<>(materialDef.getMaterialParams());
        materialParams.sort((first, second) -> StringUtils.compareIgnoreCase(first.getName(), second.getName()));
        materialParams.forEach(matParam -> buildFor(matParam, material));
    }

    /**
     * Build a control to edit the material parameter.
     *
     * @param matParam the mat param
     * @param material the material
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

    @Nullable
    public <T extends MaterialParamControl> T findControl(@NotNull final String name, @NotNull Class<T> type) {

        final ObservableList<Node> children = getChildren();
        final MaterialParamControl result = children.stream()
                .filter(type::isInstance)
                .map(MaterialParamControl.class::cast)
                .filter(control -> control.getParameterName().equals(name))
                .findAny().orElse(null);

        return type.cast(result);
    }
}
