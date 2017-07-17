package com.ss.editor.ui.control.model.property.control;

import static com.ss.editor.ui.control.model.property.control.ModelPropertyControl.newChangeHandler;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.tree.dialog.NodeSelectorDialog;
import com.ss.editor.ui.control.property.impl.AbstractElementPropertyControl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link AbstractElementPropertyControl} to edit an elements from scene.
 *
 * @param <D> the type parameter
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public abstract class AbstractElementModelPropertyControl<D, T> extends
        AbstractElementPropertyControl<ModelChangeConsumer, D, T> {

    /**
     * Instantiates a new Abstract element model property control.
     *
     * @param type           the type
     * @param propertyValue  the property value
     * @param propertyName   the property name
     * @param changeConsumer the change consumer
     */
    public AbstractElementModelPropertyControl(@NotNull final Class<T> type, @Nullable final T propertyValue,
                                               @NotNull final String propertyName,
                                               @NotNull final ModelChangeConsumer changeConsumer) {
        super(type, propertyValue, propertyName, changeConsumer, newChangeHandler());
    }

    @Override
    protected void processAdd() {
        final NodeSelectorDialog<T> dialog = createNodeSelectorDialog();
        dialog.show(this);
    }

    /**
     * Create node selector dialog node selector dialog.
     *
     * @return the node selector dialog
     */
    @NotNull
    protected NodeSelectorDialog<T> createNodeSelectorDialog() {
        final ModelChangeConsumer changeConsumer = getChangeConsumer();
        return new NodeSelectorDialog<>(changeConsumer.getCurrentModel(), type, this::processAdd);
    }

    /**
     * Process add.
     *
     * @param newElement the new element
     */
    protected void processAdd(@NotNull final T newElement) {
        changed(newElement, getPropertyValue());
    }
}
