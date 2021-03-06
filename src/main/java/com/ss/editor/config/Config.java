package com.ss.editor.config;

import com.ss.editor.JmeApplication;
import com.ss.editor.document.DocumentConfig;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.common.plugin.Version;
import com.ss.rlib.common.util.Utils;
import com.ss.rlib.common.util.os.OperatingSystem;
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
    private static final String EDITOR_FOLDER_IN_USER_HOME = ".jmonkeybuilder";

    /**
     * The editor's title.
     */
    @NotNull
    public static final String TITLE = "jMonkeyBuilder";

    /**
     * The editor's version.
     */
    @NotNull
    public static final Version APP_VERSION = new Version("1.8.0");

    /**
     * The string version.
     */
    @NotNull
    public static final String STRING_VERSION = APP_VERSION.toString();

    /**
     * The server API version.
     */
    @NotNull
    public static final int SERVER_API_VERSION = 1;

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
     * The remote control port.
     */
    public static int REMOTE_CONTROL_PORT = -1;

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

    /**
     * The flag to enable 3D part of this editor.
     */
    public static boolean ENABLE_3D;

    static {

        var graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        var device = graphicsEnvironment.getDefaultScreenDevice();

        var vars = new DocumentConfig(EditorUtil.requireInputStream(CONFIG_RESOURCE_PATH))
                .parse();

        DEV_DEBUG = vars.getBoolean("Dev.debug", false);
        DEV_CAMERA_DEBUG = vars.getBoolean("Dev.cameraDebug", false);
        DEV_TRANSFORMS_DEBUG = vars.getBoolean("Dev.transformsDebug", false);
        DEV_DEBUG_JFX_MOUSE_INPUT = vars.getBoolean("Dev.jfxMouseInput", false);
        DEV_DEBUG_JFX_KEY_INPUT = vars.getBoolean("Dev.jfxKeyInput", false);
        DEV_DEBUG_JFX = vars.getBoolean("Dev.debugJFX", false);
        ENABLE_PBR = vars.getBoolean("Graphics.enablePBR", true);
        ENABLE_3D = vars.getBoolean("Graphics.enable3D", true);

        GRAPHICS_DEVICE = device;
        OPERATING_SYSTEM = new OperatingSystem();

        PROJECT_PATH = Utils.getRootFolderFromClass(JmeApplication.class).toString();
    }

    /**
     * Get a folder to store log files.
     *
     * @return the path to a folder to store log files.
     */
    public static @NotNull Path getFolderForLog() {
        return getAppFolderInUserHome().resolve("log");
    }

    /**
     * Get a path to the folder to store data in a user home.
     *
     * @return the path to the folder to store data in a user home.
     */
    public static @NotNull Path getAppFolderInUserHome() {
        var userHome = System.getProperty("user.home");
        return Paths.get(userHome, EDITOR_FOLDER_IN_USER_HOME);
    }
}
