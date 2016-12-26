package com.ss.editor.ui.control.model.property.particle.influencer.interpolation.control;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.UpdatableControl;
import com.ss.editor.ui.control.model.property.operation.ParticleInfluencerPropertyOperation;
import com.ss.editor.ui.control.model.property.particle.influencer.interpolation.element.InterpolationElement;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.util.UIUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;
import tonegod.emitter.influencers.InterpolatedParticleInfluencer;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.interpolation.Interpolation;

/**
 * The control for editing interpolations in the {@link ParticleInfluencer}.
 *
 * @author JavaSaBr
 */
public abstract class AbstractInterpolationInfluencerControl<I extends InterpolatedParticleInfluencer> extends VBox implements UpdatableControl {

    /**
     * The consumer of changes.
     */
    private final ModelChangeConsumer modelChangeConsumer;

    /**
     * The color influencer.
     */
    private final I influencer;

    /**
     * The parent.
     */
    private final Object parent;

    /**
     * The element container.
     */
    private VBox elementContainer;

    public AbstractInterpolationInfluencerControl(@NotNull final ModelChangeConsumer modelChangeConsumer, @NotNull final I influencer,
                                                  @NotNull final Object parent) {
        setId(CSSIds.MODEL_PARAM_CONTROL_INFLUENCER_CONTROL);
        this.modelChangeConsumer = modelChangeConsumer;
        this.parent = parent;
        this.influencer = influencer;
        createControls();
    }

    protected void createControls() {

        final Label propertyNameLabel = new Label(getControlTitle() + ":");
        propertyNameLabel.setId(CSSIds.MODEL_PARAM_CONTROL_PARAM_NAME);

        elementContainer = new VBox();

        final Button addButton = new Button();
        addButton.setId(CSSIds.MODEL_PARAM_CONTROL_INFLUENCER_ICON_BUTTON);
        addButton.setGraphic(new ImageView(Icons.ADD_24));
        addButton.setOnAction(event -> processAdd());

        final Button removeButton = new Button();
        removeButton.setId(CSSIds.MODEL_PARAM_CONTROL_INFLUENCER_ICON_BUTTON);
        removeButton.setGraphic(new ImageView(Icons.REMOVE_18));
        removeButton.setOnAction(event -> processRemove());

        final HBox buttonContainer = new HBox(addButton, removeButton);
        buttonContainer.setAlignment(Pos.CENTER);

        final ObservableList<Node> children = elementContainer.getChildren();
        children.addListener((ListChangeListener<Node>) c -> removeButton.setDisable(children.size() < (getMinElements() + 1)));

        FXUtils.addToPane(propertyNameLabel, this);
        FXUtils.addToPane(elementContainer, this);
        FXUtils.addToPane(buttonContainer, this);

        FXUtils.addClassTo(propertyNameLabel, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(addButton, CSSClasses.SPECIAL_FONT_13);
    }

    /**
     * Get the count of minimum elements.
     *
     * @return the count of minimum elements.
     */
    protected int getMinElements() {
        return 2;
    }

    @NotNull
    protected String getControlTitle() {
        throw new UnsupportedOperationException();
    }

    /**
     * @return the color influencer.
     */
    @NotNull
    public I getInfluencer() {
        return influencer;
    }

    /**
     * @return the element container.
     */
    @NotNull
    protected VBox getElementContainer() {
        return elementContainer;
    }

    /**
     * @return the consumer of changes.
     */
    @NotNull
    protected ModelChangeConsumer getModelChangeConsumer() {
        return modelChangeConsumer;
    }

    /**
     * Reload this control.
     */
    public void reload() {

        final I influencer = getInfluencer();
        final VBox root = getElementContainer();
        final ObservableList<Node> children = root.getChildren();

        if (isNeedRebuild(influencer, children.size())) {
            UIUtils.clear(root);
            fillControl(influencer, root);
        } else {
            children.stream()
                    .map(node -> (InterpolationElement) node)
                    .forEach(InterpolationElement::reload);
        }
    }

    protected boolean isNeedRebuild(@NotNull final I influencer, final int currentCount) {
        return influencer.getStepCount() != currentCount;
    }

    /**
     * Fill this control.
     */
    protected void fillControl(@NotNull final I influencer, @NotNull final VBox root) {
    }

    /**
     * Handle removing last interpolation.
     */
    protected void processRemove() {
    }

    /**
     * Handle adding new interpolation.
     */
    protected void processAdd() {
    }

    /**
     * Request to change interpolation.
     *
     * @param newValue the new interpolation.
     * @param index    the index.
     */
    public void requestToChange(final Interpolation newValue, final int index) {

        final I influencer = getInfluencer();
        final Interpolation oldValue = influencer.getInterpolation(index);

        execute(newValue, oldValue, (alphaInfluencer, interpolation) -> alphaInfluencer.updateInterpolation(interpolation, index));
    }

    /**
     * Execute change operation.
     *
     * @param newValue     the new value.
     * @param oldValue     the old value.
     * @param applyHandler the apply handler.
     * @param <T>          the type of value.
     */
    protected <T> void execute(@Nullable final T newValue, @Nullable final T oldValue, @NotNull final BiConsumer<I, T> applyHandler) {

        final ParticleInfluencerPropertyOperation<I, T> operation = new ParticleInfluencerPropertyOperation<>(influencer, parent, getPropertyName(), newValue, oldValue);
        operation.setApplyHandler(applyHandler);

        final ModelChangeConsumer modelChangeConsumer = getModelChangeConsumer();
        modelChangeConsumer.execute(operation);
    }

    @NotNull
    protected String getPropertyName() {
        return getControlTitle();
    }

    @Override
    public void sync() {
        reload();
    }
}
