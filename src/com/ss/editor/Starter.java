package com.ss.editor;

import java.io.IOException;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Реализация запускатора приложения.
 *
 * @author Ronn
 */
public class Starter extends Application {

    public static void main(String[] args) throws IOException {
        Editor.start(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
    }
}
