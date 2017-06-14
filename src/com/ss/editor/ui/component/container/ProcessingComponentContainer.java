package com.ss.editor.ui.component.container;

import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.editor.Editor3DProvider;
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
 * The class container of processing components.
 *
 * @author JavaSaBr
 */
public abstract class ProcessingComponentContainer<P extends Editor3DProvider, C extends ProcessingComponent>
        extends ScrollPane {

    /**
     * The change consumer.
     */
    @NotNull
    protected final ModelChangeConsumer changeConsumer;

    /**
     * The editor 3D provider.
     */
    @NotNull
    protected final P provider;

    /**
     * The list of processing components.
     */
    @NotNull
    protected final Array<C> components;

    /**
     * The component type.
     */
    @NotNull
    protected final Class<C> componentType;

    /**
     * The container of processing components.
     */
    @NotNull
    protected final VBox container;

    /**
     * The flag of showing this container.
     */
    protected boolean showed;

    public ProcessingComponentContainer(@NotNull final ModelChangeConsumer changeConsumer, @NotNull final P provider,
                                        @NotNull final Class<C> componentType) {
        this.changeConsumer = changeConsumer;
        this.provider = provider;
        this.components = ArrayFactory.newArray(componentType);
        this.componentType = componentType;
        this.container = new VBox();
        this.container.prefWidthProperty().bind(widthProperty());

        setId(CSSIds.PROCESSING_COMPONENT_CONTAINER);
        setContent(container);
    }

    /**
     * Add a new processing component.
     *
     * @param component the processing component.
     */
    @FXThread
    public void addComponent(@NotNull final C component) {
        components.add(component);
        component.initFor(this);
    }

    /**
     * @return the container of processing components.
     */
    @NotNull
    protected VBox getContainer() {
        return container;
    }

    /**
     * @return the list of processing components.
     */
    @NotNull
    protected Array<C> getComponents() {
        return components;
    }

    /**
     * Show a processing component to process with the element.
     *
     * @param element the element to process.
     */
    @FXThread
    public void showComponentFor(@Nullable final Object element) {

        final VBox container = getContainer();
        final ObservableList<Node> children = container.getChildren();
        children.stream().filter(componentType::isInstance)
                .map(componentType::cast)
                .peek(ProcessingComponent::notifyHided)
                .forEach(ProcessingComponent::stopProcessing);
        children.clear();

        if (element == null) return;

        final C processingComponent = getComponents().search(element, C::isSupport);
        if (processingComponent == null) return;

        children.add((Node) processingComponent);

        processingComponent.startProcessing(element);

        if (isShowed()) {
            processingComponent.notifyShowed();
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
        children.stream().filter(componentType::isInstance)
                .map(componentType::cast)
                .forEach(ProcessingComponent::notifyShowed);
    }

    /**
     * Notify about hided this container.
     */
    @FXThread
    public void notifyHided() {
        setShowed(false);

        final VBox container = getContainer();
        final ObservableList<Node> children = container.getChildren();
        children.stream().filter(componentType::isInstance)
                .map(componentType::cast)
                .forEach(ProcessingComponent::notifyHided);
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

        children.stream().map(node -> (C) node).filter(editingComponent -> editingComponent.getProcessedObject() ==
                object).forEach(editingComponent -> editingComponent.notifyChangeProperty(object, propertyName));
    }
}
