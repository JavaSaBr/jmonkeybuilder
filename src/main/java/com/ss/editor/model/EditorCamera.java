package com.ss.editor.model;

import static java.lang.Math.*;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.input.*;
import com.jme3.input.controls.*;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.config.Config;
import com.ss.editor.util.JmeUtils;
import com.ss.rlib.common.geom.util.AngleUtils;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerLevel;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.common.util.ObjectUtils;
import com.ss.rlib.common.util.dictionary.ObjectDictionary;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * The implementation of editor camera based on {@link ChaseCamera}.
 *
 * @author JavaSaBr
 */
public class EditorCamera extends AbstractControl implements ActionListener, AnalogListener {

    private static final Logger LOGGER = LoggerManager.getLogger(EditorCamera.class);

    private static final String CAMERA_TOGGLE_ROTATE = "jMB." + CameraInput.CHASECAM_TOGGLEROTATE;
    private static final String CAMERA_DOWN = "jMB." + CameraInput.CHASECAM_DOWN;
    private static final String CAMERA_UP = "jMB." + CameraInput.CHASECAM_UP;
    private static final String CAMERA_MOVE_LEFT = "jMB." + CameraInput.CHASECAM_MOVELEFT;
    private static final String CAMERA_MOVE_RIGHT = "jMB." + CameraInput.CHASECAM_MOVERIGHT;
    private static final String CAMERA_ZOOM_IN = "jMB." + CameraInput.CHASECAM_ZOOMIN;
    private static final String CAMERA_ZOOM_OUT = "jMB." + CameraInput.CHASECAM_ZOOMOUT;

    private static final String[] ALL_INPUTS = {
            CAMERA_TOGGLE_ROTATE,
            CAMERA_DOWN,
            CAMERA_UP,
            CAMERA_MOVE_LEFT,
            CAMERA_MOVE_RIGHT,
            CAMERA_ZOOM_IN,
            CAMERA_ZOOM_OUT
    };

    private static final ObjectDictionary<String, Trigger> TRIGGERS = ObjectDictionary.of(
            CAMERA_DOWN, new MouseAxisTrigger(MouseInput.AXIS_Y, false),
            CAMERA_UP, new MouseAxisTrigger(MouseInput.AXIS_Y, true),
            CAMERA_ZOOM_IN, new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false),
            CAMERA_ZOOM_OUT, new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true),
            CAMERA_MOVE_LEFT, new MouseAxisTrigger(MouseInput.AXIS_X, true),
            CAMERA_MOVE_RIGHT, new MouseAxisTrigger(MouseInput.AXIS_X, false),
            CAMERA_TOGGLE_ROTATE, new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE)
    );

    public enum Perspective {
        BACK,
        RIGHT,
        TOP,
        BOTTOM
    }

    public enum Direction {
        LEFT,
        RIGHT,
        TOP,
        BOTTOM
    }

    @NotNull
    private final Vector3f position;

    @NotNull
    private final Vector3f targetLocation;

    @NotNull
    private final Vector3f initialUpVec;

    @NotNull
    private final Vector3f prevPos;

    @NotNull
    private final Vector3f lookAtOffset;

    @NotNull
    private final Camera camera;

    @Nullable
    private InputManager inputManager;

    private float minDistance;
    private float maxDistance;
    private float distance;

    private float rotationSpeed;

    private float hRotation;
    private float vRotation;

    private float zoomSensitivity;

    private float targetHRotation = hRotation;
    private float targetVRotation = vRotation;

    private float targetDistance;

    private boolean dragToRotate;
    private boolean hideCursorOnRotate;

    private boolean canRotate;
    private boolean lockRotation;

    public EditorCamera(@NotNull Camera camera, @NotNull Spatial target) {
        this(camera);
        target.addControl(this);
    }

    public EditorCamera(@NotNull Camera camera) {
        this.camera = camera;
        this.initialUpVec = Vector3f.UNIT_Y.clone();
        this.position = new Vector3f();
        this.targetLocation = new Vector3f();
        this.lookAtOffset = new Vector3f();
        this.prevPos = new Vector3f();
        this.minDistance = 1.0f;
        this.maxDistance = 40.0f;
        this.distance = 20;
        this.rotationSpeed = 1.0f;
        this.hRotation = 0;
        this.zoomSensitivity = 2f;
        this.vRotation = FastMath.PI / 6;
        this.targetDistance = distance;
        this.enabled = true;
        this.dragToRotate = true;
        this.hideCursorOnRotate = true;
    }

    /**
     * Get the current camera direction.
     *
     * @return the current direction.
     */
    @JmeThread
    public @NotNull Vector3f getDirection() {
        return camera.getDirection(new Vector3f());
    }

    /**
     * Get the current camera direction to left side.
     *
     * @return the current direction to left side.
     */
    @JmeThread
    public @NotNull Vector3f getLeft() {
        return camera.getLeft(new Vector3f());
    }

    /**
     * Rotate this camera to the direction by the value.
     *
     * @param direction the direction.
     * @param value     the value.
     */
    @JmeThread
    public void rotateTo(@NotNull Direction direction, float value) {

        var hRotation = AngleUtils.radiansToDegree(targetHRotation);
        var vRotation = AngleUtils.radiansToDegree(targetVRotation);

        if (direction == Direction.LEFT) {
            hRotation += value;
        } else if (direction == Direction.RIGHT) {
            hRotation -= value;
        } else if (direction == Direction.TOP) {
            vRotation += value;
        } else if (direction == Direction.BOTTOM) {
            vRotation -= value;
        }

        targetHRotation = AngleUtils.degreeToRadians(hRotation);
        targetVRotation = AngleUtils.degreeToRadians(vRotation);
    }

    /**
     * Rotate this camera to the perspective.
     *
     * @param perspective the perspective.
     */
    @JmeThread
    public void rotateTo(@NotNull Perspective perspective) {

        var hRotation = AngleUtils.degreeToRadians(90);
        var vRotation = AngleUtils.degreeToRadians(0);

        if (perspective == Perspective.BACK) {
            hRotation = AngleUtils.degreeToRadians(-90);
        } else if (perspective == Perspective.RIGHT) {
            hRotation = AngleUtils.degreeToRadians(180);
        } else if (perspective == Perspective.BOTTOM) {
            vRotation = AngleUtils.degreeToRadians(90);
        } else if (perspective == Perspective.TOP) {
            vRotation = AngleUtils.degreeToRadians(-90);
        }

        targetHRotation = hRotation;
        targetVRotation = vRotation;
    }

    /**
     * Get the input manager.
     *
     * @return the input manager
     */
    @JmeThread
    private @NotNull InputManager getInputManager() {
        return ObjectUtils.notNull(inputManager);
    }

    @Override
    @JmeThread
    public @NotNull Spatial getSpatial() {
        return ObjectUtils.notNull(super.getSpatial());
    }

    /**
     * Set the target rotation.
     *
     * @param targetHRotation the target rotation.
     */
    @JmeThread
    public void setTargetHRotation(float targetHRotation) {
        this.targetHRotation = targetHRotation;
    }

    /**
     * Set the target vertical rotation.
     *
     * @param targetVRotation the target vertical rotation.
     */
    @JmeThread
    public void setTargetVRotation(float targetVRotation) {
        this.targetVRotation = targetVRotation;
    }

    /**
     * Set the target distance.
     *
     * @param targetDistance the target distance.
     */
    @JmeThread
    public void setTargetDistance(float targetDistance) {
        this.targetDistance = targetDistance;
    }

    /**
     * Get the target distance.
     *
     * @return the target distance.
     */
    @JmeThread
    public float getTargetDistance() {
        return targetDistance;
    }

    @Override
    @JmeThread
    public void onAction(@NotNull String name, boolean keyPressed, float tpf) {

        if (!enabled || !dragToRotate) {
            return;
        } else if (!CAMERA_TOGGLE_ROTATE.equals(name)) {
            return;
        }

        if (Config.DEV_CAMERA_DEBUG) {
            LOGGER.debug(this, name, keyPressed,
                    (action, pressed) -> "Action[" + action + "], pressed[" + pressed + "]");
        }

        canRotate = keyPressed;

        if (hideCursorOnRotate) {
            getInputManager().setCursorVisible(!keyPressed);
        }
    }

    /**
     * Set true to lock rotation.
     *
     * @param lockRotation true to lock rotation.
     */
    @JmeThread
    public void setLockRotation(boolean lockRotation) {

        if (Config.DEV_CAMERA_DEBUG && LOGGER.isEnabled(LoggerLevel.DEBUG)) {
            LOGGER.debug(this, "Set lock rotation is: " + lockRotation);
        }

        this.lockRotation = lockRotation;
    }

    @Override
    @JmeThread
    public void onAnalog(@NotNull String name, float value, float tpf) {

        if (!enabled) {
            return;
        }

        if (name.equals(CAMERA_MOVE_LEFT) && !lockRotation) {
            hRotateCamera(-value * 3);
        } else if (name.equals(CAMERA_MOVE_RIGHT) && !lockRotation) {
            hRotateCamera(value * 3);
        } else if (name.equals(CAMERA_UP) && !lockRotation) {
            vRotateCamera(value * 3);
        } else if (name.equals(CAMERA_DOWN) && !lockRotation) {
            vRotateCamera(-value * 3);
        } else if (name.equals(CAMERA_ZOOM_IN)) {
            zoomCamera(-value);
        } else if (name.equals(CAMERA_ZOOM_OUT)) {
            zoomCamera(value);
        }
    }

    /**
     * Register inputs with the input manager.
     *
     * @param inputManager the input manager.
     */
    @JmeThread
    public void registerInput(@NotNull InputManager inputManager) {
        this.inputManager = inputManager;

        TRIGGERS.forEach(inputManager, JmeUtils::addMapping);

        inputManager.addListener(this, ALL_INPUTS);
    }

    /**
     * Unregister input.
     *
     * @param inputManager the input manager
     */
    @JmeThread
    public void unregisterInput(@NotNull InputManager inputManager) {
        inputManager.removeListener(this);
    }

    /**
     * Compute position.
     */
    private void computePosition() {

        var highDistance = distance * FastMath.sin((FastMath.PI / 2) - vRotation);

        var x = highDistance * FastMath.cos(hRotation);
        var y = distance * FastMath.sin(vRotation);
        var z = highDistance * FastMath.sin(hRotation);

        position.set(x, y, z)
                .addLocal(getSpatial().getWorldTranslation());
    }

    /**
     * Rotate this camera in horizontal.
     *
     * @param value the value.
     */
    private void hRotateCamera(float value) {

        if (!canRotate || !enabled) {
            return;
        }

        targetHRotation += value * rotationSpeed;
    }

    /**
     * Zoom camera.
     *
     * @param value the value.
     */
    private void zoomCamera(float value) {

        if (!enabled) {
            return;
        }

        targetDistance += value * zoomSensitivity * sqrt(targetDistance);
        targetDistance = max(min(targetDistance, maxDistance), minDistance);
    }

    /**
     * Vertical rotate camera.
     *
     * @param value the value
     */
    private void vRotateCamera(float value) {

        if (!canRotate || !enabled) {
            return;
        }

        targetVRotation += value * rotationSpeed;
    }

    @Override
    protected void controlUpdate(float tpf) {

        targetLocation.set(getSpatial().getWorldTranslation())
                .addLocal(lookAtOffset);

        vRotation = targetVRotation;
        hRotation = targetHRotation;
        distance = targetDistance;

        computePosition();

        camera.setLocation(position.addLocal(lookAtOffset));

        // keeping track on the previous position of the target
        prevPos.set(targetLocation);

        // the camera looks at the target
        camera.lookAt(targetLocation, initialUpVec);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    @Override
    @JmeThread
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!enabled) {
            canRotate = false; // reset this flag in-case it was on before
        }
    }

    /**
     * Returns the max zoom distance of the camera (default is 40)
     *
     * @return maxDistance max distance
     */
    public float getMaxDistance() {
        return maxDistance;
    }

    /**
     * Sets the max zoom distance of the camera (default is 40)
     *
     * @param maxDistance the max distance
     */
    public void setMaxDistance(float maxDistance) {
        this.maxDistance = maxDistance;
        if (maxDistance < distance) {
            zoomCamera(maxDistance - distance);
        }
    }

    /**
     * Returns the min zoom distance of the camera (default is 1)
     *
     * @return minDistance min distance
     */
    public float getMinDistance() {
        return minDistance;
    }

    /**
     * Sets the min zoom distance of the camera (default is 1)
     *
     * @param minDistance the min distance
     */
    public void setMinDistance(float minDistance) {
        this.minDistance = minDistance;
        if (minDistance > distance) {
            zoomCamera(distance - minDistance);
        }
    }

    /**
     * clone this camera for a spatial
     */
    @Override
    public Control cloneForSpatial(Spatial spatial) {

        var editorCamera = new EditorCamera(camera, spatial);
        editorCamera.setMaxDistance(getMaxDistance());
        editorCamera.setMinDistance(getMinDistance());

        if (inputManager != null) {
            editorCamera.registerInput(inputManager);
        }

        return editorCamera;
    }

    /**
     * Sets the spacial for the camera control, should only be used internally
     */
    public void setSpatial(@Nullable Spatial spatial) {
        super.setSpatial(spatial);

        if (spatial == null) {
            return;
        }

        computePosition();
        prevPos.set(spatial.getWorldTranslation());
        camera.setLocation(position);
    }

    /**
     * Write the camera
     *
     * @param exporter the exporter
     */
    public void write(@NotNull JmeExporter exporter) {
        throw new UnsupportedOperationException("remove ChaseCamera before saving");
    }

    /**
     * Read the camera
     */
    public void read(@NotNull JmeImporter importer) throws IOException {
        var ic = importer.getCapsule(this);
        maxDistance = ic.readFloat("maxDistance", 40);
        minDistance = ic.readFloat("minDistance", 1);
    }

    /**
     * returns the zoom sensitivity
     *
     * @return the zoom sensitivity
     */
    public float getZoomSensitivity() {
        return zoomSensitivity;
    }

    /**
     * Sets the zoom sensitivity, the lower the value, the slower the camera will zoom in and out. default is 2.
     *
     * @param zoomSensitivity the zoom sensitivity
     */
    public void setZoomSensitivity(float zoomSensitivity) {
        this.zoomSensitivity = zoomSensitivity;
    }

    /**
     * Returns the rotation speed when the mouse is moved.
     *
     * @return the rotation speed when the mouse is moved.
     */
    public float getRotationSpeed() {
        return rotationSpeed;
    }

    /**
     * Sets the rotate amount when user moves his mouse, the lower the value, the slower the camera will rotate.
     * default is 1.
     *
     * @param rotationSpeed Rotation speed on mouse movement, default is 1.
     */
    public void setRotationSpeed(float rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
    }

    /**
     * Sets the default distance at start of applicaiton
     *
     * @param defaultDistance the default distance
     */
    public void setDefaultDistance(float defaultDistance) {
        distance = defaultDistance;
        targetDistance = distance;
    }

    /**
     * sets the default horizontal rotation in radian of the camera at start of the application
     *
     * @param angleInRad the angle in rad
     */
    public void setDefaultHorizontalRotation(float angleInRad) {
        hRotation = angleInRad;
        targetHRotation = angleInRad;
    }

    /**
     * sets the default vertical rotation in radian of the camera at start of the application
     *
     * @param angleInRad the angle in rad
     */
    public void setDefaultVerticalRotation(float angleInRad) {
        vRotation = angleInRad;
        targetVRotation = angleInRad;
    }

    /**
     * Is drag to rotate boolean.
     *
     * @return If drag to rotate feature is enabled.
     * @see FlyByCamera#setDragToRotate(boolean) FlyByCamera#setDragToRotate(boolean)FlyByCamera#setDragToRotate(boolean)
     */
    public boolean isDragToRotate() {
        return dragToRotate;
    }

    /**
     * Sets drag to rotate.
     *
     * @param dragToRotate When true, the user must hold the mouse button and drag over the screen to rotate the camera,
     *                     and the cursor is visible until dragged. Otherwise, the cursor is invisible at all times and
     *                     holding the mouse button is not needed to rotate the camera. This feature is disabled by
     *                     default.
     */
    public void setDragToRotate(boolean dragToRotate) {
        this.dragToRotate = dragToRotate;
        this.canRotate = !dragToRotate;

        if (inputManager != null) {
            inputManager.setCursorVisible(dragToRotate);
        }
    }

    /**
     * return the current distance from the camera to the target
     *
     * @return the distance to target
     */
    public float getDistanceToTarget() {
        return distance;
    }

    /**
     * returns the current horizontal rotation around the target in radians
     *
     * @return the horizontal rotation
     */
    public float getHRotation() {
        return hRotation;
    }

    /**
     * returns the current vertical rotation around the target in radians.
     *
     * @return the vertical rotation
     */
    public float getVRotation() {
        return vRotation;
    }

    /**
     * returns the offset from the target's position where the camera looks at
     *
     * @return the look at offset
     */
    public Vector3f getLookAtOffset() {
        return lookAtOffset;
    }

    /**
     * Sets the offset from the target's position where the camera looks at
     *
     * @param lookAtOffset the look at offset
     */
    public void setLookAtOffset(Vector3f lookAtOffset) {
        this.lookAtOffset.set(lookAtOffset);
    }

    /**
     * Is hide cursor on rotate boolean.
     *
     * @return the boolean
     */
    public boolean isHideCursorOnRotate() {
        return hideCursorOnRotate;
    }

    /**
     * Sets hide cursor on rotate.
     *
     * @param hideCursorOnRotate the hide cursor on rotate
     */
    public void setHideCursorOnRotate(boolean hideCursorOnRotate) {
        this.hideCursorOnRotate = hideCursorOnRotate;
    }
}