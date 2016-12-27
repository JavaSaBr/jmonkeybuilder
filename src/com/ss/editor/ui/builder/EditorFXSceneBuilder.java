package com.ss.editor.ui.builder;

import static javafx.geometry.Pos.TOP_CENTER;
import static javafx.scene.paint.Color.TRANSPARENT;

import com.ss.editor.Messages;
import com.ss.editor.ui.component.asset.AssetComponent;
import com.ss.editor.ui.component.bar.EditorBarComponent;
import com.ss.editor.ui.component.editor.area.EditorAreaComponent;
import com.ss.editor.ui.component.log.LogView;
import com.ss.editor.ui.component.split.pane.GlobalToolSplitPane;
import com.ss.editor.ui.component.tab.GlobalToolComponent;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.event.EventRedirector;
import com.ss.editor.ui.scene.EditorFXScene;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import rlib.ui.util.FXUtils;

/**
 * The scene builder for building a scene for the Editor.
 *
 * @author JavaSaBr.
 */
public class EditorFXSceneBuilder {

    public static final Insets BAR_OFFSET = new Insets(34, 0, 0, 0);

    /**
     * The path to the base CSS styles.
     */
    public static final String CSS_FILE_BASE = "/ui/css/base.bss";

    /**
     * The path to the external CSS styles.
     */
    public static final String CSS_FILE_EXTERNAL = "/ui/css/external.bss";

    /**
     * The path to the custom ids CSS styles.
     */
    public static final String CSS_FILE_CUSTOM_IDS = "/ui/css/custom_ids.bss";

    /**
     * The path to the custom classes CSS styles.
     */
    public static final String CSS_FILE_CUSTOM_CLASSES = "/ui/css/custom_classes.bss";

    public static EditorFXScene build(final Stage stage) {

        final Group root = new Group();
        //root.getTransforms().add(new Scale(1.5, 1.5));

        final EditorFXScene scene = new EditorFXScene(root);
        scene.setFill(TRANSPARENT);
        scene.setRoot(root);

        final ObservableList<String> stylesheets = scene.getStylesheets();
        stylesheets.add(CSS_FILE_BASE);
        stylesheets.add(CSS_FILE_EXTERNAL);
        stylesheets.add(CSS_FILE_CUSTOM_IDS);
        stylesheets.add(CSS_FILE_CUSTOM_CLASSES);

        final StackPane container = scene.getContainer();
        container.setAlignment(TOP_CENTER);

        build(scene, container, stage);
        //bindFixedSize(container, scene.widthProperty().divide(1.5), scene.heightProperty().divide(1.5));

        stage.setScene(scene);

        return scene;
    }

    private static void build(final EditorFXScene scene, final StackPane container, final Stage stage) {

        final Canvas canvas = scene.getCanvas();
        final EditorBarComponent barComponent = new EditorBarComponent();
        final EditorAreaComponent editorAreaComponent = new EditorAreaComponent();

        new EventRedirector(editorAreaComponent, canvas, stage);

        final GlobalToolSplitPane splitContainer = new GlobalToolSplitPane(scene);
        splitContainer.setId(CSSIds.MAIN_SPLIT_PANEL);

        final GlobalToolComponent globalToolComponent = new GlobalToolComponent(splitContainer);
        globalToolComponent.addComponent(new AssetComponent(), Messages.EDITOR_TOOL_ASSET);
        globalToolComponent.addComponent(LogView.getInstance(), Messages.LOG_VIEW_TITLE);

        splitContainer.initFor(globalToolComponent, editorAreaComponent);

        FXUtils.addToPane(splitContainer, container);
        FXUtils.addToPane(barComponent, container);

        barComponent.createDrawer(container, stage);
        barComponent.toFront();

        FXUtils.bindFixedHeight(splitContainer, container.heightProperty().subtract(BAR_OFFSET.getTop()).add(2));
        FXUtils.bindFixedWidth(splitContainer, container.widthProperty());
        FXUtils.bindFixedWidth(barComponent, container.widthProperty());

        StackPane.setMargin(splitContainer, BAR_OFFSET);
    }
}
