package com.ss.editor.ui.control.model.property;

import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.css.CSSIds;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.layout.HBox;
import rlib.ui.util.FXUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * Реализация контрола для смены RenderQueue.ShadowMode.
 *
 * @author Ronn
 */
public class ShadowModeModelPropertyControl extends ModelPropertyControl<Spatial, RenderQueue.ShadowMode> {

    private static final Array<RenderQueue.ShadowMode> SHADOW_MODES = ArrayFactory.newArray(RenderQueue.ShadowMode.class);

    static {
        SHADOW_MODES.addAll(RenderQueue.ShadowMode.values());
    }

    /**
     * Список доступных режимов ShadowMode.
     */
    private ComboBox<RenderQueue.ShadowMode> shadowModeComboBox;

    public ShadowModeModelPropertyControl(final RenderQueue.ShadowMode element, final String paramName, final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer);
    }

    @Override
    protected void createComponents(final HBox container) {
        super.createComponents(container);

        shadowModeComboBox = new ComboBox<>();
        shadowModeComboBox.setId(CSSIds.MODEL_PARAM_CONTROL_COMBO_BOX);
        shadowModeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateShadowMode());
        shadowModeComboBox.prefWidthProperty().bind(container.widthProperty());

        final ObservableList<RenderQueue.ShadowMode> items = shadowModeComboBox.getItems();

        SHADOW_MODES.forEach(items::add);

        FXUtils.addToPane(shadowModeComboBox, container);
    }

    /**
     * @return список доступных режимов ShadowMode.
     */
    private ComboBox<RenderQueue.ShadowMode> getShadowModeComboBox() {
        return shadowModeComboBox;
    }

    /**
     * Обновление выбранного ShadowMode.
     */
    private void updateShadowMode() {

        if (isIgnoreListener()) {
            return;
        }

        final ComboBox<RenderQueue.ShadowMode> shadowModeComboBox = getShadowModeComboBox();
        final SingleSelectionModel<RenderQueue.ShadowMode> selectionModel = shadowModeComboBox.getSelectionModel();
        final RenderQueue.ShadowMode newValue = selectionModel.getSelectedItem();

        changed(newValue, getPropertyValue());
    }

    @Override
    protected void reload() {

        final RenderQueue.ShadowMode element = getPropertyValue();

        final ComboBox<RenderQueue.ShadowMode> shadowModeComboBox = getShadowModeComboBox();
        final SingleSelectionModel<RenderQueue.ShadowMode> selectionModel = shadowModeComboBox.getSelectionModel();
        selectionModel.select(element);
    }

    @Override
    protected boolean isSingleRow() {
        return true;
    }
}
