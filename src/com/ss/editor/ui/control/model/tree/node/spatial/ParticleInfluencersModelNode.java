package com.ss.editor.ui.control.model.tree.node.spatial;

import com.ss.editor.model.node.ParticleInfluencers;
import com.ss.editor.ui.control.model.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;

import rlib.util.ArrayUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

import static com.ss.editor.ui.control.model.tree.node.ModelNodeFactory.createFor;

/**
 * The implementation of the {@link ModelNode} for representing the {@link ParticleInfluencers} in
 * the editor.
 *
 * @author JavaSaBr
 */
public class ParticleInfluencersModelNode extends ModelNode<ParticleInfluencers> {

    public ParticleInfluencersModelNode(@NotNull final ParticleInfluencers element, final long objectId) {
        super(element, objectId);
    }

    @NotNull
    @Override
    public String getName() {
        return "Influencers";
    }

    @NotNull
    @Override
    public Array<ModelNode<?>> getChildren() {
        final Array<ModelNode<?>> result = ArrayFactory.newArray(ModelNode.class);
        final ParticleInfluencers element = getElement();
        ArrayUtils.forEach(element.getInfluencers(), influencer -> result.add(createFor(influencer)));
        return result;
    }

    @Override
    public boolean hasChildren() {
        return true;
    }
}
