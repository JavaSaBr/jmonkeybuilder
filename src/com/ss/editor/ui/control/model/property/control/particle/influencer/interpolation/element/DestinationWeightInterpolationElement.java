package com.ss.editor.ui.control.model.property.control.particle.influencer.interpolation.element;

import static java.lang.Float.parseFloat;
import com.jme3.math.Vector3f;
import com.ss.editor.ui.control.model.property.control.particle.influencer.interpolation.control.DestinationInfluencerControl;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tonegod.emitter.influencers.impl.DestinationInfluencer;

/**
 * The implementation of the element for editing vector values and interpolation.
 *
 * @author JavaSaBr
 */
public class DestinationWeightInterpolationElement extends InterpolationElement<DestinationInfluencer, Parent, DestinationInfluencerControl> {

    /**
     * The field X.
     */
    @Nullable
    private TextField xField;

    /**
     * The field Y.
     */
    @Nullable
    private TextField yField;

    /**
     * The field Z.
     */
    @Nullable
    private TextField zField;

    /**
     * The weight.
     */
    @Nullable
    private TextField weightField;

    /**
     * Instantiates a new Destination weight and interpolation element.
     *
     * @param control the control
     * @param index   the index
     */
    public DestinationWeightInterpolationElement(@NotNull final DestinationInfluencerControl control, final int index) {
        super(control, index);
    }

    @Override
    protected Parent createEditableControl() {

        final Label xLabel = new Label("x:");
        final Label yLabel = new Label("y:");
        final Label zLabel = new Label("z:");
        final Label weightLabel = new Label("w:");

        xField = new TextField();
        xField.setOnScroll(this::processScroll);
        xField.setOnKeyReleased(this::processDestinationChange);

        yField = new TextField();
        yField.setOnScroll(this::processScroll);
        yField.setOnKeyReleased(this::processDestinationChange);

        zField = new TextField();
        zField.setOnScroll(this::processScroll);
        zField.setOnKeyReleased(this::processDestinationChange);

        weightField = new TextField();
        weightField.setOnScroll(this::processScroll);
        weightField.setOnKeyReleased(this::processDestinationChange);

        final HBox container = new HBox(xLabel, xField, yLabel, yField, zLabel, zField, weightLabel, weightField);

        xField.prefWidthProperty().bind(container.widthProperty().divide(6));
        yField.prefWidthProperty().bind(container.widthProperty().divide(6));
        zField.prefWidthProperty().bind(container.widthProperty().divide(6));
        weightField.prefWidthProperty().bind(container.widthProperty().divide(6));

        FXUtils.addClassTo(container, CSSClasses.DEF_HBOX);
        FXUtils.addClassTo(xLabel, yLabel, zLabel, weightLabel, CSSClasses.ABSTRACT_PARAM_CONTROL_NUMBER_LABEL);
        FXUtils.addClassTo(xField, yField, zField, weightField, CSSClasses.ABSTRACT_PARAM_CONTROL_VECTOR3F_FIELD);

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

    /**
     * Handle changing destination value.
     */
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

    /**
     * Handle changing weight value.
     */
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

    @Override
    public void reload() {

        final DestinationInfluencerControl control = getControl();
        final DestinationInfluencer influencer = control.getInfluencer();

        final Vector3f destination = influencer.getDestination(getIndex());
        final Float weight = influencer.getWeight(getIndex());

        xField.setText(String.valueOf(destination.getX()));
        xField.positionCaret(xField.getText().length());

        yField.setText(String.valueOf(destination.getY()));
        yField.positionCaret(xField.getText().length());

        zField.setText(String.valueOf(destination.getZ()));
        zField.positionCaret(xField.getText().length());

        weightField.setText(String.valueOf(weight));
        weightField.positionCaret(weightField.getText().length());

        super.reload();
    }
}
