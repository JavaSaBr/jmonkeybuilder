package com.ss.editor.ui.event.impl;

import com.ss.editor.file.converter.FileConverterDescription;
import com.ss.editor.ui.event.SceneEvent;

import java.nio.file.Path;

import javafx.event.EventType;

/**
 * Событие об запроса на конвертирование файла.
 *
 * @author Ronn
 */
public class RequestedConvertFileEvent extends SceneEvent {

    public static final EventType<SceneEvent> EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, RequestedConvertFileEvent.class.getSimpleName());

    public static final String FILE = "file";
    public static final String CONVERTER = "converter";

    public RequestedConvertFileEvent() {
        super(EVENT_TYPE);
    }

    /**
     * @return описание конвертера.
     */
    public FileConverterDescription getDescription() {
        return get(CONVERTER);
    }

    /**
     * @param description описание конвертера.
     */
    public void setDescription(final FileConverterDescription description) {
        set(CONVERTER, description);
    }

    /**
     * @return конвертируемый файл.
     */
    public Path getFile() {
        return get(FILE);
    }

    /**
     * @param file конвертируемый файл.
     */
    public void setFile(final Path file) {
        set(FILE, file);
    }
}
