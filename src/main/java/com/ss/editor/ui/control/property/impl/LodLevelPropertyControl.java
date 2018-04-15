package com.ss.editor.ui.control.property.impl;

import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.fx.util.FXUtils;
import com.ss.rlib.common.util.ObjectUtils;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link PropertyControl} to edit the LOD levels.
 *
 * @author JavaSaBr
 */
public class LodLevelPropertyControl<C extends ChangeConsumer> extends PropertyControl<C, Geometry, Integer> {

    private class LodLevelCell extends ListCell<Integer> {

        @Override
        protected void updateItem(@Nullable final Integer level, final boolean empty) {
            super.updateItem(level, empty);

            final Geometry geometry = getEditObject();
            final Mesh mesh = geometry.getMesh();

            if (level == null || mesh == null) {
                setText("None");
                return;
            }

            int elements;

            if (level < mesh.getNumLodLevels()) {
                final VertexBuffer lodLevel = mesh.getLodLevel(level);
                elements = lodLevel.getNumElements();
            } else {
                elements = mesh.getTriangleCount();
            }

            setText(Messages.MODEL_PROPERTY_LEVEL + ": " + level + " (" + elements + " " +
                    Messages.MODEL_PROPERTY_TRIANGLE_COUNT + ")");
        }
    }

    /**
     * The lod level combobox.
     */
    @Nullable
    private ComboBox<Integer> levelComboBox;

    public LodLevelPropertyControl(@Nullable final Integer element, @NotNull final String paramName,
                                   @NotNull final C changeConsumer) {
        super(element, paramName, changeConsumer);
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

        levelComboBox = new ComboBox<>();
        levelComboBox.setCellFactory(param -> new LodLevelCell());
        levelComboBox.setButtonCell(new LodLevelCell());
        levelComboBox.setEditable(false);
        levelComboBox.prefWidthProperty().bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));
        levelComboBox.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> updateLevel(newValue));

        FXUtils.addToPane(levelComboBox, container);
        FXUtils.addClassTo(levelComboBox, CssClasses.ABSTRACT_PARAM_CONTROL_COMBO_BOX);
    }

    /**
     * Update lod level.
     *
     * @param newValue the new level.
     */
    @FxThread
    private void updateLevel(@Nullable final Integer newValue) {
        if (isIgnoreListener()) return;
        changed(newValue == null ? 0 : newValue, getPropertyValue());
    }

    @Override
    @FxThread
    public @NotNull Integer getPropertyValue() {
        final Integer value = super.getPropertyValue();
        return value == null ? 0 : value;
    }

    /**
     * Gets level combo box.
     *
     * @return The lod level combobox.
     */
    @FxThread
    protected @NotNull ComboBox<Integer> getLevelComboBox() {
        return ObjectUtils.notNull(levelComboBox);
    }

    @Override
    @FxThread
    protected void reload() {

        if (!hasEditObject()) {
            return;
        }

        final Geometry geometry = getEditObject();
        final Mesh mesh = geometry.getMesh();
        if (mesh == null) return;

        final Integer element = getPropertyValue();
        final ComboBox<Integer> levelComboBox = getLevelComboBox();
        final ObservableList<Integer> items = levelComboBox.getItems();
        items.clear();

        final int numLodLevels = mesh.getNumLodLevels();

        for (int i = 0; i < numLodLevels; i++) {
            items.add(i);
        }

        if (items.isEmpty()) {
            items.add(0);
        }

        levelComboBox.getSelectionModel().select(element);
    }
}
