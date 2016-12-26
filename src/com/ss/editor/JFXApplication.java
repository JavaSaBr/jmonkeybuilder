package com.ss.editor;

import static com.jme3x.jfx.injfx.JmeToJFXIntegrator.bind;
import static java.nio.file.Files.newOutputStream;

import com.jme3x.jfx.injfx.JmeToJFXApplication;
import com.ss.editor.analytics.google.GAEvent;
import com.ss.editor.analytics.google.GAnalytics;
import com.ss.editor.config.CommandLineConfig;
import com.ss.editor.config.Config;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.executor.impl.EditorThreadExecutor;
import com.ss.editor.manager.JMEFilePreviewManager;
import com.ss.editor.ui.builder.EditorFXSceneBuilder;
import com.ss.editor.ui.scene.EditorFXScene;

import de.codecentric.centerdevice.javafxsvg.SvgImageLoaderFactory;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
        return instance == null ? null : instance.stage;
    }

    public static void main(final String[] args) throws IOException {

        // fix of the fonts render
        //System.setProperty("prism.lcdtext", "false");
        //System.setProperty("prism.text", "t2k");

        // some settings for the render of JavaFX
        //System.setProperty("prism.cacheshapes", "true");
        //System.setProperty("prism.scrollcacheopt", "true");
        //System.setProperty("prism.allowhidpi", "true");

        //Logging.getCSSLogger().setLevel(PlatformLogger.Level.ALL);

        //System.setProperty("prism.order", "sw");
        //System.setProperty("prism.showdirty", "true");
        //System.setProperty("prism.showoverdraw", "true");
        //System.setProperty("prism.printrendergraph", "true");
        //System.setProperty("prism.debug", "true");
        //System.setProperty("prism.verbose", "true");

        CommandLineConfig.args(args);

        JmeToJFXApplication application;
        try {
            application = Editor.prepareToStart();
        } catch (final Throwable e) {
            printError(e);
            System.exit(-1);
            return;
        }

        new EditorThread(new ThreadGroup("LWJGL"), application::start, "LWJGL Render").start();
    }

    public static void start() {
        launch();
    }

    private static void printError(final Throwable throwable) {
        throwable.printStackTrace();

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

        SvgImageLoaderFactory.install();

        ImageIO.read(getClass().getResourceAsStream("/ui/icons/test/test.jpg"));

        final ObservableList<Image> icons = stage.getIcons();
        icons.add(new Image("/ui/icons/app/SSEd256.png"));
        icons.add(new Image("/ui/icons/app/SSEd128.png"));
        icons.add(new Image("/ui/icons/app/SSEd64.png"));
        icons.add(new Image("/ui/icons/app/SSEd32.png"));
        icons.add(new Image("/ui/icons/app/SSEd16.png"));

        final EditorConfig config = EditorConfig.getInstance();
        final boolean decorated = CommandLineConfig.decorated || config.isDecorated();

        stage.initStyle(decorated ? StageStyle.DECORATED : StageStyle.UNDECORATED);
        stage.setMinHeight(600);
        stage.setMinWidth(800);
        stage.setWidth(config.getScreenWidth());
        stage.setHeight(config.getScreenHeight());
        stage.setMaximized(config.isMaximized());
        stage.setTitle(Config.TITLE + " " + Config.VERSION);
        stage.setOnCloseRequest(event -> onExit());
        stage.show();

        if (!stage.isMaximized()) stage.centerOnScreen();

        stage.widthProperty().addListener((observable, oldValue, newValue) -> {
            if (stage.isMaximized()) return;
            config.setScreenWidth(newValue.intValue());
        });
        stage.heightProperty().addListener((observable, oldValue, newValue) -> {
            if (stage.isMaximized()) return;
            config.setScreenHeight(newValue.intValue());
        });

        stage.maximizedProperty().addListener((observable, oldValue, newValue) -> config.setMaximized(newValue));

        buildScene();
    }

    public void onExit() {

        GAnalytics.sendEvent(GAEvent.Category.APPLICATION,
                GAEvent.Action.CLOSED, GAEvent.Label.THE_EDITOR_APP_WAS_CLOSED);

        final EditorConfig config = EditorConfig.getInstance();
        config.save();

        final EditorThreadExecutor executor = EditorThreadExecutor.getInstance();
        executor.addToExecute(() -> {
            final Editor editor = Editor.getInstance();
            editor.destroy();
        });

        GAnalytics.waitForSend();
    }

    /**
     * Build the scene.
     */
    public void buildScene() {
        this.scene = EditorFXSceneBuilder.build(stage);
        this.scene.notifyFinishBuild();

        final Editor editor = Editor.getInstance();
        final EditorThreadExecutor executor = EditorThreadExecutor.getInstance();
        executor.addToExecute(() -> bind(editor, scene.getImageView(), editor.getViewPort()));

        JMEFilePreviewManager.getInstance();

        GAnalytics.sendEvent(GAEvent.Category.APPLICATION,
                GAEvent.Action.LAUNCHED, GAEvent.Label.THE_EDITOR_APP_WAS_LAUNCHED);
    }

    /**
     * @return the JavaFX scene.
     */
    public EditorFXScene getScene() {
        return scene;
    }
}
