package com.ss.editor.config;

import com.ss.editor.Editor;
import com.ss.editor.document.DocumentConfig;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.plugin.Version;
import com.ss.rlib.util.Utils;
import com.ss.rlib.util.VarTable;
import com.ss.rlib.util.os.OperatingSystem;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The configuration of this application.
 *
 * @author JavaSaBr
 */
public final class Config {

    /**
     * The path to config file in classpath.
     */
    @NotNull
    private static final String CONFIG_RESOURCE_PATH = "/app-config.xml";

    /**
     * The name of editor's folder in user home folder.
     */
    @NotNull
    private static final String SS_FOLDER_IN_USER_HOME = ".jmonkeybuilder";

    /**
     * The editor's title.
     */
    @NotNull
    public static final String TITLE = "jMonkeyBuilder";

    /**
     * The editor's version.
     */
    @NotNull
    public static final Version APP_VERSION = new Version("1.4.0");

    /**
     * The string version.
     */
    @NotNull
    public static final String STRING_VERSION = APP_VERSION.toString();

    /**
     * The path to application folder.
     */
    @NotNull
    public static final String PROJECT_PATH;

    /**
     * The graphics adapter.
     */
    @NotNull
    public static final GraphicsDevice GRAPHICS_DEVICE;

    /**
     * The operation system.
     */
    @NotNull
    public static final OperatingSystem OPERATING_SYSTEM;

    /**
     * The flag to enable debug mode.
     */
    public static boolean DEV_DEBUG;

    /**
     * The flag to enable camera debug mode.
     */
    public static boolean DEV_CAMERA_DEBUG;

    /**
     * The flag to enable transformations debug mode.
     */
    public static boolean DEV_TRANSFORMS_DEBUG;

    /**
     * The flag to enable JavaFX debug mode.
     */
    public static boolean DEV_DEBUG_JFX;

    /**
     * The flag to enable JavaFX mouse input debug mode.
     */
    public static boolean DEV_DEBUG_JFX_MOUSE_INPUT;

    /**
     * The flag to enable javaFX key input debug mode.
     */
    public static boolean DEV_DEBUG_JFX_KEY_INPUT;

    /**
     * The flag to enable PBR render.
     */
    public static boolean ENABLE_PBR;

    static {

        final GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice device = graphicsEnvironment.getDefaultScreenDevice();

        final VarTable vars = new DocumentConfig(EditorUtil.getInputStream(CONFIG_RESOURCE_PATH)).parse();

        DEV_DEBUG = vars.getBoolean("Dev.debug", false);
        DEV_CAMERA_DEBUG = vars.getBoolean("Dev.cameraDebug", false);
        DEV_TRANSFORMS_DEBUG = vars.getBoolean("Dev.transformsDebug", false);
        DEV_DEBUG_JFX_MOUSE_INPUT = vars.getBoolean("Dev.jfxMouseInput", false);
        DEV_DEBUG_JFX_KEY_INPUT = vars.getBoolean("Dev.jfxKeyInput", false);
        DEV_DEBUG_JFX = vars.getBoolean("Dev.debugJFX", false);
        ENABLE_PBR = vars.getBoolean("Graphics.enablePBR", true);

        GRAPHICS_DEVICE = device;
        OPERATING_SYSTEM = new OperatingSystem();

        PROJECT_PATH = Utils.getRootFolderFromClass(Editor.class).toString();
    }

    /**
     * Gets folder for log.
     *
     * @return the path to the folder for writing log files.
     */
    public static @NotNull Path getFolderForLog() {
        return getAppFolderInUserHome().resolve("log");
    }

    /**
     * The path to the folder for storing data in the user home.
     *
     * @return the app folder in user home
     */
    public static @NotNull Path getAppFolderInUserHome() {
        final String userHome = System.getProperty("user.home");
        return Paths.get(userHome, SS_FOLDER_IN_USER_HOME);
    }
}
