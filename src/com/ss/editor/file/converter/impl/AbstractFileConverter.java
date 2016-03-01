package com.ss.editor.file.converter.impl;

import com.ss.editor.Editor;
import com.ss.editor.file.converter.FileConverter;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.CreatedFileEvent;
import com.ss.editor.ui.event.impl.FileChangedEvent;

import java.nio.file.Files;
import java.nio.file.Path;

import rlib.logging.Logger;
import rlib.logging.LoggerManager;
import rlib.util.FileUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

import static rlib.util.FileUtils.containsExtensions;

/**
 * Базовая реализация конвертера файлов.
 *
 * @author Ronn
 */
public abstract class AbstractFileConverter implements FileConverter {

    protected static final Logger LOGGER = LoggerManager.getLogger(FileConverter.class);

    private static final Array<String> EMPTY_ARRAY = ArrayFactory.newArray(String.class);

    protected static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();
    protected static final Editor EDITOR = Editor.getInstance();

    @Override
    public void convert(final Path source) {

        final String targetFileName = FileUtils.getNameWithoutExtension(source) + "." + getTargetExtension();

        final Path parent = source.getParent();
        final Path targetFile = parent.resolve(targetFileName);

        convert(source, targetFile);
    }

    @Override
    public void convert(final Path source, final Path destination) {

        if (source == null || destination == null) {
            throw new IllegalArgumentException("source or destination is null.");
        } else if (Files.isDirectory(source) || Files.isDirectory(destination)) {
            throw new IllegalArgumentException("source or destination is folder.");
        }

        final Array<String> extensions = getAvailableExtensions();
        extensions.trimToSize();

        if (!extensions.isEmpty() && !containsExtensions(extensions.array(), source)) {
            throw new IllegalArgumentException("incorrect extension of file " + source);
        }

        convertImpl(source, destination, Files.exists(destination));
    }

    protected void convertImpl(final Path source, final Path destination, final boolean overwrite) {
    }

    /**
     * @return список доступных расширений для конвертации.
     */
    protected Array<String> getAvailableExtensions() {
        return EMPTY_ARRAY;
    }

    @Override
    public String getTargetExtension() {
        return "";
    }

    /**
     * Уведомление всех об изменении файла.
     *
     * @param file изменяемый файл.
     */
    protected void notifyFileChanged(final Path file) {

        final FileChangedEvent event = new FileChangedEvent();
        event.setFile(file);

        FX_EVENT_MANAGER.notify(event);
    }

    /**
     * Уведомление всех об создании файла.
     *
     * @param file созданный файл.
     */
    protected void notifyFileCreated(final Path file) {

        final CreatedFileEvent event = new CreatedFileEvent();
        event.setFile(file);

        FX_EVENT_MANAGER.notify(event);
    }
}
