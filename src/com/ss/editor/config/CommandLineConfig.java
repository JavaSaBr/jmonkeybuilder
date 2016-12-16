package com.ss.editor.config;

/**
 * Parser the configuration from command-line arguments.
 *
 * @author JavaSaBr
 */
public class CommandLineConfig {

    public static boolean decorated = false;

    public static void args(final String[] args) {
        for (final String arg : args) {
            if (arg.startsWith("--decorated")) {
                decorated = true;
            }
        }
    }
}
