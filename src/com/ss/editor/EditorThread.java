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

    /**
     * Instantiates a new Editor thread.
     */
    public EditorThread() {
        this.localObjects = new LocalObjects();
    }

    /**
     * Instantiates a new Editor thread.
     *
     * @param target the target
     */
    public EditorThread(final Runnable target) {
        super(target);
        this.localObjects = new LocalObjects();
    }

    /**
     * Instantiates a new Editor thread.
     *
     * @param group  the group
     * @param target the target
     * @param name   the name
     */
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
