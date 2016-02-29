package com.ss.editor.ui.component.creator;

import java.nio.file.Path;

/**
 * Интерфейс для реализации создателя файлов.
 *
 * @author Ronn
 */
public interface FileCreator {

    /**
     * Запуск создателя для указанного файла.
     */
    public void start(final Path file);
}
