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

        //ThreadUtils.sleep(15000);

        try {
            Editor.start(args);
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
    }
}
