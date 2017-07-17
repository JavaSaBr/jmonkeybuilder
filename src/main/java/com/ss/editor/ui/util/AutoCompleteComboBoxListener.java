package com.ss.editor.ui.util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;
import org.jetbrains.annotations.NotNull;
import com.ss.rlib.util.StringUtils;

/**
 * The implementation of autocomplete for combobox.
 *
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public class AutoCompleteComboBoxListener<T> implements EventHandler<KeyEvent> {

    /**
     * Install autocomplete to a combobox.
     *
     * @param <T>      the type parameter
     * @param comboBox the combobox.
     */
    public static <T> void install(@NotNull final ComboBox<T> comboBox) {
        new AutoCompleteComboBoxListener<>(comboBox);
    }

    /**
     * The combobox.
     */
    @NotNull
    private final ComboBox<T> comboBox;

    /**
     * The list of available values.
     */
    @NotNull
    private final ObservableList<T> data;

    /**
     * The caret position.
     */
    private int caretPos;

    private boolean moveCaretToPos;

    private AutoCompleteComboBoxListener(@NotNull final ComboBox<T> comboBox) {
        this.comboBox = comboBox;
        this.data = comboBox.getItems();

        comboBox.setEditable(true);
        comboBox.setOnKeyPressed(event -> comboBox.hide());
        comboBox.setOnKeyReleased(AutoCompleteComboBoxListener.this);
    }

    /**
     * @return the combobox.
     */
    @NotNull
    private ComboBox<T> getComboBox() {
        return comboBox;
    }

    /**
     * @return the list of available values.
     */
    @NotNull
    private ObservableList<T> getData() {
        return data;
    }

    @Override
    public void handle(@NotNull final KeyEvent event) {

        final KeyCode keyCode = event.getCode();

        final ComboBox<T> comboBox = getComboBox();
        final TextField editor = comboBox.getEditor();
        final String editorText = editor.getText();

        if (keyCode == KeyCode.UP) {
            caretPos = -1;
            moveCaret(editorText.length());
            return;
        } else if (keyCode == KeyCode.DOWN) {

            if (!comboBox.isShowing()) {
                comboBox.show();
            }

            caretPos = -1;

            moveCaret(editorText.length());
            return;

        } else if (keyCode == KeyCode.BACK_SPACE) {
            moveCaretToPos = true;
            caretPos = editor.getCaretPosition();
        } else if (keyCode == KeyCode.DELETE) {
            moveCaretToPos = true;
            caretPos = editor.getCaretPosition();
        }

        if (keyCode == KeyCode.RIGHT || keyCode == KeyCode.LEFT ||
                event.isControlDown() || keyCode == KeyCode.HOME ||
                keyCode == KeyCode.END || keyCode == KeyCode.TAB) {
            return;
        }

        final String toCheck = StringUtils.isEmpty(editorText) ? "" : editorText.toLowerCase();

        final ObservableList<T> filtered = FXCollections.observableArrayList();
        final ObservableList<T> data = getData();
        data.forEach(value -> {

            final StringConverter<T> converter = comboBox.getConverter();
            final String presentation = converter.toString(value);
            if (presentation == null) return;

            final String lowerCase = presentation.toLowerCase();
            if (!lowerCase.contains(toCheck)) return;

            filtered.add(value);
        });


        comboBox.setItems(filtered);
        editor.setText(editorText);

        if (!moveCaretToPos) {
            caretPos = -1;
        }

        moveCaret(editorText.length());

        if (!filtered.isEmpty()) {
            comboBox.show();
        }
    }

    private void moveCaret(int textLength) {

        final TextField editor = comboBox.getEditor();

        if (caretPos == -1) {
            editor.positionCaret(textLength);
        } else {
            editor.positionCaret(caretPos);
        }

        moveCaretToPos = false;
    }
}