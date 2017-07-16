package com.ss.editor.util;

import com.jme3.system.AppSettings;
import org.jetbrains.annotations.NotNull;

/**
 * The list of available OpenGL versions.
 *
 * @author JavaSaBr
 */
public enum OpenGLVersion {
    GL_20(AppSettings.LWJGL_OPENGL2, "2.0 with any profile"),
    GL_32(AppSettings.LWJGL_OPENGL3, "3.2 with core profile"),
    GL_33(AppSettings.LWJGL_OPENGL33, "3.3 with core profile"),
    GL_40(AppSettings.LWJGL_OPENGL4, "4.0 with core profile"),
    GL_41(AppSettings.LWJGL_OPENGL41, "4.1 with core profile"),
    GL_42(AppSettings.LWJGL_OPENGL42, "4.2 with core profile"),
    GL_43(AppSettings.LWJGL_OPENGL43, "4.3 with core profile"),
    GL_44(AppSettings.LWJGL_OPENGL44, "4.4 with core profile"),
    GL_45(AppSettings.LWJGL_OPENGL45, "4.5 with core profile");

    @NotNull
    private static final OpenGLVersion[] VERSIONS = values();

    @NotNull
    public static OpenGLVersion valueOf(final int index) {
        return VERSIONS[index];
    }

    /**
     * The value for render.
     */
    @NotNull
    private final String render;

    /**
     * The label for UI.
     */
    @NotNull
    private final String label;

    OpenGLVersion(@NotNull final String render, @NotNull final String label) {
        this.render = render;
        this.label = label;
    }

    /**
     * @return the value for render.
     */
    @NotNull
    public String getRender() {
        return render;
    }

    /**
     * @return the label for UI.
     */
    @Override
    public String toString() {
        return label;
    }
}
