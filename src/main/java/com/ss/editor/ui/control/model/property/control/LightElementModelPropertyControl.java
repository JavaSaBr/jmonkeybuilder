package com.ss.editor.ui.control.model.property.control;

import com.jme3.light.Light;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.tree.dialog.LightSelectorDialog;
import com.ss.editor.ui.control.model.tree.dialog.NodeSelectorDialog;
import javafx.scene.control.Label;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link ElementModelPropertyControl} to edit light from a scene.
 *
 * @param <D> the type parameter
 * @author JavaSaBr
 */
public class LightElementModelPropertyControl<L extends Light, D> extends ElementModelPropertyControl<D, L> {

    public LightElementModelPropertyControl(@NotNull final Class<L> type, @Nullable final L propertyValue,
                                            @NotNull final String propertyName,
                                            @NotNull final ModelChangeConsumer changeConsumer) {
        super(type, propertyValue, propertyName, changeConsumer);
    }

    @Override
    @FXThread
    protected @NotNull NodeSelectorDialog<L> createNodeSelectorDialog() {
        final ModelChangeConsumer changeConsumer = getChangeConsumer();
        return new LightSelectorDialog<>(changeConsumer.getCurrentModel(), type, this::processAdd);
    }

    @Override
    @FXThread
    protected void reload() {

        final L light = getPropertyValue();
        final Label elementLabel = getElementLabel();

        String name = light == null ? null : light.getName();
        name = name == null && light != null ? light.getClass().getSimpleName() : name;

        elementLabel.setText(name == null ? NO_ELEMENT : name);
    }
}
