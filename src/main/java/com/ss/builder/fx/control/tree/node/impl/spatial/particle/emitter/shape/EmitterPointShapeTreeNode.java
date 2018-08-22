package com.ss.builder.ui.control.tree.node.impl.spatial.particle.emitter.shape;

import com.jme3.effect.shapes.EmitterPointShape;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.ui.Icons;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.Icons;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link EmitterShapeTreeNode} for representing the {@link EmitterPointShape} in the editor.
 *
 * @author JavaSaBr
 */
public class EmitterPointShapeTreeNode extends EmitterShapeTreeNode {

    public EmitterPointShapeTreeNode(@NotNull final EmitterPointShape element, final long objectId) {
        super(element, objectId);
    }

    @Override
    @FxThread
    public @Nullable Image getIcon() {
        return Icons.POINTS_16;
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_PARTICLE_EMITTER_SHAPE_POINT;
    }
}
