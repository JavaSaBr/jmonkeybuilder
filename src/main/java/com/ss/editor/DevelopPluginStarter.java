package com.ss.editor;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The start class to develop plugins.
 *
 * @author JavaSaBr
 */
public class DevelopPluginStarter {

    public static void main(@NotNull final String[] args) throws IOException {
        final Path embeddedFolder = Paths.get("./embedded-plugins");
        System.setProperty("editor.embedded.plugins.path", embeddedFolder.toAbsolutePath().toString());
        JFXApplication.main(args);
    }
}
