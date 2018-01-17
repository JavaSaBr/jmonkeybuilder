package com.ss.editor.ui.scene;

import static com.ss.editor.ui.util.UIUtils.fillComponents;
import static com.ss.rlib.util.ClassUtils.unsafeCast;
import com.jme3x.jfx.injfx.input.JFXMouseInput;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.manager.InitializationManager;
import com.ss.editor.ui.component.ScreenComponent;
import com.ss.editor.ui.css.CSSIds;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.StringUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The class implementation of the scene of JavaFX.
 *
 * @author JavaSaBr
 */
public class EditorFxScene extends Scene {

    @NotNull
    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    /**
     * The list of components.
     */
    @NotNull
    private final Array<ScreenComponent> components;

    /**
     * The view to draw jME.
     */
    @NotNull
    private final ImageView canvas;

    /**
     * The container of this scene.
     */
    @NotNull
    private final StackPane container;

    /**
     * The hide layer.
     */
    @NotNull
    private final StackPane hideLayer;

    /**
     * The loading layer.
     */
    @NotNull
    private final VBox loadingLayer;

    /**
     * The loading counter.
     */
    @NotNull
    private final AtomicInteger loadingCount;

    /**
     * The indicator of loading.
     */
    @Nullable
    private ProgressIndicator progressIndicator;

    /**
     * Focused node.
     */
    @Nullable
    private Node focused;

    public EditorFxScene(@NotNull final Group root) {
        super(root);

        this.canvas = new EditorFxImageView();
        this.canvas.setMouseTransparent(true);
        this.canvas.getProperties()
                .put(JFXMouseInput.PROP_USE_LOCAL_COORDS, true);

        this.loadingCount = new AtomicInteger();
        this.components = ArrayFactory.newArraySet(ScreenComponent.class);
        this.container = new StackPane();
        this.container.setId(CSSIds.ROOT_CONTAINER);
        this.container.setPickOnBounds(false);
        this.hideLayer = new StackPane();
        this.hideLayer.setVisible(false);
        this.loadingLayer = new VBox();
        this.loadingLayer.setId(CSSIds.EDITOR_LOADING_LAYER);
        this.loadingLayer.setVisible(false);

        final Pane background = new Pane();
        background.setId(CSSIds.ROOT);

        FXUtils.addDebugBorderTo(canvas);

        root.getChildren().addAll(hideLayer, background, container, loadingLayer);

        FXUtils.bindFixedWidth(background, widthProperty());
        FXUtils.bindFixedHeight(background, heightProperty());
        FXUtils.bindFixedWidth(container, widthProperty());
        FXUtils.bindFixedHeight(container, heightProperty());
        FXUtils.bindFixedWidth(loadingLayer, widthProperty());
        FXUtils.bindFixedHeight(loadingLayer, heightProperty());
        FXUtils.setFixedSize(hideLayer, 300, 300);

        hideCanvas();
    }

    /**
     * Get the canvas.
     *
     * @return the view tp draw jME.
     */
    @FxThread
    public @NotNull ImageView getCanvas() {
        return canvas;
    }

    /**
     * Move the canvas component to hide layer.
     */
    @FxThread
    public void hideCanvas() {

        final ObservableList<Node> children = hideLayer.getChildren();
        if (children.contains(canvas)) return;

        children.add(canvas);
    }

    /**
     * Find the component with the ID.
     *
     * @param <T> the type parameter
     * @param id  the component id.
     * @return the component or null.
     */
    @FxThread
    public <T extends ScreenComponent> @Nullable T findComponent(@NotNull final String id) {
        final Array<ScreenComponent> components = getComponents();
        return unsafeCast(components.search(id, (component, toCheck) ->
                StringUtils.equals(toCheck, component.getComponentId())));
    }

    /**
     * Gets components.
     *
     * @return the list of components.
     */
    @FxThread
    public @NotNull Array<ScreenComponent> getComponents() {
        return components;
    }

    /**
     * Get the container.
     *
     * @return the container of this scene.
     */
    @FxThread
    public @NotNull StackPane getContainer() {
        return container;
    }

    /**
     * Get the loading layer.
     *
     * @return the loading layer.
     */
    @FxThread
    private @NotNull VBox getLoadingLayer() {
        return loadingLayer;
    }

    /**
     * Get the hide layer.
     *
     * @return the hide layer.
     */
    @FxThread
    public @NotNull StackPane getHideLayer() {
        return hideLayer;
    }

    /**
     * Increase the loading counter.
     */
    @FxThread
    public synchronized void incrementLoading() {
        if (loadingCount.incrementAndGet() == 1) {
            showLoading();
        }
    }

    /**
     * Decrease the loading counter.
     */
    @FxThread
    public synchronized void decrementLoading() {
        if (loadingCount.decrementAndGet() == 0) {
            hideLoading();
        }
    }

    /**
     * Show the loading process.
     */
    @FxThread
    private void showLoading() {
        focused = getFocusOwner();

        final VBox loadingLayer = getLoadingLayer();
        loadingLayer.setVisible(true);
        loadingLayer.toFront();

        progressIndicator = new ProgressIndicator(ProgressIndicator.INDETERMINATE_PROGRESS);
        progressIndicator.setId(CSSIds.EDITOR_LOADING_PROGRESS);

        FXUtils.addToPane(progressIndicator, loadingLayer);

        final StackPane container = getContainer();
        container.setDisable(true);
    }

    /**
     * Hide the loading process.
     */
    @FxThread
    private void hideLoading() {

        final VBox loadingLayer = getLoadingLayer();
        loadingLayer.setVisible(false);
        loadingLayer.getChildren().clear();

        progressIndicator = null;

        final StackPane container = getContainer();
        container.setDisable(false);

        if (focused != null) {
            EXECUTOR_MANAGER.addFxTask(() -> {
                focused.requestFocus();
                focused = null;
            });
        }
    }

    /**
     * Notify all components about finished building.
     */
    @FxThread
    public void notifyFinishBuild() {

        final Array<ScreenComponent> components = getComponents();
        fillComponents(components, getContainer());
        components.forEach(ScreenComponent::notifyFinishBuild);

        final InitializationManager initializationManager = InitializationManager.getInstance();
        initializationManager.onFinishLoading();
    }
}
