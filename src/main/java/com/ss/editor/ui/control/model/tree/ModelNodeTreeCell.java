package com.ss.editor.ui.control.model.tree;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.tree.NodeTreeCell;
import org.jetbrains.annotations.NotNull;

/**
 * THe implementation of {@link NodeTreeCell} to show model nodes.
 *
 * @author JavaSaBr
 */
public class ModelNodeTreeCell extends NodeTreeCell<ModelChangeConsumer, ModelNodeTree> {

    /**
     * Instantiates a new Model node tree cell.
     *
     * @param nodeTree the node tree
     */
    ModelNodeTreeCell(@NotNull final ModelNodeTree nodeTree) {
        super(nodeTree);
    }
}
