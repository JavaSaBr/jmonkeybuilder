package com.ss.editor.model;

import static java.lang.Math.*;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.input.*;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.config.Config;
import com.ss.editor.util.JmeUtils;
import com.ss.rlib.common.geom.util.AngleUtils;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.common.util.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * The implementation of editor camera based on {@link ChaseCamera}.
 *
 * @author JavaSaBr
 */
public class EditorCamera implements ActionListener, AnalogListener, Control {

    private static final Logger LOGGER = LoggerManager.getLogger(EditorCamera.class);

    private static final String CHASECAM_TOGGLEROTATE =
            "jMB.Editor." + CameraInput.CHASECAM_TOGGLEROTATE;

    private static final String CHASECAM_DOWN =
            "jMB.Editor." + CameraInput.CHASECAM_DOWN;

    private static final String CHASECAM_UP =
            "jMB.Editor." + CameraInput.CHASECAM_UP;

    private static final String CHASECAM_MOVELEFT =
            "jMB.Editor." + CameraInput.CHASECAM_MOVELEFT;

    private static final String CHASECAM_MOVERIGHT =
            "jMB.Editor." + CameraInput.CHASECAM_MOVERIGHT;

    private static final String CHASECAM_ZOOMIN =
            "jMB.Editor." + CameraInput.CHASECAM_ZOOMIN;

    private static final String CHASECAM_ZOOMOUT =
            "jMB.Editor." + CameraInput.CHASECAM_ZOOMOUT;

    private static final String[] ALL_INPUTS = {
            CHASECAM_TOGGLEROTATE,
            CHASECAM_DOWN,
            CHASECAM_UP,
            CHASECAM_MOVELEFT,
            CHASECAM_MOVERIGHT,
            CHASECAM_ZOOMIN,
            CHASECAM_ZOOMOUT
    };

    /**
     * The enum Perspective.
     */
    public enum Perspective {
        /**
         * Back perspective.
         */
        BACK,
        /**
         * Right perspective.
         */
        RIGHT,
        /**
         * Top perspective.
         */
        TOP,
        /**
         * Bottom perspective.
         */
        BOTTOM
    }

    /**
     * The enum Direction.
     */
    public enum Direction {
        /**
         * Left direction.
         */
        LEFT,
        /**
         * Right direction.
         */
        RIGHT,
        /**
         * Top direction.
         */
        TOP,
        /**
         * Bottom direction.
         */
        BOTTOM
    }

    @NotNull
    private final Vector3f targetDir;

    @NotNull
    private final Vector3f position;

    @NotNull
    private final Vector3f targetLocation;

    @NotNull
    private final Vector3f temp;

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

    @Nullable
    private Spatial target;

    private float minDistance;
    private float maxDistance;
    private float distance;

    private float rotationSpeed;

    private float hRotation;
    private float vRotation;

    private float trailingRotationInertia;

    private float zoomSensitivity;
    private float rotationSensitivity;
    private float chasingSensitivity;
    private float trailingSensitivity;

    private float hRotationLerpFactor;
    private float vRotationLerpFactor;
    private float distanceLerpFactor;
    private float trailingLerpFactor;

    private float targetHRotation = hRotation;
    private float targetVRotation = vRotation;

    private float targetDistance;
    private float offsetDistance;

    private float previousTargetRotation;

    private boolean enabled;
    private boolean dragToRotate;
    private boolean trailingEnabled;
    private boolean hideCursorOnRotate;

    private boolean rotating;
    private boolean verticalRotating;
    private boolean smoothMotion;
    private boolean targetMoves;

    private boolean zooming;
    private boolean trailing;
    private boolean chasing;
    private boolean canRotate;
    private boolean zoomin;
    private boolean lockRotation;

    public EditorCamera(@NotNull Camera camera, @NotNull Spatial target) {
        this(camera);
        target.addControl(this);
    }

    public EditorCamera(@NotNull Camera camera) {
        this.camera = camera;
        this.initialUpVec = Vector3f.UNIT_Y.clone();
        this.targetDir = new Vector3f();
        this.position = new Vector3f();
        this.targetLocation = new Vector3f();
        this.lookAtOffset = new Vector3f();
        this.temp = new Vector3f();
        this.prevPos = new Vector3f();
        this.minDistance = 1.0f;
        this.maxDistance = 40.0f;
        this.distance = 20;
        this.rotationSpeed = 1.0f;
        this.hRotation = 0;
        this.trailingRotationInertia = 0.05f;
        this.zoomSensitivity = 2f;
        this.chasingSensitivity = 5f;
        this.rotationSensitivity = 5f;
        this.trailingSensitivity = 0.5f;
        this.vRotation = FastMath.PI / 6;
        this.hRotationLerpFactor = 0;
        this.trailingLerpFactor = 0;
        this.vRotationLerpFactor = 0;
        this.targetDistance = distance;
        this.distanceLerpFactor = 0;
        this.offsetDistance = 0.002f;
        this.enabled = true;
        this.dragToRotate = true;
        this.trailingEnabled = true;
        this.hideCursorOnRotate = true;
        this.rotating = false;
        this.verticalRotating = false;
        this.smoothMotion = false;
        this.targetMoves = false;
        this.zooming = false;
        this.trailing = false;
        this.chasing = false;
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

    /**
     * Get the target spatial.
     *
     * @return the target spatial.
     */
    @JmeThread
    public @NotNull Spatial getTarget() {
        return ObjectUtils.notNull(target);
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
        } else if (!CHASECAM_TOGGLEROTATE.equals(name)) {
            return;
        }

        if (Config.DEV_CAMERA_DEBUG) {
            LOGGER.debug(this, keyPressed, flag -> "Toggle camera " + flag);
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
        this.lockRotation = lockRotation;
    }

    @Override
    @JmeThread
    public void onAnalog(@NotNull String name, float value, float tpf) {

        if (!enabled) {
            return;
        }

        if (name.equals(CHASECAM_MOVELEFT) && !lockRotation) {
            rotateCamera(-value * 3);
        } else if (name.equals(CHASECAM_MOVERIGHT) && !lockRotation) {
            rotateCamera(value * 3);
        } else if (name.equals(CHASECAM_UP) && !lockRotation) {
            verticalRotateCamera(value * 3);
        } else if (name.equals(CHASECAM_DOWN) && !lockRotation) {
            verticalRotateCamera(-value * 3);
        } else if (name.equals(CHASECAM_ZOOMIN)) {
            zoomCamera(-value);
            if (!zoomin) distanceLerpFactor = 0;
            zoomin = true;
        } else if (name.equals(CHASECAM_ZOOMOUT)) {
            zoomCamera(value);
            if (zoomin) distanceLerpFactor = 0;
            zoomin = false;
        }
    }

    /**
     * Register inputs with the input manager.
     *
     * @param inputManager the input manager.
     */
    @JmeThread
    public final void registerInput(@NotNull InputManager inputManager) {
        this.inputManager = inputManager;

        JmeUtils.addMapping(CHASECAM_DOWN, inputManager, new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        JmeUtils.addMapping(CHASECAM_UP, inputManager, new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        JmeUtils.addMapping(CHASECAM_ZOOMIN, inputManager, new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        JmeUtils.addMapping(CHASECAM_ZOOMOUT, inputManager, new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
        JmeUtils.addMapping(CHASECAM_MOVELEFT, inputManager, new MouseAxisTrigger(MouseInput.AXIS_X, true));
        JmeUtils.addMapping(CHASECAM_MOVERIGHT, inputManager, new MouseAxisTrigger(MouseInput.AXIS_X, false));
        JmeUtils.addMapping(CHASECAM_TOGGLEROTATE, inputManager, new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE));

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
    protected void computePosition() {

        var highDistance = (distance) * FastMath.sin((FastMath.PI / 2) - vRotation);
        var newX = highDistance * FastMath.cos(hRotation);
        var newY = (distance) * FastMath.sin(vRotation);
        var newZ = highDistance * FastMath.sin(hRotation);

        position.set(newX, newY, newZ)
                .addLocal(getTarget().getWorldTranslation());
    }

    /**
     * Rotate this camera in horizontal.
     *
     * @param value the value.
     */
    protected void rotateCamera(float value) {

        if (!canRotate || !enabled) {
            return;
        }

        rotating = true;
        targetHRotation += value * rotationSpeed;
    }

    /**
     * Zoom camera.
     *
     * @param value the value.
     */
    protected void zoomCamera(float value) {

        if (!enabled) {
            return;
        }

        zooming = true;

        targetDistance += value * zoomSensitivity * sqrt(targetDistance);
        targetDistance = max(min(targetDistance, maxDistance), minDistance);
    }

    /**
     * Vertical rotate camera.
     *
     * @param value the value
     */
    protected void verticalRotateCamera(float value) {

        if (!canRotate || !enabled) {
            return;
        }

        verticalRotating = true;
        targetVRotation += value * rotationSpeed;
    }

    /**
     * Updates the camera, should only be called internally
     *
     * @param tpf the tpf
     */
    @JmeThread
    public void updateCamera(float tpf) {

        if (!enabled) {
            return;
        }

        targetLocation.set(getTarget().getWorldTranslation())
                .addLocal(lookAtOffset);

        if (smoothMotion) {

            // computation of target direction
            targetDir.set(targetLocation)
                    .subtractLocal(prevPos);

            float dist = targetDir.length();

            //Low pass filtering on the target postition to avoid shaking when physics are enabled.
            if (offsetDistance < dist) {
                //target moves, start chasing.
                chasing = true;
                //target moves, start trailing if it has to.
                if (trailingEnabled) {
                    trailing = true;
                }
                //target moves...
                targetMoves = true;
            } else {
                //if target was moving, we compute a slight offset in rotation to avoid a rought stop of the camera
                //We do not if the player is rotationg the camera
                if (targetMoves && !canRotate) {
                    if (targetHRotation - hRotation > trailingRotationInertia) {
                        targetHRotation = hRotation + trailingRotationInertia;
                    } else if (targetHRotation - hRotation < -trailingRotationInertia) {
                        targetHRotation = hRotation - trailingRotationInertia;
                    }
                }
                //Target stops
                targetMoves = false;
            }

            //the user is rotating the camera by dragging the mouse
            if (canRotate) {
                //reseting the trailing lerp factor
                trailingLerpFactor = 0;
                //stop trailing user has the control
                trailing = false;
            }


            if (trailingEnabled && trailing) {
                if (targetMoves) {
                    //computation if the inverted direction of the target
                    Vector3f a = targetDir.negate().normalizeLocal();
                    //the x unit vector
                    Vector3f b = Vector3f.UNIT_X;
                    //2d is good enough
                    a.y = 0;
                    //computation of the rotation angle between the x axis and the trail
                    if (targetDir.z > 0) {
                        targetHRotation = FastMath.TWO_PI - FastMath.acos(a.dot(b));
                    } else {
                        targetHRotation = FastMath.acos(a.dot(b));
                    }
                    if (targetHRotation - hRotation > FastMath.PI || targetHRotation - hRotation < -FastMath.PI) {
                        targetHRotation -= FastMath.TWO_PI;
                    }

                    //if there is an important change in the direction while trailing reset of the lerp factor to avoid jumpy movements
                    if (targetHRotation != previousTargetRotation && FastMath.abs(targetHRotation - previousTargetRotation) > FastMath.PI / 8) {
                        trailingLerpFactor = 0;
                    }
                    previousTargetRotation = targetHRotation;
                }
                //computing lerp factor
                trailingLerpFactor = min(trailingLerpFactor + tpf * tpf * trailingSensitivity, 1);
                //computing rotation by linear interpolation
                hRotation = FastMath.interpolateLinear(trailingLerpFactor, hRotation, targetHRotation);

                //if the rotation is near the target rotation we're good, that's over
                if (targetHRotation + 0.01f >= hRotation && targetHRotation - 0.01f <= hRotation) {
                    trailing = false;
                    trailingLerpFactor = 0;
                }
            }

            //linear interpolation of the distance while chasing
            if (chasing) {
                distance = temp.set(targetLocation).subtractLocal(camera.getLocation()).length();
                distanceLerpFactor = min(distanceLerpFactor + (tpf * tpf * chasingSensitivity * 0.05f), 1);
                distance = FastMath.interpolateLinear(distanceLerpFactor, distance, targetDistance);
                if (targetDistance + 0.01f >= distance && targetDistance - 0.01f <= distance) {
                    distanceLerpFactor = 0;
                    chasing = false;
                }
            }

            //linear interpolation of the distance while zooming
            if (zooming) {
                distanceLerpFactor = min(distanceLerpFactor + (tpf * tpf * zoomSensitivity), 1);
                distance = FastMath.interpolateLinear(distanceLerpFactor, distance, targetDistance);
                if (targetDistance + 0.1f >= distance && targetDistance - 0.1f <= distance) {
                    zooming = false;
                    distanceLerpFactor = 0;
                }
            }

            //linear interpolation of the rotation while rotating horizontally
            if (rotating) {
                hRotationLerpFactor = min(hRotationLerpFactor + tpf * tpf * rotationSensitivity, 1);
                hRotation = FastMath.interpolateLinear(hRotationLerpFactor, hRotation, targetHRotation);
                if (targetHRotation + 0.01f >= hRotation && targetHRotation - 0.01f <= hRotation) {
                    rotating = false;
                    hRotationLerpFactor = 0;
                }
            }

            //linear interpolation of the rotation while rotating vertically
            if (verticalRotating) {
                vRotationLerpFactor = min(vRotationLerpFactor + tpf * tpf * rotationSensitivity, 1);
                vRotation = FastMath.interpolateLinear(vRotationLerpFactor, vRotation, targetVRotation);
                if (targetVRotation + 0.01f >= vRotation && targetVRotation - 0.01f <= vRotation) {
                    verticalRotating = false;
                    vRotationLerpFactor = 0;
                }
            }
            //computing the position
            computePosition();
            //setting the position at last
            camera.setLocation(position.addLocal(lookAtOffset));
        } else {
            //easy no smooth motion
            vRotation = targetVRotation;
            hRotation = targetHRotation;
            distance = targetDistance;
            computePosition();
            camera.setLocation(position.addLocal(lookAtOffset));
        }
        //keeping track on the previous position of the target
        prevPos.set(targetLocation);

        //the camera looks at the target
        camera.lookAt(targetLocation, initialUpVec);
    }

    /**
     * Return the enabled/disabled state of the camera
     *
     * @return true if the camera is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Enable or disable the camera
     *
     * @param enabled true to enable
     */
    @JmeThread
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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
        target = spatial;

        if (spatial == null) {
            return;
        }

        computePosition();
        prevPos.set(target.getWorldTranslation());
        camera.setLocation(position);
    }

    /**
     * update the camera control, should only be used internally
     */
    public void update(float tpf) {
    }

    /**
     * renders the camera control, should only be used internally
     */
    public void render(@NotNull RenderManager rm, @NotNull ViewPort vp) {
        //nothing to render
    }

    /**
     * Write the camera
     *
     * @param ex the exporter
     */
    public void write(@NotNull JmeExporter ex) throws IOException {
        throw new UnsupportedOperationException("remove ChaseCamera before saving");
    }

    /**
     * Read the camera
     */
    public void read(@NotNull JmeImporter im) throws IOException {
        InputCapsule ic = im.getCapsule(this);
        maxDistance = ic.readFloat("maxDistance", 40);
        minDistance = ic.readFloat("minDistance", 1);
    }

    /**
     * Is smooth motion boolean.
     *
     * @return True is smooth motion is enabled for this chase camera
     */
    public boolean isSmoothMotion() {
        return smoothMotion;
    }

    /**
     * Enables smooth motion for this chase camera
     *
     * @param smoothMotion the smooth motion
     */
    public void setSmoothMotion(boolean smoothMotion) {
        this.smoothMotion = smoothMotion;
    }

    /**
     * returns the chasing sensitivity
     *
     * @return the chasing sensitivity
     */
    public float getChasingSensitivity() {
        return chasingSensitivity;
    }

    /**
     * Sets the chasing sensitivity, the lower the value the slower the camera will follow the target when it moves
     * default is 5 Only has an effect if smoothMotion is set to true and trailing is enabled
     *
     * @param chasingSensitivity the chasing sensitivity
     */
    public void setChasingSensitivity(float chasingSensitivity) {
        this.chasingSensitivity = chasingSensitivity;
    }

    /**
     * Returns the rotation sensitivity
     *
     * @return the rotation sensitivity
     */
    public float getRotationSensitivity() {
        return rotationSensitivity;
    }

    /**
     * Sets the rotation sensitivity, the lower the value the slower the camera will rotates around the target when
     * draging with the mouse default is 5, values over 5 should have no effect. If you want a significant slow down try
     * values below 1. Only has an effect if smoothMotion is set to true
     *
     * @param rotationSensitivity the rotation sensitivity
     */
    public void setRotationSensitivity(float rotationSensitivity) {
        this.rotationSensitivity = rotationSensitivity;
    }

    /**
     * returns true if the trailing is enabled
     *
     * @return the boolean
     */
    public boolean isTrailingEnabled() {
        return trailingEnabled;
    }

    /**
     * Enable the camera trailing : The camera smoothly go in the targets trail when it moves. Only has an effect if
     * smoothMotion is set to true
     *
     * @param trailingEnabled the trailing enabled
     */
    public void setTrailingEnabled(boolean trailingEnabled) {
        this.trailingEnabled = trailingEnabled;
    }

    /**
     * returns the trailing rotation inertia
     *
     * @return the trailing rotation inertia
     */
    public float getTrailingRotationInertia() {
        return trailingRotationInertia;
    }

    /**
     * Sets the trailing rotation inertia : default is 0.1. This prevent the camera to roughtly stop when the target
     * stops moving before the camera reached the trail position. Only has an effect if smoothMotion is set to true and
     * trailing is enabled
     *
     * @param trailingRotationInertia the trailing rotation inertia
     */
    public void setTrailingRotationInertia(float trailingRotationInertia) {
        this.trailingRotationInertia = trailingRotationInertia;
    }

    /**
     * returns the trailing sensitivity
     *
     * @return the trailing sensitivity
     */
    public float getTrailingSensitivity() {
        return trailingSensitivity;
    }

    /**
     * Only has an effect if smoothMotion is set to true and trailing is enabled Sets the trailing sensitivity, the
     * lower the value, the slower the camera will go in the target trail when it moves. default is 0.5;
     *
     * @param trailingSensitivity the trailing sensitivity
     */
    public void setTrailingSensitivity(float trailingSensitivity) {
        this.trailingSensitivity = trailingSensitivity;
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
     * @param dragToRotate When true, the user must hold the mouse button and drag over the screen to rotate the camera,                     and the cursor is visible until dragged. Otherwise, the cursor is invisible at all times and                     holding the mouse button is not needed to rotate the camera. This feature is disabled by                     default.
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
    public float getHorizontalRotation() {
        return hRotation;
    }

    /**
     * returns the current vertical rotation around the target in radians.
     *
     * @return the vertical rotation
     */
    public float getvRotation() {
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