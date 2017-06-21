package com.ss.editor.ui.control.model.property.control;

import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.filter.property.control.AbstractElementFilterPropertyControl;
import com.ss.editor.ui.control.model.tree.dialog.NodeSelectorDialog;
import javafx.scene.control.Label;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link AbstractElementFilterPropertyControl} to edit spatial from a scene.
 *
 * @param <D> the type parameter
 * @author JavaSaBr
 */
public class SpatialElementModelPropertyControl<D> extends AbstractElementModelPropertyControl<D, Spatial> {

    /**
     * Instantiates a new Spatial element model property control.
     *
     * @param propertyValue  the property value
     * @param propertyName   the property name
     * @param changeConsumer the change consumer
     */
    SpatialElementModelPropertyControl(@Nullable final Spatial propertyValue, @NotNull final String propertyName,
                                       @NotNull final ModelChangeConsumer changeConsumer) {
        super(Spatial.class, propertyValue, propertyName, changeConsumer);
    }

    @NotNull
    protected NodeSelectorDialog<Spatial> createNodeSelectorDialog() {
        final ModelChangeConsumer changeConsumer = getChangeConsumer();
        return new NodeSelectorDialog<>(changeConsumer.getCurrentModel(), type, this::processAdd);
    }

    @Override
    protected void reload() {

        final Spatial spatial = getPropertyValue();
        final Label elementLabel = getElementLabel();

        String name = spatial == null ? null : spatial.getName();
        name = name == null && spatial != null ? spatial.getClass().getSimpleName() : name;

        elementLabel.setText(name == null ? NO_ELEMENT : name);
    }
}
