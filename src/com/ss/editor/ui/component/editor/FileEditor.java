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
    Parent getPage();

    /**
     * @return название редактируемого файла.
     */
    String getFileName();

    /**
     * @return редактируемый файл.
     */
    Path getEditFile();

    /**
     * Открыть файл на редактирование.
     *
     * @param file редактируемый файл.
     */
    void openFile(final Path file);

    /**
     * @return изменялся ли документ.
     */
    BooleanProperty dirtyProperty();

    /**
     * @return изменялся ли документ.
     */
    boolean isDirty();

    /**
     * Сохранить изменения.
     */
    default void doSave() {
    }

    /**
     * @return 3D части редактора либо null.
     */
    default Array<EditorState> getStates() {
        return null;
    }

    /**
     * Уведомление редактора о том что его закрыли.
     */
    default void notifyClosed() {
    }

    /**
     * Уведомление о переименовании файла.
     */
    default void notifyRenamed(final Path prevFile, final Path newFile) {
    }

    /**
     * Уведомление о перемещении файла.
     */
    default void notifyMoved(final Path prevFile, final Path newFile) {
    }

    /**
     * @return описание редактора.
     */
    EditorDescription getDescription();

    /**
     * Уведомление о том, что этот редактор отобразили.
     */
    default void notifyShowed() {
    }

    /**
     * Уведомление об скрытии редактора.
     */
    default void notifyHided() {
    }
}
