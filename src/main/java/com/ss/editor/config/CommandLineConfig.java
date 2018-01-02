package com.ss.editor.config;

import java.util.Map;

/**
 * Parser the configuration from command-line arguments.
 *
 * @author JavaSaBr
 */
public class CommandLineConfig {

    /**
     * @param args the args
     */
    public static void args(final String[] args) {
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
            }
        }

        System.out.println(System.getenv());

        final Map<String, String> env = System.getenv();
        if (env.containsKey("Server.api.version")) {
            final int version = Integer.parseInt(env.get("Server.api.version"));
            if (version == Config.SERVER_API_VERSION) {
                System.exit(100);
            } else {
                System.exit(-1);
            }
        }
    }
}
