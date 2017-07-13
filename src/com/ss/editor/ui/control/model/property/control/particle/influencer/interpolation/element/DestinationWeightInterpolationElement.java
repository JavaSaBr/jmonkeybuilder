package com.ss.editor.ui.control.model.property.control.particle.influencer.interpolation.element;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.math.Vector3f;
import com.ss.editor.ui.control.model.property.control.particle.influencer.interpolation.control.DestinationInfluencerControl;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.rlib.ui.control.input.FloatTextField;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.Parent;
import javafx.scene.control.Label;
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
     * The weight field.
     */
    @Nullable
    private FloatTextField weightField;

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

        xField = new FloatTextField();
        xField.addChangeListener((observable, oldValue, newValue) -> processDestinationChange());

        yField = new FloatTextField();
        yField.addChangeListener((observable, oldValue, newValue) -> processDestinationChange());

        zField = new FloatTextField();
        zField.addChangeListener((observable, oldValue, newValue) -> processDestinationChange());

        weightField = new FloatTextField();
        weightField.addChangeListener((observable, oldValue, newValue) -> processWeightChange());

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
     * Handle changing destination value.
     */
    private void processDestinationChange() {
        if (isIgnoreListeners()) return;

        final float x = getXField().getValue();
        final float y = getYField().getValue();
        final float z = getZField().getValue();

        final DestinationInfluencerControl control = getControl();
        control.requestToChange(new Vector3f(x, y, z), getIndex());
    }

    /**
     * Handle changing weight value.
     */
    private void processWeightChange() {
        if (isIgnoreListeners()) return;

        final float weight = weightField.getValue();

        final DestinationInfluencerControl control = getControl();
        control.requestToChange(weight, getIndex());
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
     * @return the weight field.
     */
    @NotNull
    private FloatTextField getWeightField() {
        return notNull(weightField);
    }

    @Override
    public void reload() {

        final DestinationInfluencerControl control = getControl();
        final DestinationInfluencer influencer = control.getInfluencer();

        final Vector3f destination = influencer.getDestination(getIndex());
        final Float weight = influencer.getWeight(getIndex());

        final FloatTextField xField = getXField();
        xField.setValue(destination.getX());
        xField.positionCaret(xField.getText().length());

        final FloatTextField yField = getYField();
        yField.setValue(destination.getY());
        yField.positionCaret(yField.getText().length());

        final FloatTextField zField = getZField();
        zField.setValue(destination.getZ());
        zField.positionCaret(zField.getText().length());

        final FloatTextField weightField = getWeightField();
        weightField.setValue(weight);
        weightField.positionCaret(weightField.getText().length());

        super.reload();
    }
}
