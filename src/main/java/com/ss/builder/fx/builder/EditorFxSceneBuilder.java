package com.ss.builder.ui.builder;

import static com.ss.editor.config.DefaultSettingsProvider.Defaults.PREF_DEFAULT_THEME;
import static com.ss.editor.config.DefaultSettingsProvider.Preferences.PREF_UI_THEME;
import com.ss.builder.Messages;
import com.ss.builder.annotation.BackgroundThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.config.DefaultSettingsProvider;
import com.ss.builder.config.EditorConfig;
import com.ss.builder.manager.AsyncEventManager;
import com.ss.builder.manager.ExecutorManager;
import com.ss.builder.ui.component.asset.AssetComponent;
import com.ss.builder.ui.component.bar.EditorMenuBarComponent;
import com.ss.builder.ui.component.editor.area.EditorAreaComponent;
import com.ss.builder.ui.component.log.LogView;
import com.ss.builder.ui.component.split.pane.GlobalBottomToolSplitPane;
import com.ss.builder.ui.component.split.pane.GlobalLeftToolSplitPane;
import com.ss.builder.ui.component.tab.GlobalBottomToolComponent;
import com.ss.builder.ui.component.tab.GlobalLeftToolComponent;
import com.ss.builder.ui.css.CssClasses;
import com.ss.builder.ui.event.EventRedirector;
import com.ss.builder.ui.event.impl.*;
import com.ss.builder.ui.scene.EditorFxScene;
import com.ss.editor.Messages;
import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.manager.AsyncEventManager;
import com.ss.editor.manager.AsyncEventManager.CombinedAsyncEventHandlerBuilder;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.ui.component.asset.AssetComponent;
import com.ss.editor.ui.component.bar.EditorMenuBarComponent;
import com.ss.editor.ui.component.editor.area.EditorAreaComponent;
import com.ss.editor.ui.component.log.LogView;
import com.ss.editor.ui.component.split.pane.GlobalBottomToolSplitPane;
import com.ss.editor.ui.component.split.pane.GlobalLeftToolSplitPane;
import com.ss.editor.ui.component.tab.GlobalBottomToolComponent;
import com.ss.editor.ui.component.tab.GlobalLeftToolComponent;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.event.EventRedirector;
import com.ss.editor.ui.event.impl.*;
import com.ss.editor.ui.scene.EditorFxScene;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.fx.util.FxUtils;
import javafx.scene.Group;
import javafx.scene.control.Menu;
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

    private static final Logger LOGGER = LoggerManager.getLogger(EditorFxSceneBuilder.class);

    /**
     * Build the Editor's main scene.
     *
     * @param window the main window.
     * @return the main scene.
     */
    @FxThread
    public static @NotNull EditorFxScene build(@NotNull Stage window) {

        // init FX classes
        var t1 = Menu.ON_SHOWN;

        var editorConfig = EditorConfig.getInstance();
        var theme = editorConfig.getEnum(DefaultSettingsProvider.Preferences.PREF_UI_THEME, DefaultSettingsProvider.Defaults.PREF_DEFAULT_THEME);

        var root = new Group();
        var scene = new EditorFxScene(root);
        scene.setFill(theme.getBackgroundColor());
        scene.setRoot(root);
        scene.incrementLoading();

        AsyncEventManager.CombinedAsyncEventHandlerBuilder.of(() -> build(scene, window))
                .add(FxSceneCreatedEvent.EVENT_TYPE)
                .add(ImageSystemInitializedEvent.EVENT_TYPE)
                .buildAndRegister();

        AsyncEventManager.CombinedAsyncEventHandlerBuilder.of(() -> attachScene(window, scene))
                .add(CssAppliedEvent.EVENT_TYPE)
                .buildAndRegister();

        LOGGER.info("created an empty scene.");

        return scene;
    }

    /**
     * Attach the created scene to the window.
     *
     * @param window the window.
     * @param scene  the scene.
     */
    @BackgroundThread
    private static void attachScene(@NotNull Stage window, @NotNull EditorFxScene scene) {
        ExecutorManager.getInstance()
                .addFxTask(() -> attachSceneInFx(window, scene));
    }

    @FxThread
    private static void attachSceneInFx(@NotNull Stage window, @NotNull EditorFxScene scene) {

        window.setScene(scene);

        LOGGER.info("the main scene is attached.");

        AsyncEventManager.getInstance()
                .notify(new FxSceneAttachedEvent());
    }

    /**
     * Build main scene components in background.
     *
     * @param scene  the main scene.
     * @param window the main window.
     */
    @BackgroundThread
    private static void build(@NotNull EditorFxScene scene, @NotNull Stage window) {

        var container = scene.getContainer();
        var canvas = scene.getCanvas();
        var barComponent = new EditorMenuBarComponent();
        var editorAreaComponent = new EditorAreaComponent();

        new EventRedirector(editorAreaComponent, canvas, window);

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

        leftSplitContainer.prefWidthProperty()
                .bind(container.widthProperty());
        barComponent.prefWidthProperty()
                .bind(container.widthProperty());

        LOGGER.info("created the main components.");

        ExecutorManager.getInstance()
                .addFxTask(() -> finishBuildingScene(container, barComponent, leftSplitContainer));
    }

    @FxThread
    private static void finishBuildingScene(
            @NotNull StackPane container,
            @NotNull EditorMenuBarComponent barComponent,
            @NotNull GlobalLeftToolSplitPane leftSplitContainer
    ) {

        FxUtils.addChild(container, new VBox(barComponent, leftSplitContainer));

        LOGGER.info("put main components to the main scene.");

        AsyncEventManager.getInstance()
                .notify(new FxContextCreatedEvent());
    }
}
