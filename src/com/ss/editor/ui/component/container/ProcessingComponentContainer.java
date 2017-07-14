package com.ss.editor.ui.component.container;

import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.editor.Editor3DProvider;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.FXConstants;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The class container of processing components.
 *
 * @param <P> the type parameter
 * @param <C> the type parameter
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

    /**
     * Instantiates a new Processing component container.
     *
     * @param changeConsumer the change consumer
     * @param provider       the provider
     * @param componentType  the component type
     */
    public ProcessingComponentContainer(@NotNull final ModelChangeConsumer changeConsumer, @NotNull final P provider,
                                        @NotNull final Class<C> componentType) {
        this.changeConsumer = changeConsumer;
        this.provider = provider;
        this.components = ArrayFactory.newArray(componentType);
        this.componentType = componentType;
        this.container = new VBox();
        this.container.prefWidthProperty()
                .bind(widthProperty().subtract(FXConstants.PROPERTY_LIST_OFFSET));

        setContent(container);

        FXUtils.addClassTo(container, CSSClasses.DEF_VBOX);
        FXUtils.addClassTo(this, CSSClasses.PROCESSING_COMPONENT_CONTAINER);
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
     * Gets container.
     *
     * @return the container of processing components.
     */
    @NotNull
    protected VBox getContainer() {
        return container;
    }

    /**
     * Gets components.
     *
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
     * Gets provider.
     *
     * @return the provider.
     */
    @NotNull
    public P getProvider() {
        return provider;
    }

    /**
     * Gets change consumer.
     *
     * @return the change consumer.
     */
    @NotNull
    public ModelChangeConsumer getChangeConsumer() {
        return changeConsumer;
    }

    /**
     * Is showed boolean.
     *
     * @return true if this component is showed.
     */
    protected boolean isShowed() {
        return showed;
    }

    /**
     * Sets showed.
     *
     * @param showed true if this component is showed.
     */
    protected void setShowed(final boolean showed) {
        this.showed = showed;
    }

    /**
     * Notify about changed property.
     *
     * @param object       the object
     * @param propertyName the property name
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
