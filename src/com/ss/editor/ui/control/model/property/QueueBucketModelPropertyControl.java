package com.ss.editor.ui.control.model.property;

import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.css.CSSIds;

import org.jetbrains.annotations.NotNull;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.layout.HBox;
import rlib.ui.util.FXUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * Реализация контрола для смены QueueBucket.
 *
 * @author Ronn
 */
public class QueueBucketModelPropertyControl extends ModelPropertyControl<Spatial, RenderQueue.Bucket> {

    private static final Array<RenderQueue.Bucket> BUCKETS = ArrayFactory.newArray(RenderQueue.Bucket.class);

    static {
        BUCKETS.addAll(RenderQueue.Bucket.values());
    }

    /**
     * Список доступных режимов QueueBucket.
     */
    private ComboBox<RenderQueue.Bucket> shadowModeComboBox;

    public QueueBucketModelPropertyControl(final RenderQueue.Bucket element, final String paramName, final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer);
    }

    @Override
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        shadowModeComboBox = new ComboBox<>();
        shadowModeComboBox.setId(CSSIds.MODEL_PARAM_CONTROL_COMBO_BOX);
        shadowModeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateShadowMode());
        shadowModeComboBox.prefWidthProperty().bind(container.widthProperty());

        final ObservableList<RenderQueue.Bucket> items = shadowModeComboBox.getItems();

        BUCKETS.forEach(items::add);

        FXUtils.addToPane(shadowModeComboBox, container);
    }

    /**
     * @return список доступных режимов QueueBucket.
     */
    private ComboBox<RenderQueue.Bucket> getShadowModeComboBox() {
        return shadowModeComboBox;
    }

    /**
     * Обновление выбранного QueueBucket.
     */
    private void updateShadowMode() {
        if (isIgnoreListener()) return;

        final ComboBox<RenderQueue.Bucket> bucketComboBox = getShadowModeComboBox();
        final SingleSelectionModel<RenderQueue.Bucket> selectionModel = bucketComboBox.getSelectionModel();
        final RenderQueue.Bucket newValue = selectionModel.getSelectedItem();

        changed(newValue, getPropertyValue());
    }

    @Override
    protected void reload() {

        final RenderQueue.Bucket element = getPropertyValue();

        final ComboBox<RenderQueue.Bucket> bucketComboBox = getShadowModeComboBox();
        final SingleSelectionModel<RenderQueue.Bucket> selectionModel = bucketComboBox.getSelectionModel();
        selectionModel.select(element);
    }

    @Override
    protected boolean isSingleRow() {
        return true;
    }
}
