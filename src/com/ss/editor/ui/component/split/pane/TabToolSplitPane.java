package com.ss.editor.ui.component.split.pane;

import static java.lang.Math.max;
import static java.lang.Math.min;

import com.ss.editor.ui.component.tab.TabToolComponent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    protected C config;

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

    public TabToolSplitPane(@NotNull final Scene scene, @Nullable C config) {
        this.scene = scene;
        this.config = config;
        if (config != null) {
            this.width = loadWidth();
            this.collapsed = loadCollapsed();
        }
    }

    /**
     * Update this pane for the new config.
     */
    public void updateFor(@NotNull final C config) {
        this.config = config;
        this.width = loadWidth();
        this.collapsed = loadCollapsed();
        update();
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
        addElements(toolComponent, other);
        toolComponent.widthProperty().addListener((observable, oldValue, newValue) -> handleToolChanged(newValue));
        bindToScene();
        update();
    }

    protected void bindToScene() {
        scene.widthProperty().addListener((observableValue, oldValue, newValue) -> handleSceneChanged(getSceneWidth()));
    }

    protected void addElements(final @NotNull TabToolComponent toolComponent, final @NotNull Node other) {
        getItems().addAll(toolComponent, other);
    }

    protected void update() {
        if (config == null) return;

        if (isCollapsed()) {
            toolComponent.collapse();
        }

        handleSceneChanged(getSceneWidth());
    }

    /**
     * Handle changing tool's width.
     */
    private void handleToolChanged(@NotNull final Number newValue) {
        if (config == null) return;

        this.collapsed = toolComponent.isCollapsed();
        saveCollapsed(collapsed);

        if (!isCollapsed()) {
            this.width = newValue.intValue();
            saveWidth(width);
        }

        toolComponent.setExpandPosition(getExpandPosition(width, getSceneWidth()));
        handleSceneChanged(getSceneWidth());
    }

    protected double getSceneWidth() {
        return scene.getWidth();
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
    protected void handleSceneChanged(@NotNull final Number newWidth) {
        if (config == null) return;

        if (isCollapsed()) {
            setDividerPosition(getDividerIndex(), getCollapsedPosition());
            return;
        }

        setDividerPosition(getDividerIndex(), getExpandPosition(width, newWidth.doubleValue()));
    }

    protected double getCollapsedPosition() {
        return 0;
    }

    protected int getDividerIndex() {
        return 0;
    }

    /**
     * Calculate an expand position.
     */
    protected double getExpandPosition(final double toolWidth, final double sceneWidth) {
        return min(0.5, max(0.1, toolWidth / sceneWidth));
    }
}
