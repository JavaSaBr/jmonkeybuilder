package com.ss.editor.ui.control.model.property.particle.influencer.interpolation.element;

import com.jme3.math.Vector3f;
import com.ss.editor.ui.control.model.property.particle.influencer.interpolation.control.RotationInfluencerControl;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.influencers.impl.RotationInfluencer;

/**
 * The implementation of the element for {@link RotationInfluencer} for editing speeds and
 * interpolation.
 *
 * @author JavaSaBr
 */
public class RotationAndInterpolationElement extends Vector3fAndInterpolationElement<RotationInfluencer, RotationInfluencerControl> {

    public RotationAndInterpolationElement(@NotNull final RotationInfluencerControl control, final int index) {
        super(control, index);
    }

    @NotNull
    @Override
    protected String getEditableTitle() {
        return "Speeds:";
    }

    @Override
    protected void requestToChange(final float x, final float y, final float z) {
        final RotationInfluencerControl control = getControl();
        control.requestToChange(new Vector3f(x, y, z), getIndex());
    }

    @Override
    protected Vector3f getValue(final RotationInfluencer influencer) {
        return influencer.getRotationSpeed(getIndex());
    }

    @Override
    protected float getMaxValue() {
        return Float.MAX_VALUE;
    }

    @Override
    protected float getMinValue() {
        return Float.MIN_VALUE;
    }
}
