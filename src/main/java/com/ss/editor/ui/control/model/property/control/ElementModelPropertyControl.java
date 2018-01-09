package com.ss.editor.ui.control.model.property.control;

import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.tree.dialog.NodeSelectorDialog;
import com.ss.editor.ui.control.property.impl.ElementPropertyControl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link ElementPropertyControl} to edit an elements from scene.
 *
 * @param <D> the type parameter
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public abstract class ElementModelPropertyControl<D, T> extends ElementPropertyControl<ModelChangeConsumer, D, T> {

    public ElementModelPropertyControl(@NotNull final Class<T> type, @Nullable final T propertyValue,
                                       @NotNull final String propertyName,
                                       @NotNull final ModelChangeConsumer changeConsumer) {
        super(type, propertyValue, propertyName, changeConsumer);
    }

    @Override
    @FXThread
    protected void processAdd() {
        final NodeSelectorDialog<T> dialog = createNodeSelectorDialog();
        dialog.show(this);
    }

    /**
     * Create node selector dialog node selector dialog.
     *
     * @return the node selector dialog
     */
    @FXThread
    protected @NotNull NodeSelectorDialog<T> createNodeSelectorDialog() {
        final ModelChangeConsumer changeConsumer = getChangeConsumer();
        return new NodeSelectorDialog<>(changeConsumer.getCurrentModel(), type, this::processAdd);
    }

    /**
     * Process add.
     *
     * @param newElement the new element
     */
    @FXThread
    protected void processAdd(@NotNull final T newElement) {
        changed(newElement, getPropertyValue());
    }
}
