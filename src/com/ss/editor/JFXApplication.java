package com.ss.editor;

import static java.nio.file.Files.newOutputStream;

import com.jme3x.jfx.injfx.JmeToJFXApplication;
import com.ss.editor.config.CommandLineConfig;
import com.ss.editor.config.Config;
import com.ss.editor.executor.impl.EditorThreadExecutor;
import com.ss.editor.ui.builder.EditorFXSceneBuilder;
import com.ss.editor.ui.scene.EditorFXScene;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * The starter of the JavaFX application.
 *
 * @author JavaSaBr
 */
public class JFXApplication extends Application {

    private static JFXApplication instance;

    public static JFXApplication getInstance() {
        return instance;
    }

    public static Stage getStage() {
        return instance.stage;
    }

    public static void main(final String[] args) throws IOException {

        // fix of the fonts render
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("prism.text", "t2k");

        // some settings for the render of JavaFX
        System.setProperty("prism.vsync", "false");
        System.setProperty("javafx.animation.fullspeed", "false");
        System.setProperty("prism.cacheshapes", "true");

        CommandLineConfig.args(args);

        launch(args);
    }

    private static void printError(final Throwable throwable) {

        final String userHome = System.getProperty("user.home");
        final String fileName = "jme3-spaceshift-editor-error.log";

        try (final PrintStream out = new PrintStream(newOutputStream(Paths.get(userHome, fileName)))) {
            throwable.printStackTrace(out);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The JavaFX scene.
     */
    private volatile EditorFXScene scene;

    /**
     * The stage.
     */
    private Stage stage;

    @Override
    public void start(final Stage stage) throws Exception {
        JFXApplication.instance = this;
        this.stage = stage;

        JmeToJFXApplication application;
        try {
            application = Editor.prepareToStart();
        } catch (final Throwable e) {
            printError(e);
            System.exit(-1);
            return;
        }

        new EditorThread(application::start).start();

        final ObservableList<Image> icons = stage.getIcons();
        icons.add(new Image("/ui/icons/app/SSEd256.png"));
        icons.add(new Image("/ui/icons/app/SSEd128.png"));
        icons.add(new Image("/ui/icons/app/SSEd64.png"));
        icons.add(new Image("/ui/icons/app/SSEd32.png"));
        icons.add(new Image("/ui/icons/app/SSEd16.png"));

        stage.setTitle(Config.TITLE + " " + Config.VERSION);
        stage.setOnCloseRequest(event -> onExit());
        stage.show();
    }

    protected void onExit() {
        final EditorThreadExecutor executor = EditorThreadExecutor.getInstance();
        executor.addToExecute(() -> {
            final Editor editor = Editor.getInstance();
            editor.destroy();
        });
    }

    /**
     * Build the scene.
     */
    public void buildScene() {
        this.scene = EditorFXSceneBuilder.build(stage);
    }

    /**
     * @return the JavaFX scene.
     */
    public EditorFXScene getScene() {
        return scene;
    }
}
