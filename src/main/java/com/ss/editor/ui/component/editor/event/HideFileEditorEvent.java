package com.ss.editor.ui.component.editor.event;

import org.jetbrains.annotations.NotNull;

/**
 * The event about the file editor was hide.
 *
 * @author JavaSaBr
 */
public class HideFileEditorEvent extends AbstractFileEditorEvent {

    private static final HideFileEditorEvent INSTANCE = new HideFileEditorEvent();

    private static @NotNull HideFileEditorEvent getInstance() {
        return INSTANCE;
    }
}
