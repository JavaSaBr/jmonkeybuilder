package com.ss.editor.ui.util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

/**
 * Реализация слушателя комбобокса для формирования подсказок.
 */
public class AutoCompleteComboBoxListener<T> implements EventHandler<KeyEvent> {

    public static <T> void install(ComboBox<T> comboBox) {
        new AutoCompleteComboBoxListener<T>(comboBox);
    }

    /**
     * Комбобокс.
     */
    private final ComboBox<T> comboBox;

    /**
     * Изначальный список значений.
     */
    private ObservableList<T> data;

    /**
     * Позиция курсора.
     */
    private int caretPos;

    private boolean moveCaretToPos;

    public AutoCompleteComboBoxListener(final ComboBox<T> comboBox) {
        this.comboBox = comboBox;
        this.data = comboBox.getItems();

        comboBox.setEditable(true);
        comboBox.setOnKeyPressed(event -> comboBox.hide());
        comboBox.setOnKeyReleased(AutoCompleteComboBoxListener.this);
    }

    /**
     * @return комбобокс.
     */
    private ComboBox<T> getComboBox() {
        return comboBox;
    }

    /**
     * @return изначальный список значений.
     */
    private ObservableList<T> getData() {
        return data;
    }

    @Override
    public void handle(KeyEvent event) {

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

        if (keyCode == KeyCode.RIGHT || keyCode == KeyCode.LEFT || event.isControlDown() || keyCode == KeyCode.HOME || keyCode == KeyCode.END || keyCode == KeyCode.TAB) {
            return;
        }

        final ObservableList<T> filtered = FXCollections.observableArrayList();
        final ObservableList<T> data = getData();
        data.forEach(value -> {

            final StringConverter<T> converter = comboBox.getConverter();
            final String presentation = converter.toString(value);

            if(presentation == null || !presentation.contains(editorText)) {
                return;
            }

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