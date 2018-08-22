package com.ss.builder.ui.component.painting.impl;

import static com.ss.rlib.common.util.ClassUtils.unsafeCast;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.scene.Node;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.jme.control.painting.PaintingControl;
import com.ss.builder.manager.ExecutorManager;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.control.painting.PaintingControl;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.component.editor.state.EditorState;
import com.ss.editor.ui.component.painting.PaintingComponent;
import com.ss.editor.ui.component.painting.PaintingComponentContainer;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * The base implementation of a processing component.
 *
 * @param <T> the painted object's type.
 * @param <S> the component state's type.
 * @param <C> the painting control's type.
 * @author JavaSaBr
 */
public abstract class AbstractPaintingComponent<T, S extends AbstractPaintingStateWithEditorTool, C extends PaintingControl> extends
        VBox implements PaintingComponent {

    protected static final Logger LOGGER = LoggerManager.getLogger(PaintingComponent.class);

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
     * The current tool control.
     */
    @Nullable
    private volatile C toolControl;

    /**
     * The state of this component.
     */
    @Nullable
    protected S state;

    /**
     * The flag of showing this component.
     */
    protected boolean showed;

    /**
     * The flag of ignoring listeners.
     */
    private boolean ignoreListeners;

    public AbstractPaintingComponent(@NotNull PaintingComponentContainer container) {
        this.container = container;
        createComponents();
        prefWidthProperty().bind(widthProperty());
    }

    @Override
    public void loadState(@NotNull EditorState editorState) {
        var state = editorState.getOrCreateAdditionalState(getStateType(), getStateConstructor());
        this.state = state;
        readState(state);
    }

    /**
     * Get the component's state.
     *
     * @return the component's state.
     */
    @FxThread
    protected @NotNull S getState() {
        return notNull(state);
    }

    /**
     * Get the state's constructor.
     *
     * @return the state's constructor.
     */
    @FromAnyThread
    protected abstract @NotNull Supplier<S> getStateConstructor();

    /**
     * Get the state's type.
     *
     * @return the state's type.
     */
    @FromAnyThread
    protected abstract @NotNull Class<S> getStateType();

    /**
     * Read the saved component's state.
     *
     * @param state the saved component's state.
     */
    @FxThread
    protected void readState(@NotNull S state) {
    }

    /**
     * Get the current tool control.
     *
     * @return the current tool control.
     */
    @FromAnyThread
    protected @NotNull C getToolControl() {
        return notNull(toolControl);
    }

    /**
     * Get the current tool control.
     *
     * @param toolControl the current tool control.
     */
    @FromAnyThread
    protected void setToolControl(@Nullable C toolControl) {
        this.toolControl = toolControl;
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
        return getContainer().getChangeConsumer();
    }

    @Override
    @FxThread
    public @Nullable T getPaintedObject() {
        return paintedObject;
    }

    @Override
    @FxThread
    public void startPainting(@NotNull Object object) {
        this.paintedObject = unsafeCast(object);
    }

    /**
     * Create components.
     */
    @FxThread
    protected void createComponents() {
    }

    /**
     * Get the cursor node.
     *
     * @return the cursor node.
     */
    @JmeThread
    public @NotNull Node getCursorNode() {
        return getContainer().getProvider()
                .getCursorNode();
    }

    /**
     * Get the node to place some markers in 3D editor.
     *
     * @return the markers node.
     */
    @JmeThread
    public @NotNull Node getMarkersNode() {
        return getContainer().getProvider()
                .getMarkersNode();
    }

    @Override
    @FxThread
    public void notifyShowed() {
        setShowed(true);
        EXECUTOR_MANAGER.addJmeTask(() ->
                getCursorNode().addControl(getToolControl()));
    }

    @Override
    @FxThread
    public void notifyHided() {
        setShowed(false);
        EXECUTOR_MANAGER.addJmeTask(() ->
                getCursorNode().removeControl(getToolControl()));
    }

    /**
     * Return true if this component is showed.
     *
     * @return true if this component is showed.
     */
    @FxThread
    protected boolean isShowed() {
        return showed;
    }

    /**
     * Set true if this component is showed.
     *
     * @param showed true if this component is showed.
     */
    @FxThread
    protected void setShowed(boolean showed) {
        this.showed = showed;
    }

    /**
     * Set true of need to ignore listeners
     *
     * @param ignoreListeners true of need to ignore listeners
     */
    @FxThread
    protected void setIgnoreListeners(boolean ignoreListeners) {
        this.ignoreListeners = ignoreListeners;
    }

    /**
     * Return true of need to ignore listeners
     *
     * @return true of need to ignore listeners
     */
    @FxThread
    protected boolean isIgnoreListeners() {
        return ignoreListeners;
    }

}
