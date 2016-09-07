package com.ss.editor;

import com.ss.editor.util.LocalObjects;

/**
 * The implementation of the {@link Thread} for this application.
 *
 * @author JavaSaBr
 */
public class EditorThread extends Thread {

    /**
     * The container of thread local objects.
     */
    private final LocalObjects localObjects;

    public EditorThread() {
        this.localObjects = new LocalObjects();
    }

    public EditorThread(final Runnable target) {
        super(target);
        this.localObjects = new LocalObjects();
    }

    public EditorThread(final ThreadGroup group, final Runnable target, final String name) {
        super(group, target, name);
        this.localObjects = new LocalObjects();
    }

    /**
     * Get the container of thread local objects.
     *
     * @return the container of thread local objects.
     */
    public LocalObjects getLocal() {
        return localObjects;
    }
}
