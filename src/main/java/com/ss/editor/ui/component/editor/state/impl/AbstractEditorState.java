package com.ss.editor.ui.component.editor.state.impl;

import static com.ss.rlib.util.ObjectUtils.notNull;
import static java.lang.Math.abs;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.ui.component.editor.state.EditorState;
import com.ss.editor.ui.component.editor.state.EditorToolConfig;
import com.ss.rlib.util.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * The base implementation of a state container for an editor.
 *
 * @author JavaSaBr
 */
public abstract class AbstractEditorState implements EditorState, EditorToolConfig {

    /**
     * The constant serialVersionUID.
     */
    public static final long serialVersionUID = 3;

    /**
     * The constant EDITOR_CONFIG.
     */
    @NotNull
    protected static final EditorConfig EDITOR_CONFIG = EditorConfig.getInstance();

    /**
     * The constant EMPTY_ADDITIONAL_STATES.
     */
    @NotNull
    protected static final AdditionalEditorState[] EMPTY_ADDITIONAL_STATES = new AdditionalEditorState[0];

    /**
     * The change handler.
     */
    @Nullable
    protected transient volatile Runnable changeHandler;

    /**
     * The list of additional states.
     */
    @NotNull
    private volatile AdditionalEditorState[] additionalStates;

    /**
     * The camera location.
     */
    @Nullable
    protected volatile Vector3f cameraLocation;

    /**
     * The vertical camera rotation.
     */
    protected volatile float cameraVRotation;

    /**
     * The horizontal camera rotation.
     */
    protected volatile float cameraHRotation;

    /**
     * The camera zoom.
     */
    protected volatile float cameraTDistance;

    /**
     * The width of tool split panel.
     */
    protected volatile int toolWidth;

    /**
     * The flag of collapsing split panel.
     */
    protected volatile boolean toolCollapsed;

    /**
     * Instantiates a new Abstract editor state.
     */
    public AbstractEditorState() {
        this.additionalStates = EMPTY_ADDITIONAL_STATES;
        this.toolWidth = 250;
        this.toolCollapsed = false;
        this.cameraLocation = new Vector3f();
        this.cameraVRotation = FastMath.PI / 6;
        this.cameraTDistance = 20;
        this.cameraHRotation = 0;
    }

    @Override
    public void setChangeHandler(@NotNull final Runnable changeHandler) {
        this.changeHandler = changeHandler;
        for (final AdditionalEditorState additionalState : additionalStates) {
            additionalState.setChangeHandler(changeHandler);
        }
    }

    @NotNull
    @Override
    public <T extends AbstractEditorState> T getOrCreateAdditionalState(@NotNull final Class<T> type,
                                                                        @NotNull final Supplier<T> factory) {

        for (final AdditionalEditorState additionalState : additionalStates) {
            if (type.isInstance(additionalState)) {
                return type.cast(additionalState);
            }
        }

        final AdditionalEditorState newAdditionalState = (AdditionalEditorState) factory.get();
        newAdditionalState.setChangeHandler(notNull(changeHandler));

        this.additionalStates = ArrayUtils.addToArray(additionalStates, newAdditionalState,
                AdditionalEditorState.class);

        return type.cast(newAdditionalState);
    }

    /**
     * Gets change handler.
     *
     * @return the change handler.
     */
    @Nullable
    protected Runnable getChangeHandler() {
        return changeHandler;
    }

    @Override
    public int getToolWidth() {
        return toolWidth;
    }

    @Override
    public void setToolWidth(final int toolWidth) {
        final boolean changed = abs(getToolWidth() - toolWidth) > 3;
        this.toolWidth = toolWidth;
        if (changed) notifyChange();
    }

    @Override
    public boolean isToolCollapsed() {
        return toolCollapsed;
    }

    @Override
    public void setToolCollapsed(final boolean toolCollapsed) {
        final boolean changed = isToolCollapsed() != toolCollapsed;
        this.toolCollapsed = toolCollapsed;
        if (changed) notifyChange();
    }

    /**
     * Sets camera h rotation.
     *
     * @param cameraHRotation the new horizontal rotation.
     */
    public void setCameraHRotation(final float cameraHRotation) {
        final boolean changed = getCameraHRotation() != cameraHRotation;
        this.cameraHRotation = cameraHRotation;
        if (changed) notifyChange();
    }

    protected void notifyChange() {
        if (changeHandler != null) {
            changeHandler.run();
        }
    }

    /**
     * Gets camera h rotation.
     *
     * @return the horizontal camera rotation.
     */
    public float getCameraHRotation() {
        return cameraHRotation;
    }

    /**
     * Sets camera location.
     *
     * @param cameraLocation the new camera position.
     */
    public void setCameraLocation(@NotNull final Vector3f cameraLocation) {
        final boolean changed = Objects.equals(getCameraLocation(), cameraLocation);
        getCameraLocation().set(cameraLocation);
        if (changed) notifyChange();
    }

    /**
     * Gets camera location.
     *
     * @return the camera location.
     */
    @NotNull
    public Vector3f getCameraLocation() {
        if (cameraLocation == null) cameraLocation = new Vector3f();
        return notNull(cameraLocation);
    }

    /**
     * Sets camera t distance.
     *
     * @param cameraTDistance the new camera zoom.
     */
    public void setCameraTDistance(final float cameraTDistance) {
        final boolean changed = getCameraTDistance() != cameraTDistance;
        this.cameraTDistance = cameraTDistance;
        if (changed) notifyChange();
    }

    /**
     * Gets camera t distance.
     *
     * @return the camera zoom.
     */
    public float getCameraTDistance() {
        return cameraTDistance;
    }

    /**
     * Sets camera v rotation.
     *
     * @param cameraVRotation the new vertical rotation.
     */
    public void setCameraVRotation(final float cameraVRotation) {
        final boolean changed = getCameraVRotation() != cameraVRotation;
        this.cameraVRotation = cameraVRotation;
        if (changed) notifyChange();
    }

    /**
     * Gets camera v rotation.
     *
     * @return the vertical camera rotation.
     */
    public float getCameraVRotation() {
        return cameraVRotation;
    }

    @Override
    public String toString() {
        return "AbstractEditorState{" +
                "cameraLocation=" + cameraLocation +
                ", cameraVRotation=" + cameraVRotation +
                ", cameraHRotation=" + cameraHRotation +
                ", cameraTDistance=" + cameraTDistance +
                ", toolWidth=" + toolWidth +
                ", toolCollapsed=" + toolCollapsed +
                '}';
    }
}
