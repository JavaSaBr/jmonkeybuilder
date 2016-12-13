package com.ss.editor.ui.control.model.property;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.builder.PropertyBuilderFactory;
import com.ss.editor.ui.css.CSSIds;

import org.jetbrains.annotations.Nullable;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

/**
 * The component for containing property controls in the editor.
 *
 * @author JavaSaBr
 */
public class ModelPropertyEditor extends ScrollPane {

    /**
     * The consumer of changes.
     */
    private final ModelChangeConsumer modelChangeConsumer;

    /**
     * The container of controls.
     */
    private VBox container;

    /**
     * The current editable object.
     */
    private Object currentObject;

    public ModelPropertyEditor(final ModelChangeConsumer modelChangeConsumer) {
        this.modelChangeConsumer = modelChangeConsumer;
        createComponents();
    }

    /**
     * @return The container of controls.
     */
    private VBox getContainer() {
        return container;
    }

    /**
     * Create components.
     */
    private void createComponents() {
        container = new VBox();
        container.setId(CSSIds.MODEL_PARAM_CONTROL_CONTAINER);
        container.prefWidthProperty().bind(widthProperty());
        setContent(new VBox(container));
    }

    /**
     * Build property controls for the object.
     */
    public void buildFor(@Nullable final Object object, @Nullable final Object parent) {
        if (getCurrentObject() == object) return;

        final VBox container = getContainer();
        final ObservableList<Node> children = container.getChildren();
        children.clear();

        if (object != null) {
            PropertyBuilderFactory.buildFor(object, parent, container, modelChangeConsumer);
        }

        setCurrentObject(object);
    }

    /**
     * Sync all properties with controls.
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
     * @param currentObject the current editable object.
     */
    private void setCurrentObject(final Object currentObject) {
        this.currentObject = currentObject;
    }

    /**
     * @return the current editable object.
     */
    private Object getCurrentObject() {
        return currentObject;
    }
}
