package com.ss.builder.fx.component.editor.event;

import org.jetbrains.annotations.NotNull;

/**
 * The event about the file editor was showed.
 *
 * @author JavaSaBr
 */
public class ShowedFileEditorEvent extends AbstractFileEditorEvent {

    private static final ShowedFileEditorEvent INSTANCE = new ShowedFileEditorEvent();

    public static @NotNull ShowedFileEditorEvent getInstance() {
        return INSTANCE;
    }
}
