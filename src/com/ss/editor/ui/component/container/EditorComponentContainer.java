package com.ss.editor.ui.component.container;

import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The class container of components.
 *
 * @author JavaSaBr
 */
public abstract class EditorComponentContainer<P, C extends EditorComponent> extends ScrollPane {

    /**
     * The change consumer.
     */
    @NotNull
    protected final ModelChangeConsumer changeConsumer;

    /**
     * The provider.
     */
    @NotNull
    protected final P provider;

    /**
     * The list of components.
     */
    @NotNull
    protected final Array<C> components;

    /**
     * The container of painting components.
     */
    @NotNull
    protected final VBox container;

    /**
     * Is showed this component.
     */
    protected boolean showed;

    public EditorComponentContainer(@NotNull final ModelChangeConsumer changeConsumer, @NotNull final P provider,
                                    @NotNull final Class<C> componentType) {
        this.changeConsumer = changeConsumer;
        this.provider = provider;
        this.components = ArrayFactory.newArray(componentType);
        this.container = new VBox();
        this.container.prefWidthProperty().bind(widthProperty());

        setContent(container);
    }

    /**
     * Add a new component.
     *
     * @param component the component.
     */
    @FXThread
    public void addComponent(@NotNull final C component) {
        components.add(component);
        component.initFor(this);
    }

    /**
     * @return the container of components.
     */
    @NotNull
    private VBox getContainer() {
        return container;
    }

    /**
     * @return the list of components.
     */
    @NotNull
    private Array<C> getComponents() {
        return components;
    }

    /**
     * Show a component to work with an element.
     *
     * @param element the element to work.
     */
    @FXThread
    public void showComponentFor(@Nullable final Object element) {

        final VBox container = getContainer();
        final ObservableList<Node> children = container.getChildren();
        children.forEach(node -> ((C) node).notifyHided());
        children.forEach(node -> ((C) node).stopWorking());
        children.clear();

        if (element == null) return;

        final C editingComponent = getComponents().search(element, C::isSupport);
        if (editingComponent == null) return;

        children.add((Node) editingComponent);

        editingComponent.startWorkingWith(element);

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
        children.forEach(node -> ((C) node).notifyShowed());
    }

    /**
     * Notify about hided this container.
     */
    @FXThread
    public void notifyHided() {
        setShowed(false);

        final VBox container = getContainer();
        final ObservableList<Node> children = container.getChildren();
        children.forEach(node -> ((C) node).notifyHided());
    }

    /**
     * @return the provider.
     */
    @NotNull
    public P getProvider() {
        return provider;
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

    /**
     * Notify about changed property.
     */
    @FXThread
    public void notifyChangeProperty(@NotNull final Object object, @NotNull final String propertyName) {

        final VBox container = getContainer();
        final ObservableList<Node> children = container.getChildren();
        if (children.isEmpty()) return;

        children.stream().map(node -> (C) node).filter(editingComponent -> editingComponent.getWorkedObject() ==
                object).forEach(editingComponent -> editingComponent.notifyChangeProperty(object, propertyName));
    }
}
