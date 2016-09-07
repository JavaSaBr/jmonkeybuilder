package com.ss.editor;

import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;
import com.jme3.system.NativeLibraryLoader;
import com.jme3.system.lwjgl.LwjglDisplay;

import rlib.logging.Logger;
import rlib.logging.LoggerManager;

/**
 * The implementation of the {@link JmeContext} for this application based on {@link LwjglDisplay}.
 *
 * @author JavaSaBr
 */
public final class EditorContext extends LwjglDisplay {

    protected static final Logger LOGGER = LoggerManager.getLogger(EditorContext.class);

    /**
     * The main thread of this application.
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

    @Override
    protected void createContext(final AppSettings settings) {
        settings.setRenderer(AppSettings.LWJGL_OPENGL3);
        super.createContext(settings);
    }
}
