package com.ss.editor.ui.control.model.property.control.particle.influencer.interpolation.element;

import com.jme3.math.Vector3f;
import com.ss.editor.Messages;
import com.ss.editor.ui.control.model.property.control.particle.influencer.interpolation.control.RotationInfluencerControl;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.influencers.impl.RotationInfluencer;

/**
 * The implementation of the element for {@link RotationInfluencer} for editing speeds and interpolation.
 *
 * @author JavaSaBr
 */
public class RotationInterpolationElement extends
        Vector3fInterpolationElement<RotationInfluencer, RotationInfluencerControl> {

    /**
     * Instantiates a new Rotation and interpolation element.
     *
     * @param control the control
     * @param index   the index
     */
    public RotationInterpolationElement(@NotNull final RotationInfluencerControl control, final int index) {
        super(control, index);
    }

    @NotNull
    @Override
    protected String getEditableTitle() {
        return Messages.MODEL_PROPERTY_SPEED;
    }

    @Override
    protected void requestToChange(final float x, final float y, final float z) {
        final RotationInfluencerControl control = getControl();
        control.requestToChange(new Vector3f(x, y, z), getIndex());
    }

    @Override
    protected Vector3f getValue(@NotNull final RotationInfluencer influencer) {
        return influencer.getRotationSpeed(getIndex());
    }
}
