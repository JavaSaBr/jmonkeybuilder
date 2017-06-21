package com.ss.editor.ui.control.model.property.control.particle.influencer.interpolation.control;

import com.jme3.math.Vector3f;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.control.particle.influencer.interpolation.element.RotationAndInterpolationElement;

import org.jetbrains.annotations.NotNull;

import javafx.scene.layout.VBox;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.array.Array;
import tonegod.emitter.influencers.impl.RotationInfluencer;
import tonegod.emitter.interpolation.Interpolation;

/**
 * The control for editing sizes in the {@link RotationInfluencer}.
 *
 * @author JavaSaBr
 */
public class RotationInfluencerControl extends AbstractInterpolationInfluencerControl<RotationInfluencer> {

    public RotationInfluencerControl(@NotNull final ModelChangeConsumer modelChangeConsumer, @NotNull final RotationInfluencer influencer, @NotNull final Object parent) {
        super(modelChangeConsumer, influencer, parent);
    }

    @NotNull
    @Override
    protected String getControlTitle() {
        return Messages.PARTICLE_EMITTER_INFLUENCER_ROTATION_INTERPOLATION;
    }

    @Override
    protected int getMinElements() {
        return 1;
    }

    public void requestToChange(@NotNull final Vector3f newValue, final int index) {

        final RotationInfluencer influencer = getInfluencer();
        final Vector3f oldValue = influencer.getRotationSpeed(index);

        execute(newValue, oldValue, (rotationInfluencer, alpha) -> rotationInfluencer.updateRotationSpeed(alpha, index));
    }

    @Override
    protected void fillControl(@NotNull final RotationInfluencer influencer, @NotNull final VBox root) {

        final Array<Vector3f> speeds = influencer.getRotationSpeeds();

        for (int i = 0, length = speeds.size(); i < length; i++) {

            final RotationAndInterpolationElement element = new RotationAndInterpolationElement(this, i);
            element.prefWidthProperty().bind(widthProperty());

            FXUtils.addToPane(element, root);
        }
    }

    @Override
    protected void processAdd() {
        execute(true, false, (influencer, needAdd) -> {
            if (needAdd) {
                influencer.addRotationSpeed(Vector3f.UNIT_Z, Interpolation.LINEAR);
            } else {
                influencer.removeLast();
            }
        });
    }

    @Override
    protected void processRemove() {

        final RotationInfluencer influencer = getInfluencer();
        final Array<Vector3f> speeds = influencer.getRotationSpeeds();

        final Vector3f speed = influencer.getRotationSpeed(speeds.size() - 1);
        final Interpolation interpolation = influencer.getInterpolation(speeds.size() - 1);

        execute(true, false, (rotationInfluencer, needRemove) -> {
            if (needRemove) {
                rotationInfluencer.removeLast();
            } else {
                rotationInfluencer.addRotationSpeed(speed, interpolation);
            }
        });
    }
}
