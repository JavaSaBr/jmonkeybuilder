package com.ss.editor.ui.component.split.pane;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Objects.requireNonNull;
import com.ss.editor.ui.component.tab.TabToolComponent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link SplitPane} for the {@link TabToolComponent}.
 *
 * @author JavaSaBr
 */
public abstract class TabToolSplitPane<C> extends SplitPane {

    /**
     * The scene.
     */
    @NotNull
    protected final Scene scene;

    /**
     * The stored config.
     */
    @Nullable
    protected C config;

    /**
     * The tool component.
     */
    @Nullable
    protected TabToolComponent toolComponent;

    /**
     * The width.
     */
    private int width;

    /**
     * The flag of collapsing the tool.
     */
    private boolean collapsed;

    protected TabToolSplitPane(@NotNull final Scene scene, @Nullable C config) {
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
    private boolean isCollapsed() {
        return collapsed;
    }

    /**
     * @return the tool component.
     */
    @NotNull
    private TabToolComponent getToolComponent() {
        return requireNonNull(toolComponent);
    }

    /**
     * @return the config.
     */
    @NotNull
    public C getConfig() {
        return requireNonNull(config);
    }

    /**
     * Init this split pane for the tool component.
     */
    public void initFor(@NotNull final TabToolComponent toolComponent, @NotNull final Node other) {
        this.toolComponent = toolComponent;
        addElements(toolComponent, other);
        addListeners(toolComponent);
        bindToScene();
        update();
    }

    /**
     * Add with listener to handle width changes.
     */
    protected void addListeners(@NotNull final TabToolComponent toolComponent) {
        toolComponent.widthProperty()
                .addListener((observable, oldValue, newValue) -> handleToolChanged(newValue));
    }

    /**
     * Bind to the scene.
     */
    protected void bindToScene() {
        scene.widthProperty()
                .addListener((observableValue, oldValue, newValue) -> handleSceneChanged(getSceneWidth()));
    }

    /**
     * Add an element to the tool component.
     *
     * @param toolComponent the tool component.
     * @param other         the element.
     */
    protected void addElements(final @NotNull TabToolComponent toolComponent, final @NotNull Node other) {
        getItems().addAll(toolComponent, other);
    }

    /**
     * Update.
     */
    protected void update() {
        if (config == null) return;

        if (isCollapsed()) {
            getToolComponent().collapse();
        }

        handleSceneChanged(getSceneWidth());
    }

    /**
     * Handle changing tool's width.
     */
    protected void handleToolChanged(@NotNull final Number newValue) {
        if (config == null) return;

        this.collapsed = toolComponent.isCollapsed();
        saveCollapsed(collapsed);

        if (!isCollapsed()) {
            int abs = Math.abs(width - newValue.intValue());
            width = abs > 2 ? newValue.intValue() : width;
            saveWidth(width);
        }

        toolComponent.setExpandPosition(getExpandPosition(width, getSceneWidth()));
        handleSceneChanged(getSceneWidth());
    }

    /**
     * @return the scene width.
     */
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

    /**
     * @return the collapsed position.
     */
    protected double getCollapsedPosition() {
        return 0;
    }

    /**
     * @return the divider index.
     */
    private int getDividerIndex() {
        return 0;
    }

    /**
     * Calculate an expand position.
     */
    protected double getExpandPosition(final double toolWidth, final double sceneWidth) {
        return min(1, max(0.1, toolWidth / sceneWidth));
    }
}
