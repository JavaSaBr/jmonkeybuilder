package com.ss.editor.ui.control.model.property.particle;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.ModelPropertyControl;
import com.ss.editor.ui.css.CSSIds;

import org.jetbrains.annotations.NotNull;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.layout.HBox;
import rlib.ui.util.FXUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;
import tonegod.emitter.EmitterMesh.DirectionType;
import tonegod.emitter.ParticleEmitterNode;

/**
 * The implementation of the {@link ModelPropertyControl} for changing the {@link DirectionType}.
 *
 * @author JavaSaBr
 */
public class DirectionEmitterPropertyControl extends ModelPropertyControl<ParticleEmitterNode, DirectionType> {

    private static final Array<DirectionType> DIRECTION_TYPES = ArrayFactory.newArray(DirectionType.class);

    static {
        DIRECTION_TYPES.addAll(DirectionType.values());
    }

    /**
     * The list of available options of the {@link DirectionType}.
     */
    private ComboBox<DirectionType> directionTypeComboBox;

    public DirectionEmitterPropertyControl(final DirectionType element, final String paramName, final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer);
    }

    @Override
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        directionTypeComboBox = new ComboBox<>();
        directionTypeComboBox.setId(CSSIds.MODEL_PARAM_CONTROL_COMBO_BOX);
        directionTypeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateDirectionType());
        directionTypeComboBox.prefWidthProperty().bind(container.widthProperty());

        final ObservableList<DirectionType> items = directionTypeComboBox.getItems();

        DIRECTION_TYPES.forEach(items::add);

        FXUtils.addToPane(directionTypeComboBox, container);
    }

    /**
     * @return the list of available options of the {@link DirectionType}.
     */
    private ComboBox<DirectionType> getDirectionTypeComboBox() {
        return directionTypeComboBox;
    }

    /**
     * Update selected {@link DirectionType}.
     */
    private void updateDirectionType() {
        if (isIgnoreListener()) return;

        final ComboBox<DirectionType> directionTypeComboBox = getDirectionTypeComboBox();
        final SingleSelectionModel<DirectionType> selectionModel = directionTypeComboBox.getSelectionModel();
        final DirectionType newValue = selectionModel.getSelectedItem();

        changed(newValue, getPropertyValue());
    }

    @Override
    protected void reload() {
        final DirectionType element = getPropertyValue();
        final ComboBox<DirectionType> directionTypeComboBox = getDirectionTypeComboBox();
        final SingleSelectionModel<DirectionType> selectionModel = directionTypeComboBox.getSelectionModel();
        selectionModel.select(element);
    }

    @Override
    protected boolean isSingleRow() {
        return true;
    }
}
