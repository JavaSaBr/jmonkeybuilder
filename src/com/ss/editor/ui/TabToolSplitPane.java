package com.ss.editor.ui;

import static java.lang.Math.max;
import static java.lang.Math.min;

import com.ss.editor.ui.component.tab.TabToolComponent;

import org.jetbrains.annotations.NotNull;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;

/**
 * The implementation of the {@link SplitPane} for the {@link TabToolComponent}.
 *
 * @author JavaSaBr
 */
public abstract class TabToolSplitPane<C> extends SplitPane {

    /**
     * The scene.
     */
    protected final Scene scene;

    /**
     * The stored config.
     */
    protected final C config;

    /**
     * The tool component.
     */
    protected TabToolComponent toolComponent;

    /**
     * The width.
     */
    private int width;

    /**
     * Flag is for collapsing the tool.
     */
    private boolean collapsed;

    public TabToolSplitPane(@NotNull final Scene scene, @NotNull C config) {
        this.scene = scene;
        this.config = config;
        this.width = loadWidth();
        this.collapsed = loadCollapsed();
    }

    /**
     * @return the stored flag of collapsed.
     */
    protected boolean loadCollapsed() {
        return false;
    }

    /**
     * @return the stored width of the tool component.
     */
    protected int loadWidth() {
        return 1;
    }

    /**
     * @return true if the tool collapsed.
     */
    protected boolean isCollapsed() {
        return collapsed;
    }

    /**
     * Init this split pane for the tool component.
     */
    public void initFor(@NotNull final TabToolComponent toolComponent, @NotNull final Node other) {
        this.toolComponent = toolComponent;

        getItems().addAll(toolComponent, other);

        toolComponent.widthProperty().addListener((observable, oldValue, newValue) -> handleToolChanged(newValue));
        scene.widthProperty().addListener((observableValue, oldValue, newValue) -> handleSceneChanged(newValue));

        if (isCollapsed()) {
            toolComponent.collapse();
        }

        handleSceneChanged(scene.getWidth());
    }

    /**
     * Handle changing tool's width.
     */
    private void handleToolChanged(@NotNull final Number newValue) {
        this.collapsed = toolComponent.isCollapsed();
        saveCollapsed(collapsed);

        if (!isCollapsed()) {
            this.width = newValue.intValue();
            saveWidth(width);
        }

        toolComponent.setExpandPosition(getExpandPosition(width, scene.getWidth()));
        handleSceneChanged(scene.getWidth());
    }

    /**
     * Save the flag of collapsed.
     */
    protected void saveCollapsed(final boolean collapsed) {
    }

    /**
     * Save the width of the tool component.
     */
    protected void saveWidth(final int width) {
    }

    /**
     * Handle changing scene's width.
     */
    private void handleSceneChanged(@NotNull final Number newWidth) {
        if (isCollapsed()) return;
        setDividerPosition(0, getExpandPosition(width, newWidth.doubleValue()));
    }

    /**
     * Calculate an expand position.
     */
    private double getExpandPosition(final double toolWidth, final double sceneWidth) {
        return min(0.5, max(0.1, toolWidth / sceneWidth));
    }
}
