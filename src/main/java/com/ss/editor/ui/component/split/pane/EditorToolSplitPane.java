package com.ss.editor.ui.component.split.pane;

import com.ss.editor.annotation.FXThread;
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
    @FXThread
    public void initFor(@NotNull final TabToolComponent toolComponent, @NotNull final Node other) {
        super.initFor(toolComponent, other);
        root.widthProperty().addListener((observableValue, oldValue, newValue) -> handleSceneChanged(getSceneSize()));
    }

    @Override
    @FXThread
    protected void handleSceneChanged(@NotNull final Number newSize) {
        super.handleSceneChanged(newSize);
        Platform.runLater(this::requestLayout);
    }

    @Override
    @FXThread
    protected void bindToScene() {
    }

    @Override
    @FXThread
    protected void addElements(@NotNull final TabToolComponent toolComponent, @NotNull final Node other) {
        getItems().setAll(other, toolComponent);
    }

    @Override
    @FXThread
    protected boolean loadCollapsed() {
        return getConfig().isToolCollapsed();
    }

    @Override
    @FXThread
    protected int loadSize() {
        return getConfig().getToolWidth();
    }

    @Override
    @FXThread
    protected void saveCollapsed(final boolean collapsed) {
        getConfig().setToolCollapsed(collapsed);
    }

    @Override
    @FXThread
    protected void saveSize(final int size) {
        getConfig().setToolWidth(size);
    }

    @Override
    @FXThread
    protected double getCollapsedPosition() {
        return 1;
    }

    @Override
    @FXThread
    protected double getSceneSize() {
        final double width = root.getWidth();
        return width == 0 ? scene.getWidth() : width;
    }

    @Override
    @FXThread
    protected double getExpandPosition(final double toolSize, final double sceneSize) {
        return 1D - super.getExpandPosition(toolSize, sceneSize);
    }
}
