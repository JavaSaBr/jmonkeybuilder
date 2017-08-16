package com.ss.editor.plugin.api.property.control;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.util.AutoCompleteComboBoxListener;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.StringUtils;
import com.ss.rlib.util.VarTable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.util.StringConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Arrays;

/**
 * The control to choose string value from list.
 *
 * @author JavaSaBr
 */
public class AwtFontPropertyEditorControl extends PropertyEditorControl<Font> {

    @NotNull
    private static final GraphicsEnvironment GRAPHICS_ENVIRONMENT = GraphicsEnvironment.getLocalGraphicsEnvironment();

    @NotNull
    private static final Font[] FONTS = GRAPHICS_ENVIRONMENT.getAllFonts();

    @NotNull
    private static final StringConverter<Font> STRING_CONVERTER = new StringConverter<Font>() {

        @Override
        public String toString(@Nullable final Font font) {
            return font == null ? StringUtils.EMPTY : font.getFontName();
        }

        @Override
        public Font fromString(@NotNull final String fontName) {
            return Arrays.stream(FONTS)
                    .filter(font -> font.getFontName().equals(fontName))
                    .findAny().orElse(null);
        }
    };

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

        comboBox = new ComboBox<>();
        comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> change());
        comboBox.prefWidthProperty().bind(widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));
        comboBox.getItems().addAll(FONTS);
        comboBox.setVisibleRowCount(20);
        comboBox.setConverter(STRING_CONVERTER);

        AutoCompleteComboBoxListener.install(comboBox);

        FXUtils.addClassesTo(comboBox.getEditor(), CSSClasses.TRANSPARENT_TEXT_FIELD, CSSClasses.TEXT_FIELD_IN_COMBO_BOX);
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
    protected void changeImpl() {
        final ComboBox<Font> comboBox = getComboBox();
        final SingleSelectionModel<Font> selectionModel = comboBox.getSelectionModel();
        setPropertyValue(selectionModel.getSelectedItem());
        super.changeImpl();
    }
}
