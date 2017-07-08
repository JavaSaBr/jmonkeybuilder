package com.ss.editor.ui.scene;

import static com.ss.editor.ui.util.UIUtils.fillComponents;
import static com.ss.rlib.util.ClassUtils.unsafeCast;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.ui.component.ScreenComponent;
import com.ss.editor.ui.css.CSSIds;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.StringUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ProgressIndicator;
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
public class EditorFXScene extends Scene {

    /**
     * The list of components.
     */
    @NotNull
    private final Array<ScreenComponent> components;

    /**
     * The view for drawing JME.
     */
    @NotNull
    private final Canvas canvas;

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
     * Instantiates a new Editor fx scene.
     *
     * @param root the root
     */
    public EditorFXScene(@NotNull final Group root) {
        super(root);

        this.canvas = new Canvas();
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

        root.getChildren().addAll(hideLayer, background, canvas, container, loadingLayer);

        canvas.setPickOnBounds(true);
        canvas.heightProperty().bind(heightProperty());
        canvas.widthProperty().bind(widthProperty());
        canvas.setOpacity(0);

        FXUtils.bindFixedWidth(background, widthProperty());
        FXUtils.bindFixedHeight(background, heightProperty());
        FXUtils.bindFixedWidth(hideLayer, widthProperty());
        FXUtils.bindFixedHeight(hideLayer, heightProperty());
        FXUtils.bindFixedWidth(container, widthProperty());
        FXUtils.bindFixedHeight(container, heightProperty());
        FXUtils.bindFixedWidth(loadingLayer, widthProperty());
        FXUtils.bindFixedHeight(loadingLayer, heightProperty());
    }

    /**
     * Gets canvas.
     *
     * @return the view for drawing JME.
     */
    @NotNull
    @FXThread
    public Canvas getCanvas() {
        return canvas;
    }

    /**
     * Find the component with the ID.
     *
     * @param <T> the type parameter
     * @param id  the component id.
     * @return the component or null.
     */
    @Nullable
    @FXThread
    public <T extends ScreenComponent> T findComponent(@NotNull final String id) {
        final Array<ScreenComponent> components = getComponents();
        return unsafeCast(components.search(id, (component, toCheck) ->
                StringUtils.equals(toCheck, component.getComponentId())));
    }

    /**
     * Gets components.
     *
     * @return the list of components.
     */
    @NotNull
    @FXThread
    public Array<ScreenComponent> getComponents() {
        return components;
    }

    /**
     * Gets container.
     *
     * @return the container of this scene.
     */
    @NotNull
    @FXThread
    public StackPane getContainer() {
        return container;
    }

    /**
     * @return the loading layer.
     */
    @NotNull
    @FXThread
    private VBox getLoadingLayer() {
        return loadingLayer;
    }

    /**
     * Gets hide layer.
     *
     * @return the hide layer.
     */
    @NotNull
    @FXThread
    public StackPane getHideLayer() {
        return hideLayer;
    }

    /**
     * Increase the loading counter.
     */
    @FXThread
    public synchronized void incrementLoading() {
        if (loadingCount.incrementAndGet() == 1) {
            showLoading();
        }
    }

    /**
     * Decrease the loading counter.
     */
    @FXThread
    public synchronized void decrementLoading() {
        if (loadingCount.decrementAndGet() == 0) {
            hideLoading();
        }
    }

    /**
     * Show the loading process.
     */
    @FXThread
    private void showLoading() {

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
    @FXThread
    private void hideLoading() {

        final VBox loadingLayer = getLoadingLayer();
        loadingLayer.setVisible(false);
        loadingLayer.getChildren().clear();

        progressIndicator = null;

        final StackPane container = getContainer();
        container.setDisable(false);
    }

    /**
     * Notify all components about finished building.
     */
    @FXThread
    public void notifyFinishBuild() {
        final Array<ScreenComponent> components = getComponents();
        fillComponents(components, getContainer());
        components.forEach(ScreenComponent::notifyFinishBuild);
    }
}
