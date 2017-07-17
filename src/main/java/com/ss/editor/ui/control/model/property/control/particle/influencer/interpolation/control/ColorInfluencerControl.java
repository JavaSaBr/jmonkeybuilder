package com.ss.editor.ui.control.model.property.control.particle.influencer.interpolation.control;

import com.jme3.math.ColorRGBA;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.control.particle.influencer.interpolation.element.ColorInterpolationElement;

import org.jetbrains.annotations.NotNull;

import javafx.scene.layout.VBox;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.array.Array;
import tonegod.emitter.influencers.impl.ColorInfluencer;
import tonegod.emitter.interpolation.Interpolation;

/**
 * The control for editing colors in the {@link ColorInfluencer}.
 *
 * @author JavaSaBr
 */
public class ColorInfluencerControl extends AbstractInterpolationInfluencerControl<ColorInfluencer> {

    /**
     * Instantiates a new Color influencer control.
     *
     * @param modelChangeConsumer the model change consumer
     * @param influencer          the influencer
     * @param parent              the parent
     */
    public ColorInfluencerControl(@NotNull final ModelChangeConsumer modelChangeConsumer, @NotNull final ColorInfluencer influencer, @NotNull final Object parent) {
        super(modelChangeConsumer, influencer, parent);
    }

    @NotNull
    @Override
    protected String getControlTitle() {
        return Messages.MODEL_PROPERTY_COLOR_INTERPOLATION;
    }

    @Override
    protected void fillControl(@NotNull final ColorInfluencer influencer, @NotNull final VBox root) {

        final Array<ColorRGBA> colors = influencer.getColors();

        for (int i = 0, length = colors.size(); i < length; i++) {

            final ColorInterpolationElement element = new ColorInterpolationElement(this, i);
            element.prefWidthProperty().bind(widthProperty());

            FXUtils.addToPane(element, root);
        }
    }

    /**
     * Request to change.
     *
     * @param newValue the new value
     * @param index    the index
     */
    public void requestToChange(@NotNull final ColorRGBA newValue, final int index) {

        final ColorInfluencer influencer = getInfluencer();
        final ColorRGBA oldValue = influencer.getColor(index);

        execute(newValue, oldValue, (colorInfluencer, colorRGBA) -> colorInfluencer.updateColor(colorRGBA, index));
    }

    @Override
    protected void processAdd() {
        execute(true, false, (colorInfluencer, needAdd) -> {
            if (needAdd) {
                colorInfluencer.addColor(new ColorRGBA(ColorRGBA.White), Interpolation.LINEAR);
            } else {
                colorInfluencer.removeLast();
            }
        });
    }

    @Override
    protected void processRemove() {

        final ColorInfluencer influencer = getInfluencer();
        final Array<ColorRGBA> colors = influencer.getColors();

        final ColorRGBA color = influencer.getColor(colors.size() - 1);
        final Interpolation interpolation = influencer.getInterpolation(colors.size() - 1);

        execute(true, false, (colorInfluencer, needRemove) -> {
            if (needRemove) {
                colorInfluencer.removeLast();
            } else {
                colorInfluencer.addColor(color, interpolation);
            }
        });
    }
}
