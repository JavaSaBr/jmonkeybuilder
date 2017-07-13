package com.ss.editor.ui.control.model.property.control.particle.influencer.interpolation.element;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.Messages;
import com.ss.editor.ui.control.model.property.control.particle.influencer.interpolation.control.AbstractInterpolationInfluencerControl;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.rlib.ui.util.FXUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tonegod.emitter.influencers.InterpolatedParticleInfluencer;
import tonegod.emitter.interpolation.Interpolation;
import tonegod.emitter.interpolation.InterpolationManager;

/**
 * The implementation of the element for {@link AbstractInterpolationInfluencerControl} for editing something and
 * interpolation.
 *
 * @param <P> the type parameter
 * @param <E> the type parameter
 * @param <C> the type parameter
 * @author JavaSaBr
 */
public abstract class InterpolationElement<P extends InterpolatedParticleInfluencer, E extends Node,
        C extends AbstractInterpolationInfluencerControl<P>> extends HBox {

    /**
     * The constant STRING_CONVERTER.
     */
    @NotNull
    protected static final StringConverter<Interpolation> STRING_CONVERTER = new StringConverter<Interpolation>() {

        @Override
        public String toString(final Interpolation object) {
            return object.getName();
        }

        @Override
        public Interpolation fromString(final String string) {
            return null;
        }
    };

    /**
     * The constant INTERPOLATIONS.
     */
    @NotNull
    protected static final ObservableList<Interpolation> INTERPOLATIONS;

    static {
        INTERPOLATIONS = FXCollections.observableArrayList();
        INTERPOLATIONS.addAll(InterpolationManager.getAvailable());
    }

    @NotNull
    private final C control;

    /**
     * The editable control.
     */
    @Nullable
    protected E editableControl;

    /**
     * The interpolation chooser.
     */
    @Nullable
    protected ComboBox<Interpolation> interpolationComboBox;

    /**
     * The index.
     */
    private final int index;

    /**
     * The flag for ignoring listeners.
     */
    private boolean ignoreListeners;

    /**
     * Instantiates a new Interpolation element.
     *
     * @param control the control
     * @param index   the index
     */
    public InterpolationElement(@NotNull final C control, final int index) {
        this.control = control;
        this.index = index;
        createComponents();
        setIgnoreListeners(true);
        reload();
        setIgnoreListeners(false);
        FXUtils.addClassesTo(this, CSSClasses.DEF_HBOX, CSSClasses.ABSTRACT_PARAM_CONTROL_INFLUENCER_ELEMENT);
    }

    /**
     * Create components.
     */
    protected void createComponents() {

        Label editableLabel = null;

        if (isNeedEditableLabel()) {

            editableLabel = new Label(getEditableTitle() + ":");
            editableLabel.prefWidthProperty().bind(widthProperty().multiply(0.2));

            FXUtils.addClassTo(editableLabel, CSSClasses.ABSTRACT_PARAM_CONTROL_PARAM_NAME_SINGLE_ROW);
        }

        editableControl = createEditableControl();

        final Label interpolationLabel = new Label(Messages.MODEL_PROPERTY_INTERPOLATION + ":");
        interpolationLabel.prefWidthProperty().bind(widthProperty().multiply(0.25));

        interpolationComboBox = new ComboBox<>();
        interpolationComboBox.setEditable(false);
        interpolationComboBox.prefWidthProperty().bind(widthProperty().multiply(0.35));
        interpolationComboBox.setConverter(STRING_CONVERTER);
        interpolationComboBox.getItems().setAll(INTERPOLATIONS);
        interpolationComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> processChange(newValue));

        if (editableLabel != null) {
            FXUtils.addToPane(editableLabel, this);
        }

        FXUtils.addToPane(editableControl, this);
        FXUtils.addToPane(interpolationLabel, this);
        FXUtils.addToPane(interpolationComboBox, this);

        FXUtils.addClassTo(interpolationLabel, CSSClasses.ABSTRACT_PARAM_CONTROL_PARAM_NAME_SINGLE_ROW);
        FXUtils.addClassTo(interpolationComboBox, CSSClasses.ABSTRACT_PARAM_CONTROL_COMBO_BOX);
    }

    /**
     * Gets editable title.
     *
     * @return the editable title
     */
    @NotNull
    protected String getEditableTitle() {
        throw new UnsupportedOperationException();
    }

    /**
     * Create editable control.
     *
     * @return the e
     */
    protected abstract E createEditableControl();

    /**
     * Process change.
     *
     * @param newValue the new value
     */
    protected void processChange(@NotNull final Interpolation newValue) {
        if (isIgnoreListeners()) return;
        final C control = getControl();
        control.requestToChange(newValue, index);
    }

    /**
     * Gets control.
     *
     * @return the control
     */
    @NotNull
    protected C getControl() {
        return control;
    }

    /**
     * Is ignore listeners boolean.
     *
     * @return true if listeners is ignored.
     */
    protected boolean isIgnoreListeners() {
        return ignoreListeners;
    }

    /**
     * Gets index.
     *
     * @return the index.
     */
    protected int getIndex() {
        return index;
    }

    /**
     * Sets ignore listeners.
     *
     * @param ignoreListeners the flag for ignoring listeners.
     */
    protected void setIgnoreListeners(final boolean ignoreListeners) {
        this.ignoreListeners = ignoreListeners;
    }

    /**
     * Gets editable control.
     *
     * @return the color picker.
     */
    @NotNull
    protected E getEditableControl() {
        return notNull(editableControl);
    }

    /**
     * Gets interpolation combo box.
     *
     * @return the interpolation chooser.
     */
    @NotNull
    protected ComboBox<Interpolation> getInterpolationComboBox() {
        return notNull(interpolationComboBox);
    }

    /**
     * Reload this element.
     */
    public void reload() {

        final C control = getControl();
        final P influencer = control.getInfluencer();

        final Interpolation newInterpolation = influencer.getInterpolation(getIndex());
        final ComboBox<Interpolation> interpolationComboBox = getInterpolationComboBox();
        interpolationComboBox.getSelectionModel().select(newInterpolation);
    }

    /**
     * Is need editable label boolean.
     *
     * @return the boolean
     */
    public boolean isNeedEditableLabel() {
        return true;
    }
}
