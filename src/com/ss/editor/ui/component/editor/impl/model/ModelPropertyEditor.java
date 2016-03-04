package com.ss.editor.ui.component.editor.impl.model;

import com.ss.editor.Messages;

import javafx.scene.control.TitledPane;

/**
 * Реализация компонента для редактирования свойств моделей.
 *
 * @author Ronn
 */
public class ModelPropertyEditor extends TitledPane {

    public ModelPropertyEditor() {
        setText(Messages.MODEL_FILE_EDITOR_PROPERTIES);
    }
}
