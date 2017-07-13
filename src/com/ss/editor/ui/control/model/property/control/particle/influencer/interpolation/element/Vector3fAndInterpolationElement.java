package com.ss.editor.ui.control.model.property.control.particle.influencer.interpolation.element;

import static java.lang.Float.parseFloat;
import static java.lang.Math.max;
import static java.lang.Math.min;
import com.jme3.math.Vector3f;
import com.ss.editor.ui.control.model.property.control.particle.influencer.interpolation.control.AbstractInterpolationInfluencerControl;
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
import tonegod.emitter.influencers.InterpolatedParticleInfluencer;

/**
 * The implementation of the element for editing vector values and interpolation.
 *
 * @param <P> the type parameter
 * @param <C> the type parameter
 * @author JavaSaBr
 */
public class Vector3fAndInterpolationElement<P extends InterpolatedParticleInfluencer, C extends AbstractInterpolationInfluencerControl<P>> extends InterpolationElement<P, Parent, C> {

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
     * Instantiates a new Vector 3 f and interpolation element.
     *
     * @param control the control
     * @param index   the index
     */
    public Vector3fAndInterpolationElement(@NotNull final C control, final int index) {
        super(control, index);
    }

    @Override
    protected Parent createEditableControl() {

        final Label xLabel = new Label("x:");
        final Label yLabel = new Label("y:");
        final Label zLabel = new Label("z:");

        xField = new TextField();
        xField.setOnScroll(this::processScroll);
        xField.setOnKeyReleased(this::processChange);

        yField = new TextField();
        yField.setOnScroll(this::processScroll);
        yField.setOnKeyReleased(this::processChange);

        zField = new TextField();
        zField.setOnScroll(this::processScroll);
        zField.setOnKeyReleased(this::processChange);

        final HBox container = new HBox(xLabel, xField, yLabel, yField, zLabel, zField);

        FXUtils.addClassTo(xLabel, yLabel, zLabel, CSSClasses.ABSTRACT_PARAM_CONTROL_NUMBER_LABEL);
        FXUtils.addClassTo(xField, yField, zField, CSSClasses.ABSTRACT_PARAM_CONTROL_VECTOR3F_FIELD);

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

        final String result = String.valueOf(max(min(longValue / 1000F, getMaxValue()), getMinValue()));
        source.setText(result);
        source.positionCaret(result.length());

        processChange((KeyEvent) null);
    }

    /**
     * Gets min value.
     *
     * @return the min available value.
     */
    protected float getMinValue() {
        return 0F;
    }

    /**
     * Gets max value.
     *
     * @return the max available value.
     */
    protected float getMaxValue() {
        return 1F;
    }

    /**
     * Handle changing vector value.
     */
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

        requestToChange(x, y, z);
    }

    /**
     * Request to change the vector value.
     *
     * @param x the x
     * @param y the y
     * @param z the z
     */
    protected void requestToChange(final float x, final float y, final float z) {
    }

    /**
     * Reload this element.
     */
    public void reload() {

        final C control = getControl();
        final P influencer = control.getInfluencer();

        final Vector3f value = getValue(influencer);

        xField.setText(String.valueOf(value.getX()));
        xField.positionCaret(xField.getText().length());

        yField.setText(String.valueOf(value.getY()));
        yField.positionCaret(xField.getText().length());

        zField.setText(String.valueOf(value.getZ()));
        zField.positionCaret(xField.getText().length());

        super.reload();
    }

    /**
     * Get vector value from the influencer.
     *
     * @param influencer the influencer
     * @return the value
     */
    protected Vector3f getValue(final P influencer) {
        throw new UnsupportedOperationException();
    }
}
