package com.ss.editor.ui.control.model.property;

import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.builder.PropertyBuilderFactory;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

/**
 * The component for containing property controls in the editor.
 *
 * @author JavaSaBr
 */
public class ModelPropertyEditor extends TitledPane {

    public static final Insets PROPERTIES_OFFSET = new Insets(0, 0, 0, 4);

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
        setText(Messages.MODEL_FILE_EDITOR_PROPERTIES);
        createComponents();
        setAnimated(false);
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
        container.setAlignment(Pos.TOP_CENTER);
        VBox.setMargin(container, PROPERTIES_OFFSET);
        setContent(new ScrollPane(new VBox(container)));
    }

    /**
     * Build property controls for the object.
     */
    public void buildFor(final Object object) {
        if (getCurrentObject() == object) return;

        final VBox container = getContainer();
        final ObservableList<Node> children = container.getChildren();
        children.clear();

        if (object != null) {
            PropertyBuilderFactory.buildFor(object, container, modelChangeConsumer);
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
