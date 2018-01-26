package com.ss.editor.ui.control.property.impl;

import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.dialog.node.selector.NodeSelectorDialog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link ElementPropertyControl} to edit elements from models.
 *
 * @param <D> the edited object's type.
 * @author JavaSaBr
 */
public abstract class ElementModelPropertyControl<D, T> extends ElementPropertyControl<ModelChangeConsumer, D, T> {

    public ElementModelPropertyControl(@NotNull final Class<T> type, @Nullable final T propertyValue,
                                       @NotNull final String propertyName,
                                       @NotNull final ModelChangeConsumer changeConsumer) {
        super(type, propertyValue, propertyName, changeConsumer);
    }

    @Override
    @FxThread
    protected void processAdd() {
        final NodeSelectorDialog<T> dialog = createNodeSelectorDialog();
        dialog.show(this);
    }

    /**
     * Create node selector dialog node selector dialog.
     *
     * @return the node selector dialog
     */
    @FxThread
    protected @NotNull NodeSelectorDialog<T> createNodeSelectorDialog() {
        final ModelChangeConsumer changeConsumer = getChangeConsumer();
        return new NodeSelectorDialog<>(changeConsumer.getCurrentModel(), type, this::processAdd);
    }

    /**
     * Process add.
     *
     * @param newElement the new element
     */
    @FxThread
    protected void processAdd(@NotNull final T newElement) {
        changed(newElement, getPropertyValue());
    }
}
