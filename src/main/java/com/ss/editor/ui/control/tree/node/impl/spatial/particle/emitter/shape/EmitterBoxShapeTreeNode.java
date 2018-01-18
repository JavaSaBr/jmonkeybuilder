package com.ss.editor.ui.control.tree.node.impl.spatial.particle.emitter.shape;

import com.jme3.effect.shapes.EmitterBoxShape;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.Icons;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link EmitterShapeTreeNode} for representing the {@link EmitterShapeTreeNode} in the editor.
 *
 * @author JavaSaBr
 */
public class EmitterBoxShapeTreeNode extends EmitterShapeTreeNode {

    public EmitterBoxShapeTreeNode(@NotNull final EmitterBoxShape element, final long objectId) {
        super(element, objectId);
    }

    @Override
    @FxThread
    public @Nullable Image getIcon() {
        return Icons.CUBE_16;
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_PARTICLE_EMITTER_SHAPE_BOX;
    }
}
