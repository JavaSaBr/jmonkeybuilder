package com.ss.editor;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Реализация запускатора приложения.
 *
 * @author Ronn
 */
public class Starter extends Application {

    public static void main(final String[] args) throws IOException {
        try {
            Editor.start(args);
        } catch (final Throwable e) {
            printError(e);
            System.exit(-1);
        }
    }

    private static void printError(final Throwable throwable) {

        final String userHome = System.getProperty("user.home");
        final String fileName = "jme3-spaceshift-editor-error.log";

        try (final PrintStream out = new PrintStream(Files.newOutputStream(Paths.get(userHome, fileName)))) {
            throwable.printStackTrace(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
    }
}
