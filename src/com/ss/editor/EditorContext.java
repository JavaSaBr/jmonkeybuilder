package com.ss.editor;

import com.jme3.system.AppSettings;
import com.jme3.system.NativeLibraryLoader;
import com.jme3.system.lwjgl.LwjglDisplay;

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
    private EditorThread thread;

    @Override
    public void create(final boolean waitFor) {

        if ("LWJGL".equals(settings.getAudioRenderer())) {
            NativeLibraryLoader.loadNativeLibrary("openal-lwjgl3", true);
        }

        NativeLibraryLoader.loadNativeLibrary("lwjgl3", true);
        NativeLibraryLoader.loadNativeLibrary("glfw-lwjgl3", true);
        NativeLibraryLoader.loadNativeLibrary("jemalloc-lwjgl3", true);
        NativeLibraryLoader.loadNativeLibrary("jinput", true);
        NativeLibraryLoader.loadNativeLibrary("jinput-dx8", true);

        if (created.get()) return;

        thread = new EditorThread(this);
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.setName("LWJGL Renderer Thread");
        thread.start();

        if (waitFor) waitFor(true);
    }

    /**
     * @return игровой поток рендера экрана.
     */
    public EditorThread getThread() {
        return thread;
    }

    @Override
    protected void initContextFirstTime() {
        settings.setRenderer(AppSettings.LWJGL_OPENGL3);
        super.initContextFirstTime();
    }
}
