package com.ss.editor;

import org.lwjgl.LWJGLException;
import org.sample.client.util.LocalObjects;

/**
 * Модель игрового потока.
 *
 * @author Ronn
 */
public class EditorThread extends Thread {

    private final LocalObjects localObects;

    public EditorThread() {
        this.localObects = new LocalObjects();
    }

    public EditorThread(final Runnable target) throws LWJGLException {
        super(target);

        this.localObects = new LocalObjects();
    }

    public EditorThread(final ThreadGroup group, final Runnable target, final String name) throws LWJGLException {
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
