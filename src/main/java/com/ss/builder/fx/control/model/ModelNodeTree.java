package com.ss.builder.fx.control.model;

import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.fx.control.tree.NodeTree;
import com.ss.builder.fx.control.tree.action.impl.multi.RemoveElementsAction;
import com.ss.rlib.common.util.array.Array;
import javafx.scene.control.SelectionMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * The implementation of {@link NodeTree} to present a structure of model in an editor.
 *
 * @author JavaSaBr
 */
public class ModelNodeTree extends NodeTree<ModelChangeConsumer> {

    static {
        register(RemoveElementsAction.ACTION_FILLER);
    }

    public ModelNodeTree(@NotNull Consumer<Array<Object>> selectionHandler, @Nullable ModelChangeConsumer consumer) {
        super(selectionHandler, consumer, SelectionMode.MULTIPLE);
    }

    public ModelNodeTree(
            @NotNull Consumer<Array<Object>> selectionHandler,
            @Nullable ModelChangeConsumer consumer,
            @NotNull SelectionMode selectionMode
    ) {
        super(selectionHandler, consumer, selectionMode);
    }
}
