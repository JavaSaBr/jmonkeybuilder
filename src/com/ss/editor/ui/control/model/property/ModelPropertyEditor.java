package com.ss.editor.ui.control.model.property;

import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.builder.PropertyBuilderFactory;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

/**
 * Реализация компонента для редактирования свойств моделей.
 *
 * @author Ronn
 */
public class ModelPropertyEditor extends TitledPane {

    /**
     * Потребитель изменений модели.
     */
    private final ModelChangeConsumer modelChangeConsumer;

    /**
     * Контейнер контролов свойст объекта.
     */
    private VBox container;

    /**
     * Текущий редактируемый объект.
     */
    private Object currentObject;

    public ModelPropertyEditor(final ModelChangeConsumer modelChangeConsumer) {
        this.modelChangeConsumer = modelChangeConsumer;
        setText(Messages.MODEL_FILE_EDITOR_PROPERTIES);
        createComponents();
        setAnimated(false);
    }

    /**
     * @return контейнер контролов свойст объекта.
     */
    private VBox getContainer() {
        return container;
    }

    /**
     * @return потребитель изменений модели.
     */
    private ModelChangeConsumer getModelChangeConsumer() {
        return modelChangeConsumer;
    }

    /**
     * Построение компонентов.
     */
    private void createComponents() {
        container = new VBox();
        container.setAlignment(Pos.TOP_CENTER);
        setContent(new ScrollPane(container));
    }

    /**
     * Построение контролов для указанного объекта.
     */
    public void buildFor(final Object object) {
        if (getCurrentObject() == object) return;

        final VBox container = getContainer();
        final ObservableList<Node> children = container.getChildren();
        children.clear();

        if (object != null) {
            PropertyBuilderFactory.buildFor(object, container, getModelChangeConsumer());
        }

        setCurrentObject(object);
    }

    /**
     * Синхронизация свойств.
     */
    public void syncFor(final Object object) {
        if (getCurrentObject() != object) return;

        final VBox container = getContainer();
        final ObservableList<Node> children = container.getChildren();
        children.forEach(node -> {
            if (node instanceof ModelPropertyControl<?, ?>) {
                ((ModelPropertyControl) node).sync();
            }
        });
    }

    /**
     * @param currentObject текущий редактируемый объект.
     */
    private void setCurrentObject(Object currentObject) {
        this.currentObject = currentObject;
    }

    /**
     * @return текущий редактируемый объект.
     */
    private Object getCurrentObject() {
        return currentObject;
    }
}
