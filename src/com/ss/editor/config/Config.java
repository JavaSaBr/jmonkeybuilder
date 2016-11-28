package com.ss.editor.config;

import com.ss.editor.Editor;
import com.ss.editor.document.DocumentConfig;
import com.ss.editor.util.EditorUtil;

import java.nio.file.Path;
import java.nio.file.Paths;

import rlib.util.Util;
import rlib.util.VarTable;

/**
 * The configuration of this application.
 *
 * @author JavaSaBr
 */
public abstract class Config {

    public static final String CONFIG_RESOURCE_PATH = "/com/ss/editor/config/config.xml";

    public static final String TITLE = "jME3 SpaceShift Editor";
    public static final String VERSION = "v.0.7.6";

    public static final String SS_FOLDER_IN_USER_HOME = ".jme3-spaceshift-editor";

    /**
     * The path to application folder.
     */
    public static final String PROJECT_PATH;

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

        final VarTable vars = new DocumentConfig(EditorUtil.getInputStream(CONFIG_RESOURCE_PATH)).parse();

        DEV_DEBUG = vars.getBoolean("Dev.debug", false);
        DEV_DEBUG_JFX = vars.getBoolean("Dev.debugJFX", false);
        ENABLE_PBR = vars.getBoolean("Graphics.enablePBR", true);

        PROJECT_PATH = Util.getRootFolderFromClass(Editor.class).toString();
    }

    /**
     * @return the path to the folder for writing log files.
     */
    public static Path getFolderForLog() {
        return getAppFolderInUserHome().resolve("log");
    }

    /**
     * The path to the folder for storing data in the user home.
     */
    public static Path getAppFolderInUserHome() {
        final String userHome = System.getProperty("user.home");
        return Paths.get(userHome, SS_FOLDER_IN_USER_HOME);
    }
}
