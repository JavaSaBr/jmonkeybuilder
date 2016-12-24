package com.ss.editor.ui.control.model.property.particle.influencer.interpolation.element;

import static java.lang.Float.parseFloat;

import com.jme3.math.Vector3f;
import com.ss.editor.ui.control.model.property.particle.influencer.interpolation.control.DestinationInfluencerControl;
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
import tonegod.emitter.influencers.DestinationInfluencer;
import tonegod.emitter.interpolation.Interpolation;

/**
 * The implementation of the element for editing vector values and interpolation.
 *
 * @author JavaSaBr
 */
public class DestinationWeightAndInterpolationElement extends InterpolationElement<DestinationInfluencer, Parent, DestinationInfluencerControl> {

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

    /**
     * The weight.
     */
    private TextField weightField;

    public DestinationWeightAndInterpolationElement(@NotNull final DestinationInfluencerControl control, final int index) {
        super(control, index);
    }

    @Override
    protected Parent createEditableControl() {

        final Label xLabel = new Label("x:");
        xLabel.setId(CSSIds.MODEL_PARAM_CONTROL_NUMBER_LABEL);

        final Label yLabel = new Label("y:");
        yLabel.setId(CSSIds.MODEL_PARAM_CONTROL_NUMBER_LABEL);

        final Label zLabel = new Label("z:");
        zLabel.setId(CSSIds.MODEL_PARAM_CONTROL_NUMBER_LABEL);

        final Label weightLabel = new Label("w:");
        weightLabel.setId(CSSIds.MODEL_PARAM_CONTROL_NUMBER_LABEL);

        xField = new TextField();
        xField.setId(CSSIds.MODEL_PARAM_CONTROL_VECTOR3F_FIELD);
        xField.setOnScroll(this::processScroll);
        xField.setOnKeyReleased(this::processDestinationChange);

        yField = new TextField();
        yField.setId(CSSIds.MODEL_PARAM_CONTROL_VECTOR3F_FIELD);
        yField.setOnScroll(this::processScroll);
        yField.setOnKeyReleased(this::processDestinationChange);

        zField = new TextField();
        zField.setId(CSSIds.MODEL_PARAM_CONTROL_VECTOR3F_FIELD);
        zField.setOnScroll(this::processScroll);
        zField.setOnKeyReleased(this::processDestinationChange);

        weightField = new TextField();
        weightField.setId(CSSIds.MODEL_PARAM_CONTROL_VECTOR3F_FIELD);
        weightField.setOnScroll(this::processScroll);
        weightField.setOnKeyReleased(this::processDestinationChange);

        final HBox container = new HBox(xLabel, xField, yLabel, yField, zLabel, zField, weightLabel, weightField);

        FXUtils.addClassTo(xLabel, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(yLabel, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(zLabel, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(weightLabel, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(xField, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(yField, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(zField, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(weightField, CSSClasses.SPECIAL_FONT_13);

        xField.prefWidthProperty().bind(container.widthProperty().divide(6));
        yField.prefWidthProperty().bind(container.widthProperty().divide(6));
        zField.prefWidthProperty().bind(container.widthProperty().divide(6));
        weightField.prefWidthProperty().bind(container.widthProperty().divide(6));

        container.prefWidthProperty().bind(widthProperty().multiply(0.6));

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

        final String result = String.valueOf(longValue / 1000F);
        source.setText(result);
        source.positionCaret(result.length());

        if (source == weightField) {
            processWeightChange(null);
        } else {
            processDestinationChange(null);
        }
    }


    private void processDestinationChange(@Nullable final KeyEvent event) {
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

        final DestinationInfluencerControl control = getControl();
        control.requestToChange(new Vector3f(x, y, z), getIndex());
    }

    private void processWeightChange(@Nullable final KeyEvent event) {
        if (isIgnoreListeners() || (event != null && event.getCode() != KeyCode.ENTER)) return;

        float weight;
        try {
            weight = Float.parseFloat(weightField.getText());
        } catch (final NumberFormatException e) {
            return;
        }


        final DestinationInfluencerControl control = getControl();
        control.requestToChange(weight, getIndex());
    }


    /**
     * Reload this element.
     */
    public void reload() {

        final DestinationInfluencerControl control = getControl();
        final DestinationInfluencer influencer = control.getInfluencer();

        final Vector3f destination = influencer.getDestination(getIndex());
        final Interpolation newInterpolation = influencer.getInterpolation(getIndex());
        final Float weight = influencer.getWeight(getIndex());

        xField.setText(String.valueOf(destination.getX()));
        xField.positionCaret(xField.getText().length());

        yField.setText(String.valueOf(destination.getY()));
        yField.positionCaret(xField.getText().length());

        zField.setText(String.valueOf(destination.getZ()));
        zField.positionCaret(xField.getText().length());

        weightField.setText(String.valueOf(weight));
        weightField.positionCaret(weightField.getText().length());

        final ComboBox<Interpolation> interpolationComboBox = getInterpolationComboBox();
        interpolationComboBox.getSelectionModel().select(newInterpolation);
    }
}
