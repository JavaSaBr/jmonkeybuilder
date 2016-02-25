package com.ss.editor.ui.scene;

import com.ss.editor.ui.component.ScreenComponent;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
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

    public EditorFXScene(final Group root) {
        super(root);

        this.components = ArrayFactory.newArray(ScreenComponent.class);
        this.container = new StackPane();

        root.getChildren().add(container);

        FXUtils.bindFixedWidth(container, widthProperty());
        FXUtils.bindFixedHeight(container, heightProperty());
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
     * Уведомление сцены о том, что было завершено ее построение.
     */
    public void notifyFinishBuild() {
        final Array<ScreenComponent> components = getComponents();
        fillComponents(components, getContainer());
        components.forEach(ScreenComponent::notifyFinishBuild);
    }
}
