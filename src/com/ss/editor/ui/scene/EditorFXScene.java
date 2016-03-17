package com.ss.editor.ui.scene;

import com.ss.editor.ui.component.ScreenComponent;

import java.util.concurrent.atomic.AtomicInteger;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;
import rlib.util.StringUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

import static com.ss.editor.ui.util.UIUtils.fillComponents;

/**
 * Реализация сцены редактора для работы JavaFX.
 *
 * @author Ronn
 */
public class EditorFXScene extends Scene {

    /**
     * Список компонентов в сцене.
     */
    private final Array<ScreenComponent> components;

    /**
     * Контейнер элементов сцены.
     */
    private final StackPane container;

    /**
     * Слой для отображения загрузки.
     */
    private final VBox loadingLayer;

    /**
     * Счетчик загрузок.
     */
    private final AtomicInteger loadingCount;

    /**
     * Анимация загрузки.
     */
    private ProgressIndicator progressIndicator;

    public EditorFXScene(final Group root) {
        super(root);

        this.loadingCount = new AtomicInteger();
        this.components = ArrayFactory.newArray(ScreenComponent.class);
        this.container = new StackPane();
        this.loadingLayer = new VBox();
        this.loadingLayer.setVisible(false);

        root.getChildren().add(container);

        FXUtils.bindFixedWidth(container, widthProperty());
        FXUtils.bindFixedHeight(container, heightProperty());
        FXUtils.bindFixedWidth(loadingLayer, container.widthProperty());
        FXUtils.bindFixedHeight(loadingLayer, container.heightProperty());

        FXUtils.addToPane(loadingLayer, container);
    }

    /**
     * Поиск интересуемого компонента через его ид.
     *
     * @param id ид интересуемого компонента.
     * @return искомый компонент либо <code>null</code>.
     */
    public <T extends ScreenComponent> T findComponent(final String id) {

        final Array<ScreenComponent> components = getComponents();

        for (final ScreenComponent component : components.array()) {

            if (component == null) {
                break;
            } else if (StringUtils.equals(id, component.getComponentId())) {
                return (T) component;
            }
        }

        return null;
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
        if(loadingCount.incrementAndGet() == 1) {
            showLoading();
        }
    }

    /**
     * Уменьшение счетчика загрузок.
     */
    public synchronized void decrementLoading() {
        if(loadingCount.decrementAndGet() == 0) {
            hideLoading();
        }
    }

    /**
     * Отобразить загрузку.
     */
    private void showLoading() {

        final VBox loadingLayer = getLoadingLayer();
        loadingLayer.setVisible(true);

        progressIndicator = new ProgressIndicator(ProgressIndicator.INDETERMINATE_PROGRESS);

        FXUtils.addToPane(progressIndicator, loadingLayer);
    }

    /**
     * Скрыть загрузку.
     */
    private void hideLoading() {

        final VBox loadingLayer = getLoadingLayer();
        loadingLayer.setVisible(false);
        loadingLayer.getChildren().clear();

        progressIndicator = null;
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
