package com.ss.builder.fx.component.editor.state.impl;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.component.editor.event.CameraChangedFileEditorEvent;
import com.ss.builder.fx.component.editor.event.FileEditorEvent;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.component.editor.event.CameraChangedFileEditorEvent;
import com.ss.builder.fx.component.editor.event.FileEditorEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * The base implementation of a state container for an 3D editor.
 *
 * @author JavaSaBr
 */
public class Editor3dEditorState extends AbstractEditorState {

    /**
     * The constant serialVersionUID.
     */
    public static final long serialVersionUID = 2;

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
    protected volatile float cameraFlySpeed;

    /**
     * The camera zoom.
     */
    protected volatile float cameraTargetDistance;

    public Editor3dEditorState() {
        this.cameraLocation = new Vector3f();
        this.cameraVRotation = FastMath.PI / 6;
        this.cameraTargetDistance = 20;
        this.cameraHRotation = 0;
        this.cameraFlySpeed = 1;
    }

    @Override
    @FxThread
    public void notify(@NotNull FileEditorEvent event) {
        if (event instanceof CameraChangedFileEditorEvent) {
            var state = ((CameraChangedFileEditorEvent) event).getCameraState();
            setCameraLocation(state.getCameraLocation());
            setCameraHRotation(state.getHRotation());
            setCameraVRotation(state.getVRotation());
            setCameraFlySpeed(state.getCameraFlySpeed());
            setCameraTargetDistance(state.getTargetDistance());
        }
    }

    /**
     * Set the horizontal camera rotation.
     *
     * @param cameraHRotation the new horizontal rotation.
     */
    @FxThread
    public void setCameraHRotation(float cameraHRotation) {
        var changed = getCameraHRotation() != cameraHRotation;
        this.cameraHRotation = cameraHRotation;
        if (changed) notifyChange();
    }

    /**
     * Get the horizontal camera rotation.
     *
     * @return the horizontal camera rotation.
     */
    @FromAnyThread
    public float getCameraHRotation() {
        return cameraHRotation;
    }

    /**
     * Set the new camera position.
     *
     * @param cameraLocation the new camera position.
     */
    @FxThread
    public void setCameraLocation(@NotNull Vector3f cameraLocation) {
        var changed = Objects.equals(getCameraLocation(), cameraLocation);
        getCameraLocation().set(cameraLocation);
        if (changed) notifyChange();
    }

    /**
     * Get the camera location.
     *
     * @return the camera location.
     */
    @FromAnyThread
    public @NotNull Vector3f getCameraLocation() {

        if (cameraLocation == null) {
            cameraLocation = new Vector3f();
        }

        return notNull(cameraLocation);
    }

    /**
     * Set the new camera target distance.
     *
     * @param cameraTargetDistance the new camera target distance.
     */
    @FxThread
    public void setCameraTargetDistance(float cameraTargetDistance) {
        var changed = getCameraTargetDistance() != cameraTargetDistance;
        this.cameraTargetDistance = cameraTargetDistance;
        if (changed) notifyChange();
    }

    /**
     * Get the camera target distance.
     *
     * @return the camera target distance.
     */
    @FromAnyThread
    public float getCameraTargetDistance() {
        return cameraTargetDistance;
    }

    /**
     * Set the camera fly speed.
     *
     * @param cameraFlySpeed the camera fly speed.
     */
    @FxThread
    public void setCameraFlySpeed(float cameraFlySpeed) {
        var changed = getCameraFlySpeed() != cameraFlySpeed;
        this.cameraFlySpeed = cameraFlySpeed;
        if (changed) notifyChange();
    }

    /**
     * Get the camera fly speed.
     *
     * @return the camera fly speed.
     */
    @FromAnyThread
    public float getCameraFlySpeed() {
        return cameraFlySpeed;
    }

    /**
     * Set the new vertical camera rotation.
     *
     * @param cameraVRotation the new vertical camera rotation.
     */
    @FxThread
    public void setCameraVRotation(float cameraVRotation) {
        var changed = getCameraVRotation() != cameraVRotation;
        this.cameraVRotation = cameraVRotation;
        if (changed) notifyChange();
    }

    /**
     * Get the camera vertical rotation.
     *
     * @return the camera vertical rotation.
     */
    @FromAnyThread
    public float getCameraVRotation() {
        return cameraVRotation;
    }

    @Override
    public String toString() {
        return "Editor3DEditorState{" + "cameraLocation=" + cameraLocation + ", cameraVRotation=" + cameraVRotation +
                ", cameraHRotation=" + cameraHRotation + ", cameraFlySpeed=" + cameraFlySpeed + ", cameraTargetDistance=" +
                cameraTargetDistance + '}';
    }
}
