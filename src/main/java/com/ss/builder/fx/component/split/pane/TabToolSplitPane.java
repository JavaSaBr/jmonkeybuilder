package com.ss.builder.ui.component.split.pane;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import static java.lang.Math.max;
import static java.lang.Math.min;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.manager.ExecutorManager;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.ui.component.tab.TabToolComponent;
import com.ss.rlib.fx.util.ObservableUtils;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link SplitPane} for the {@link TabToolComponent}.
 *
 * @param <C> the config's type.
 * @author JavaSaBr
 */
public abstract class TabToolSplitPane<C> extends SplitPane {

    protected static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

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
     * The size.
     */
    private int size;

    /**
     * The flag of collapsing the tool.
     */
    private boolean collapsed;

    protected TabToolSplitPane(@NotNull Scene scene, @Nullable C config) {
        this.scene = scene;
        this.config = config;
        if (config != null) {
            this.size = loadSize();
            this.collapsed = loadCollapsed();
        }
    }

    /**
     * Update this pane to use the new config.
     *
     * @param config the config.
     */
    @FxThread
    public void updateFor(@NotNull C config) {
        this.config = config;
        this.size = loadSize();
        this.collapsed = loadCollapsed();
        update();
    }

    /**
     * Load stored collapsed state of this pane.
     *
     * @return true of this pane was collapsed.
     */
    @FxThread
    protected boolean loadCollapsed() {
        return false;
    }

    /**
     * Load stored size of this pane.
     *
     * @return the stored size of this pane.
     */
    @FxThread
    protected int loadSize() {
        return 1;
    }

    /**
     * Return true if the tool is collapsed.
     *
     * @return true if the tool is collapsed.
     */
    @FxThread
    private boolean isCollapsed() {
        return collapsed;
    }

    /**
     * Get the tool component.
     *
     * @return the tool component.
     */
    @FxThread
    private @NotNull TabToolComponent getToolComponent() {
        return notNull(toolComponent);
    }

    /**
     * Get the current config.
     *
     * @return the current config.
     */
    @FxThread
    public @NotNull C getConfig() {
        return notNull(config);
    }

    /**
     * Init this split pane for the tool component.
     *
     * @param toolComponent the tool component.
     * @param another       the another node.
     */
    @FxThread
    public void initFor(@NotNull TabToolComponent toolComponent, @NotNull Node another) {
        this.toolComponent = toolComponent;
        addElements(toolComponent, another);
        addListeners(toolComponent);
        bindToScene();
        update();
    }

    /**
     * Add a width listener to handle changes of the tool component's size.
     *
     * @param toolComponent the tool component.
     */
    @FxThread
    protected void addListeners(@NotNull TabToolComponent toolComponent) {
        ObservableUtils.onChange(toolComponent.widthProperty(), this::handleToolChanged);
    }

    /**
     * Bind to the scene.
     */
    @FxThread
    protected void bindToScene() {
        ObservableUtils.onChange(scene.widthProperty(),
                () -> handleSceneChanged(getSceneSize()));
    }

    /**
     * Add the tool component to this pane.
     *
     * @param toolComponent the tool component.
     * @param other         the element.
     */
    @FxThread
    protected void addElements(@NotNull TabToolComponent toolComponent, @NotNull Node other) {
        getItems().addAll(toolComponent, other);
    }

    /**
     * Update.
     */
    protected void update() {

        if (config == null) {
            return;
        }

        if (isCollapsed()) {
            getToolComponent().collapse();
        }

        handleSceneChanged(getSceneSize());
    }

    /**
     * Handle changing tool's size.
     *
     * @param newValue the new value
     */
    protected void handleToolChanged(@NotNull Number newValue) {

        if (config == null) {
            return;
        }

        var toolComponent = getToolComponent();

        this.collapsed = toolComponent.isCollapsed();

        storeCollapsed(isCollapsed());

        if (!isCollapsed()) {
            int abs = Math.abs(size - newValue.intValue());
            size = abs > 2 ? newValue.intValue() : size;
            storeSize(size);
        }

        toolComponent.setExpandPosition(getExpandPosition(size, getSceneSize()));
    }

    /**
     * Get the current scene size.
     *
     * @return the scene size.
     */
    protected double getSceneSize() {
        return scene.getWidth();
    }

    /**
     * Store the current collapsed state.
     *
     * @param collapsed the current collapsed state.
     */
    protected void storeCollapsed(boolean collapsed) {
    }

    /**
     * Save the current size.
     *
     * @param size the current size.
     */
    protected void storeSize(int size) {
    }

    /**
     * Handle changing scene's size.
     *
     * @param newSize the new size
     */
    protected void handleSceneChanged(@NotNull Number newSize) {

        if (config == null) {
            return;
        } else if (isCollapsed()) {
            setDividerPosition(getDividerIndex(), getCollapsedPosition());
            return;
        }

        setDividerPosition(getDividerIndex(), getExpandPosition(size, newSize.doubleValue()));
    }

    /**
     * Get the collapsed position.
     *
     * @return the collapsed position.
     */
    protected double getCollapsedPosition() {
        return 0;
    }

    /**
     * Get the divider index.
     *
     * @return the divider index.
     */
    private int getDividerIndex() {
        return 0;
    }

    /**
     * Calculate an expand position.
     *
     * @param toolSize  the tool size
     * @param sceneSize the scene size
     * @return the expand position
     */
    protected double getExpandPosition(double toolSize, double sceneSize) {
        return min(1, max(0.1, toolSize / sceneSize));
    }
}
