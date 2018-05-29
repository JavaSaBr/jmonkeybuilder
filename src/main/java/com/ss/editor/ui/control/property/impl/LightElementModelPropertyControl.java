package com.ss.editor.ui.control.property.impl;

import com.jme3.light.Light;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.dialog.node.selector.LightSelectorDialog;
import com.ss.editor.ui.dialog.node.selector.NodeSelectorDialog;
import com.ss.rlib.common.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link ElementModelPropertyControl} to edit light from a scene.
 *
 * @param <D> the edited object's type.
 * @author JavaSaBr
 */
public class LightElementModelPropertyControl<L extends Light, D> extends ElementModelPropertyControl<D, L> {

    public LightElementModelPropertyControl(
            @NotNull Class<L> type,
            @Nullable L propertyValue,
            @NotNull String propertyName,
            @NotNull ModelChangeConsumer changeConsumer
    ) {
        super(type, propertyValue, propertyName, changeConsumer);
    }

    @Override
    @FxThread
    protected @NotNull NodeSelectorDialog<L> createNodeSelectorDialog() {
        return new LightSelectorDialog<>(getChangeConsumer().getCurrentModel(), type, this::addElement);
    }

    @Override
    @FxThread
    protected void reload() {

        var light = getPropertyValue();
        var elementLabel = getElementLabel();

        String name = light == null ? null : light.getName();
        name = name == null && light != null ? light.getClass().getSimpleName() : name;

        elementLabel.setText(StringUtils.ifEmpty(name, NO_ELEMENT));
    }
}
