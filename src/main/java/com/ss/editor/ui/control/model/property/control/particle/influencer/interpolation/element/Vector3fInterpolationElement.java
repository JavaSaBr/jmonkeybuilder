package com.ss.editor.ui.control.model.property.control.particle.influencer.interpolation.element;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.math.Vector3f;
import com.ss.editor.ui.control.model.property.control.particle.influencer.interpolation.control.AbstractInterpolationInfluencerControl;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.rlib.ui.control.input.FloatTextField;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.Parent;
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
public class Vector3fInterpolationElement<P extends InterpolatedParticleInfluencer, C extends AbstractInterpolationInfluencerControl<P>>
        extends InterpolationElement<P, Parent, C> {

    /**
     * The field X.
     */
    @Nullable
    private FloatTextField xField;

    /**
     * The field Y.
     */
    @Nullable
    private FloatTextField yField;

    /**
     * The field Z.
     */
    @Nullable
    private FloatTextField zField;

    /**
     * Instantiates a new Vector 3 f and interpolation element.
     *
     * @param control the control
     * @param index   the index
     */
    public Vector3fInterpolationElement(@NotNull final C control, final int index) {
        super(control, index);
    }

    @Override
    protected Parent createEditableControl() {

        final HBox container = new HBox();
        container.prefWidthProperty().bind(widthProperty().multiply(0.4));

        xField = new FloatTextField();
        xField.setMinMax(getMinValue(), getMaxValue());
        xField.prefWidthProperty().bind(container.widthProperty().multiply(0.33));
        xField.addChangeListener((observable, oldValue, newValue) -> processChange());

        yField = new FloatTextField();
        yField.setMinMax(getMinValue(), getMaxValue());
        yField.prefWidthProperty().bind(container.widthProperty().multiply(0.33));
        yField.addChangeListener((observable, oldValue, newValue) -> processChange());

        zField = new FloatTextField();
        zField.setMinMax(getMinValue(), getMaxValue());
        zField.prefWidthProperty().bind(container.widthProperty().multiply(0.33));
        zField.addChangeListener((observable, oldValue, newValue) -> processChange());

        FXUtils.addToPane(xField, container);
        FXUtils.addToPane(yField, container);
        FXUtils.addToPane(zField, container);

        FXUtils.addClassesTo(xField, yField, zField, CSSClasses.ABSTRACT_PARAM_CONTROL_VECTOR3F_FIELD,
                CSSClasses.TRANSPARENT_TEXT_FIELD);
        FXUtils.addClassesTo(container, CSSClasses.DEF_HBOX, CSSClasses.TEXT_INPUT_CONTAINER,
                CSSClasses.ABSTRACT_PARAM_CONTROL_SHORT_INPUT_CONTAINER);

        return container;
    }

    @NotNull
    @Override
    protected String getEditableTitle() {
        return "xyz";
    }

    /**
     * Gets min value.
     *
     * @return the min available value.
     */
    protected float getMinValue() {
        return Integer.MIN_VALUE;
    }

    /**
     * Gets max value.
     *
     * @return the max available value.
     */
    protected float getMaxValue() {
        return Integer.MAX_VALUE;
    }

    /**
     * Handle changing vector value.
     */
    private void processChange() {
        if (isIgnoreListeners()) return;

        final float x = getXField().getValue();
        final float y = getYField().getValue();
        final float z = getZField().getValue();

        requestToChange(x, y, z);
    }

    /**
     * @return the field X.
     */
    @NotNull
    private FloatTextField getXField() {
        return notNull(xField);
    }

    /**
     * @return the field Y.
     */
    @NotNull
    private FloatTextField getYField() {
        return notNull(yField);
    }

    /**
     * @return the field Z.
     */
    @NotNull
    private FloatTextField getZField() {
        return notNull(zField);
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

        final FloatTextField xField = getXField();
        xField.setValue(value.getX());
        xField.positionCaret(xField.getText().length());

        final FloatTextField yField = getYField();
        yField.setValue(value.getY());
        yField.positionCaret(yField.getText().length());

        final FloatTextField zField = getZField();
        zField.setValue(value.getZ());
        zField.positionCaret(zField.getText().length());

        super.reload();
    }

    /**
     * Get vector value from the influencer.
     *
     * @param influencer the influencer
     * @return the value
     */
    protected Vector3f getValue(@NotNull final P influencer) {
        throw new UnsupportedOperationException();
    }
}
