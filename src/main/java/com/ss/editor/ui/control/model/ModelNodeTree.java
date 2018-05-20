package com.ss.editor.ui.control.model;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.action.impl.multi.RemoveElementsAction;
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

    public ModelNodeTree(@NotNull final Consumer<Array<Object>> selectionHandler,
                         @Nullable final ModelChangeConsumer consumer) {
        super(selectionHandler, consumer, SelectionMode.MULTIPLE);
    }

    public ModelNodeTree(@NotNull final Consumer<Array<Object>> selectionHandler,
                          @Nullable final ModelChangeConsumer consumer, @NotNull final SelectionMode selectionMode) {
        super(selectionHandler, consumer, selectionMode);
    }
}
