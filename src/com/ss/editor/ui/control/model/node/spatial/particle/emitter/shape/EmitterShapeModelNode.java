package com.ss.editor.ui.control.model.node.spatial.particle.emitter.shape;

import com.jme3.effect.shapes.EmitterShape;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.node.ModelNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link ModelNode} for representing the {@link EmitterShape} in the editor.
 *
 * @author JavaSaBr
 */
public class EmitterShapeModelNode extends ModelNode<EmitterShape> {

    /**
     * Instantiates a new Emitter shape model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public EmitterShapeModelNode(@NotNull final EmitterShape element, final long objectId) {
        super(element, objectId);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.GEOMETRY_16;
    }

    @NotNull
    @Override
    public String getName() {
        final EmitterShape element = getElement();
        return element.getClass().getSimpleName();
    }
}
