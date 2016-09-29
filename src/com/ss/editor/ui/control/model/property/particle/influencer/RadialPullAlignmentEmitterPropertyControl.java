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

/**
 * The implementation of the {@link ModelPropertyControl} for editing the {@link
 * RadialPullAlignment}.
 *
 * @author JavaSaBr
 */
public class RadialPullAlignmentEmitterPropertyControl<T extends ParticleInfluencer> extends ModelPropertyControl<T, RadialPullAlignment> {

    private static final Array<RadialPullAlignment> ALIGNMENT_MODES = ArrayFactory.newArray(RadialPullAlignment.class);

    static {
        ALIGNMENT_MODES.addAll(RadialPullAlignment.values());
    }

    /**
     * The parent of the influencer.
     */
    @NotNull
    private Object parent;

    /**
     * The list of available options of the {@link RadialPullAlignment}.
     */
    private ComboBox<RadialPullAlignment> alignmentComboBox;

    public RadialPullAlignmentEmitterPropertyControl(final RadialPullAlignment element, final String paramName, final ModelChangeConsumer modelChangeConsumer, final @NotNull Object parent) {
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

        final ObservableList<RadialPullAlignment> items = alignmentComboBox.getItems();

        ALIGNMENT_MODES.forEach(items::add);

        FXUtils.addToPane(alignmentComboBox, container);
    }

    /**
     * @return the list of available options of the {@link RadialPullAlignment}.
     */
    private ComboBox<RadialPullAlignment> getAlignmentComboBox() {
        return alignmentComboBox;
    }

    /**
     * Update selected {@link RadialPullAlignment}.
     */
    private void updateAlignment() {
        if (isIgnoreListener()) return;

        final ComboBox<RadialPullAlignment> alignmentComboBox = getAlignmentComboBox();
        final SingleSelectionModel<RadialPullAlignment> selectionModel = alignmentComboBox.getSelectionModel();
        final RadialPullAlignment newValue = selectionModel.getSelectedItem();

        changed(newValue, getPropertyValue());
    }

    @Override
    protected void reload() {
        final RadialPullAlignment element = getPropertyValue();
        final ComboBox<RadialPullAlignment> alignmentModeComboBox = getAlignmentComboBox();
        final SingleSelectionModel<RadialPullAlignment> selectionModel = alignmentModeComboBox.getSelectionModel();
        selectionModel.select(element);
    }

    @Override
    protected void changed(@Nullable final RadialPullAlignment newValue, @Nullable final RadialPullAlignment oldValue) {

        final T editObject = getEditObject();
        final ParticleInfluencerPropertyOperation<T, RadialPullAlignment> operation = new ParticleInfluencerPropertyOperation<>(editObject, parent, getPropertyName(), newValue, oldValue);
        operation.setApplyHandler(getApplyHandler());

        final ModelChangeConsumer modelChangeConsumer = getModelChangeConsumer();
        modelChangeConsumer.execute(operation);
    }

    @Override
    protected boolean isSingleRow() {
        return true;
    }
}
