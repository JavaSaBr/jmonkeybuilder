package com.ss.editor.ui.control.layer;

import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.control.tree.NodeTreeCell;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of {@link NodeTreeCell} to show layer nodes.
 *
 * @author JavaSaBr
 */
public class LayerNodeTreeCell extends NodeTreeCell<SceneChangeConsumer, LayerNodeTree> {

    /**
     * Instantiates a new Layer node tree cell.
     *
     * @param nodeTree the node tree
     */
    LayerNodeTreeCell(@NotNull final LayerNodeTree nodeTree) {
        super(nodeTree);
    }
}
