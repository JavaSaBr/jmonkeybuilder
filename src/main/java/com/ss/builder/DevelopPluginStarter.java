package com.ss.builder;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * The start class to develop plugins.
 *
 * @author JavaSaBr
 */
public class DevelopPluginStarter {

    public static void main(@NotNull String[] args) throws IOException {
        var embeddedFolder = Paths.get("./embedded-plugins");
        System.setProperty("editor.embedded.plugins.path", embeddedFolder.toAbsolutePath().toString());
        JfxApplication.main(args);
    }
}
