package com.ss.editor.ui.control.model.property.control.particle.influencer.interpolation.control;

import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.control.particle.influencer.interpolation.element.AlphaInterpolationElement;

import org.jetbrains.annotations.NotNull;

import javafx.scene.layout.VBox;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.array.Array;
import tonegod.emitter.influencers.impl.AlphaInfluencer;
import tonegod.emitter.interpolation.Interpolation;

/**
 * The control for editing alphas in the {@link AlphaInfluencer}.
 *
 * @author JavaSaBr
 */
public class AlphaInfluencerControl extends AbstractInterpolationInfluencerControl<AlphaInfluencer> {

    /**
     * Instantiates a new Alpha influencer control.
     *
     * @param modelChangeConsumer the model change consumer
     * @param influencer          the influencer
     * @param parent              the parent
     */
    public AlphaInfluencerControl(@NotNull final ModelChangeConsumer modelChangeConsumer, @NotNull final AlphaInfluencer influencer,
                                  @NotNull final Object parent) {
        super(modelChangeConsumer, influencer, parent);
    }

    @NotNull
    @Override
    protected String getControlTitle() {
        return Messages.MODEL_PROPERTY_ALPHA_INTERPOLATION;
    }

    /**
     * Request to change.
     *
     * @param newValue the new value
     * @param index    the index
     */
    public void requestToChange(@NotNull final Float newValue, final int index) {

        final AlphaInfluencer influencer = getInfluencer();
        final Float oldValue = influencer.getAlpha(index);

        execute(newValue, oldValue, (alphaInfluencer, alpha) -> alphaInfluencer.updateAlpha(alpha, index));
    }

    @Override
    protected void fillControl(@NotNull final AlphaInfluencer influencer, @NotNull final VBox root) {

        final Array<Float> alphas = influencer.getAlphas();

        for (int i = 0, length = alphas.size(); i < length; i++) {

            final AlphaInterpolationElement element = new AlphaInterpolationElement(this, i);
            element.prefWidthProperty().bind(widthProperty());

            FXUtils.addToPane(element, root);
        }
    }

    @Override
    protected void processAdd() {
        execute(true, false, (alphaInfluencer, needAdd) -> {
            if (needAdd) {
                alphaInfluencer.addAlpha(1F, Interpolation.LINEAR);
            } else {
                alphaInfluencer.removeLast();
            }
        });
    }

    @Override
    protected void processRemove() {

        final AlphaInfluencer influencer = getInfluencer();
        final Array<Float> alphas = influencer.getAlphas();

        final Float alpha = influencer.getAlpha(alphas.size() - 1);
        final Interpolation interpolation = influencer.getInterpolation(alphas.size() - 1);

        execute(true, false, (alphaInfluencer, needRemove) -> {
            if (needRemove) {
                alphaInfluencer.removeLast();
            } else {
                alphaInfluencer.addAlpha(alpha, interpolation);
            }
        });
    }
}
