package com.ss.editor.ui.control.model.property.particle.influencer.interpolation.element;

import static java.lang.Float.parseFloat;
import static java.lang.Math.max;
import static java.lang.Math.min;

import com.jme3.math.Vector3f;
import com.ss.editor.ui.control.model.property.particle.influencer.interpolation.control.SizeInfluencerControl;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import rlib.ui.util.FXUtils;
import tonegod.emitter.influencers.SizeInfluencer;
import tonegod.emitter.interpolation.Interpolation;

/**
 * The implementation of the element for {@link SizeInfluencer} for editing size and interpolation.
 *
 * @author JavaSaBr
 */
public class SizeAndInterpolationElement extends InterpolationElement<SizeInfluencer, Parent, SizeInfluencerControl> {

    /**
     * The field X.
     */
    private TextField xField;

    /**
     * The field Y.
     */
    private TextField yField;

    /**
     * The field Z.
     */
    private TextField zField;

    public SizeAndInterpolationElement(@NotNull final SizeInfluencerControl control, final int index) {
        super(control, index);
    }

    @NotNull
    @Override
    protected String getEditableTitle() {
        return "Size:";
    }

    @Override
    protected Parent createEditableControl() {

        final Label xLabel = new Label("x:");
        xLabel.setId(CSSIds.MODEL_PARAM_CONTROL_NUMBER_LABEL);

        final Label yLabel = new Label("y:");
        yLabel.setId(CSSIds.MODEL_PARAM_CONTROL_NUMBER_LABEL);

        final Label zLabel = new Label("z:");
        zLabel.setId(CSSIds.MODEL_PARAM_CONTROL_NUMBER_LABEL);

        xField = new TextField();
        xField.setId(CSSIds.MODEL_PARAM_CONTROL_VECTOR3F_FIELD);
        xField.setOnScroll(this::processScroll);
        xField.setOnKeyReleased(this::processChange);

        yField = new TextField();
        yField.setId(CSSIds.MODEL_PARAM_CONTROL_VECTOR3F_FIELD);
        yField.setOnScroll(this::processScroll);
        yField.setOnKeyReleased(this::processChange);

        zField = new TextField();
        zField.setId(CSSIds.MODEL_PARAM_CONTROL_VECTOR3F_FIELD);
        zField.setOnScroll(this::processScroll);
        zField.setOnKeyReleased(this::processChange);

        final HBox container = new HBox(xLabel, xField, yLabel, yField, zLabel, zField);

        FXUtils.addClassTo(xLabel, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(yLabel, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(zLabel, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(xField, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(yField, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(zField, CSSClasses.SPECIAL_FONT_13);

        xField.prefWidthProperty().bind(container.widthProperty().divide(4));
        yField.prefWidthProperty().bind(container.widthProperty().divide(4));
        zField.prefWidthProperty().bind(container.widthProperty().divide(4));

        container.prefWidthProperty().bind(widthProperty().multiply(0.5));

        return container;
    }

    @Override
    public boolean isNeedEditableLabel() {
        return false;
    }

    /**
     * The process of scrolling value.
     */
    private void processScroll(final ScrollEvent event) {
        if (!event.isControlDown()) return;

        final TextField source = (TextField) event.getSource();
        final String text = source.getText();

        float value;
        try {
            value = parseFloat(text);
        } catch (final NumberFormatException e) {
            return;
        }

        long longValue = (long) (value * 1000);
        longValue += event.getDeltaY() * 10;

        final String result = String.valueOf(max(min(longValue / 1000F, 1F), 0F));
        source.setText(result);
        source.positionCaret(result.length());

        processChange((KeyEvent) null);
    }

    private void processChange(@Nullable final KeyEvent event) {
        if (isIgnoreListeners() || (event != null && event.getCode() != KeyCode.ENTER)) return;

        float x;
        try {
            x = Float.parseFloat(xField.getText());
        } catch (final NumberFormatException e) {
            return;
        }

        float y;
        try {
            y = Float.parseFloat(yField.getText());
        } catch (final NumberFormatException e) {
            return;
        }

        float z;
        try {
            z = Float.parseFloat(zField.getText());
        } catch (final NumberFormatException e) {
            return;
        }

        final SizeInfluencerControl control = getControl();
        control.requestToChange(new Vector3f(x, y, z), getIndex());
    }

    /**
     * Reload this element.
     */
    public void reload() {

        final SizeInfluencerControl control = getControl();
        final SizeInfluencer influencer = control.getInfluencer();

        final Vector3f size = influencer.getSize(getIndex());
        final Interpolation newInterpolation = influencer.getInterpolation(getIndex());

        xField.setText(String.valueOf(size.getX()));
        xField.positionCaret(xField.getText().length());

        yField.setText(String.valueOf(size.getY()));
        yField.positionCaret(xField.getText().length());

        zField.setText(String.valueOf(size.getZ()));
        zField.positionCaret(xField.getText().length());

        final ComboBox<Interpolation> interpolationComboBox = getInterpolationComboBox();
        interpolationComboBox.getSelectionModel().select(newInterpolation);
    }
}
