package com.ss.editor.ui.control.model.node.spatial.particle.emitter.shape;

import com.jme3.effect.shapes.EmitterShape;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.node.TreeNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link TreeNode} for representing the {@link EmitterShape} in the editor.
 *
 * @author JavaSaBr
 */
public class EmitterShapeTreeNode extends TreeNode<EmitterShape> {

    public EmitterShapeTreeNode(@NotNull final EmitterShape element, final long objectId) {
        super(element, objectId);
    }

    @Override
    @FxThread
    public @Nullable Image getIcon() {
        return Icons.GEOMETRY_16;
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        final EmitterShape element = getElement();
        return element.getClass().getSimpleName();
    }
}
