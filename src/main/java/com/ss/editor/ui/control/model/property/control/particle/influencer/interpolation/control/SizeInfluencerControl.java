package com.ss.editor.ui.control.model.property.control.particle.influencer.interpolation.control;

import com.jme3.math.Vector3f;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.control.particle.influencer.interpolation.element.SizeInterpolationElement;

import org.jetbrains.annotations.NotNull;

import javafx.scene.layout.VBox;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.array.Array;
import tonegod.emitter.influencers.impl.SizeInfluencer;
import tonegod.emitter.interpolation.Interpolation;

/**
 * The control for editing sizes in the {@link SizeInfluencer}.
 *
 * @author JavaSaBr
 */
public class SizeInfluencerControl extends AbstractInterpolationInfluencerControl<SizeInfluencer> {

    /**
     * Instantiates a new Size influencer control.
     *
     * @param modelChangeConsumer the model change consumer
     * @param influencer          the influencer
     * @param parent              the parent
     */
    public SizeInfluencerControl(@NotNull final ModelChangeConsumer modelChangeConsumer, @NotNull final SizeInfluencer influencer, @NotNull final Object parent) {
        super(modelChangeConsumer, influencer, parent);
    }

    @NotNull
    @Override
    protected String getControlTitle() {
        return Messages.MODEL_PROPERTY_SIZE_INTERPOLATION;
    }

    /**
     * Request to change.
     *
     * @param newValue the new value
     * @param index    the index
     */
    public void requestToChange(@NotNull final Vector3f newValue, final int index) {

        final SizeInfluencer influencer = getInfluencer();
        final Vector3f oldValue = influencer.getSize(index);

        execute(newValue, oldValue, (sizeInfluencer, alpha) -> sizeInfluencer.updateSize(alpha, index));
    }

    @Override
    protected void fillControl(@NotNull final SizeInfluencer influencer, @NotNull final VBox root) {

        final Array<Vector3f> sizes = influencer.getSizes();

        for (int i = 0, length = sizes.size(); i < length; i++) {

            final SizeInterpolationElement element = new SizeInterpolationElement(this, i);
            element.prefWidthProperty().bind(widthProperty());

            FXUtils.addToPane(element, root);
        }
    }

    @Override
    protected void processAdd() {
        execute(true, false, (influencer, needAdd) -> {
            if (needAdd) {
                influencer.addSize(1F, Interpolation.LINEAR);
            } else {
                influencer.removeLast();
            }
        });
    }

    @Override
    protected void processRemove() {

        final SizeInfluencer influencer = getInfluencer();
        final Array<Vector3f> sizes = influencer.getSizes();

        final Vector3f size = influencer.getSize(sizes.size() - 1);
        final Interpolation interpolation = influencer.getInterpolation(sizes.size() - 1);

        execute(true, false, (sizeInfluencer, needRemove) -> {
            if (needRemove) {
                sizeInfluencer.removeLast();
            } else {
                sizeInfluencer.addSize(size, interpolation);
            }
        });
    }
}
