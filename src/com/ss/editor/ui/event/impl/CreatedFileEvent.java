package com.ss.editor.ui.event.impl;

import com.ss.editor.ui.event.SceneEvent;

import java.nio.file.Path;

import javafx.event.EventType;

/**
 * Событие об создании нового файла в Asset.
 *
 * @author Ronn
 */
public class CreatedFileEvent extends SceneEvent {

    public static final EventType<SceneEvent> EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, CreatedFileEvent.class.getSimpleName());

    public static final String FILE = "file";
    public static final String NEED_SELECT = "need_select";

    public CreatedFileEvent() {
        super(EVENT_TYPE);
    }

    /**
     * @param needSelect нужно ли выбрать этот файл.
     */
    public void setNeedSelect(boolean needSelect) {
        set(NEED_SELECT, needSelect);
    }

    /**
     * @return нужно ли выбрать этот файл.
     */
    public boolean isNeedSelect() {
        return get(NEED_SELECT) == Boolean.TRUE;
    }

    /**
     * @return новый файл.
     */
    public Path getFile() {
        return get(FILE);
    }

    /**
     * @param file новый файл.
     */
    public void setFile(final Path file) {
        set(FILE, file);
    }
}
