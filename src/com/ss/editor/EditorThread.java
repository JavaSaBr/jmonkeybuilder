package com.ss.editor;

import com.ss.editor.util.LocalObjects;

/**
 * Модель потока редактора.
 *
 * @author Ronn
 */
public class EditorThread extends Thread {

    private final LocalObjects localObects;

    public EditorThread() {
        this.localObects = new LocalObjects();
    }

    public EditorThread(final Runnable target) {
        super(target);

        this.localObects = new LocalObjects();
    }

    public EditorThread(final ThreadGroup group, final Runnable target, final String name) {
        super(group, target, name);

        this.localObects = new LocalObjects();
    }

    /**
     * @return контейнер локальных объектов.
     */
    public LocalObjects getLocalObects() {
        return localObects;
    }
}
