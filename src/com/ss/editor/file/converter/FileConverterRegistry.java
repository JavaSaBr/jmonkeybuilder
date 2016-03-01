package com.ss.editor.file.converter;

import com.ss.editor.file.converter.impl.BlendToJ3oFileConverter;

import java.nio.file.Path;
import java.util.concurrent.Callable;

import rlib.logging.Logger;
import rlib.logging.LoggerManager;
import rlib.util.FileUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * Реестр конвертеров файлов.
 *
 * @author Ronn
 */
public class FileConverterRegistry {

    private static final Logger LOGGER = LoggerManager.getLogger(FileConverterRegistry.class);

    private static final FileConverterRegistry INSTANCE = new FileConverterRegistry();

    public static FileConverterRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * Список описаний конвертеров файлов.
     */
    private final Array<FileConverterDescription> descriptions;

    public FileConverterRegistry() {
        this.descriptions = ArrayFactory.newArray(FileConverterDescription.class);
        addDescription(BlendToJ3oFileConverter.DESCRIPTION);
    }

    /**
     * Добавление нового описания конвертера.
     */
    private void addDescription(final FileConverterDescription description) {
        this.descriptions.add(description);
    }

    /**
     * @return список описаний конвертеров файлов.
     */
    public Array<FileConverterDescription> getDescriptions() {
        return descriptions;
    }

    /**
     * @return список описаний конвертеров файлов подходящих под этот.
     */
    public Array<FileConverterDescription> getDescriptions(final Path path) {

        final Array<FileConverterDescription> result = ArrayFactory.newArray(FileConverterDescription.class);
        final Array<FileConverterDescription> descriptions = getDescriptions();
        descriptions.forEach(description -> {

            final Array<String> extensions = description.getExtensions();

            if (FileUtils.containsExtensions(extensions.array(), path)) {
                result.add(description);
            }
        });

        return result;
    }

    /**
     * Создание нового конвертера файлов по указанному описанию.
     *
     * @param description описание конвертера файлов.
     * @param file        файл который надо конвертировать.
     * @return новый конвертер.
     */
    public FileConverter newCreator(final FileConverterDescription description, final Path file) {

        final Callable<FileConverter> constructor = description.getConstructor();

        try {
            return constructor.call();
        } catch (Exception e) {
            LOGGER.warning(e);
        }

        return null;
    }
}
