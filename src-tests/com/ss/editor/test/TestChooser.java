package com.ss.editor.test;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;

/**
 * Created by Alex on 16.07.17.
 */
public class TestChooser extends Application {
    @Override
    public void start(final Stage stage) throws Exception {

        Button button = new Button("Click");

        Group root = new Group(button);
        Scene scene = new Scene(root);

        stage.initStyle(StageStyle.DECORATED);
        stage.setMinHeight(600);
        stage.setMinWidth(800);
        stage.setWidth(500);
        stage.setHeight(500);
        stage.setMaximized(false);
        stage.setTitle("Some title");
        stage.setScene(scene);
        stage.show();

        button.setOnAction(event -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Some title");
            chooser.setInitialDirectory(new File(System.getProperty("user.home")));
            File folder = chooser.showDialog(stage);
            System.out.println(folder);
        });
    }
}
