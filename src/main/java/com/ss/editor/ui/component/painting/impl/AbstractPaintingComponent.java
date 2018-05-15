package com.ss.editor.ui.component.painting.impl;

import static com.ss.editor.ui.component.painting.PaintingComponentContainer.FIELD_PERCENT;
import static com.ss.editor.ui.component.painting.PaintingComponentContainer.LABEL_PERCENT;
import static com.ss.rlib.common.util.ClassUtils.unsafeCast;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.scene.Node;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.control.painting.PaintingControl;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.component.editor.state.EditorState;
import com.ss.editor.ui.component.painting.PaintingComponent;
import com.ss.editor.ui.component.painting.PaintingComponentContainer;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.fx.control.input.FloatTextField;
import com.ss.rlib.fx.util.FXUtils;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
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
     * The brush size field.
     */
    @Nullable
    private FloatTextField brushSizeField;

    /**
     * The brush power field.
     */
    @Nullable
    private FloatTextField brushPowerField;

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

    @FxThread
    protected @NotNull GridPane createBrushSettings() {

        final Label brushSizeLabel = new Label(Messages.MODEL_PROPERTY_BRUSH_SIZE + ":");
        brushSizeLabel.prefWidthProperty().bind(widthProperty().multiply(LABEL_PERCENT));

        brushSizeField = new FloatTextField();
        brushSizeField.prefWidthProperty().bind(widthProperty().multiply(FIELD_PERCENT));
        brushSizeField.setMinMax(0.0001F, Integer.MAX_VALUE);
        brushSizeField.addChangeListener((observable, oldValue, newValue) -> changeBrushSize(newValue));

        final Label brushPowerLabel = new Label(Messages.MODEL_PROPERTY_BRUSH_POWER + ":");
        brushPowerLabel.prefWidthProperty().bind(widthProperty().multiply(LABEL_PERCENT));

        brushPowerField = new FloatTextField();
        brushPowerField.prefWidthProperty().bind(widthProperty().multiply(FIELD_PERCENT));
        brushPowerField.setScrollPower(3F);
        brushPowerField.setMinMax(0.0001F, Integer.MAX_VALUE);
        brushPowerField.addChangeListener((observable, oldValue, newValue) -> changeBrushPower(newValue));

        final GridPane settings = new GridPane();
        settings.add(brushSizeLabel, 0, 0);
        settings.add(brushSizeField, 1, 0);
        settings.add(brushPowerLabel, 0, 1);
        settings.add(brushPowerField, 1, 1);

        FXUtils.addClassTo(settings, CssClasses.DEF_GRID_PANE);
        FXUtils.addClassTo(brushSizeLabel, brushPowerLabel, CssClasses.ABSTRACT_PARAM_CONTROL_PARAM_NAME_SINGLE_ROW);
        FXUtils.addClassTo(brushSizeField, brushPowerField, CssClasses.PROPERTY_CONTROL_COMBO_BOX);

        return settings;
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
        getBrushSizeField().setValue(state.getBrushSize());
        getBrushPowerField().setValue(state.getBrushPower());
    }

    /**
     * Change brush sizes.
     */
    @FromAnyThread
    protected void changeBrushSize(@NotNull final Float size) {

        if (state != null) {
            state.setBrushSize(size);
        }

        EXECUTOR_MANAGER.addJmeTask(() -> setBrushSize(size));
    }

    /**
     * Set the brush size.
     *
     * @param size the brush size.
     */
    @JmeThread
    protected void setBrushSize(@NotNull Float size) {
        getToolControl().setBrushSize(size);
    }

    /**
     * Change brush powers.
     */
    @FromAnyThread
    protected void changeBrushPower(@NotNull Float power) {

        if (state != null) {
            state.setBrushPower(power);
        }

        EXECUTOR_MANAGER.addJmeTask(() -> setBrushPower(power));
    }

    /**
     * Set the brush power.
     *
     * @param power the brush power.
     */
    @JmeThread
    protected void setBrushPower(@NotNull Float power) {
        getToolControl().setBrushPower(power);
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

    /**
     * Get the brush power field.
     *
     * @return the brush power field.
     */
    @FxThread
    protected @NotNull FloatTextField getBrushPowerField() {
        return notNull(brushPowerField);
    }

    /**
     * Get the brush size field.
     *
     * @return the brush size field.
     */
    @FxThread
    protected @NotNull FloatTextField getBrushSizeField() {
        return notNull(brushSizeField);
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
        EXECUTOR_MANAGER.addJmeTask(() -> getCursorNode().addControl(getToolControl()));
    }

    @Override
    @FxThread
    public void notifyHided() {
        setShowed(false);
        EXECUTOR_MANAGER.addJmeTask(() -> getCursorNode().removeControl(getToolControl()));
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
