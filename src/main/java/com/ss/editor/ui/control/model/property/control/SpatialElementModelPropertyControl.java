package com.ss.editor.ui.control.model.property.control;

import com.jme3.scene.Spatial;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.tree.dialog.NodeSelectorDialog;
import com.ss.editor.ui.control.model.tree.dialog.SpatialSelectorDialog;
import javafx.scene.control.Label;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link ElementModelPropertyControl} to edit a spatial from a scene.
 *
 * @param <D> the type parameter
 * @author JavaSaBr
 */
public class SpatialElementModelPropertyControl<S extends Spatial, D> extends ElementModelPropertyControl<D, S> {

    public SpatialElementModelPropertyControl(@NotNull Class<S> type, @Nullable final S propertyValue,
                                              @NotNull final String propertyName,
                                              @NotNull final ModelChangeConsumer changeConsumer) {
        super(type, propertyValue, propertyName, changeConsumer);
    }

    @Override
    @FxThread
    protected @NotNull NodeSelectorDialog<S> createNodeSelectorDialog() {
        final ModelChangeConsumer changeConsumer = getChangeConsumer();
        return new SpatialSelectorDialog<>(changeConsumer.getCurrentModel(), type, this::processAdd);
    }

    @Override
    @FxThread
    protected void reload() {

        final Spatial spatial = getPropertyValue();
        final Label elementLabel = getElementLabel();

        String name = spatial == null ? null : spatial.getName();
        name = name == null && spatial != null ? spatial.getClass().getSimpleName() : name;

        elementLabel.setText(name == null ? NO_ELEMENT : name);
    }
}
