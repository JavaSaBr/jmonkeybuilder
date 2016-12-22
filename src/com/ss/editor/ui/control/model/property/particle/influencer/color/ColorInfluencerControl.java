package com.ss.editor.ui.control.model.property.particle.influencer.color;

import com.jme3.math.ColorRGBA;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.UpdatableControl;
import com.ss.editor.ui.control.model.property.operation.ParticleInfluencerPropertyOperation;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.util.UIUtils;

import org.jetbrains.annotations.NotNull;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;
import rlib.util.array.Array;
import tonegod.emitter.influencers.ColorInfluencer;
import tonegod.emitter.interpolation.Interpolation;

/**
 * The control for editing colors in the {@link ColorInfluencer}.
 *
 * @author JavaSaBr
 */
public class ColorInfluencerControl extends VBox implements UpdatableControl {

    /**
     * The consumer of changes.
     */
    private final ModelChangeConsumer modelChangeConsumer;

    /**
     * The color influencer.
     */
    private final ColorInfluencer influencer;

    /**
     * The parent.
     */
    private final Object parent;

    /**
     * The element container.
     */
    private VBox elementContainer;

    public ColorInfluencerControl(@NotNull final ModelChangeConsumer modelChangeConsumer, @NotNull final ColorInfluencer influencer, @NotNull final Object parent) {
        this.modelChangeConsumer = modelChangeConsumer;
        this.parent = parent;
        setId(CSSIds.MODEL_PARAM_CONTROL_INFLUENCER_CONTROL);
        this.influencer = influencer;
        createControls();
    }

    private void createControls() {

        final Label propertyNameLabel = new Label("Color Interpolations");
        propertyNameLabel.setId(CSSIds.MODEL_PARAM_CONTROL_PARAM_NAME);

        elementContainer = new VBox();

        final Button addButton = new Button();
        addButton.setId(CSSIds.MODEL_PARAM_CONTROL_INFLUENCER_CONTROL_ICON_BUTTON);
        addButton.setGraphic(new ImageView(Icons.ADD_24));
        addButton.setOnAction(event -> processAdd());

        FXUtils.addToPane(propertyNameLabel, this);
        FXUtils.addToPane(elementContainer, this);
        FXUtils.addToPane(addButton, this);

        FXUtils.addClassTo(propertyNameLabel, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(addButton, CSSClasses.SPECIAL_FONT_13);
    }

    /**
     * @return the color influencer.
     */
    protected ColorInfluencer getInfluencer() {
        return influencer;
    }

    /**
     * @return the element container.
     */
    public VBox getElementContainer() {
        return elementContainer;
    }

    /**
     * @return the consumer of changes.
     */
    protected ModelChangeConsumer getModelChangeConsumer() {
        return modelChangeConsumer;
    }

    /**
     * Reload this control.
     */
    public void reload() {

        final ColorInfluencer influencer = getInfluencer();
        final VBox root = getElementContainer();

        UIUtils.clear(root);

        final Array<ColorRGBA> colors = influencer.getColors();

        for (int i = 0, length = colors.size(); i < length; i++) {

            final ColorAndInterpolationElement element = new ColorAndInterpolationElement(this, i);
            element.prefWidthProperty().bind(widthProperty());

            FXUtils.addToPane(element, root);
        }
    }

    public void requestToChange(final ColorRGBA newValue, final int index) {

        final ColorInfluencer influencer = getInfluencer();
        final ColorRGBA oldValue = influencer.getColors().get(index);

        final ParticleInfluencerPropertyOperation<ColorInfluencer, ColorRGBA> operation = new ParticleInfluencerPropertyOperation<>(influencer, parent, "Colors", newValue, oldValue);
        operation.setApplyHandler((colorInfluencer, colorRGBA) -> colorInfluencer.updateColor(colorRGBA, index));

        final ModelChangeConsumer modelChangeConsumer = getModelChangeConsumer();
        modelChangeConsumer.execute(operation);
    }

    public void requestToChange(final Interpolation newValue, final int index) {

        final ColorInfluencer influencer = getInfluencer();
        final Interpolation oldValue = influencer.getInterpolations().get(index);

        final ParticleInfluencerPropertyOperation<ColorInfluencer, Interpolation> operation = new ParticleInfluencerPropertyOperation<>(influencer, parent, "Colors", newValue, oldValue);
        operation.setApplyHandler((colorInfluencer, interpolation) -> colorInfluencer.updateInterpolation(interpolation, index));

        final ModelChangeConsumer modelChangeConsumer = getModelChangeConsumer();
        modelChangeConsumer.execute(operation);
    }

    private void processAdd() {

        final ColorInfluencer influencer = getInfluencer();
        final Array<ColorRGBA> colors = influencer.getColors();
        final int index = colors.size();

        final ParticleInfluencerPropertyOperation<ColorInfluencer, Integer> operation = new ParticleInfluencerPropertyOperation<>(influencer, parent, "Colors", -1, index);
        operation.setApplyHandler((colorInfluencer, newIndex) -> {
            if (newIndex != -1) {
                colorInfluencer.removeColor(newIndex);
            } else {
                colorInfluencer.addColor(new ColorRGBA(ColorRGBA.White), Interpolation.LINEAR);
            }
        });

        final ModelChangeConsumer modelChangeConsumer = getModelChangeConsumer();
        modelChangeConsumer.execute(operation);
    }

    @Override
    public void sync() {
        reload();
    }
}
