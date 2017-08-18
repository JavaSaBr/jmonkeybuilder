package com.ss.editor.ui.component.editor.state.impl;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * The base implementation of a state container for an 3D editor.
 *
 * @author JavaSaBr
 */
public class Editor3DEditorState extends AbstractEditorState {

    /**
     * The constant serialVersionUID.
     */
    public static final long serialVersionUID = 1;

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
     * The camera speed.
     */
    protected volatile float cameraSpeed;

    /**
     * The camera zoom.
     */
    protected volatile float cameraTDistance;

    /**
     * Instantiates a new Abstract editor state.
     */
    public Editor3DEditorState() {
        this.cameraLocation = new Vector3f();
        this.cameraVRotation = FastMath.PI / 6;
        this.cameraTDistance = 20;
        this.cameraHRotation = 0;
        this.cameraSpeed = 1;
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
     * Set the camera speed.
     *
     * @param cameraSpeed the camera speed.
     */
    public void setCameraSpeed(final float cameraSpeed) {
        final boolean changed = getCameraSpeed() != cameraSpeed;
        this.cameraSpeed = cameraSpeed;
        if (changed) notifyChange();
    }

    /**
     * Get the camera speed.
     *
     * @return the camera speed.
     */
    public float getCameraSpeed() {
        return cameraSpeed;
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
        return "Editor3DEditorState{" + "cameraLocation=" + cameraLocation + ", cameraVRotation=" + cameraVRotation +
                ", cameraHRotation=" + cameraHRotation + ", cameraSpeed=" + cameraSpeed + ", cameraTDistance=" +
                cameraTDistance + '}';
    }
}
