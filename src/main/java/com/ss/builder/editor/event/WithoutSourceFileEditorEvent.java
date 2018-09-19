package com.ss.builder.editor.event;

/**
 * The event without any source.
 *
 * @author JavaSaBr
 */
public abstract class WithoutSourceFileEditorEvent extends AbstractFileEditorEvent<Object> {

    private static final Object SOURCE = new Object();

    protected WithoutSourceFileEditorEvent() {
        super(SOURCE);
    }
}
