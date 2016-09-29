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
import tonegod.emitter.influencers.RadialVelocityInfluencer.RadialPullAlignment;
import tonegod.emitter.influencers.RadialVelocityInfluencer.RadialUpAlignment;

/**
 * The implementation of the {@link ModelPropertyControl} for editing the {@link
 * RadialUpAlignment}.
 *
 * @author JavaSaBr
 */
public class RadialUpAlignmentEmitterPropertyControl<T extends ParticleInfluencer> extends ModelPropertyControl<T, RadialUpAlignment> {

    private static final Array<RadialUpAlignment> ALIGNMENT_MODES = ArrayFactory.newArray(RadialUpAlignment.class);

    static {
        ALIGNMENT_MODES.addAll(RadialUpAlignment.values());
    }

    /**
     * The parent of the influencer.
     */
    @NotNull
    private Object parent;

    /**
     * The list of available options of the {@link RadialUpAlignment}.
     */
    private ComboBox<RadialUpAlignment> alignmentComboBox;

    public RadialUpAlignmentEmitterPropertyControl(final RadialUpAlignment element, final String paramName, final ModelChangeConsumer modelChangeConsumer, final @NotNull Object parent) {
        super(element, paramName, modelChangeConsumer);
        this.parent = parent;
    }

    @Override
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        alignmentComboBox = new ComboBox<>();
        alignmentComboBox.setId(CSSIds.MODEL_PARAM_CONTROL_COMBO_BOX);
        alignmentComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateAlignment());
        alignmentComboBox.prefWidthProperty().bind(container.widthProperty());

        final ObservableList<RadialUpAlignment> items = alignmentComboBox.getItems();

        ALIGNMENT_MODES.forEach(items::add);

        FXUtils.addToPane(alignmentComboBox, container);
    }

    /**
     * @return the list of available options of the {@link RadialPullAlignment}.
     */
    private ComboBox<RadialUpAlignment> getAlignmentComboBox() {
        return alignmentComboBox;
    }

    /**
     * Update selected {@link RadialUpAlignment}.
     */
    private void updateAlignment() {
        if (isIgnoreListener()) return;

        final ComboBox<RadialUpAlignment> alignmentComboBox = getAlignmentComboBox();
        final SingleSelectionModel<RadialUpAlignment> selectionModel = alignmentComboBox.getSelectionModel();
        final RadialUpAlignment newValue = selectionModel.getSelectedItem();

        changed(newValue, getPropertyValue());
    }

    @Override
    protected void reload() {
        final RadialUpAlignment element = getPropertyValue();
        final ComboBox<RadialUpAlignment> alignmentModeComboBox = getAlignmentComboBox();
        final SingleSelectionModel<RadialUpAlignment> selectionModel = alignmentModeComboBox.getSelectionModel();
        selectionModel.select(element);
    }

    @Override
    protected void changed(@Nullable final RadialUpAlignment newValue, @Nullable final RadialUpAlignment oldValue) {

        final T editObject = getEditObject();
        final ParticleInfluencerPropertyOperation<T, RadialUpAlignment> operation = new ParticleInfluencerPropertyOperation<>(editObject, parent, getPropertyName(), newValue, oldValue);
        operation.setApplyHandler(getApplyHandler());

        final ModelChangeConsumer modelChangeConsumer = getModelChangeConsumer();
        modelChangeConsumer.execute(operation);
    }

    @Override
    protected boolean isSingleRow() {
        return true;
    }
}
