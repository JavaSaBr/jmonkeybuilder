package com.ss.editor.config;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Paths;
import java.util.Map;

/**
 * Parser the configuration from command-line arguments.
 *
 * @author JavaSaBr
 */
public class CommandLineConfig {

    @NotNull
    public static final String PREF_SERVER_API_PORT = "Server.api.port";

    @NotNull
    public static final String PREF_SERVER_API_VERSION = "Server.api.version";

    @NotNull
    public static final String PREF_EDITOR_ASSET_FOLDER = "Editor.assetFolder";

    /**
     * @param args the args
     */
    public static void args(final String[] args) {

        final EditorConfig editorConfig = EditorConfig.getInstance();

        for (final String arg : args) {

            if (!arg.contains("=")) {
                continue;
            }

            final String[] values = arg.split("=");
            if (values.length != 2) {
                continue;
            }

            final String name = values[0];
            final String value = values[1];

            if ("Dev.debug".equals(name)) {
                Config.DEV_DEBUG = Boolean.parseBoolean(value);
            } else if ("Dev.cameraDebug".equals(name)) {
                Config.DEV_CAMERA_DEBUG = Boolean.parseBoolean(value);
            } else if ("Dev.transformsDebug".equals(name)) {
                Config.DEV_TRANSFORMS_DEBUG = Boolean.parseBoolean(value);
            } else if ("Dev.jfxMouseInput".equals(name)) {
                Config.DEV_DEBUG_JFX_MOUSE_INPUT = Boolean.parseBoolean(value);
            } else if ("Dev.jfxKeyInput".equals(name)) {
                Config.DEV_DEBUG_JFX_KEY_INPUT = Boolean.parseBoolean(value);
            } else if ("Dev.debugJFX".equals(name)) {
                Config.DEV_DEBUG_JFX = Boolean.parseBoolean(value);
            } else if ("Graphics.enablePBR".equals(name)) {
                Config.ENABLE_PBR = Boolean.parseBoolean(value);
            } else if (PREF_SERVER_API_PORT.equals(name)) {
                Config.REMOTE_CONTROL_PORT = Integer.parseInt(value);
            } else if (PREF_EDITOR_ASSET_FOLDER.equals(name)) {
                editorConfig.setCurrentAsset(Paths.get(value));
            }
        }

        final Map<String, String> env = System.getenv();

        if (env.containsKey(PREF_SERVER_API_VERSION)) {
            final int version = Integer.parseInt(env.get(PREF_SERVER_API_VERSION));
            if (version == Config.SERVER_API_VERSION) {
                System.exit(100);
            } else {
                System.exit(-1);
            }
        }

        if (env.containsKey(PREF_SERVER_API_PORT)) {
            Config.REMOTE_CONTROL_PORT = Integer.parseInt(env.get(PREF_SERVER_API_PORT));
        }
        if (env.containsKey(PREF_EDITOR_ASSET_FOLDER)) {
            editorConfig.setCurrentAsset(Paths.get(env.get(PREF_EDITOR_ASSET_FOLDER)));
        }
    }
}
