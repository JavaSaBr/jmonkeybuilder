package com.ss.editor.ui.control.model.tree.node.spatial;

import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;
import tonegod.emitter.EmitterMesh;

/**
 * The implementation of the {@link ModelNode} for representing the {@link EmitterMesh} in the
 * editor.
 *
 * @author JavaSaBr
 */
public class ParticleEmitterMeshModelNode extends ModelNode<EmitterMesh> {

    public ParticleEmitterMeshModelNode(final EmitterMesh element, final long objectId) {
        super(element, objectId);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.MESH_16;
    }

    @NotNull
    @Override
    public String getName() {
        return "Emitter mesh";
    }

    @Override
    public boolean canMove() {
        return false;
    }

    @Override
    public boolean canCopy() {
        return false;
    }
}
