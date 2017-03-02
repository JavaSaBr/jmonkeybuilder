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

    public EditorToolSplitPane(@NotNull final Scene scene, @NotNull final Region root) {
        super(scene, null);
        this.root = root;
    }

    @Override
    public void initFor(@NotNull final TabToolComponent toolComponent, @NotNull final Node other) {
        super.initFor(toolComponent, other);
        root.widthProperty().addListener((observableValue, oldValue, newValue) -> handleSceneChanged(getSceneWidth()));
    }

    @Override
    protected void handleSceneChanged(@NotNull final Number newWidth) {
        super.handleSceneChanged(newWidth);
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
    protected int loadWidth() {
        return getConfig().getToolWidth();
    }

    @Override
    protected void saveCollapsed(final boolean collapsed) {
        getConfig().setToolCollapsed(collapsed);
    }

    @Override
    protected void saveWidth(final int width) {
        getConfig().setToolWidth(width);
    }

    @Override
    protected double getCollapsedPosition() {
        return 1;
    }

    @Override
    protected double getSceneWidth() {
        final double width = root.getWidth();
        return width == 0 ? scene.getWidth() : width;
    }

    @Override
    protected double getExpandPosition(final double toolWidth, final double sceneWidth) {
        return 1D - super.getExpandPosition(toolWidth, sceneWidth);
    }
}
