package com.ss.editor.ui.control.model.node.spatial.particle.emitter.shape;

import com.jme3.effect.shapes.EmitterPointShape;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
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
    @FXThread
    public @Nullable Image getIcon() {
        return Icons.POINTS_16;
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_PARTICLE_EMITTER_SHAPE_POINT;
    }
}
