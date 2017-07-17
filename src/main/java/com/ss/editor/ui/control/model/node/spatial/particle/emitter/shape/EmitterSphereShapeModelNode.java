package com.ss.editor.ui.control.model.node.spatial.particle.emitter.shape;

import com.jme3.effect.shapes.EmitterSphereShape;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link EmitterShapeModelNode} for representing the {@link EmitterSphereShape} in the editor.
 *
 * @author JavaSaBr
 */
public class EmitterSphereShapeModelNode extends EmitterShapeModelNode {

    /**
     * Instantiates a new Emitter sphere shape model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public EmitterSphereShapeModelNode(@NotNull final EmitterSphereShape element, final long objectId) {
        super(element, objectId);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.SPHERE_16;
    }

    @NotNull
    @Override
    public String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_PARTICLE_EMITTER_SHAPE_SPHERE;
    }
}
