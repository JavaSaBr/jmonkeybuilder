package com.ss.editor.ui.component.editor.state.impl;

import static java.lang.Math.abs;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.ss.editor.ui.component.editor.state.EditorState;
import com.ss.editor.ui.component.editor.state.EditorToolConfig;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * The base implementation of a state container for an editor.
 *
 * @author JavaSaBr
 */
public abstract class AbstractEditorState implements EditorState, EditorToolConfig {

    public static final long serialVersionUID = 2;

    /**
     * The change handler.
     */
    protected transient volatile Runnable changeHandler;

    /**
     * The camera location.
     */
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

    public AbstractEditorState() {
        this.toolWidth = 250;
        this.toolCollapsed = false;
        this.cameraLocation = new Vector3f();
        this.cameraVRotation = FastMath.PI / 6;
        this.cameraTDistance = 20;
        this.cameraHRotation = 0;
    }

    @Override
    public void setChangeHandler(final Runnable changeHandler) {
        this.changeHandler = changeHandler;
    }

    /**
     * @return the change handler.
     */
    private Runnable getChangeHandler() {
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
        if (changed && changeHandler != null) {
            changeHandler.run();
        }
    }

    @Override
    public boolean isToolCollapsed() {
        return toolCollapsed;
    }

    @Override
    public void setToolCollapsed(final boolean toolCollapsed) {
        final boolean changed = isToolCollapsed() != toolCollapsed;
        this.toolCollapsed = toolCollapsed;
        if (changed && changeHandler != null) {
            changeHandler.run();
        }
    }

    /**
     * @param cameraHRotation the new horizontal rotation.
     */
    public void setCameraHRotation(final float cameraHRotation) {
        final boolean changed = getCameraHRotation() != cameraHRotation;
        this.cameraHRotation = cameraHRotation;
        if (changed && changeHandler != null) {
            changeHandler.run();
        }
    }

    /**
     * @return the horizontal camera rotation.
     */
    public float getCameraHRotation() {
        return cameraHRotation;
    }

    /**
     * @param cameraLocation the new camera position.
     */
    public void setCameraLocation(@NotNull final Vector3f cameraLocation) {
        final boolean changed = Objects.equals(getCameraLocation(), cameraLocation);
        getCameraLocation().set(cameraLocation);
        if (changed && changeHandler != null) {
            changeHandler.run();
        }
    }

    /**
     * @return the camera location.
     */
    @NotNull
    public Vector3f getCameraLocation() {
        if (cameraLocation == null) cameraLocation = new Vector3f();
        return cameraLocation;
    }

    /**
     * @param cameraTDistance the new camera zoom.
     */
    public void setCameraTDistance(final float cameraTDistance) {
        final boolean changed = getCameraTDistance() != cameraTDistance;
        this.cameraTDistance = cameraTDistance;
        if (changed && changeHandler != null) {
            changeHandler.run();
        }
    }

    /**
     * @return the camera zoom.
     */
    public float getCameraTDistance() {
        return cameraTDistance;
    }

    /**
     * @param cameraVRotation the new vertical rotation.
     */
    public void setCameraVRotation(final float cameraVRotation) {
        final boolean changed = getCameraVRotation() != cameraVRotation;
        this.cameraVRotation = cameraVRotation;
        if (changed && changeHandler != null) {
            changeHandler.run();
        }
    }

    /**
     * @return the vertical camera rotation.
     */
    public float getCameraVRotation() {
        return cameraVRotation;
    }
}
