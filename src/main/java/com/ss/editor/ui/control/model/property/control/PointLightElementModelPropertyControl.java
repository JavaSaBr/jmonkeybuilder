package com.ss.editor.ui.control.model.property.control;

import com.jme3.light.PointLight;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.tree.dialog.LightSelectorDialog;
import com.ss.editor.ui.control.model.tree.dialog.NodeSelectorDialog;
import javafx.scene.control.Label;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link ElementModelPropertyControl} to edit point light from a scene.
 *
 * @param <D> the type parameter
 * @author JavaSaBr
 */
public class PointLightElementModelPropertyControl<D> extends ElementModelPropertyControl<D, PointLight> {

    public PointLightElementModelPropertyControl(@Nullable final PointLight propertyValue,
                                                 @NotNull final String propertyName,
                                                 @NotNull final ModelChangeConsumer changeConsumer) {
        super(PointLight.class, propertyValue, propertyName, changeConsumer);
    }

    @NotNull
    protected NodeSelectorDialog<PointLight> createNodeSelectorDialog() {
        final ModelChangeConsumer changeConsumer = getChangeConsumer();
        return new LightSelectorDialog<>(changeConsumer.getCurrentModel(), type, this::processAdd);
    }

    @Override
    protected void reload() {

        final PointLight light = getPropertyValue();
        final Label elementLabel = getElementLabel();

        String name = light == null ? null : light.getName();
        name = name == null && light != null ? light.getClass().getSimpleName() : name;

        elementLabel.setText(name == null ? NO_ELEMENT : name);
    }
}
