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
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.ParticleEmitterNode.BillboardMode;

/**
 * The implementation of the {@link ModelPropertyControl} for changing the {@link BillboardMode}.
 *
 * @author JavaSaBr
 */
public class BillboardEmitterPropertyControl extends ModelPropertyControl<ParticleEmitterNode, BillboardMode> {

    private static final Array<BillboardMode> BILLBOARD_MODES = ArrayFactory.newArray(BillboardMode.class);

    static {
        BILLBOARD_MODES.addAll(BillboardMode.values());
    }

    /**
     * The list of available options of the {@link BillboardMode}.
     */
    private ComboBox<BillboardMode> billboardModeComboBox;

    public BillboardEmitterPropertyControl(final BillboardMode element, final String paramName, final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer);
    }

    @Override
    protected void createComponents(final HBox container) {
        super.createComponents(container);

        billboardModeComboBox = new ComboBox<>();
        billboardModeComboBox.setId(CSSIds.MODEL_PARAM_CONTROL_COMBO_BOX);
        billboardModeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateCullHint());
        billboardModeComboBox.prefWidthProperty().bind(container.widthProperty());

        final ObservableList<BillboardMode> items = billboardModeComboBox.getItems();

        BILLBOARD_MODES.forEach(items::add);

        FXUtils.addToPane(billboardModeComboBox, container);
    }

    /**
     * @return the list of available options of the {@link BillboardMode}.
     */
    private ComboBox<BillboardMode> getBillboardModeComboBox() {
        return billboardModeComboBox;
    }

    /**
     * Update selected {@link BillboardMode}.
     */
    private void updateCullHint() {
        if (isIgnoreListener()) return;

        final ComboBox<BillboardMode> billboardModeComboBox = getBillboardModeComboBox();
        final SingleSelectionModel<BillboardMode> selectionModel = billboardModeComboBox.getSelectionModel();
        final BillboardMode newValue = selectionModel.getSelectedItem();

        changed(newValue, getPropertyValue());
    }

    @Override
    protected void reload() {
        final BillboardMode element = getPropertyValue();
        final ComboBox<BillboardMode> cullHintComboBox = getBillboardModeComboBox();
        final SingleSelectionModel<BillboardMode> selectionModel = cullHintComboBox.getSelectionModel();
        selectionModel.select(element);
    }

    @Override
    protected boolean isSingleRow() {
        return true;
    }
}
