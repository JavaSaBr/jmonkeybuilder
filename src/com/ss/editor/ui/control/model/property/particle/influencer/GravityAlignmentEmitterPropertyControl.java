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
import tonegod.emitter.influencers.GravityInfluencer.GravityAlignment;
import tonegod.emitter.influencers.ParticleInfluencer;

/**
 * The implementation of the {@link ModelPropertyControl} for editing the {@link GravityAlignment}.
 *
 * @author JavaSaBr
 */
public class GravityAlignmentEmitterPropertyControl<T extends ParticleInfluencer> extends ModelPropertyControl<T, GravityAlignment> {

    private static final Array<GravityAlignment> ALIGNMENT_MODES = ArrayFactory.newArray(GravityAlignment.class);

    static {
        ALIGNMENT_MODES.addAll(GravityAlignment.values());
    }

    /**
     * The parent of the influencer.
     */
    @NotNull
    private Object parent;

    /**
     * The list of available options of the {@link GravityAlignment}.
     */
    private ComboBox<GravityAlignment> alignmentModeComboBox;

    public GravityAlignmentEmitterPropertyControl(final GravityAlignment element, final String paramName, final ModelChangeConsumer modelChangeConsumer, final @NotNull Object parent) {
        super(element, paramName, modelChangeConsumer);
        this.parent = parent;
    }

    @Override
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        alignmentModeComboBox = new ComboBox<>();
        alignmentModeComboBox.setId(CSSIds.MODEL_PARAM_CONTROL_COMBO_BOX);
        alignmentModeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateAlignment());
        alignmentModeComboBox.prefWidthProperty().bind(container.widthProperty());

        final ObservableList<GravityAlignment> items = alignmentModeComboBox.getItems();

        ALIGNMENT_MODES.forEach(items::add);

        FXUtils.addToPane(alignmentModeComboBox, container);
    }

    /**
     * @return the list of available options of the {@link GravityAlignment}.
     */
    private ComboBox<GravityAlignment> getAlignmentModeComboBox() {
        return alignmentModeComboBox;
    }

    /**
     * Update selected {@link GravityAlignment}.
     */
    private void updateAlignment() {
        if (isIgnoreListener()) return;

        final ComboBox<GravityAlignment> alignmentModeComboBox = getAlignmentModeComboBox();
        final SingleSelectionModel<GravityAlignment> selectionModel = alignmentModeComboBox.getSelectionModel();
        final GravityAlignment newValue = selectionModel.getSelectedItem();

        changed(newValue, getPropertyValue());
    }

    @Override
    protected void reload() {
        final GravityAlignment element = getPropertyValue();
        final ComboBox<GravityAlignment> alignmentModeComboBox = getAlignmentModeComboBox();
        final SingleSelectionModel<GravityAlignment> selectionModel = alignmentModeComboBox.getSelectionModel();
        selectionModel.select(element);
    }

    @Override
    protected void changed(@Nullable final GravityAlignment newValue, @Nullable final GravityAlignment oldValue) {

        final T editObject = getEditObject();
        final ParticleInfluencerPropertyOperation<T, GravityAlignment> operation = new ParticleInfluencerPropertyOperation<>(editObject, parent, getPropertyName(), newValue, oldValue);
        operation.setApplyHandler(getApplyHandler());

        final ModelChangeConsumer modelChangeConsumer = getModelChangeConsumer();
        modelChangeConsumer.execute(operation);
    }

    @Override
    protected boolean isSingleRow() {
        return true;
    }
}
