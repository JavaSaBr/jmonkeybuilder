package com.ss.editor.ui.control.property.impl;

import com.jme3.light.Light;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.dialog.node.selector.LightSelectorDialog;
import com.ss.editor.ui.dialog.node.selector.NodeSelectorDialog;
import javafx.scene.control.Label;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link ElementModelPropertyControl} to edit light from a scene.
 *
 * @param <D> the edited object's type.
 * @author JavaSaBr
 */
public class LightElementModelPropertyControl<L extends Light, D> extends ElementModelPropertyControl<D, L> {

    public LightElementModelPropertyControl(@NotNull final Class<L> type, @Nullable final L propertyValue,
                                            @NotNull final String propertyName,
                                            @NotNull final ModelChangeConsumer changeConsumer) {
        super(type, propertyValue, propertyName, changeConsumer);
    }

    @Override
    @FxThread
    protected @NotNull NodeSelectorDialog<L> createNodeSelectorDialog() {
        final ModelChangeConsumer changeConsumer = getChangeConsumer();
        return new LightSelectorDialog<>(changeConsumer.getCurrentModel(), type, this::processAdd);
    }

    @Override
    @FxThread
    protected void reload() {

        final L light = getPropertyValue();
        final Label elementLabel = getElementLabel();

        String name = light == null ? null : light.getName();
        name = name == null && light != null ? light.getClass().getSimpleName() : name;

        elementLabel.setText(name == null ? NO_ELEMENT : name);
    }
}
