package com.ss.editor.ui.builder;

import com.jme3x.jfx.JmeFxContainer;
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
import rlib.ui.util.FXUtils;

import static javafx.geometry.Pos.TOP_CENTER;
import static javafx.scene.paint.Color.TRANSPARENT;
import static rlib.ui.util.FXUtils.bindFixedSize;

/**
 * Реализация конструктора UI сцены редактора.
 *
 * @author Ronn
 */
public class EditorFXSceneBuilder {

    /**
     * Фаил для переопределения стандартных стилей.
     */
    public static final String CSS_FILE_BASE = "/ui/css/base.css";

    /**
     * Фаил для переопределения чтилей из внешних библиотек.
     */
    public static final String CSS_FILE_EXTERNAL = "/ui/css/external.css";

    /**
     * Фаил для описания стилей для своих id.
     */
    public static final String CSS_FILE_CUSTOM_IDS = "/ui/css/custom_ids.css";

    /**
     * Фаил для описания стилей своих классов.
     */
    public static final String CSS_FILE_CUSTOM_CLASSES = "/ui/css/custom_classes.css";

    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    public static EditorFXScene build(final JmeFxContainer fxContainer) {

        for (final CSSFont font : CSSFont.FONTS) {
            Font.loadFont(EditorUtil.getInputStream(font.getPath()), font.getSize());
        }

        final Group root = new Group();

        final EditorFXScene scene = new EditorFXScene(root);
        scene.setFill(TRANSPARENT);

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

        fxContainer.setScene(scene, root);

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

        final Runnable resizeHandler = () -> {
            splitContainer.setDividerPosition(0, 0.01);
        };

        scene.widthProperty().addListener((observableValue, oldValue, newValue) -> resizeHandler.run());
        scene.heightProperty().addListener((observableValue, oldValue, newValue) -> resizeHandler.run());
    }
}
