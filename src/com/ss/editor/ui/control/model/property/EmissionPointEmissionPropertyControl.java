package com.ss.editor.ui.control.model.property;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.css.CSSIds;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.layout.HBox;
import rlib.ui.util.FXUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;
import tonegod.emitter.EmitterMesh.DirectionType;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.ParticleEmitterNode.ParticleEmissionPoint;

/**
 * The implementation of the {@link ModelPropertyControl} for changing the {@link
 * ParticleEmissionPoint}.
 *
 * @author JavaSaBr
 */
public class EmissionPointEmissionPropertyControl extends ModelPropertyControl<ParticleEmitterNode, ParticleEmissionPoint> {

    private static final Array<ParticleEmissionPoint> EMISSION_POINTS = ArrayFactory.newArray(ParticleEmissionPoint.class);

    static {
        EMISSION_POINTS.addAll(ParticleEmissionPoint.values());
    }

    /**
     * The list of available options of the {@link ParticleEmissionPoint}.
     */
    private ComboBox<ParticleEmissionPoint> emissionPointComboBox;

    public EmissionPointEmissionPropertyControl(final ParticleEmissionPoint element, final String paramName, final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer);
    }

    @Override
    protected void createComponents(final HBox container) {
        super.createComponents(container);

        emissionPointComboBox = new ComboBox<>();
        emissionPointComboBox.setId(CSSIds.MODEL_PARAM_CONTROL_COMBO_BOX);
        emissionPointComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateCullHint());
        emissionPointComboBox.prefWidthProperty().bind(container.widthProperty());

        final ObservableList<ParticleEmissionPoint> items = emissionPointComboBox.getItems();

        EMISSION_POINTS.forEach(items::add);

        FXUtils.addToPane(emissionPointComboBox, container);
    }

    /**
     * @return the list of available options of the {@link DirectionType}.
     */
    private ComboBox<ParticleEmissionPoint> getEmissionPointComboBox() {
        return emissionPointComboBox;
    }

    /**
     * Update selected {@link ParticleEmissionPoint}.
     */
    private void updateCullHint() {
        if (isIgnoreListener()) return;

        final ComboBox<ParticleEmissionPoint> billboardModeComboBox = getEmissionPointComboBox();
        final SingleSelectionModel<ParticleEmissionPoint> selectionModel = billboardModeComboBox.getSelectionModel();
        final ParticleEmissionPoint newValue = selectionModel.getSelectedItem();

        changed(newValue, getPropertyValue());
    }

    @Override
    protected void reload() {
        final ParticleEmissionPoint element = getPropertyValue();
        final ComboBox<ParticleEmissionPoint> cullHintComboBox = getEmissionPointComboBox();
        final SingleSelectionModel<ParticleEmissionPoint> selectionModel = cullHintComboBox.getSelectionModel();
        selectionModel.select(element);
    }

    @Override
    protected boolean isSingleRow() {
        return true;
    }
}
