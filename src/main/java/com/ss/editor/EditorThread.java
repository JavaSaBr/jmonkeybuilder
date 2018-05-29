package com.ss.editor;

import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.util.LocalObjects;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link Thread} for this application.
 *
 * @author JavaSaBr
 */
public class EditorThread extends Thread {

    /**
     * The container of thread local objects.
     */
    @NotNull
    private final LocalObjects localObjects;

    public EditorThread() {
        this.localObjects = new LocalObjects();
    }

    public EditorThread(@NotNull Runnable target) {
        super(target);
        this.localObjects = new LocalObjects();
    }

    public EditorThread(@NotNull ThreadGroup group, @NotNull Runnable target, @NotNull String name) {
        super(group, target, name);
        this.localObjects = new LocalObjects();
    }

    /**
     * Get the container of thread local objects.
     *
     * @return the container of thread local objects.
     */
    @FromAnyThread
    public @NotNull LocalObjects getLocal() {
        return localObjects;
    }
}
