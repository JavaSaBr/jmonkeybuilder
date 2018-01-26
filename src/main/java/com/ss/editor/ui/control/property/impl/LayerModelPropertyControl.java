package com.ss.editor.ui.control.property.impl;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.scene.Spatial;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.extension.scene.SceneLayer;
import com.ss.editor.extension.scene.SceneNode;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.control.property.operation.PropertyOperation;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.function.SixObjectConsumer;
import com.ss.rlib.ui.util.FXUtils;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

/**
 * The implementation of the {@link PropertyControl} to edit layers.
 *
 * @author JavaSaBr
 */
public class LayerModelPropertyControl extends PropertyControl<ModelChangeConsumer, Spatial, SceneLayer> {

    @NotNull
    private class LayerCell extends ListCell<SceneLayer> {

        @Override
        protected void updateItem(@Nullable final SceneLayer layer, final boolean empty) {
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
    @Nullable
    private ComboBox<SceneLayer> layerComboBox;

    public LayerModelPropertyControl(@Nullable final SceneLayer layer, @NotNull final SceneChangeConsumer changeConsumer) {
        super(layer == null ? SceneLayer.NO_LAYER : layer, Messages.MODEL_PROPERTY_LAYER, changeConsumer);
        setApplyHandler(this::setLayer);
        setSyncHandler(this::getLayer);
    }

    @Override
    @FromAnyThread
    public @NotNull SixObjectConsumer<ModelChangeConsumer, Spatial, String, SceneLayer, SceneLayer, BiConsumer<Spatial, SceneLayer>> newChangeHandler() {
        return (changeConsumer, object, propName, newValue, oldValue, handler) -> {

            final PropertyOperation<ChangeConsumer, Spatial, SceneLayer> operation =
                    new PropertyOperation<>(object, SceneLayer.KEY, newValue, oldValue);
            operation.setApplyHandler(handler);

            changeConsumer.execute(operation);
        };
    }

    @JmeThread
    private void setLayer(@NotNull final Spatial spatial, @NotNull final SceneLayer newLayer) {
        SceneLayer.setLayer(newLayer, spatial);
    }

    @JmeThread
    private SceneLayer getLayer(@NotNull final Spatial spatial) {
        final SceneLayer sceneLayer = SceneLayer.getLayer(spatial);
        return sceneLayer == null ? SceneLayer.NO_LAYER : sceneLayer;
    }

    @Override
    @FromAnyThread
    protected boolean isSingleRow() {
        return true;
    }

    @Override
    @FxThread
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        layerComboBox = new ComboBox<>();
        layerComboBox.setCellFactory(param -> new LayerCell());
        layerComboBox.setButtonCell(new LayerCell());
        layerComboBox.setEditable(false);
        layerComboBox.prefWidthProperty().bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));
        layerComboBox.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> updateLevel(newValue));

        FXUtils.addToPane(layerComboBox, container);
        FXUtils.addClassTo(layerComboBox, CssClasses.ABSTRACT_PARAM_CONTROL_COMBO_BOX);
    }

    @FxThread
    private void updateLevel(@Nullable final SceneLayer layer) {
        if (isIgnoreListener()) return;
        changed(layer, getPropertyValue());
    }

    /**
     * Gets layer combo box.
     *
     * @return the layers combo box.
     */
    @FxThread
    protected @NotNull ComboBox<SceneLayer> getLayerComboBox() {
        return notNull(layerComboBox);
    }

    @Override
    @FxThread
    protected void reload() {

        final SceneChangeConsumer changeConsumer = (SceneChangeConsumer) getChangeConsumer();
        final SceneNode currentModel = changeConsumer.getCurrentModel();
        final SceneLayer sceneLayer = getPropertyValue();

        final ComboBox<SceneLayer> levelComboBox = getLayerComboBox();
        final ObservableList<SceneLayer> items = levelComboBox.getItems();
        items.clear();
        items.add(SceneLayer.NO_LAYER);
        items.addAll(currentModel.getLayers());

        levelComboBox.getSelectionModel().select(sceneLayer == null ? SceneLayer.NO_LAYER : sceneLayer);
    }
}
