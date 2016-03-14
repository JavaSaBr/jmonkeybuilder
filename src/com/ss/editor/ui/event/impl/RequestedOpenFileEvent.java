package com.ss.editor.ui.event.impl;

import com.ss.editor.ui.component.editor.EditorDescription;
import com.ss.editor.ui.event.SceneEvent;

import java.nio.file.Path;

import javafx.event.EventType;

/**
 * Событие об запроса на открытие файла.
 *
 * @author Ronn
 */
public class RequestedOpenFileEvent extends SceneEvent {

    public static final EventType<SceneEvent> EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, RequestedOpenFileEvent.class.getSimpleName());

    public static final String FILE = "file";
    public static final String EDITOR = "editor";
    public static final String NEED_SHOW = "need_show";

    public RequestedOpenFileEvent() {
        super(EVENT_TYPE);
        setNeedShow(true);
    }

    /**
     * @return описание редактора.
     */
    public EditorDescription getDescription() {
        return get(EDITOR);
    }

    /**
     * @param description описание редактора.
     */
    public void setDescription(final EditorDescription description) {
        set(EDITOR, description);
    }

    /**
     * @return открываемый файл.
     */
    public Path getFile() {
        return get(FILE);
    }

    /**
     * @param file открываемый файл.
     */
    public void setFile(final Path file) {
        set(FILE, file);
    }

    /**
     * @return нужно ли переходить в этот редактор.
     */
    public boolean isNeedShow() {
        return get(NEED_SHOW);
    }

    /**
     * @param needShow нужно ли переходить в этот редактор.
     */
    public void setNeedShow(final boolean needShow) {
        set(NEED_SHOW, needShow);
    }
}
