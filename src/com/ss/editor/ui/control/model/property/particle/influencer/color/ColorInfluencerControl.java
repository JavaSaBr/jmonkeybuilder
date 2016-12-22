package com.ss.editor.ui.control.model.property.particle.influencer.color;

import com.jme3.math.ColorRGBA;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.particle.influencer.AbstractInterpolationInfluencerControl;

import org.jetbrains.annotations.NotNull;

import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;
import rlib.util.array.Array;
import tonegod.emitter.influencers.ColorInfluencer;
import tonegod.emitter.interpolation.Interpolation;

/**
 * The control for editing colors in the {@link ColorInfluencer}.
 *
 * @author JavaSaBr
 */
public class ColorInfluencerControl extends AbstractInterpolationInfluencerControl<ColorInfluencer> {

    public ColorInfluencerControl(@NotNull final ModelChangeConsumer modelChangeConsumer, @NotNull final ColorInfluencer influencer, @NotNull final Object parent) {
        super(modelChangeConsumer, influencer, parent);
    }

    @NotNull
    @Override
    protected String getControlTitle() {
        return "Colors";
    }

    @NotNull
    @Override
    protected String getPropertyName() {
        return "Colors";
    }

    @Override
    protected void fillControl(@NotNull final ColorInfluencer influencer, @NotNull final VBox root) {

        final Array<ColorRGBA> colors = influencer.getColors();

        for (int i = 0, length = colors.size(); i < length; i++) {

            final ColorAndInterpolationElement element = new ColorAndInterpolationElement(this, i);
            element.prefWidthProperty().bind(widthProperty());

            FXUtils.addToPane(element, root);
        }
    }

    public void requestToChange(final ColorRGBA newValue, final int index) {

        final ColorInfluencer influencer = getInfluencer();
        final ColorRGBA oldValue = influencer.getColors().get(index);

        execute(newValue, oldValue, (colorInfluencer, colorRGBA) -> colorInfluencer.updateColor(colorRGBA, index));
    }

    public void requestToChange(final Interpolation newValue, final int index) {

        final ColorInfluencer influencer = getInfluencer();
        final Interpolation oldValue = influencer.getInterpolations().get(index);

        execute(newValue, oldValue, (colorInfluencer, interpolation) -> colorInfluencer.updateInterpolation(interpolation, index));
    }

    @Override
    protected void processAdd() {

        final ColorInfluencer influencer = getInfluencer();
        final Array<ColorRGBA> colors = influencer.getColors();
        final int index = colors.size();

        execute(-1, index, (colorInfluencer, newIndex) -> {
            if (newIndex != -1) {
                colorInfluencer.removeColor(newIndex);
            } else {
                colorInfluencer.addColor(new ColorRGBA(ColorRGBA.White), Interpolation.LINEAR);
            }
        });
    }
}
