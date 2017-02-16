package com.ss.editor.ui.component.editing;

import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.Editing3DProvider;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.css.CSSIds;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The class container of editing components.
 *
 * @author JavaSaBr
 */
public class EditingContainer extends ScrollPane {

    /**
     * The change consumer.
     */
    @NotNull
    private final ModelChangeConsumer changeConsumer;

    /**
     * The provider to edit 3D.
     */
    @NotNull
    private final Editing3DProvider editingProvider;

    /**
     * The list of editing components.
     */
    @NotNull
    private final Array<EditingComponent> components;

    /**
     * The container of editing component.
     */
    @NotNull
    private final VBox container;

    /**
     * Is showed this component.
     */
    protected boolean showed;

    public EditingContainer(@NotNull final ModelChangeConsumer changeConsumer,
                            @NotNull final Editing3DProvider editingProvider) {
        this.changeConsumer = changeConsumer;
        this.editingProvider = editingProvider;
        setId(CSSIds.EDITING_CONTAINER);
        this.components = ArrayFactory.newArray(EditingComponent.class);
        this.container = new VBox();
        setContent(container);
    }

    /**
     * Add a new editing component.
     *
     * @param editingComponent the editing component.
     */
    @FXThread
    public void addComponent(@NotNull final EditingComponent editingComponent) {
        components.add(editingComponent);
        editingComponent.initFor(this);
    }

    /**
     * @return the container of editing component.
     */
    @NotNull
    private VBox getContainer() {
        return container;
    }

    /**
     * @return the list of editing components.
     */
    @NotNull
    private Array<EditingComponent> getComponents() {
        return components;
    }

    /**
     * Show an editing component to edit an element.
     *
     * @param element the element to edit.
     */
    @FXThread
    public void showComponentFor(@Nullable final Object element) {

        final VBox container = getContainer();
        final ObservableList<Node> children = container.getChildren();
        children.forEach(node -> ((EditingComponent) node).notifyHided());
        children.forEach(node -> ((EditingComponent) node).stopEditing());
        children.clear();

        if (element == null) return;

        final EditingComponent editingComponent = getComponents().search(element, EditingComponent::isSupport);
        if (editingComponent == null) return;

        children.add((Node) editingComponent);

        editingComponent.startEditing(element);

        if (isShowed()) {
            editingComponent.notifyShowed();
        }
    }

    /**
     * Notify about showed this container.
     */
    @FXThread
    public void notifyShowed() {
        setShowed(true);

        final VBox container = getContainer();
        final ObservableList<Node> children = container.getChildren();
        children.forEach(node -> ((EditingComponent) node).notifyShowed());
    }

    /**
     * Notify about hided this container.
     */
    @FXThread
    public void notifyHided() {
        setShowed(false);

        final VBox container = getContainer();
        final ObservableList<Node> children = container.getChildren();
        children.forEach(node -> ((EditingComponent) node).notifyHided());
    }

    /**
     * @return the provider to edit 3D.
     */
    @NotNull
    public Editing3DProvider getEditingProvider() {
        return editingProvider;
    }

    /**
     * @return the change consumer.
     */
    @NotNull
    public ModelChangeConsumer getChangeConsumer() {
        return changeConsumer;
    }

    /**
     * @return true if this component is showed.
     */
    protected boolean isShowed() {
        return showed;
    }

    /**
     * @param showed true if this component is showed.
     */
    protected void setShowed(final boolean showed) {
        this.showed = showed;
    }

}
