package com.ss.editor.ui.control.layer;

import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.AbstractNodeTreeCell;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * The implementation of {@link AbstractNodeTree} to present the structure of layers in an editor.
 *
 * @author JavaSaBr
 */
public class LayerNodeTree extends AbstractNodeTree<SceneChangeConsumer> {

    public LayerNodeTree(@NotNull final Consumer<Object> selectionHandler, @Nullable final SceneChangeConsumer consumer) {
        super(selectionHandler, consumer);
    }

    @NotNull
    @Override
    protected AbstractNodeTreeCell<SceneChangeConsumer, ?> createNodeTreeCell() {
        return new LayerNodeTreeCell(this);
    }
}
