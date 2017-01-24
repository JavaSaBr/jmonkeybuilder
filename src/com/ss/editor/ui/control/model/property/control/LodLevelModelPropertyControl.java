package com.ss.editor.ui.control.model.property.control;

import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import rlib.ui.util.FXUtils;

/**
 * The implementation of the {@link ModelPropertyControl} for editing the LOD levels.
 *
 * @author JavaSaBr
 */
public class LodLevelModelPropertyControl extends ModelPropertyControl<Geometry, Integer> {

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
    private ComboBox<Integer> levelComboBox;

    public LodLevelModelPropertyControl(@Nullable final Integer element, @NotNull final String paramName,
                                        @NotNull final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer);
    }

    @Override
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        levelComboBox = new ComboBox<>();
        levelComboBox.setId(CSSIds.ABSTRACT_PARAM_CONTROL_COMBO_BOX);
        levelComboBox.setCellFactory(param -> new LodLevelCell());
        levelComboBox.setButtonCell(new LodLevelCell());
        levelComboBox.setEditable(false);
        levelComboBox.prefWidthProperty().bind(widthProperty());
        levelComboBox.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> updateLevel(newValue));

        FXUtils.addToPane(levelComboBox, container);
        FXUtils.addClassTo(levelComboBox, CSSClasses.SPECIAL_FONT_13);
    }

    private void updateLevel(@Nullable final Integer newValue) {
        if (isIgnoreListener()) return;
        changed(newValue == null ? 0 : newValue, getPropertyValue());
    }

    @NotNull
    @Override
    public Integer getPropertyValue() {
        final Integer value = super.getPropertyValue();
        return value == null ? 0 : value;
    }

    /**
     * @return The lod level combobox.
     */
    @NotNull
    public ComboBox<Integer> getLevelComboBox() {
        return Objects.requireNonNull(levelComboBox);
    }

    @Override
    protected void reload() {
        if (!hasEditObject()) return;

        final Geometry geometry = getEditObject();
        final Mesh mesh = geometry.getMesh();
        if (mesh == null) return;

        final Integer element = getPropertyValue() == null ? 0 : getPropertyValue();
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
