package com.ss.editor.ui.scene;

import static com.ss.editor.ui.util.UIUtils.fillComponents;
import static rlib.util.ClassUtils.unsafeCast;

import com.ss.editor.ui.component.ScreenComponent;
import com.ss.editor.ui.css.CSSIds;

import java.util.concurrent.atomic.AtomicInteger;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;
import rlib.util.StringUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The class implementation of the scene of JavaFX.
 *
 * @author JavaSaBr.
 */
public class EditorFXScene extends Scene {

    /**
     * The list of components.
     */
    private final Array<ScreenComponent> components;

    /**
     * The view for drawing JME.
     */
    private final ImageView imageView;

    /**
     * The container of this scene.
     */
    private final StackPane container;

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

    public EditorFXScene(final Group root, final double width, final double height) {
        super(root, width, height);

        this.imageView = new ImageView();
        this.loadingCount = new AtomicInteger();
        this.components = ArrayFactory.newArraySet(ScreenComponent.class);
        this.container = new StackPane();
        this.container.setPickOnBounds(false);
        this.loadingLayer = new VBox();
        this.loadingLayer.setId(CSSIds.EDITOR_LOADING_LAYER);
        this.loadingLayer.setVisible(false);

        root.getChildren().addAll(imageView, container, loadingLayer);

        imageView.setPickOnBounds(true);
        imageView.fitHeightProperty().bind(heightProperty());
        imageView.fitWidthProperty().bind(widthProperty());

        FXUtils.bindFixedWidth(container, widthProperty());
        FXUtils.bindFixedHeight(container, heightProperty());
        FXUtils.bindFixedWidth(loadingLayer, widthProperty());
        FXUtils.bindFixedHeight(loadingLayer, heightProperty());
    }

    /**
     * @return the view for drawing JME.
     */
    public ImageView getImageView() {
        return imageView;
    }

    /**
     * Поиск интересуемого компонента через его ид.
     *
     * @param id ид интересуемого компонента.
     * @return искомый компонент либо <code>null</code>.
     */
    public <T extends ScreenComponent> T findComponent(final String id) {
        final Array<ScreenComponent> components = getComponents();
        return unsafeCast(components.search(id, (component, toCheck) -> StringUtils.equals(toCheck, component.getComponentId())));
    }

    /**
     * @return список компонентов в сцене.
     */
    public Array<ScreenComponent> getComponents() {
        return components;
    }

    /**
     * @return контейнер элементов сцены.
     */
    public StackPane getContainer() {
        return container;
    }

    /**
     * @return слой для отображения загрузки.
     */
    private VBox getLoadingLayer() {
        return loadingLayer;
    }

    /**
     * Увеличение счетчика загрузок.
     */
    public synchronized void incrementLoading() {
        if (loadingCount.incrementAndGet() == 1) {
            showLoading();
        }
    }

    /**
     * Уменьшение счетчика загрузок.
     */
    public synchronized void decrementLoading() {
        if (loadingCount.decrementAndGet() == 0) {
            hideLoading();
        }
    }

    /**
     * Отобразить загрузку.
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
     * Скрыть загрузку.
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
     * Уведомление сцены о том, что было завершено ее построение.
     */
    public void notifyFinishBuild() {
        final Array<ScreenComponent> components = getComponents();
        fillComponents(components, getContainer());
        components.forEach(ScreenComponent::notifyFinishBuild);
    }
}
