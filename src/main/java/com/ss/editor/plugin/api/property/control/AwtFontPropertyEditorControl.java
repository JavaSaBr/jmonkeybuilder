package com.ss.editor.plugin.api.property.control;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.StringUtils;
import com.ss.rlib.util.VarTable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.SingleSelectionModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * The control to choose string value from list.
 *
 * @author JavaSaBr
 */
public class AwtFontPropertyEditorControl extends PropertyEditorControl<Font> {

    @NotNull
    private class FontCell extends ListCell<Font> {

        @Override
        protected void updateItem(@Nullable final Font font, final boolean empty) {
            super.updateItem(font, empty);

            if (font == null) {
                setText(StringUtils.EMPTY);
                return;
            }

            setText(font.getFontName());
        }
    }

    /**
     * The list of available options of the string value.
     */
    @Nullable
    private ComboBox<Font> comboBox;

    protected AwtFontPropertyEditorControl(@NotNull final VarTable vars, @NotNull final PropertyDefinition definition,
                                           @NotNull final Runnable validationCallback) {
        super(vars, definition, validationCallback);
    }

    @Override
    protected void createComponents() {
        super.createComponents();

        final GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();

        comboBox = new ComboBox<>();
        comboBox.setCellFactory(param -> new FontCell());
        comboBox.setButtonCell(new FontCell());
        comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> change());
        comboBox.prefWidthProperty().bind(widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));
        comboBox.getItems().addAll(environment.getAllFonts());

        FXUtils.addClassTo(comboBox, CSSClasses.ABSTRACT_PARAM_CONTROL_COMBO_BOX);
        FXUtils.addToPane(comboBox, this);
    }

    /**
     * @return The list of available options of the string value.
     */
    @NotNull
    private ComboBox<Font> getComboBox() {
        return notNull(comboBox);
    }

    @Override
    protected void reload() {
        super.reload();
        final Font value = getPropertyValue();
        final ComboBox<Font> enumComboBox = getComboBox();
        enumComboBox.getSelectionModel().select(value);
    }

    @Override
    protected void change() {
        final ComboBox<Font> comboBox = getComboBox();
        final SingleSelectionModel<Font> selectionModel = comboBox.getSelectionModel();
        setPropertyValue(selectionModel.getSelectedItem());
        super.change();
    }
}
