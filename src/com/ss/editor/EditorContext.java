package com.ss.editor;

import com.jme3.system.AppSettings;
import com.jme3.system.lwjgl.LwjglDisplay;

import org.lwjgl.LWJGLException;

import rlib.logging.Logger;
import rlib.logging.LoggerManager;

/**
 * Модель контекста игры.
 *
 * @author Ronn
 */
public final class EditorContext extends LwjglDisplay {

    protected static final Logger LOGGER = LoggerManager.getLogger(EditorContext.class);

    /**
     * Игровой поток рендера экрана.
     */
    private GameThread thread;

    @Override
    public void create(final boolean waitFor) {

        if (created.get()) {
            return;
        }

        try {
            thread = new GameThread(this);
            thread.setPriority(Thread.MAX_PRIORITY);
        } catch (final LWJGLException e) {
            LOGGER.warning(e);
        }

        thread.setName("LWJGL Renderer Thread");
        thread.start();

        if (waitFor) {
            waitFor(true);
        }
    }

    /**
     * @return игровой поток рендера экрана.
     */
    public GameThread getThread() {
        return thread;
    }

    @Override
    protected void initContextFirstTime() {
        settings.setRenderer(AppSettings.LWJGL_OPENGL3);
        super.initContextFirstTime();
    }
}
