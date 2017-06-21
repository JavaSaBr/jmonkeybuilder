package com.ss.editor.ui.component.split.pane;

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

    /**
     * Instantiates a new Editor tool split pane.
     *
     * @param scene the scene
     * @param root  the root
     */
    public EditorToolSplitPane(@NotNull final Scene scene, @NotNull final Region root) {
        super(scene, null);
        this.root = root;
    }

    @Override
    public void initFor(@NotNull final TabToolComponent toolComponent, @NotNull final Node other) {
        super.initFor(toolComponent, other);
        root.widthProperty().addListener((observableValue, oldValue, newValue) -> handleSceneChanged(getSceneSize()));
    }

    @Override
    protected void handleSceneChanged(@NotNull final Number newSize) {
        super.handleSceneChanged(newSize);
        Platform.runLater(this::requestLayout);
    }

    @Override
    protected void bindToScene() {
    }

    @Override
    protected void addElements(final @NotNull TabToolComponent toolComponent, final @NotNull Node other) {
        getItems().setAll(other, toolComponent);
    }

    @Override
    protected boolean loadCollapsed() {
        return getConfig().isToolCollapsed();
    }

    @Override
    protected int loadSize() {
        return getConfig().getToolWidth();
    }

    @Override
    protected void saveCollapsed(final boolean collapsed) {
        getConfig().setToolCollapsed(collapsed);
    }

    @Override
    protected void saveSize(final int size) {
        getConfig().setToolWidth(size);
    }

    @Override
    protected double getCollapsedPosition() {
        return 1;
    }

    @Override
    protected double getSceneSize() {
        final double width = root.getWidth();
        return width == 0 ? scene.getWidth() : width;
    }

    @Override
    protected double getExpandPosition(final double toolSize, final double sceneSize) {
        return 1D - super.getExpandPosition(toolSize, sceneSize);
    }
}
