package com.ss.builder.ui.component.painting;

import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.model.editor.Editor3DProvider;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.ui.FxConstants;
import com.ss.builder.ui.css.CssClasses;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.model.editor.Editor3DProvider;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.FxConstants;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.fx.util.FxUtils;
import javafx.beans.value.ObservableValue;
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

    public static final double LABEL_PERCENT = 1D - PropertyControl.CONTROL_WIDTH_PERCENT_2;
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
     * The painted object.
     */
    @Nullable
    private Object paintedObject;

    /**
     * The current component.
     */
    @Nullable
    private PaintingComponent currentComponent;

    /**
     * The flag of showing this container.
     */
    protected boolean showed;

    public PaintingComponentContainer(@NotNull ModelChangeConsumer changeConsumer, @NotNull Editor3DProvider provider) {
        this.changeConsumer = changeConsumer;
        this.provider = provider;
        this.container = new VBox();
        this.container.prefWidthProperty()
                .bind(widthProperty().subtract(FxConstants.PROPERTY_LIST_OFFSET));

        var toolTypeContainer = new HBox();
        toolTypeContainer.prefWidthProperty()
                .bind(widthProperty().subtract(FxConstants.PROPERTY_LIST_OFFSET));

        var label = new Label(Messages.PAINTING_COMPONENT_CONTAINER_TOOL + ":");
        label.maxWidthProperty().bind(toolTypeContainer.widthProperty()
                .multiply(LABEL_PERCENT));

        componentBox = new ComboBox<>();
        componentBox.setCellFactory(PaintingComponentListCell::new);
        componentBox.setButtonCell(new PaintingComponentListCell(null));
        componentBox.setPromptText("No tools");
        componentBox.prefWidthProperty().bind(toolTypeContainer.widthProperty()
                .multiply(FIELD_PERCENT));
        componentBox.getSelectionModel()
                .selectedItemProperty()
                .addListener(this::activate);

        var resultContainer = new VBox();

        setContent(resultContainer);

        FxUtils.addClass(container, CssClasses.DEF_VBOX)
                .addClass(toolTypeContainer, CssClasses.DEF_HBOX)
                .addClass(resultContainer, CssClasses.PAINTING_COMPONENT_ROOT);

        FxUtils.addChild(toolTypeContainer, label, componentBox)
                .addChild(resultContainer, toolTypeContainer, container);

        var registry = PaintingComponentRegistry.getInstance();

        this.components = registry.createComponents(this);
    }

    /**
     * Get the painted object.
     *
     * @return the painted object.
     */
    @FxThread
    private @Nullable Object getPaintedObject() {
        return paintedObject;
    }

    /**
     * Set the painted object.
     *
     * @param paintedObject the painted object.
     */
    @FxThread
    private void setPaintedObject(@Nullable Object paintedObject) {
        this.paintedObject = paintedObject;
    }

    /**
     * Get the current painting component.
     *
     * @return the current painting component.
     */
    @FxThread
    private @Nullable PaintingComponent getCurrentComponent() {
        return currentComponent;
    }

    /**
     * Set the current painting component.
     *
     * @param currentComponent the current painting component.
     */
    @FxThread
    private void setCurrentComponent(@Nullable PaintingComponent currentComponent) {
        this.currentComponent = currentComponent;
    }

    /**
     * Activate the selected painting component.
     *
     * @param observable the component box's property.
     * @param oldValue   the previous component.
     * @param newValue   the new component.
     */
    @FxThread
    private void activate(
            @NotNull ObservableValue<? extends PaintingComponent> observable,
            @Nullable PaintingComponent oldValue,
            @Nullable PaintingComponent newValue
    ) {

        var items = getContainer().getChildren();

        if (oldValue != null) {
            oldValue.notifyHided();
            oldValue.stopPainting();
            items.remove(oldValue);
        }

        var paintedObject = getPaintedObject();

        if (newValue != null) {

            if (paintedObject != null) {
                newValue.startPainting(paintedObject);
            }

            if (isShowed()) {
                newValue.notifyShowed();
            }

            items.add((Node) newValue);
        }

        setCurrentComponent(newValue);
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
     * Get the component box.
     *
     * @return the component box.
     */
    @FxThread
    private @NotNull ComboBox<PaintingComponent> getComponentBox() {
        return componentBox;
    }

    /**
     * Prepare painting components to work with the element
     *
     * @param element the element.
     */
    @FxThread
    public void prepareFor(@Nullable Object element) {
        setPaintedObject(element);

        var componentBox = getComponentBox();
        var items = componentBox.getItems();
        items.clear();

        if (element != null) {
            getComponents().forEach(toCheck -> toCheck.isSupport(element),
                    toAdd -> items.add(toAdd));
        }

        if (!items.isEmpty()) {
            componentBox.getSelectionModel().select(0);
        }
    }

    /**
     * Notify about showed this container.
     */
    @FxThread
    public void notifyShowed() {
        setShowed(true);
        var currentComponent = getCurrentComponent();
        if (currentComponent != null) {
            currentComponent.notifyShowed();
        }
    }

    /**
     * Notify about hided this container.
     */
    @FxThread
    public void notifyHided() {
        setShowed(false);
        var currentComponent = getCurrentComponent();
        if (currentComponent != null) {
            currentComponent.notifyHided();
        }
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
     * Return the showed state.
     *
     * @return true if this component is showed.
     */
    @FxThread
    protected boolean isShowed() {
        return showed;
    }

    /**
     * Set the showed state.
     *
     * @param showed true if this component is showed.
     */
    @FxThread
    protected void setShowed(boolean showed) {
        this.showed = showed;
    }

    /**
     * Notify about changed property.
     *
     * @param object       the object.
     * @param propertyName the property name.
     */
    @FxThread
    public void notifyChangeProperty(@NotNull Object object, @NotNull String propertyName) {
        var currentComponent = getCurrentComponent();
        if (currentComponent != null) {
            currentComponent.notifyChangeProperty(object, propertyName);
        }
    }
}
