package com.ss.builder.fx.component.editor.event;

import org.jetbrains.annotations.NotNull;

/**
 * The event about the file editor was hide.
 *
 * @author JavaSaBr
 */
public class HideFileEditorEvent extends AbstractFileEditorEvent {

    private static final HideFileEditorEvent INSTANCE = new HideFileEditorEvent();

    public static @NotNull HideFileEditorEvent getInstance() {
        return INSTANCE;
    }
}
