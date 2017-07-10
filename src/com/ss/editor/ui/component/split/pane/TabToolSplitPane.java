package com.ss.editor.ui.component.split.pane;

import static com.ss.rlib.util.ObjectUtils.notNull;
import static java.lang.Math.max;
import static java.lang.Math.min;
import com.ss.editor.ui.component.tab.TabToolComponent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link SplitPane} for the {@link TabToolComponent}.
 *
 * @param <C> the type parameter
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
     * The size.
     */
    private int size;

    /**
     * The flag of collapsing the tool.
     */
    private boolean collapsed;

    /**
     * Instantiates a new Tab tool split pane.
     *
     * @param scene  the scene
     * @param config the config
     */
    protected TabToolSplitPane(@NotNull final Scene scene, @Nullable C config) {
        this.scene = scene;
        this.config = config;
        if (config != null) {
            this.size = loadSize();
            this.collapsed = loadCollapsed();
        }
    }

    /**
     * Update this pane for the new config.
     *
     * @param config the config
     */
    public void updateFor(@NotNull final C config) {
        this.config = config;
        this.size = loadSize();
        this.collapsed = loadCollapsed();
        update();
    }

    /**
     * Load collapsed boolean.
     *
     * @return the stored flag of collapsed.
     */
    protected boolean loadCollapsed() {
        return false;
    }

    /**
     * Load size int.
     *
     * @return the stored size of the tool component.
     */
    protected int loadSize() {
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
        return notNull(toolComponent);
    }

    /**
     * Gets config.
     *
     * @return the config.
     */
    @NotNull
    public C getConfig() {
        return notNull(config);
    }

    /**
     * Init this split pane for the tool component.
     *
     * @param toolComponent the tool component
     * @param other         the other
     */
    public void initFor(@NotNull final TabToolComponent toolComponent, @NotNull final Node other) {
        this.toolComponent = toolComponent;
        addElements(toolComponent, other);
        addListeners(toolComponent);
        bindToScene();
        update();
    }

    /**
     * Add with listener to handle size changes.
     *
     * @param toolComponent the tool component
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
                .addListener((observableValue, oldValue, newValue) -> handleSceneChanged(getSceneSize()));
    }

    /**
     * Add an element to the tool component.
     *
     * @param toolComponent the tool component.
     * @param other         the element.
     */
    protected void addElements(@NotNull final TabToolComponent toolComponent, @NotNull final Node other) {
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

        handleSceneChanged(getSceneSize());
    }

    /**
     * Handle changing tool's size.
     *
     * @param newValue the new value
     */
    protected void handleToolChanged(@NotNull final Number newValue) {
        if (config == null) return;

        this.collapsed = toolComponent.isCollapsed();
        saveCollapsed(collapsed);

        if (!isCollapsed()) {
            int abs = Math.abs(size - newValue.intValue());
            size = abs > 2 ? newValue.intValue() : size;
            saveSize(size);
        }

        toolComponent.setExpandPosition(getExpandPosition(size, getSceneSize()));
        handleSceneChanged(getSceneSize());
    }

    /**
     * Gets scene size.
     *
     * @return the scene size.
     */
    protected double getSceneSize() {
        return scene.getWidth();
    }

    /**
     * Save the flag of collapsed.
     *
     * @param collapsed the collapsed
     */
    protected void saveCollapsed(final boolean collapsed) {
    }

    /**
     * Save the size of the tool component.
     *
     * @param size the size
     */
    protected void saveSize(final int size) {
    }

    /**
     * Handle changing scene's size.
     *
     * @param newSize the new size
     */
    protected void handleSceneChanged(@NotNull final Number newSize) {
        if (config == null) return;

        if (isCollapsed()) {
            setDividerPosition(getDividerIndex(), getCollapsedPosition());
            return;
        }

        setDividerPosition(getDividerIndex(), getExpandPosition(size, newSize.doubleValue()));
    }

    /**
     * Gets collapsed position.
     *
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
     *
     * @param toolSize  the tool size
     * @param sceneSize the scene size
     * @return the expand position
     */
    protected double getExpandPosition(final double toolSize, final double sceneSize) {
        return min(1, max(0.1, toolSize / sceneSize));
    }
}
