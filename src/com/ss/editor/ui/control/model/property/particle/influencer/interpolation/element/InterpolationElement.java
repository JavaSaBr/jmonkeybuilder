package com.ss.editor.ui.control.model.property.particle.influencer.interpolation.element;

import com.ss.editor.ui.control.model.property.particle.influencer.interpolation.control.AbstractInterpolationInfluencerControl;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;

import org.jetbrains.annotations.NotNull;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import rlib.ui.util.FXUtils;
import rlib.util.array.Array;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.interpolation.Interpolation;
import tonegod.emitter.interpolation.InterpolationManager;

/**
 * The implementation of the element for {@link AbstractInterpolationInfluencerControl} for editing
 * something and interpolation.
 *
 * @author JavaSaBr
 */
public abstract class InterpolationElement<P extends ParticleInfluencer, E extends Node, C extends AbstractInterpolationInfluencerControl<P>> extends HBox {

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
    private final C control;

    /**
     * The editable control.
     */
    private E editableControl;

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

    public InterpolationElement(@NotNull final C control, final int index) {
        setId(CSSIds.MODEL_PARAM_CONTROL_INFLUENCER_ELEMENT);
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

        final Label editableLabel = new Label(getEditableTitle());
        editableLabel.setId(CSSIds.MODEL_PARAM_CONTROL_PARAM_NAME_SINGLE_ROW);

        editableControl = createEditableControl();

        final Label interpolationLabel = new Label("Interpolation:");
        interpolationLabel.setId(CSSIds.MODEL_PARAM_CONTROL_PARAM_NAME_SINGLE_ROW);

        interpolationComboBox = new ComboBox<>();
        interpolationComboBox.setId(CSSIds.MODEL_PARAM_CONTROL_COMBO_BOX);
        interpolationComboBox.setEditable(false);
        interpolationComboBox.prefWidthProperty().bind(widthProperty().multiply(0.3));
        interpolationComboBox.setConverter(STRING_CONVERTER);
        interpolationComboBox.getItems().setAll(INTERPOLATIONS);
        interpolationComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> processChange(newValue));

        FXUtils.addToPane(editableLabel, this);
        FXUtils.addToPane(editableControl, this);
        FXUtils.addToPane(interpolationLabel, this);
        FXUtils.addToPane(interpolationComboBox, this);

        FXUtils.addClassTo(editableLabel, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(editableControl, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(interpolationLabel, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(interpolationComboBox, CSSClasses.SPECIAL_FONT_13);
    }

    @NotNull
    protected String getEditableTitle() {
        throw new UnsupportedOperationException();
    }

    /**
     * Create editable control.
     */
    protected abstract E createEditableControl();

    private void processChange(@NotNull final Interpolation newValue) {
        if (isIgnoreListeners()) return;
        final C control = getControl();
        control.requestToChange(newValue, index);
    }

    @NotNull
    protected C getControl() {
        return control;
    }

    /**
     * @return true if listeners is ignored.
     */
    protected boolean isIgnoreListeners() {
        return ignoreListeners;
    }

    /**
     * @return the index.
     */
    protected int getIndex() {
        return index;
    }

    /**
     * @param ignoreListeners the flag for ignoring listeners.
     */
    protected void setIgnoreListeners(final boolean ignoreListeners) {
        this.ignoreListeners = ignoreListeners;
    }

    /**
     * @return the color picker.
     */
    @NotNull
    protected E getEditableControl() {
        return editableControl;
    }

    /**
     * @return the interpolation chooser.
     */
    @NotNull
    protected ComboBox<Interpolation> getInterpolationComboBox() {
        return interpolationComboBox;
    }

    /**
     * Reload this element.
     */
    public void reload() {
    }
}
