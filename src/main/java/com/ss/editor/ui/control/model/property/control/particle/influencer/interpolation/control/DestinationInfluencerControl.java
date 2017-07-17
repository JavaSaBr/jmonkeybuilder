package com.ss.editor.ui.control.model.property.control.particle.influencer.interpolation.control;

import com.jme3.math.Vector3f;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.control.particle.influencer.interpolation.element.DestinationWeightInterpolationElement;

import org.jetbrains.annotations.NotNull;

import javafx.scene.layout.VBox;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.array.Array;
import tonegod.emitter.influencers.impl.DestinationInfluencer;
import tonegod.emitter.interpolation.Interpolation;

/**
 * The control for editing sizes in the {@link DestinationInfluencer}.
 *
 * @author JavaSaBr
 */
public class DestinationInfluencerControl extends AbstractInterpolationInfluencerControl<DestinationInfluencer> {

    /**
     * Instantiates a new Destination influencer control.
     *
     * @param modelChangeConsumer the model change consumer
     * @param influencer          the influencer
     * @param parent              the parent
     */
    public DestinationInfluencerControl(@NotNull final ModelChangeConsumer modelChangeConsumer,
                                        @NotNull final DestinationInfluencer influencer, @NotNull final Object parent) {
        super(modelChangeConsumer, influencer, parent);
    }

    @NotNull
    @Override
    protected String getControlTitle() {
        return Messages.MODEL_PROPERTY_DESTINATION_INTERPOLATION;
    }

    @Override
    protected int getMinElements() {
        return 1;
    }

    /**
     * Request to change.
     *
     * @param newValue the new value
     * @param index    the index
     */
    public void requestToChange(@NotNull final Vector3f newValue, final int index) {

        final DestinationInfluencer influencer = getInfluencer();
        final Vector3f oldValue = influencer.getDestination(index);

        execute(newValue, oldValue, (destinationInfluencer, destination) -> destinationInfluencer.updateDestination(destination, index));
    }

    /**
     * Request to change.
     *
     * @param newValue the new value
     * @param index    the index
     */
    public void requestToChange(@NotNull final Float newValue, final int index) {

        final DestinationInfluencer influencer = getInfluencer();
        final Float oldValue = influencer.getWeight(index);

        execute(newValue, oldValue, (destinationInfluencer, weight) -> destinationInfluencer.updateWeight(weight, index));
    }

    @Override
    protected void fillControl(@NotNull final DestinationInfluencer influencer, @NotNull final VBox root) {

        final Array<Vector3f> speeds = influencer.getDestinations();

        for (int i = 0, length = speeds.size(); i < length; i++) {

            final DestinationWeightInterpolationElement element = new DestinationWeightInterpolationElement(this, i);
            element.prefWidthProperty().bind(widthProperty());

            FXUtils.addToPane(element, root);
        }
    }

    @Override
    protected void processAdd() {
        execute(true, false, (influencer, needAdd) -> {
            if (needAdd) {
                influencer.addDestination(Vector3f.UNIT_Z, 1, Interpolation.LINEAR);
            } else {
                influencer.removeLast();
            }
        });
    }

    @Override
    protected void processRemove() {

        final DestinationInfluencer influencer = getInfluencer();
        final Array<Vector3f> destinations = influencer.getDestinations();

        final Vector3f destination = influencer.getDestination(destinations.size() - 1);
        final Interpolation interpolation = influencer.getInterpolation(destinations.size() - 1);
        final Float weight = influencer.getWeight(destinations.size() - 1);

        execute(true, false, (rotationInfluencer, needRemove) -> {
            if (needRemove) {
                rotationInfluencer.removeLast();
            } else {
                rotationInfluencer.addDestination(destination, weight, interpolation);
            }
        });
    }
}
