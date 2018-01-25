package com.ss.editor.ui.component.painting;

import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.model.editor.Editor3DProvider;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.FXConstants;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.array.Array;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The class container of painting components.
 *
 * @author JavaSaBr
 */
public class PaintingComponentContainer extends ScrollPane {

    /**
     * The constant LABEL_PERCENT.
     */
    public static final double LABEL_PERCENT = 1D - PropertyControl.CONTROL_WIDTH_PERCENT_2;
    /**
     * The constant FIELD_PERCENT.
     */
    public static final double FIELD_PERCENT = PropertyControl.CONTROL_WIDTH_PERCENT_2;

    /**
     * The change consumer.
     */
    @NotNull
    protected final ModelChangeConsumer changeConsumer;

    /**
     * The editor 3D provider.
     */
    @NotNull
    protected final Editor3DProvider provider;

    /**
     * The list of painting components.
     */
    @NotNull
    protected final Array<PaintingComponent> components;

    /**
     * The container of painting components.
     */
    @NotNull
    protected final VBox container;

    /**
     * The components box.
     */
    @NotNull
    protected final ComboBox<PaintingComponent> componentBox;

    /**
     * The flag of showing this container.
     */
    protected boolean showed;

    public PaintingComponentContainer(@NotNull final ModelChangeConsumer changeConsumer, @NotNull final Editor3DProvider provider) {
        this.changeConsumer = changeConsumer;
        this.provider = provider;
        this.container = new VBox();
        this.container.prefWidthProperty()
                .bind(widthProperty().subtract(FXConstants.PROPERTY_LIST_OFFSET));

        final HBox horContainer = new HBox();
        horContainer.prefWidthProperty()
                .bind(widthProperty().subtract(FXConstants.PROPERTY_LIST_OFFSET));

        final Label label = new Label("Painting tool:");
        label.maxWidthProperty().bind(horContainer.widthProperty()
                .multiply(LABEL_PERCENT));

        componentBox = new ComboBox<>();
        componentBox.prefWidthProperty().bind(horContainer.widthProperty()
                .multiply(FIELD_PERCENT));

        final VBox resultContainer = new VBox();

        setContent(resultContainer);

        FXUtils.addToPane(label, componentBox, horContainer);
        FXUtils.addToPane(horContainer, container, resultContainer);
        FXUtils.addClassTo(container, CssClasses.DEF_VBOX);
        FXUtils.addClassTo(horContainer, CssClasses.DEF_HBOX);
        FXUtils.addClassTo(resultContainer, CssClasses.PAINTING_COMPONENT_ROOT);

        final PaintingComponentRegistry registry = PaintingComponentRegistry.getInstance();
        this.components = registry.createComponents(this);
    }

    /**
     * Get the container.
     *
     * @return the container.
     */
    @FxThread
    protected @NotNull VBox getContainer() {
        return container;
    }

    /**
     * Get the list of painting components.
     *
     * @return the list of painting components.
     */
    @FxThread
    public @NotNull Array<PaintingComponent> getComponents() {
        return components;
    }

    /**
     * Show a painting component to process with the element.
     *
     * @param element the element to process.
     */
    @FxThread
    public void showComponentFor(@Nullable final Object element) {

        final VBox container = getContainer();
        final ObservableList<Node> children = container.getChildren();
        children.stream().filter(PaintingComponent.class::isInstance)
                .map(PaintingComponent.class::cast)
                .peek(PaintingComponent::notifyHided)
                .forEach(PaintingComponent::stopProcessing);

        children.clear();

        if (element == null) return;

        final PaintingComponent processingComponent = getComponents().search(element, PaintingComponent::isSupport);
        if (processingComponent == null) return;

        children.add((Node) processingComponent);

        processingComponent.startPainting(element);

        if (isShowed()) {
            processingComponent.notifyShowed();
        }
    }

    /**
     * Notify about showed this container.
     */
    @FxThread
    public void notifyShowed() {
        setShowed(true);

        final VBox container = getContainer();
        final ObservableList<Node> children = container.getChildren();
        children.stream().filter(PaintingComponent.class::isInstance)
                .map(PaintingComponent.class::cast)
                .forEach(PaintingComponent::notifyShowed);
    }

    /**
     * Notify about hided this container.
     */
    @FxThread
    public void notifyHided() {
        setShowed(false);

        final VBox container = getContainer();
        final ObservableList<Node> children = container.getChildren();
        children.stream().filter(PaintingComponent.class::isInstance)
                .map(PaintingComponent.class::cast)
                .forEach(PaintingComponent::notifyHided);
    }

    /**
     * Get the provider.
     *
     * @return the provider.
     */
    @JmeThread
    public @NotNull Editor3DProvider getProvider() {
        return provider;
    }

    /**
     * Get the change consumer.
     *
     * @return the change consumer.
     */
    @FromAnyThread
    public @NotNull ModelChangeConsumer getChangeConsumer() {
        return changeConsumer;
    }

    /**
     * Is showed boolean.
     *
     * @return true if this component is showed.
     */
    @FxThread
    protected boolean isShowed() {
        return showed;
    }

    /**
     * Sets showed.
     *
     * @param showed true if this component is showed.
     */
    @FxThread
    protected void setShowed(final boolean showed) {
        this.showed = showed;
    }

    /**
     * Notify about changed property.
     *
     * @param object       the object
     * @param propertyName the property name
     */
    @FxThread
    public void notifyChangeProperty(@NotNull final Object object, @NotNull final String propertyName) {

        final VBox container = getContainer();
        final ObservableList<Node> children = container.getChildren();
        if (children.isEmpty()) {
            return;
        }

        children.stream().map(PaintingComponent.class::cast)
                .filter(component -> component.getPaintedObject() == object)
                .forEach(c -> c.notifyChangeProperty(object, propertyName));
    }
}
