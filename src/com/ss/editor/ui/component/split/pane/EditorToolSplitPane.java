package com.ss.editor.ui.component.split.pane;

import com.ss.editor.ui.component.editor.state.EditorToolConfig;
import com.ss.editor.ui.component.tab.GlobalToolComponent;
import com.ss.editor.ui.component.tab.TabToolComponent;

import org.jetbrains.annotations.NotNull;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Region;

/**
 * The implementation of the {@link SplitPane} for the {@link GlobalToolComponent}.
 *
 * @author JavaSaBr
 */
public class EditorToolSplitPane extends TabToolSplitPane<EditorToolConfig> {

    /**
     * The root of the editor.
     */
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
    protected void bindToScene() {
    }

    @Override
    protected void addElements(final @NotNull TabToolComponent toolComponent, final @NotNull Node other) {
        getItems().setAll(other, toolComponent);
    }

    @Override
    protected boolean loadCollapsed() {
        return config.isToolCollapsed();
    }

    @Override
    protected int loadWidth() {
        return config.getToolWidth();
    }

    @Override
    protected void saveCollapsed(final boolean collapsed) {
        config.setToolCollapsed(collapsed);
    }

    @Override
    protected void saveWidth(final int width) {
        config.setToolWidth(width);
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
