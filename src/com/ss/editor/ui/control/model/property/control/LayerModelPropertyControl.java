package com.ss.editor.ui.control.model.property.control;

import static java.util.Objects.requireNonNull;

import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.control.model.property.operation.ModelPropertyOperation;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.extension.scene.SceneLayer;
import com.ss.extension.scene.SceneNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import rlib.function.SixObjectConsumer;
import rlib.ui.util.FXUtils;

/**
 * The implementation of the {@link ModelPropertyControl} to edit layers.
 *
 * @author JavaSaBr
 */
public class LayerModelPropertyControl extends ModelPropertyControl<Spatial, SceneLayer> {

    private class LayerCell extends ListCell<SceneLayer> {

        @Override
        protected void updateItem(@Nullable final SceneLayer layer, final boolean empty) {
            super.updateItem(layer, empty);
            if (layer == null) return;
            setText(layer == SceneLayer.NO_LAYER ? "No layer" : layer.getName());
        }
    }

    @NotNull
    public static <D, T> SixObjectConsumer<ModelChangeConsumer, D, String, T, T, BiConsumer<D, T>> newChangeHandler() {
        return (changeConsumer, object, propName, newValue, oldValue, handler) -> {

            final ModelPropertyOperation<D, T> operation = new ModelPropertyOperation<>(object, SceneLayer.KEY, newValue, oldValue);
            operation.setApplyHandler(handler);

            changeConsumer.execute(operation);
        };
    }

    /**
     * The layers combo box.
     */
    private ComboBox<SceneLayer> layerComboBox;

    public LayerModelPropertyControl(@Nullable final SceneLayer layer, @NotNull final SceneChangeConsumer changeConsumer) {
        super(layer == null ? SceneLayer.NO_LAYER : layer, "Layer", changeConsumer, newChangeHandler());
        setApplyHandler(this::setLayer);
        setSyncHandler(this::getLayer);
    }

    private void setLayer(@NotNull final Spatial spatial, @NotNull final SceneLayer newLayer) {
        SceneLayer.setLayer(newLayer, spatial);
    }

    private SceneLayer getLayer(@NotNull final Spatial spatial) {
        final SceneLayer sceneLayer = SceneLayer.getLayer(spatial);
        return sceneLayer == null ? SceneLayer.NO_LAYER : sceneLayer;
    }

    @Override
    protected boolean isSingleRow() {
        return true;
    }

    @Override
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        layerComboBox = new ComboBox<>();
        layerComboBox.setId(CSSIds.ABSTRACT_PARAM_CONTROL_COMBO_BOX);
        layerComboBox.setCellFactory(param -> new LayerCell());
        layerComboBox.setButtonCell(new LayerCell());
        layerComboBox.setEditable(false);
        layerComboBox.prefWidthProperty().bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));
        layerComboBox.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> updateLevel(newValue));

        FXUtils.addToPane(layerComboBox, container);
        FXUtils.addClassTo(layerComboBox, CSSClasses.SPECIAL_FONT_13);
    }

    private void updateLevel(@Nullable final SceneLayer layer) {
        if (isIgnoreListener()) return;
        changed(layer, getPropertyValue());
    }

    /**
     * @return the layers combo box.
     */
    @NotNull
    public ComboBox<SceneLayer> getLayerComboBox() {
        return requireNonNull(layerComboBox);
    }

    @Override
    protected void reload() {

        final SceneChangeConsumer changeConsumer = (SceneChangeConsumer) getChangeConsumer();
        final SceneNode currentModel = changeConsumer.getCurrentModel();
        final SceneLayer sceneLayer = getPropertyValue();

        final ComboBox<SceneLayer> levelComboBox = getLayerComboBox();
        final ObservableList<SceneLayer> items = levelComboBox.getItems();
        items.clear();
        items.add(SceneLayer.NO_LAYER);
        items.addAll(currentModel.getLayers());

        levelComboBox.getSelectionModel().select(sceneLayer);
    }
}
