package com.ss.editor.ui.control.model.property.particle.influencer.interpolation.control;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.particle.influencer.interpolation.element.AlphaAndInterpolationElement;

import org.jetbrains.annotations.NotNull;

import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;
import rlib.util.array.Array;
import tonegod.emitter.influencers.AlphaInfluencer;
import tonegod.emitter.interpolation.Interpolation;

/**
 * The control for editing alphas in the {@link AlphaInfluencer}.
 *
 * @author JavaSaBr
 */
public class AlphaInfluencerControl extends AbstractInterpolationInfluencerControl<AlphaInfluencer> {

    public AlphaInfluencerControl(@NotNull final ModelChangeConsumer modelChangeConsumer, @NotNull final AlphaInfluencer influencer, @NotNull final Object parent) {
        super(modelChangeConsumer, influencer, parent);
    }

    @NotNull
    @Override
    protected String getControlTitle() {
        return "Alphas";
    }

    @NotNull
    @Override
    protected String getPropertyName() {
        return "Alphas";
    }

    @Override
    public void requestToChange(@NotNull final Interpolation newValue, final int index) {

        final AlphaInfluencer influencer = getInfluencer();
        final Interpolation oldValue = influencer.getInterpolation(index);

        execute(newValue, oldValue, (colorInfluencer, interpolation) -> colorInfluencer.updateInterpolation(interpolation, index));
    }

    public void requestToChange(@NotNull final Float newValue, final int index) {

        final AlphaInfluencer influencer = getInfluencer();
        final Float oldValue = influencer.getAlpha(index);

        execute(newValue, oldValue, (colorInfluencer, alpha) -> colorInfluencer.updateAlpha(alpha, index));
    }

    @Override
    protected boolean isNeedRebuild(@NotNull final AlphaInfluencer influencer, final int currentCount) {
        return influencer.getAlphas().size() != currentCount;
    }

    @Override
    protected void fillControl(@NotNull final AlphaInfluencer influencer, @NotNull final VBox root) {

        final Array<Float> alphas = influencer.getAlphas();

        for (int i = 0, length = alphas.size(); i < length; i++) {

            final AlphaAndInterpolationElement element = new AlphaAndInterpolationElement(this, i);
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
