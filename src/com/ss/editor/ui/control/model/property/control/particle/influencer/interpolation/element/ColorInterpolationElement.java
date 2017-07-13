package com.ss.editor.ui.control.model.property.control.particle.influencer.interpolation.element;

import static java.lang.Math.min;
import com.jme3.math.ColorRGBA;
import com.ss.editor.Messages;
import com.ss.editor.ui.control.model.property.control.particle.influencer.interpolation.control.ColorInfluencerControl;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.util.UIUtils;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;
import tonegod.emitter.influencers.impl.ColorInfluencer;

/**
 * The implementation of the element for {@link ColorInfluencerControl} for editing color and interpolation.
 *
 * @author JavaSaBr
 */
public class ColorInterpolationElement extends InterpolationElement<ColorInfluencer, ColorPicker, ColorInfluencerControl> {

    /**
     * Instantiates a new Color and interpolation element.
     *
     * @param control the control
     * @param index   the index
     */
    public ColorInterpolationElement(@NotNull final ColorInfluencerControl control, final int index) {
        super(control, index);
    }

    @NotNull
    @Override
    protected String getEditableTitle() {
        return Messages.MODEL_PROPERTY_COLOR;
    }

    @Override
    protected ColorPicker createEditableControl() {

        final ColorPicker colorPicker = new ColorPicker();
        colorPicker.prefWidthProperty().bind(widthProperty().multiply(0.35));
        colorPicker.valueProperty().addListener((observable, oldValue, newValue) -> processChange(newValue));

        FXUtils.addClassTo(colorPicker, CSSClasses.ABSTRACT_PARAM_CONTROL_COLOR_PICKER);

        return colorPicker;
    }

    /**
     * Handle change color value.
     */
    private void processChange(@NotNull final Color newValue) {
        if (isIgnoreListeners()) return;
        final ColorRGBA newColor = UIUtils.convertColor(newValue);
        final ColorInfluencerControl control = getControl();
        control.requestToChange(newColor, getIndex());
    }

    @Override
    public void reload() {

        final ColorInfluencerControl control = getControl();
        final ColorInfluencer influencer = control.getInfluencer();

        final ColorRGBA newColor = influencer.getColor(getIndex());

        final float red = min(newColor.getRed(), 1F);
        final float green = min(newColor.getGreen(), 1F);
        final float blue = min(newColor.getBlue(), 1F);
        final float alpha = min(newColor.getAlpha(), 1F);

        final ColorPicker colorPicker = getEditableControl();
        colorPicker.setValue(new Color(red, green, blue, alpha));

        super.reload();
    }
}
