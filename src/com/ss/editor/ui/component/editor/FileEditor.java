package com.ss.editor.ui.component.editor;

import com.ss.editor.state.editor.EditorState;

import java.nio.file.Path;

import javafx.beans.property.BooleanProperty;
import javafx.scene.Parent;
import rlib.util.array.Array;

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
    public default void doSave() {
    }

    /**
     * @return 3D части редактора либо null.
     */
    public default Array<EditorState> getStates() {
        return null;
    }

    /**
     * Уведомление редактора о том что его закрыли.
     */
    public default void notifyClosed() {
    }

    /**
     * Уведомление о переименовании файла.
     */
    public default void notifyRenamed(final Path prevFile, final Path newFile) {
    }

    /**
     * Уведомление о перемещении файла.
     */
    public default void notifyMoved(final Path prevFile, final Path newFile) {
    }

    /**
     * @return описание редактора.
     */
    public EditorDescription getDescription();

    /**
     * Уведомление о том, что этот редактор отобразили.
     */
    public default void notifyShowed() {
    }

    /**
     * Уведомление об скрытии редактора.
     */
    public default void notifyHided() {
    }
}
