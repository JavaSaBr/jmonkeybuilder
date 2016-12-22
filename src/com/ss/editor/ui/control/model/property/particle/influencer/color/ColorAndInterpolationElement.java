package com.ss.editor.ui.control.model.property.particle.influencer.color;

import static java.lang.Math.min;

import com.jme3.math.ColorRGBA;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.util.UIUtils;

import org.jetbrains.annotations.NotNull;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;
import rlib.ui.util.FXUtils;
import rlib.util.array.Array;
import tonegod.emitter.influencers.ColorInfluencer;
import tonegod.emitter.interpolation.Interpolation;
import tonegod.emitter.interpolation.InterpolationManager;

/**
 * The implementation of the element for {@link ColorInfluencerControl} for editing color and
 * interpolation.
 *
 * @author JavaSaBr
 */
public class ColorAndInterpolationElement extends HBox {

    private static final StringConverter<Interpolation> STRING_CONVERTER = new StringConverter<Interpolation>() {

        @Override
        public String toString(final Interpolation object) {
            return object.getName();
        }

        @Override
        public Interpolation fromString(final String string) {
            return null;
        }
    };

    private static final ObservableList<Interpolation> INTERPOLATIONS;

    static {

        INTERPOLATIONS = FXCollections.observableArrayList();

        final Array<Interpolation> available = InterpolationManager.getAvailable();
        available.forEach(INTERPOLATIONS::add);
    }

    @NotNull
    private final ColorInfluencerControl control;

    /**
     * The color picker.
     */
    private ColorPicker colorPicker;

    /**
     * The interpolation chooser.
     */
    private ComboBox<Interpolation> interpolationComboBox;

    /**
     * The index.
     */
    private final int index;

    /**
     * The flag for ignoring listeners.
     */
    private boolean ignoreListeners;

    public ColorAndInterpolationElement(@NotNull final ColorInfluencerControl control, final int index) {
        setId(CSSIds.MODEL_PARAM_CONTROL_COLOR_INFLUENCER_ELEMENT);
        this.control = control;
        this.index = index;
        createComponents();
        setIgnoreListeners(true);
        reload();
        setIgnoreListeners(false);
    }

    /**
     * Create components.
     */
    private void createComponents() {

        final Label colorLabel = new Label("Color:");
        colorLabel.setId(CSSIds.MODEL_PARAM_CONTROL_PARAM_NAME_SINGLE_ROW);

        colorPicker = new ColorPicker();
        colorPicker.setId(CSSIds.MODEL_PARAM_CONTROL_COLOR_PICKER);
        colorPicker.prefWidthProperty().bind(widthProperty().multiply(0.3));
        colorPicker.valueProperty().addListener((observable, oldValue, newValue) -> processChange(newValue));

        final Label interpolationLabel = new Label("Interpolation:");
        interpolationLabel.setId(CSSIds.MODEL_PARAM_CONTROL_PARAM_NAME_SINGLE_ROW);

        interpolationComboBox = new ComboBox<>();
        interpolationComboBox.setId(CSSIds.MODEL_PARAM_CONTROL_COMBO_BOX);
        interpolationComboBox.setEditable(false);
        interpolationComboBox.prefWidthProperty().bind(widthProperty().multiply(0.3));
        interpolationComboBox.setConverter(STRING_CONVERTER);
        interpolationComboBox.getItems().setAll(INTERPOLATIONS);
        interpolationComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> processChange(newValue));

        FXUtils.addToPane(colorLabel, this);
        FXUtils.addToPane(colorPicker, this);
        FXUtils.addToPane(interpolationLabel, this);
        FXUtils.addToPane(interpolationComboBox, this);

        FXUtils.addClassTo(colorLabel, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(colorPicker, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(interpolationLabel, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(interpolationComboBox, CSSClasses.SPECIAL_FONT_13);
    }

    private void processChange(@NotNull final Interpolation newValue) {
        if (isIgnoreListeners()) return;
        final ColorInfluencerControl control = getControl();
        control.requestToChange(newValue, index);
    }

    private void processChange(@NotNull final Color newValue) {
        if (isIgnoreListeners()) return;
        final ColorRGBA newColor = UIUtils.convertColor(newValue);
        final ColorInfluencerControl control = getControl();
        control.requestToChange(newColor, index);
    }

    @NotNull
    private ColorInfluencerControl getControl() {
        return control;
    }

    /**
     * @return true if listeners is ignored.
     */
    private boolean isIgnoreListeners() {
        return ignoreListeners;
    }

    /**
     * @param ignoreListeners the flag for ignoring listeners.
     */
    private void setIgnoreListeners(final boolean ignoreListeners) {
        this.ignoreListeners = ignoreListeners;
    }

    /**
     * @return the color picker.
     */
    @NotNull
    private ColorPicker getColorPicker() {
        return colorPicker;
    }

    /**
     * @return the interpolation chooser.
     */
    @NotNull
    private ComboBox<Interpolation> getInterpolationComboBox() {
        return interpolationComboBox;
    }

    /**
     * Reload this element.
     */
    public void reload() {

        final ColorInfluencerControl control = getControl();
        final ColorInfluencer influencer = control.getInfluencer();

        final ColorRGBA newColor = influencer.getColors().get(index);
        final Interpolation newInterpolation = influencer.getInterpolations().get(index);

        final float red = min(newColor.getRed(), 1F);
        final float green = min(newColor.getGreen(), 1F);
        final float blue = min(newColor.getBlue(), 1F);
        final float alpha = min(newColor.getAlpha(), 1F);

        final ColorPicker colorPicker = getColorPicker();
        colorPicker.setValue(new Color(red, green, blue, alpha));

        final ComboBox<Interpolation> interpolationComboBox = getInterpolationComboBox();
        interpolationComboBox.getSelectionModel().select(newInterpolation);
    }
}
