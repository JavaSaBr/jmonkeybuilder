package com.ss.editor.ui.control.model.property.control.particle.influencer.interpolation.element;

import static java.lang.Float.parseFloat;
import static java.lang.Math.max;
import static java.lang.Math.min;
import com.ss.editor.Messages;
import com.ss.editor.ui.control.model.property.control.particle.influencer.interpolation.control.AlphaInfluencerControl;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tonegod.emitter.influencers.impl.AlphaInfluencer;

/**
 * The implementation of the element for {@link AlphaInfluencer} for editing alpha and interpolation.
 *
 * @author JavaSaBr
 */
public class AlphaInterpolationElement extends InterpolationElement<AlphaInfluencer, TextField, AlphaInfluencerControl> {

    /**
     * Instantiates a new Alpha and interpolation element.
     *
     * @param control the control
     * @param index   the index
     */
    public AlphaInterpolationElement(@NotNull final AlphaInfluencerControl control, final int index) {
        super(control, index);
    }

    @NotNull
    @Override
    protected String getEditableTitle() {
        return Messages.MODEL_PROPERTY_ALPHA;
    }

    @Override
    protected TextField createEditableControl() {

        final TextField textField = new TextField();
        textField.setOnScroll(this::processScroll);
        textField.prefWidthProperty().bind(widthProperty().multiply(0.35));
        textField.setOnKeyReleased(this::processChange);

        FXUtils.addClassTo(textField, CSSClasses.ABSTRACT_PARAM_CONTROL_VECTOR2F_FIELD);

        return textField;
    }

    /**
     * The process of scrolling value.
     */
    private void processScroll(@NotNull final ScrollEvent event) {
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
        longValue += event.getDeltaY() * 1;

        final String result = String.valueOf(max(min(longValue / 1000F, 1F), 0F));
        source.setText(result);
        source.positionCaret(result.length());

        processChange((KeyEvent) null);
    }

    /**
     * Handle changing alpha value.
     */
    private void processChange(@Nullable final KeyEvent event) {
        if (isIgnoreListeners() || (event != null && event.getCode() != KeyCode.ENTER)) return;

        final TextField editableControl = getEditableControl();
        float newValue;
        try {
            newValue = Float.parseFloat(editableControl.getText());
        } catch (final NumberFormatException e) {
            return;
        }

        newValue = max(min(newValue, 1F), 0F);

        final AlphaInfluencerControl control = getControl();
        control.requestToChange(newValue, getIndex());
    }

    /**
     * Reload this element.
     */
    public void reload() {

        final AlphaInfluencerControl control = getControl();
        final AlphaInfluencer influencer = control.getInfluencer();

        final Float alpha = influencer.getAlpha(getIndex());

        final TextField editableControl = getEditableControl();
        final int caretPosition = editableControl.getCaretPosition();
        editableControl.setText(alpha.toString());
        editableControl.positionCaret(caretPosition);

        super.reload();
    }
}
