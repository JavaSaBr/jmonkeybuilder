package com.ss.builder.editor.event;

import org.jetbrains.annotations.NotNull;

/**
 * The interface to implement a file editor's event.
 *
 * @author Alex Brui
 */
public interface FileEditorEvent {

    @NotNull Object getSource();
}
