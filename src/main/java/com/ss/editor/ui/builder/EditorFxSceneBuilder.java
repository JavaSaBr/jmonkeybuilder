package com.ss.editor.ui.builder;

import static com.ss.editor.config.DefaultSettingsProvider.Defaults.PREF_DEFAULT_THEME;
import static com.ss.editor.config.DefaultSettingsProvider.Preferences.PREF_UI_THEME;
import static javafx.scene.paint.Color.TRANSPARENT;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.ui.component.asset.AssetComponent;
import com.ss.editor.ui.component.bar.EditorMenuBarComponent;
import com.ss.editor.ui.component.editor.area.EditorAreaComponent;
import com.ss.editor.ui.component.log.LogView;
import com.ss.editor.ui.component.split.pane.GlobalBottomToolSplitPane;
import com.ss.editor.ui.component.split.pane.GlobalLeftToolSplitPane;
import com.ss.editor.ui.component.tab.GlobalBottomToolComponent;
import com.ss.editor.ui.component.tab.GlobalLeftToolComponent;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.css.CssRegistry;
import com.ss.editor.ui.event.EventRedirector;
import com.ss.editor.ui.scene.EditorFxScene;
import com.ss.rlib.fx.util.FxUtils;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

/**
 * The scene builder for building a scene for the Editor.
 *
 * @author JavaSaBr
 */
public class EditorFxSceneBuilder {

    private static final CssRegistry CSS_REGISTRY = CssRegistry.getInstance();

    /**
     * The path to the base CSS styles.
     */
    public static final String CSS_FILE_BASE = "ui/css/base.css";

    /**
     * The path to the external CSS styles.
     */
    public static final String CSS_FILE_EXTERNAL = "ui/css/external.css";

    /**
     * The path to the custom ids CSS styles.
     */
    public static final String CSS_FILE_CUSTOM_IDS = "ui/css/custom_ids.css";

    /**
     * The path to the custom classes CSS styles.
     */
    public static final String CSS_FILE_CUSTOM_CLASSES = "ui/css/custom_classes.css";

    static {
        CSS_REGISTRY.register(CSS_FILE_BASE, EditorFxSceneBuilder.class.getClassLoader());
        CSS_REGISTRY.register(CSS_FILE_EXTERNAL, EditorFxSceneBuilder.class.getClassLoader());
        CSS_REGISTRY.register(CSS_FILE_CUSTOM_IDS, EditorFxSceneBuilder.class.getClassLoader());
        CSS_REGISTRY.register(CSS_FILE_CUSTOM_CLASSES, EditorFxSceneBuilder.class.getClassLoader());
    }

    /**
     * Build editor fx scene.
     *
     * @param stage the stage
     * @return the editor fx scene
     */
    @FxThread
    public static @NotNull EditorFxScene build(@NotNull Stage stage) {

        var editorConfig = EditorConfig.getInstance();
        var theme = editorConfig.getEnum(PREF_UI_THEME, PREF_DEFAULT_THEME);

        var root = new Group();

        var scene = new EditorFxScene(root);
        scene.setFill(TRANSPARENT);
        scene.setRoot(root);

        var stylesheets = scene.getStylesheets();
        stylesheets.addAll(CSS_REGISTRY.getAvailableCssFiles());
        stylesheets.add(theme.getCssFile());

        var container = scene.getContainer();

        build(scene, container, stage);

        stage.setScene(scene);

        return scene;
    }

    @FxThread
    private static void build(@NotNull EditorFxScene scene, @NotNull StackPane container, @NotNull Stage stage) {

        var canvas = scene.getCanvas();
        var barComponent = new EditorMenuBarComponent();
        var editorAreaComponent = new EditorAreaComponent();

        new EventRedirector(editorAreaComponent, canvas, stage);

        var leftSplitContainer = new GlobalLeftToolSplitPane(scene);
        leftSplitContainer.prefHeightProperty()
                .bind(container.heightProperty());

        var bottomSplitContainer = new GlobalBottomToolSplitPane(scene);
        var globalLeftToolComponent = new GlobalLeftToolComponent(leftSplitContainer);
        globalLeftToolComponent.addComponent(new AssetComponent(), Messages.EDITOR_TOOL_ASSET);

        var globalBottomToolComponent = new GlobalBottomToolComponent(bottomSplitContainer);
        globalBottomToolComponent.addComponent(LogView.getInstance(), Messages.LOG_VIEW_TITLE);

        leftSplitContainer.initFor(globalLeftToolComponent, bottomSplitContainer);
        bottomSplitContainer.initFor(globalBottomToolComponent, editorAreaComponent);

        FxUtils.addClass(leftSplitContainer, bottomSplitContainer,
                CssClasses.MAIN_SPLIT_PANEL);

        FxUtils.addChild(container, new VBox(barComponent, leftSplitContainer));

        leftSplitContainer.prefWidthProperty()
                .bind(container.widthProperty());
        barComponent.prefWidthProperty()
                .bind(container.widthProperty());
    }
}
