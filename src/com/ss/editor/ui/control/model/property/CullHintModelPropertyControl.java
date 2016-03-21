package com.ss.editor.ui.control.model.property;

import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.EditorOperation;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.css.CSSIds;

import java.util.function.Consumer;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.layout.HBox;
import rlib.ui.util.FXUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * Реализация контрола для смены CullHint.
 *
 * @author Ronn
 */
public class CullHintModelPropertyControl extends ModelPropertyControl<Spatial, Spatial.CullHint> {

    private static final Array<Spatial.CullHint> CULL_HINTS = ArrayFactory.newArray(Spatial.CullHint.class);

    static {
        CULL_HINTS.addAll(Spatial.CullHint.values());
    }

    /**
     * Список доступных режимов CullHint.
     */
    private ComboBox<Spatial.CullHint> cullHintComboBox;

    public CullHintModelPropertyControl(final Consumer<EditorOperation> changeHandler, final Spatial.CullHint element, final String paramName, final ModelChangeConsumer modelChangeConsumer) {
        super(changeHandler, element, paramName, modelChangeConsumer);
    }

    @Override
    protected void createComponents(final HBox container) {
        super.createComponents(container);

        cullHintComboBox = new ComboBox<>();
        cullHintComboBox.setId(CSSIds.MODEL_PARAM_CONTROL_COMBO_BOX);
        cullHintComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateCullHint());
        cullHintComboBox.prefWidthProperty().bind(container.widthProperty());

        final ObservableList<Spatial.CullHint> items = cullHintComboBox.getItems();

        CULL_HINTS.forEach(items::add);

        FXUtils.addToPane(cullHintComboBox, container);
    }

    /**
     * @return список доступных режимов CullHint.
     */
    private ComboBox<Spatial.CullHint> getCullHintComboBox() {
        return cullHintComboBox;
    }

    /**
     * Обновление выбранного CullHint.
     */
    private void updateCullHint() {

        if (isIgnoreListener()) {
            return;
        }

        final ComboBox<Spatial.CullHint> cullHintComboBox = getCullHintComboBox();
        final SingleSelectionModel<Spatial.CullHint> selectionModel = cullHintComboBox.getSelectionModel();
        final Spatial.CullHint newValue = selectionModel.getSelectedItem();

        changed(newValue, getPropertyValue());
    }

    @Override
    protected void reload() {

        final Spatial.CullHint element = getPropertyValue();

        final ComboBox<Spatial.CullHint> cullHintComboBox = getCullHintComboBox();
        final SingleSelectionModel<Spatial.CullHint> selectionModel = cullHintComboBox.getSelectionModel();
        selectionModel.select(element);
    }

    @Override
    protected boolean isSingleRow() {
        return true;
    }
}
