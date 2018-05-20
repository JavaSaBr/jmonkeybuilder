package com.ss.editor.ui.control.property.impl;

import com.jme3.scene.Spatial;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.dialog.node.selector.NodeSelectorDialog;
import com.ss.editor.ui.dialog.node.selector.SpatialSelectorDialog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link ElementModelPropertyControl} to edit a spatial from a scene.
 *
 * @param <D> the type of an editing object.
 * @param <T> the type of an editing property.
 * @author JavaSaBr
 */
public class SpatialElementModelPropertyControl<T extends Spatial, D> extends ElementModelPropertyControl<D, T> {

    public SpatialElementModelPropertyControl(
            @NotNull Class<T> type,
            @Nullable T propertyValue,
            @NotNull String propertyName,
            @NotNull ModelChangeConsumer changeConsumer
    ) {
        super(type, propertyValue, propertyName, changeConsumer);
    }

    @Override
    @FxThread
    protected @NotNull NodeSelectorDialog<T> createNodeSelectorDialog() {
        return new SpatialSelectorDialog<>(getChangeConsumer().getCurrentModel(), type, this::addElement);
    }

    @Override
    @FxThread
    protected void reload() {

        var spatial = getPropertyValue();

        String name = spatial == null ? null : spatial.getName();
        name = name == null && spatial != null ? spatial.getClass().getSimpleName() : name;

        getElementLabel().setText(name == null ? NO_ELEMENT : name);
    }
}
