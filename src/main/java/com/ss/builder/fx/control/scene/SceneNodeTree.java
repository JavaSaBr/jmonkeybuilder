package com.ss.builder.fx.control.scene;

import com.ss.builder.model.undo.editor.SceneChangeConsumer;
import com.ss.builder.model.undo.editor.SceneChangeConsumer;
import com.ss.builder.fx.control.tree.NodeTree;
import com.ss.rlib.common.util.array.Array;
import javafx.scene.control.SelectionMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * The implementation of {@link NodeTree} to present scene's objects.
 *
 * @author JavaSaBr
 */
public class SceneNodeTree extends NodeTree<SceneChangeConsumer> {

    public SceneNodeTree(@NotNull final Consumer<Array<Object>> selectionHandler, @Nullable final SceneChangeConsumer consumer) {
        super(selectionHandler, consumer, SelectionMode.SINGLE);
    }
}
