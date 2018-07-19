package com.ss.editor.ui.control.property.impl;

import static com.ss.rlib.common.util.ObjectUtils.ifNull;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.extension.scene.SceneLayer;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.fx.util.FxControlUtils;
import com.ss.rlib.fx.util.FxUtils;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link PropertyControl} to edit layers.
 *
 * @param <D> the type of an editing object.
 * @author JavaSaBr
 */
public class LayerModelPropertyControl<D> extends PropertyControl<ModelChangeConsumer, D, SceneLayer> {

    private class LayerCell extends ListCell<SceneLayer> {

        @Override
        protected void updateItem(@Nullable SceneLayer layer, boolean empty) {
            super.updateItem(layer, empty);

            if (layer == null || layer == SceneLayer.NO_LAYER) {
                setText(Messages.LAYER_PROPERTY_CONTROL_NO_LAYER);
                return;
            }

            setText(layer.getName());
        }
    }

    /**
     * The layers combo box.
     */
    @NotNull
    private final ComboBox<SceneLayer> layerComboBox;

    public LayerModelPropertyControl(
            @Nullable SceneLayer layer,
            @NotNull String propertyName,
            @NotNull SceneChangeConsumer changeConsumer
    ) {
        super(ifNull(layer, SceneLayer.NO_LAYER), propertyName, changeConsumer);
        this.layerComboBox = new ComboBox<>();
    }

    @Override
    @FromAnyThread
    protected boolean isSingleRow() {
        return true;
    }

    @Override
    @FxThread
    protected void createControls(@NotNull HBox container) {
        super.createControls(container);

        layerComboBox.setCellFactory(param -> new LayerCell());
        layerComboBox.setButtonCell(new LayerCell());
        layerComboBox.setEditable(false);
        layerComboBox.prefWidthProperty()
                .bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));

        FxControlUtils.onSelectedItemChange(layerComboBox, this::updateLevel);

        FxUtils.addClass(layerComboBox,
                CssClasses.PROPERTY_CONTROL_COMBO_BOX);

        FxUtils.addChild(container, layerComboBox);
    }

    @FxThread
    private void updateLevel(@Nullable SceneLayer layer) {
        if (!isIgnoreListener()) {
            changed(layer, getPropertyValue());
        }
    }

    @Override
    @FxThread
    protected void reloadImpl() {

        var changeConsumer = (SceneChangeConsumer) getChangeConsumer();
        var currentModel = changeConsumer.getCurrentModel();
        var sceneLayer = getPropertyValue();

        var items = layerComboBox.getItems();
        items.clear();
        items.add(SceneLayer.NO_LAYER);
        items.addAll(currentModel.getLayers());

        layerComboBox.getSelectionModel()
                .select(sceneLayer == null ? SceneLayer.NO_LAYER : sceneLayer);

        super.reloadImpl();
    }
}
