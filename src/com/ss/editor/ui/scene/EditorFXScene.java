package com.ss.editor.ui.scene;

import static com.ss.editor.ui.util.UIUtils.fillComponents;
import static rlib.util.ClassUtils.unsafeCast;

import com.ss.editor.ui.component.ScreenComponent;
import com.ss.editor.ui.css.CSSIds;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;
import rlib.util.StringUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The class implementation of the scene of JavaFX.
 *
 * @author JavaSaBr
 */
public class EditorFXScene extends Scene {

    /**
     * The list of components.
     */
    private final Array<ScreenComponent> components;

    /**
     * The view for drawing JME.
     */
    private final Canvas canvas;

    /**
     * The container of this scene.
     */
    private final StackPane container;

    /**
     * The hide layer.
     */
    private final StackPane hideLayer;

    /**
     * The loading layer.
     */
    private final VBox loadingLayer;

    /**
     * THe loading counter.
     */
    private final AtomicInteger loadingCount;

    /**
     * THe indicator of loading.
     */
    private ProgressIndicator progressIndicator;

    public EditorFXScene(@NotNull final Group root) {
        super(root);

        this.canvas = new Canvas();
        this.loadingCount = new AtomicInteger();
        this.components = ArrayFactory.newArraySet(ScreenComponent.class);
        this.container = new StackPane();
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
     * @return the view for drawing JME.
     */
    @NotNull
    public Canvas getCanvas() {
        return canvas;
    }

    /**
     * Find the component with the ID.
     *
     * @param id the component id.
     * @return the component or null.
     */
    @Nullable
    public <T extends ScreenComponent> T findComponent(@NotNull final String id) {
        final Array<ScreenComponent> components = getComponents();
        return unsafeCast(components.search(id, (component, toCheck) -> StringUtils.equals(toCheck, component.getComponentId())));
    }

    /**
     * @return the list of components.
     */
    @NotNull
    public Array<ScreenComponent> getComponents() {
        return components;
    }

    /**
     * @return the container of this scene.
     */
    @NotNull
    public StackPane getContainer() {
        return container;
    }

    /**
     * @return the loading layer.
     */
    @NotNull
    private VBox getLoadingLayer() {
        return loadingLayer;
    }

    /**
     * @return the hide layer.
     */
    @NotNull
    public StackPane getHideLayer() {
        return hideLayer;
    }

    /**
     * Increase the loading counter.
     */
    public synchronized void incrementLoading() {
        if (loadingCount.incrementAndGet() == 1) {
            showLoading();
        }
    }

    /**
     * Decrease the loading counter.
     */
    public synchronized void decrementLoading() {
        if (loadingCount.decrementAndGet() == 0) {
            hideLoading();
        }
    }

    /**
     * Show the loading process.
     */
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
    public void notifyFinishBuild() {
        final Array<ScreenComponent> components = getComponents();
        fillComponents(components, getContainer());
        components.forEach(ScreenComponent::notifyFinishBuild);
    }
}
