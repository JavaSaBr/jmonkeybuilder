package com.ss.editor.ui.component.split.pane;

import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.component.editor.state.EditorToolConfig;
import com.ss.editor.ui.component.tab.GlobalLeftToolComponent;
import com.ss.editor.ui.component.tab.TabToolComponent;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Region;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link SplitPane} for the {@link GlobalLeftToolComponent}.
 *
 * @author JavaSaBr
 */
public class EditorToolSplitPane extends TabToolSplitPane<EditorToolConfig> {

    /**
     * The root of the editor.
     */
    @NotNull
    private final Region root;

    public EditorToolSplitPane(@NotNull final Scene scene, @NotNull final Region root) {
        super(scene, null);
        this.root = root;
    }

    @Override
    @FxThread
    public void initFor(@NotNull final TabToolComponent toolComponent, @NotNull final Node other) {
        super.initFor(toolComponent, other);
        root.widthProperty().addListener((observableValue, oldValue, newValue) -> handleSceneChanged(getSceneSize()));
    }

    @Override
    @FxThread
    protected void handleSceneChanged(@NotNull final Number newSize) {
        super.handleSceneChanged(newSize);
        Platform.runLater(this::requestLayout);
    }

    @Override
    @FxThread
    protected void bindToScene() {
    }

    @Override
    @FxThread
    protected void addElements(@NotNull final TabToolComponent toolComponent, @NotNull final Node other) {
        getItems().setAll(other, toolComponent);
    }

    @Override
    @FxThread
    protected boolean loadCollapsed() {
        return getConfig().isToolCollapsed();
    }

    @Override
    @FxThread
    protected int loadSize() {
        return getConfig().getToolWidth();
    }

    @Override
    @FxThread
    protected void saveCollapsed(final boolean collapsed) {
        getConfig().setToolCollapsed(collapsed);
    }

    @Override
    @FxThread
    protected void saveSize(final int size) {
        getConfig().setToolWidth(size);
    }

    @Override
    @FxThread
    protected double getCollapsedPosition() {
        return 1;
    }

    @Override
    @FxThread
    protected double getSceneSize() {
        final double width = root.getWidth();
        return width == 0 ? scene.getWidth() : width;
    }

    @Override
    @FxThread
    protected double getExpandPosition(final double toolSize, final double sceneSize) {
        return 1D - super.getExpandPosition(toolSize, sceneSize);
    }
}
