package com.ss.builder.ui.control.layer;

import com.ss.builder.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.control.tree.NodeTreeCell;
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
