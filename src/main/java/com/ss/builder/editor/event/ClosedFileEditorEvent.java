package com.ss.builder.editor.event;

import org.jetbrains.annotations.NotNull;

/**
 * The event about the file editor was closed.
 *
 * @author JavaSaBr
 */
public class ClosedFileEditorEvent extends AbstractFileEditorEvent {

    private static final ClosedFileEditorEvent INSTANCE = new ClosedFileEditorEvent();

    public static @NotNull ClosedFileEditorEvent getInstance() {
        return INSTANCE;
    }
}
