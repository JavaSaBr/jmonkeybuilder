package com.ss.editor.ui.control.model.property.control.particle.influencer.interpolation.element;

import com.jme3.math.Vector3f;
import com.ss.editor.Messages;
import com.ss.editor.ui.control.model.property.control.particle.influencer.interpolation.control.SizeInfluencerControl;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.influencers.impl.SizeInfluencer;

/**
 * The implementation of the element for {@link SizeInfluencer} for editing size and interpolation.
 *
 * @author JavaSaBr
 */
public class SizeAndInterpolationElement extends Vector3fAndInterpolationElement<SizeInfluencer, SizeInfluencerControl> {

    public SizeAndInterpolationElement(@NotNull final SizeInfluencerControl control, final int index) {
        super(control, index);
    }

    @NotNull
    @Override
    protected String getEditableTitle() {
        return Messages.PARTICLE_EMITTER_INFLUENCER_SIZE;
    }

    @Override
    protected void requestToChange(final float x, final float y, final float z) {
        final SizeInfluencerControl control = getControl();
        control.requestToChange(new Vector3f(x, y, z), getIndex());
    }

    @Override
    protected Vector3f getValue(final SizeInfluencer influencer) {
        return influencer.getSize(getIndex());
    }
}
