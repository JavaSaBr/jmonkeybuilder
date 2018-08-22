package com.ss.builder.fx.control.property.impl;

import com.jme3.scene.Spatial;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.fx.dialog.node.selector.NodeSelectorDialog;
import com.ss.builder.fx.dialog.node.selector.SpatialSelectorDialog;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.fx.dialog.node.selector.NodeSelectorDialog;
import com.ss.builder.fx.dialog.node.selector.SpatialSelectorDialog;
import com.ss.rlib.common.util.StringUtils;
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
    protected @NotNull String getElementText() {
        return getPropertyValueOpt()
                .map(Spatial::getName)
                .filter(StringUtils::isNotEmpty)
                .or(() -> getPropertyValueOpt()
                        .map(Spatial::getClass)
                        .map(Class::getSimpleName))
                .orElse(NO_ELEMENT);
    }
}
