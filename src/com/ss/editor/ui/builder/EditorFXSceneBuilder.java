package com.ss.editor.ui.builder;

import static javafx.scene.paint.Color.TRANSPARENT;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.ui.component.asset.AssetComponent;
import com.ss.editor.ui.component.bar.EditorMenuBarComponent;
import com.ss.editor.ui.component.editor.area.EditorAreaComponent;
import com.ss.editor.ui.component.log.LogView;
import com.ss.editor.ui.component.split.pane.GlobalBottomToolSplitPane;
import com.ss.editor.ui.component.split.pane.GlobalLeftToolSplitPane;
import com.ss.editor.ui.component.tab.GlobalBottomToolComponent;
import com.ss.editor.ui.component.tab.GlobalLeftToolComponent;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CssColorTheme;
import com.ss.editor.ui.event.EventRedirector;
import com.ss.editor.ui.scene.EditorFXScene;
import com.ss.editor.ui.util.UIUtils;
import com.ss.rlib.ui.util.FXUtils;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

/**
 * The scene builder for building a scene for the Editor.
 *
 * @author JavaSaBr.
 */
public class EditorFXSceneBuilder {

    /**
     * The path to the base CSS styles.
     */
    @NotNull
    public static final String CSS_FILE_BASE = "/ui/css/base.bss";

    /**
     * The path to the external CSS styles.
     */
    @NotNull
    public static final String CSS_FILE_EXTERNAL = "/ui/css/external.bss";

    /**
     * The path to the custom ids CSS styles.
     */
    @NotNull
    public static final String CSS_FILE_CUSTOM_IDS = "/ui/css/custom_ids.bss";

    /**
     * The path to the custom classes CSS styles.
     */
    @NotNull
    public static final String CSS_FILE_CUSTOM_CLASSES = "/ui/css/custom_classes.bss";

    /**
     * Build editor fx scene.
     *
     * @param stage the stage
     * @return the editor fx scene
     */
    @NotNull
    @FXThread
    public static EditorFXScene build(@NotNull final Stage stage) {

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final CssColorTheme theme = editorConfig.getTheme();

        final Group root = new Group();
        //root.getTransforms().add(new Scale(1.5, 1.5));

        final EditorFXScene scene = new EditorFXScene(root);
        scene.setFill(TRANSPARENT);
        scene.setRoot(root);

        final ObservableList<String> stylesheets = scene.getStylesheets();
        stylesheets.add(theme.getCssFile());
        stylesheets.add(CSS_FILE_BASE);
        stylesheets.add(CSS_FILE_EXTERNAL);
        stylesheets.add(CSS_FILE_CUSTOM_IDS);
        stylesheets.add(CSS_FILE_CUSTOM_CLASSES);

        final StackPane container = scene.getContainer();

        build(scene, container, stage);

        //TODO implement scalling
        //bindFixedSize(container, scene.widthProperty().divide(1.5), scene.heightProperty().divide(1.5));

        stage.setScene(scene);

        return scene;
    }

    private static void build(@NotNull final EditorFXScene scene,
                              @NotNull final StackPane container,
                              @NotNull final Stage stage) {

        final Canvas canvas = scene.getCanvas();
        final EditorMenuBarComponent barComponent = new EditorMenuBarComponent();
        final EditorAreaComponent editorAreaComponent = new EditorAreaComponent();

        new EventRedirector(editorAreaComponent, canvas, stage);

        final GlobalLeftToolSplitPane leftSplitContainer = new GlobalLeftToolSplitPane(scene);
        leftSplitContainer.prefHeightProperty().bind(container.heightProperty());

        final GlobalBottomToolSplitPane bottomSplitContainer = new GlobalBottomToolSplitPane(scene);
        final GlobalLeftToolComponent globalLeftToolComponent = new GlobalLeftToolComponent(leftSplitContainer);
        globalLeftToolComponent.addComponent(new AssetComponent(), Messages.EDITOR_TOOL_ASSET);

        final GlobalBottomToolComponent globalBottomToolComponent = new GlobalBottomToolComponent(bottomSplitContainer);
        globalBottomToolComponent.addComponent(LogView.getInstance(), Messages.LOG_VIEW_TITLE);

        leftSplitContainer.initFor(globalLeftToolComponent, bottomSplitContainer);
        bottomSplitContainer.initFor(globalBottomToolComponent, editorAreaComponent);

        FXUtils.addToPane(new VBox(barComponent, leftSplitContainer), container);

        FXUtils.bindFixedWidth(leftSplitContainer, container.widthProperty());
        FXUtils.bindFixedWidth(barComponent, container.widthProperty());

        FXUtils.addClassTo(leftSplitContainer, bottomSplitContainer, CSSClasses.MAIN_SPLIT_PANEL);

        UIUtils.overrideTooltipBehavior(100, 5000, 100);
    }
}
