package com.ss.editor.ui.control.model.property.control;

import com.jme3.light.DirectionalLight;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.tree.dialog.LightSelectorDialog;
import com.ss.editor.ui.control.model.tree.dialog.NodeSelectorDialog;
import javafx.scene.control.Label;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link ElementModelPropertyControl} to edit direction light from a scene.
 *
 * @param <D> the type parameter
 * @author JavaSaBr
 */
public class DirectionLightElementModelPropertyControl<D> extends ElementModelPropertyControl<D, DirectionalLight> {

    public DirectionLightElementModelPropertyControl(@Nullable final DirectionalLight propertyValue,
                                                     @NotNull final String propertyName,
                                                     @NotNull final ModelChangeConsumer changeConsumer) {
        super(DirectionalLight.class, propertyValue, propertyName, changeConsumer);
    }

    @NotNull
    protected NodeSelectorDialog<DirectionalLight> createNodeSelectorDialog() {
        final ModelChangeConsumer changeConsumer = getChangeConsumer();
        return new LightSelectorDialog<>(changeConsumer.getCurrentModel(), type, this::processAdd);
    }

    @Override
    protected void reload() {

        final DirectionalLight light = getPropertyValue();
        final Label elementLabel = getElementLabel();

        String name = light == null ? null : light.getName();
        name = name == null && light != null ? light.getClass().getSimpleName() : name;

        elementLabel.setText(name == null ? NO_ELEMENT : name);
    }
}
