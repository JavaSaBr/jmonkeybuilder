package com.ss.editor.config;

import com.ss.editor.Editor;
import com.ss.editor.document.DocumentConfig;
import com.ss.editor.util.AppVersion;
import com.ss.editor.util.EditorUtil;
import org.jetbrains.annotations.NotNull;
import com.ss.rlib.util.Utils;
import com.ss.rlib.util.VarTable;
import com.ss.rlib.util.os.OperatingSystem;

import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The configuration of this application.
 *
 * @author JavaSaBr
 */
public abstract class Config {

    @NotNull
    private static final String CONFIG_RESOURCE_PATH = "/com/ss/editor/config/config.xml";

    @NotNull
    private static final String SS_FOLDER_IN_USER_HOME = ".jme3-spaceshift-editor";

    /**
     * The constant TITLE.
     */
    @NotNull
    public static final String TITLE = "jME3 SpaceShift Editor";

    /**
     * The constant APP_VERSION.
     */
    @NotNull
    public static final AppVersion APP_VERSION = new AppVersion("0.9.10");

    /**
     * The constant STRING_VERSION.
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
     * Flag is for showing debug.
     */
    public static final boolean DEV_DEBUG;

    /**
     * Flag is for showing debug of the JavaFX.
     */
    public static final boolean DEV_DEBUG_JFX;

    /**
     * Flag is for activating the PBR.
     */
    public static final boolean ENABLE_PBR;

    static {

        final GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice device = graphicsEnvironment.getDefaultScreenDevice();

        final VarTable vars = new DocumentConfig(EditorUtil.getInputStream(CONFIG_RESOURCE_PATH)).parse();

        DEV_DEBUG = vars.getBoolean("Dev.debug", false);
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
    @NotNull
    public static Path getFolderForLog() {
        return getAppFolderInUserHome().resolve("log");
    }

    /**
     * The path to the folder for storing data in the user home.
     *
     * @return the app folder in user home
     */
    @NotNull
    public static Path getAppFolderInUserHome() {
        final String userHome = System.getProperty("user.home");
        return Paths.get(userHome, SS_FOLDER_IN_USER_HOME);
    }
}
