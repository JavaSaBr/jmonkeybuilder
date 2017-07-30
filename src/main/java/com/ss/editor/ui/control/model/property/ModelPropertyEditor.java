package com.ss.editor.ui.control.model.property;

import static com.ss.editor.util.NodeUtils.findParent;
import com.jme3.scene.AssetLinkNode;
import com.jme3.scene.Spatial;
import com.ss.editor.model.node.Toneg0dParticleInfluencers;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.property.AbstractPropertyEditor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.node.ParticleNode;

/**
 * The component to contains property controls in the editor.
 *
 * @author JavaSaBr
 */
public class ModelPropertyEditor extends AbstractPropertyEditor<ModelChangeConsumer> {

    /**
     * Instantiates a new Model property editor.
     *
     * @param changeConsumer the change consumer
     */
    public ModelPropertyEditor(@NotNull final ModelChangeConsumer changeConsumer) {
        super(changeConsumer);
    }

    protected boolean isNeedUpdate(@Nullable final Object object) {

        final Object currentObject = getCurrentObject();

        if (currentObject instanceof ParticleNode && object instanceof ParticleEmitterNode) {
            final Object parent = findParent((Spatial) currentObject, ParticleEmitterNode.class::isInstance);
            return parent == object;
        }

        return super.isNeedUpdate(object);
    }

    @Override
    protected boolean canEdit(@NotNull final Object object, @Nullable final Object parent) {

        if (object instanceof Spatial) {
            final Object linkNode = findParent((Spatial) object, AssetLinkNode.class::isInstance);
            return linkNode == null || linkNode == object;
        } else if (parent instanceof Spatial) {
            final Object linkNode = findParent((Spatial) parent, AssetLinkNode.class::isInstance);
            return linkNode == null;
        } else if (parent instanceof Toneg0dParticleInfluencers) {
            final ParticleEmitterNode emitterNode = ((Toneg0dParticleInfluencers) parent).getEmitterNode();
            final Object linkNode = findParent(emitterNode, AssetLinkNode.class::isInstance);
            return linkNode == null;
        }

        return super.canEdit(object, parent);
    }
}
