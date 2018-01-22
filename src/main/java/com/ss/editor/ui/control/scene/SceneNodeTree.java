package com.ss.editor.ui.control.scene;

import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.control.tree.NodeTree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * The implementation of {@link NodeTree} to present scene's objects.
 *
 * @author JavaSaBr
 */
public class SceneNodeTree extends NodeTree<SceneChangeConsumer> {

    public SceneNodeTree(@NotNull final Consumer<Object> selectionHandler, @Nullable final SceneChangeConsumer consumer) {
        super(selectionHandler, consumer);
    }
}
