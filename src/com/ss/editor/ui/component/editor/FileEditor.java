package com.ss.editor.ui.component.editor;

import java.nio.file.Path;

import javafx.beans.property.BooleanProperty;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

/**
 * Интерфейс для реализации редактора.
 *
 * @author Ronn
 */
public interface FileEditor {

    /**
     * Получение страницы для отображения на UI.
     *
     * @return страница для отображения на UI.
     */
    public Parent getPage();

    /**
     * @return название редактируемого файла.
     */
    public String getFileName();

    /**
     * @return редактируемый файл.
     */
    public Path getEditFile();

    /**
     * Открыть файл на редактирование.
     *
     * @param file редактируемый файл.
     */
    public void openFile(final Path file);

    /**
     * @return изменялся ли документ.
     */
    public BooleanProperty dirtyProperty();

    /**
     * @return изменялся ли документ.
     */
    public boolean isDirty();

    /**
     * Сохранить изменения.
     */
    public void doSave();

}
