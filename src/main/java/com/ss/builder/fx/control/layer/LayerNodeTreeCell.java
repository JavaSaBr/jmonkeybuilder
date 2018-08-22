package com.ss.builder.fx.control.layer;

import com.ss.builder.model.undo.editor.SceneChangeConsumer;
import com.ss.builder.model.undo.editor.SceneChangeConsumer;
import com.ss.builder.fx.control.tree.NodeTreeCell;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of {@link NodeTreeCell} to show layer nodes.
 *
 * @author JavaSaBr
 */
public class LayerNodeTreeCell extends NodeTreeCell<SceneChangeConsumer, LayerNodeTree> {

    public LayerNodeTreeCell(@NotNull final LayerNodeTree nodeTree) {
        super(nodeTree);
    }
}
