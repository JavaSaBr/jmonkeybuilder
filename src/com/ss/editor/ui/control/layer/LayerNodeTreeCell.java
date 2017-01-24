package com.ss.editor.ui.control.layer;

import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.control.tree.AbstractNodeTreeCell;

import org.jetbrains.annotations.NotNull;

/**
 * The implementation of {@link AbstractNodeTreeCell} to show layer nodes.
 *
 * @author JavaSaBr
 */
public class LayerNodeTreeCell extends AbstractNodeTreeCell<SceneChangeConsumer, LayerNodeTree> {

    public LayerNodeTreeCell(@NotNull final LayerNodeTree nodeTree) {
        super(nodeTree);
    }
}
