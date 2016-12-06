package com.ss.editor.ui.builder;

import static javafx.geometry.Pos.TOP_CENTER;
import static javafx.scene.paint.Color.TRANSPARENT;
import static rlib.ui.util.FXUtils.bindFixedSize;

import com.ss.editor.config.EditorConfig;
import com.ss.editor.config.ScreenSize;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.ui.component.asset.AssetComponent;
import com.ss.editor.ui.component.bar.EditorBarComponent;
import com.ss.editor.ui.component.editor.area.EditorAreaComponent;
import com.ss.editor.ui.css.CSSFont;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.scene.EditorFXScene;
import com.ss.editor.util.EditorUtil;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import rlib.ui.util.FXUtils;

/**
 * The scene builder for building a scene for the Editor.
 *
 * @author JavaSaBr.
 */
public class EditorFXSceneBuilder {

    /**
     * The path to the base CSS styles.
     */
    public static final String CSS_FILE_BASE = "/ui/css/base.css";

    /**
     * The path to the external CSS styles.
     */
    public static final String CSS_FILE_EXTERNAL = "/ui/css/external.css";

    /**
     * The path to the custom ids CSS styles.
     */
    public static final String CSS_FILE_CUSTOM_IDS = "/ui/css/custom_ids.css";

    /**
     * The path to the custom classes CSS styles.
     */
    public static final String CSS_FILE_CUSTOM_CLASSES = "/ui/css/custom_classes.css";

    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();
    private static final EditorConfig EDITOR_CONFIG = EditorConfig.getInstance();

    public static EditorFXScene build(final Stage stage) {

        for (final CSSFont font : CSSFont.FONTS) {
            Font.loadFont(EditorUtil.getInputStream(font.getPath()), font.getSize());
        }

        final ScreenSize screenSize = EDITOR_CONFIG.getScreenSize();

        final Group root = new Group();

        final EditorFXScene scene = new EditorFXScene(root, screenSize.getWidth(), screenSize.getHeight());
        scene.setFill(TRANSPARENT);
        scene.setRoot(root);

        final ObservableList<String> stylesheets = scene.getStylesheets();
        stylesheets.add(CSS_FILE_BASE);
        stylesheets.add(CSS_FILE_EXTERNAL);
        stylesheets.add(CSS_FILE_CUSTOM_IDS);
        stylesheets.add(CSS_FILE_CUSTOM_CLASSES);

        final StackPane container = scene.getContainer();
        container.setAlignment(TOP_CENTER);

        build(scene, container);
        bindFixedSize(container, scene.widthProperty(), scene.heightProperty());

        EXECUTOR_MANAGER.schedule(() -> EXECUTOR_MANAGER.addFXTask(scene::notifyFinishBuild), 500);

        stage.setScene(scene);

        return scene;
    }

    private static void build(final EditorFXScene scene, final StackPane container) {

        final EditorBarComponent barComponent = new EditorBarComponent();

        final AssetComponent assetComponent = new AssetComponent();
        final EditorAreaComponent editorAreaComponent = new EditorAreaComponent();

        final SplitPane splitContainer = new SplitPane(assetComponent, editorAreaComponent);
        splitContainer.setId(CSSIds.MAIN_SPLIT_PANEL);

        FXUtils.addToPane(splitContainer, container);
        FXUtils.addToPane(barComponent, container);

        FXUtils.bindFixedHeight(splitContainer, container.heightProperty().subtract(barComponent.heightProperty()).add(2));
        FXUtils.bindFixedWidth(splitContainer, container.widthProperty());
        FXUtils.bindFixedWidth(barComponent, container.widthProperty());

        barComponent.heightProperty().addListener((observable, oldValue, newValue) -> StackPane.setMargin(splitContainer, new Insets(barComponent.getHeight() - 2, 0, 0, 0)));

        final Runnable resizeHandler = () -> splitContainer.setDividerPosition(0, 0.01);

        scene.widthProperty().addListener((observableValue, oldValue, newValue) -> resizeHandler.run());
        scene.heightProperty().addListener((observableValue, oldValue, newValue) -> resizeHandler.run());
    }
}
