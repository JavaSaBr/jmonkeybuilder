package com.ss.editor.ui.component.creator;

import com.ss.editor.ui.component.creator.impl.MaterialFileCreator;
import com.ss.editor.ui.component.creator.impl.PostFilterViewFileCreator;

import java.nio.file.Path;
import java.util.concurrent.Callable;

import rlib.logging.Logger;
import rlib.logging.LoggerManager;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * Реестр создателей файлов.
 *
 * @author Ronn
 */
public class FileCreatorRegistry {

    private static final Logger LOGGER = LoggerManager.getLogger(FileCreatorRegistry.class);

    private static final FileCreatorRegistry INSTANCE = new FileCreatorRegistry();

    public static FileCreatorRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * Список описаний создателей файлов.
     */
    private final Array<FileCreatorDescription> descriptions;

    public FileCreatorRegistry() {
        this.descriptions = ArrayFactory.newArray(FileCreatorDescription.class);
        addDescription(MaterialFileCreator.DESCRIPTION);
        addDescription(PostFilterViewFileCreator.DESCRIPTION);
    }

    /**
     * Добавление нового описания создателя контента.
     */
    private void addDescription(final FileCreatorDescription description) {
        this.descriptions.add(description);
    }

    /**
     * @return список описаний создателей файлов.
     */
    public Array<FileCreatorDescription> getDescriptions() {
        return descriptions;
    }

    /**
     * Создание нового создателя файлов по указанному описанию.
     *
     * @param description описание создателя контента.
     * @param file        файл, в котором надо создать новый файл.
     * @return создатель контента.
     */
    public FileCreator newCreator(final FileCreatorDescription description, final Path file) {

        final Callable<FileCreator> constructor = description.getConstructor();

        try {
            return constructor.call();
        } catch (Exception e) {
            LOGGER.warning(e);
        }

        return null;
    }
}
