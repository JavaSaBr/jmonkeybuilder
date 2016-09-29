package com.ss.editor.ui.control.model.property.particle.influencer;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.ModelPropertyControl;
import com.ss.editor.ui.control.model.property.operation.ParticleInfluencerPropertyOperation;
import com.ss.editor.ui.css.CSSIds;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.layout.HBox;
import rlib.ui.util.FXUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.influencers.RadialVelocityInfluencer.RadialPullCenter;

/**
 * The implementation of the {@link ModelPropertyControl} for editing the {@link RadialPullCenter}.
 *
 * @author JavaSaBr
 */
public class RadialPullCenterEmitterPropertyControl<T extends ParticleInfluencer> extends ModelPropertyControl<T, RadialPullCenter> {

    private static final Array<RadialPullCenter> ALIGNMENT_MODES = ArrayFactory.newArray(RadialPullCenter.class);

    static {
        ALIGNMENT_MODES.addAll(RadialPullCenter.values());
    }

    /**
     * The parent of the influencer.
     */
    @NotNull
    private Object parent;

    /**
     * The list of available options of the {@link RadialPullCenter}.
     */
    private ComboBox<RadialPullCenter> radialPullCenterComboBox;

    public RadialPullCenterEmitterPropertyControl(final RadialPullCenter element, final String paramName, final ModelChangeConsumer modelChangeConsumer, final @NotNull Object parent) {
        super(element, paramName, modelChangeConsumer);
        this.parent = parent;
    }

    @Override
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        radialPullCenterComboBox = new ComboBox<>();
        radialPullCenterComboBox.setId(CSSIds.MODEL_PARAM_CONTROL_COMBO_BOX);
        radialPullCenterComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updatePullCenter());
        radialPullCenterComboBox.prefWidthProperty().bind(container.widthProperty());

        final ObservableList<RadialPullCenter> items = radialPullCenterComboBox.getItems();

        ALIGNMENT_MODES.forEach(items::add);

        FXUtils.addToPane(radialPullCenterComboBox, container);
    }

    /**
     * @return the list of available options of the {@link RadialPullCenter}.
     */
    private ComboBox<RadialPullCenter> getRadialPullCenterComboBox() {
        return radialPullCenterComboBox;
    }

    /**
     * Update selected {@link RadialPullCenter}.
     */
    private void updatePullCenter() {
        if (isIgnoreListener()) return;

        final ComboBox<RadialPullCenter> radialPullCenterComboBox = getRadialPullCenterComboBox();
        final SingleSelectionModel<RadialPullCenter> selectionModel = radialPullCenterComboBox.getSelectionModel();
        final RadialPullCenter newValue = selectionModel.getSelectedItem();

        changed(newValue, getPropertyValue());
    }

    @Override
    protected void reload() {
        final RadialPullCenter element = getPropertyValue();
        final ComboBox<RadialPullCenter> radialPullCenterComboBox = getRadialPullCenterComboBox();
        final SingleSelectionModel<RadialPullCenter> selectionModel = radialPullCenterComboBox.getSelectionModel();
        selectionModel.select(element);
    }

    @Override
    protected void changed(@Nullable final RadialPullCenter newValue, @Nullable final RadialPullCenter oldValue) {

        final T editObject = getEditObject();
        final ParticleInfluencerPropertyOperation<T, RadialPullCenter> operation = new ParticleInfluencerPropertyOperation<>(editObject, parent, getPropertyName(), newValue, oldValue);
        operation.setApplyHandler(getApplyHandler());

        final ModelChangeConsumer modelChangeConsumer = getModelChangeConsumer();
        modelChangeConsumer.execute(operation);
    }

    @Override
    protected boolean isSingleRow() {
        return true;
    }
}
