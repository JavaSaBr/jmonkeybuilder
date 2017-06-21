package com.ss.editor.ui.component.container.impl;

import static java.util.Objects.requireNonNull;
import static com.ss.rlib.util.ClassUtils.unsafeCast;
import com.jme3.scene.Node;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.editor.Editor3DProvider;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.component.container.ProcessingComponent;
import com.ss.editor.ui.component.container.ProcessingComponentContainer;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The base implementation of a processing component.
 *
 * @author JavaSaBr
 */
public abstract class AbstractProcessingComponent<T, C extends ProcessingComponentContainer> extends VBox implements
        ProcessingComponent {

    @NotNull
    protected static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    /**
     * The parent container.
     */
    @Nullable
    protected C container;

    /**
     * The processed object.
     */
    @Nullable
    protected T processedObject;

    /**
     * The flag of showing this compnent.
     */
    protected boolean showed;

    public AbstractProcessingComponent() {
        createComponents();
    }

    @Override
    public void initFor(@NotNull final Object container) {
        this.container = unsafeCast(container);
        prefWidthProperty().bind(widthProperty());
    }

    /**
     * @return the parent container.
     */
    @NotNull
    public C getContainer() {
        return requireNonNull(container);
    }

    /**
     * @return the change consumer.
     */
    @NotNull
    public ModelChangeConsumer getChangeConsumer() {
        final C editingContainer = getContainer();
        return editingContainer.getChangeConsumer();
    }

    @NotNull
    @Override
    public T getProcessedObject() {
        return requireNonNull(processedObject);
    }

    @Override
    public void startProcessing(@NotNull final Object object) {
        this.processedObject = unsafeCast(object);
    }

    protected void createComponents() {
    }

    /**
     * Get a cursor node.
     *
     * @return the cursor node.
     */
    @NotNull
    public Node getCursorNode() {
        final C container = getContainer();
        final Editor3DProvider provider = container.getProvider();
        return provider.getCursorNode();
    }

    /**
     * Get a node to place some markers in 3D editor.
     *
     * @return the markers node.
     */
    @NotNull
    public Node getMarkersNode() {
        final C container = getContainer();
        final Editor3DProvider provider = container.getProvider();
        return provider.getMarkersNode();
    }

    @Override
    public void notifyShowed() {
        setShowed(true);
    }

    @Override
    public void notifyHided() {
        setShowed(false);
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
