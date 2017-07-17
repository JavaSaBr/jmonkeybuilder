package com.ss.editor.ui.control.model.tree;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.AbstractNodeTreeCell;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * The implementation of {@link AbstractNodeTree} to present a structure of model in an editor.
 *
 * @author JavaSaBr
 */
public class ModelNodeTree extends AbstractNodeTree<ModelChangeConsumer> {

    /**
     * Instantiates a new Model node tree.
     *
     * @param selectionHandler the selection handler
     * @param consumer         the consumer
     */
    public ModelNodeTree(@NotNull final Consumer<Object> selectionHandler, @Nullable final ModelChangeConsumer consumer) {
        super(selectionHandler, consumer);
    }

    @NotNull
    @Override
    protected AbstractNodeTreeCell<ModelChangeConsumer, ?> createNodeTreeCell() {
        return new ModelNodeTreeCell(this);
    }
}
