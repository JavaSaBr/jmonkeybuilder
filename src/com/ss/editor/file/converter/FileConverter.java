package com.ss.editor.file.converter;

import java.nio.file.Path;

/**
 * Интерфейс для реализации конвертера файлов.
 *
 * @author Ronn
 */
public interface FileConverter {

    /**
     * Конвертация указанного файла.
     *
     * @param source исходный файл для конвертации.
     */
    public void convert(Path source);

    /**
     * Конвертация указанного файла и запись в другой указанный файл.
     *
     * @param source      исходный файл для конвертации.
     * @param destination файл для записи результата.
     */
    public void convert(Path source, Path destination);


    /**
     * @return итоговый формат.
     */
    public String getTargetExtension();
}
