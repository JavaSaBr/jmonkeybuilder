package com.ss.editor.ui.control.property;

import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.UpdatableControl;
import com.ss.editor.ui.control.property.builder.PropertyBuilderFactory;
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
 * The component to contains property controls in the editor.
 *
 * @author JavaSaBr
 */
public abstract class AbstractPropertyEditor<C extends ChangeConsumer> extends ScrollPane {

    /**
     * The consumer of changes.
     */
    @NotNull
    private final C changeConsumer;

    /**
     * The container of controls.
     */
    private VBox container;

    /**
     * The current editable object.
     */
    private Object currentObject;

    public AbstractPropertyEditor(@NotNull final C changeConsumer) {
        this.changeConsumer = changeConsumer;
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
        container.setId(CSSIds.ABSTRACT_PARAM_CONTROL_CONTAINER);
        container.prefWidthProperty().bind(widthProperty());
        setContent(new VBox(container));
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

    /**
     * Build property controls for the object.
     */
    public void buildFor(@Nullable final Object object, @Nullable final Object parent) {
        if (getCurrentObject() == object) return;

        final VBox container = getContainer();
        final ObservableList<Node> children = container.getChildren();
        children.clear();

        if (object != null) {
            PropertyBuilderFactory.buildFor(object, parent, container, changeConsumer);
        }

        setCurrentObject(object);
    }

    /**
     * Re-build property controls for the object.
     */
    public void rebuildFor(@Nullable final Object object, @Nullable final Object parent) {
        if (getCurrentObject() != object) return;

        final VBox container = getContainer();
        final ObservableList<Node> children = container.getChildren();
        children.clear();

        if (object != null) {
            PropertyBuilderFactory.buildFor(object, parent, container, changeConsumer);
        }
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
