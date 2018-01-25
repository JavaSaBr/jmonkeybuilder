package com.ss.editor.ui.component.painting.impl;

import static com.ss.rlib.util.ClassUtils.unsafeCast;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.scene.Node;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.editor.Editor3DProvider;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.component.painting.PaintingComponent;
import com.ss.editor.ui.component.painting.PaintingComponentContainer;
import com.ss.editor.ui.component.editor.state.impl.AdditionalEditorState;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The base implementation of a processing component.
 *
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public abstract class AbstractPaintingComponent<T, S extends AdditionalEditorState> extends VBox implements PaintingComponent {

    /**
     * The executor manager.
     */
    @NotNull
    protected static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    /**
     * The container.
     */
    @Nullable
    protected PaintingComponentContainer container;

    /**
     * The painted object.
     */
    @Nullable
    protected volatile T paintedObject;

    /**
     * The state of this component.
     */
    @Nullable
    protected S state;

    /**
     * The flag of showing this component.
     */
    protected boolean showed;

    public AbstractPaintingComponent(@NotNull final PaintingComponentContainer container) {
        this.container = container;
        createComponents();
        prefWidthProperty().bind(widthProperty());
    }

    @Override
    @FromAnyThread
    public @NotNull PaintingComponentContainer getContainer() {
        return notNull(container);
    }

    /**
     * Get the change consumer.
     *
     * @return the change consumer.
     */
    @FromAnyThread
    public @NotNull ModelChangeConsumer getChangeConsumer() {
        final PaintingComponentContainer editingContainer = getContainer();
        return editingContainer.getChangeConsumer();
    }

    @Override
    @FxThread
    public @Nullable T getPaintedObject() {
        return paintedObject;
    }

    @Override
    @FxThread
    public void startPainting(@NotNull final Object object) {
        this.paintedObject = unsafeCast(object);
    }

    /**
     * Create components.
     */
    protected void createComponents() {
    }

    /**
     * Get the cursor node.
     *
     * @return the cursor node.
     */
    @JmeThread
    public @NotNull Node getCursorNode() {
        final PaintingComponentContainer container = getContainer();
        final Editor3DProvider provider = container.getProvider();
        return provider.getCursorNode();
    }

    /**
     * Get the node to place some markers in 3D editor.
     *
     * @return the markers node.
     */
    @JmeThread
    public @NotNull Node getMarkersNode() {
        final PaintingComponentContainer container = getContainer();
        final Editor3DProvider provider = container.getProvider();
        return provider.getMarkersNode();
    }

    @Override
    @FxThread
    public void notifyShowed() {
        setShowed(true);
    }

    @Override
    @FxThread
    public void notifyHided() {
        setShowed(false);
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
}
