package com.ss.editor.ui.control.model.property;

import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.UpdatableControl;
import com.ss.editor.ui.control.model.property.builder.PropertyBuilderFactory;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.util.NodeUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.node.ParticleNode;

/**
 * The component for containing property controls in the editor.
 *
 * @author JavaSaBr
 */
public class ModelPropertyEditor extends ScrollPane {

    /**
     * The consumer of changes.
     */
    @NotNull
    private final ModelChangeConsumer modelChangeConsumer;

    /**
     * The container of controls.
     */
    private VBox container;

    /**
     * The current editable object.
     */
    private Object currentObject;

    public ModelPropertyEditor(@NotNull final ModelChangeConsumer modelChangeConsumer) {
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
    public void syncFor(@Nullable final Object object) {
        if (!isNeedUpdate(object)) return;

        final VBox container = getContainer();
        final ObservableList<Node> children = container.getChildren();
        children.forEach(node -> {
            if (node instanceof UpdatableControl) {
                ((UpdatableControl) node).sync();
            }
        });
    }

    protected boolean isNeedUpdate(@Nullable final Object object) {

        final Object currentObject = getCurrentObject();
        if (currentObject == object) return true;

        if (currentObject instanceof ParticleNode && object instanceof ParticleEmitterNode) {
            final Object parent = NodeUtils.findParent((Spatial) currentObject, spatial -> spatial instanceof ParticleEmitterNode);
            return parent == object;
        }

        return false;
    }

    /**
     * @param currentObject the current editable object.
     */
    private void setCurrentObject(@Nullable final Object currentObject) {
        this.currentObject = currentObject;
    }

    /**
     * @return the current editable object.
     */
    @Nullable
    private Object getCurrentObject() {
        return currentObject;
    }
}
