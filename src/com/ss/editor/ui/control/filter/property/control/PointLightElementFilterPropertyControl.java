package com.ss.editor.ui.control.filter.property.control;

import com.jme3.light.PointLight;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.control.model.tree.dialog.LightSelectorDialog;
import com.ss.editor.ui.control.model.tree.dialog.NodeSelectorDialog;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.control.Label;

/**
 * The implementation of the {@link AbstractElementFilterPropertyControl} to edit point light from a scene.
 *
 * @param <D> the type parameter
 * @author JavaSaBr
 */
public class PointLightElementFilterPropertyControl<D> extends AbstractElementFilterPropertyControl<D, PointLight> {

    /**
     * Instantiates a new Point light element filter property control.
     *
     * @param propertyValue  the property value
     * @param propertyName   the property name
     * @param changeConsumer the change consumer
     */
    public PointLightElementFilterPropertyControl(@Nullable final PointLight propertyValue,
                                                  @NotNull final String propertyName,
                                                  @NotNull final SceneChangeConsumer changeConsumer) {
        super(PointLight.class, propertyValue, propertyName, changeConsumer);
    }

    @NotNull
    protected NodeSelectorDialog<PointLight> createNodeSelectorDialog() {
        final SceneChangeConsumer changeConsumer = getChangeConsumer();
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
