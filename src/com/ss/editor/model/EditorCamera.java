package com.ss.editor.model;

import static com.ss.rlib.geom.util.AngleUtils.degreeToRadians;
import static com.ss.rlib.geom.util.AngleUtils.radiansToDegree;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.sqrt;
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

import java.io.IOException;

/**
 * The implementation of editor camera based on {@link ChaseCamera}.
 *
 * @author JavaSaBr
 */
public class EditorCamera implements ActionListener, AnalogListener, Control {

    private static final String CHASECAM_TOGGLEROTATE = EditorCamera.class.getSimpleName() + "_" + CameraInput.CHASECAM_TOGGLEROTATE;
    private static final String CHASECAM_DOWN = EditorCamera.class.getSimpleName() + "_" + CameraInput.CHASECAM_DOWN;
    private static final String CHASECAM_UP = EditorCamera.class.getSimpleName() + "_" + CameraInput.CHASECAM_UP;
    private static final String CHASECAM_MOVELEFT = EditorCamera.class.getSimpleName() + "_" + CameraInput.CHASECAM_MOVELEFT;
    private static final String CHASECAM_MOVERIGHT = EditorCamera.class.getSimpleName() + "_" + CameraInput.CHASECAM_MOVERIGHT;
    private static final String CHASECAM_ZOOMIN = EditorCamera.class.getSimpleName() + "_" + CameraInput.CHASECAM_ZOOMIN;
    private static final String CHASECAM_ZOOMOUT = EditorCamera.class.getSimpleName() + "_" + CameraInput.CHASECAM_ZOOMOUT;

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

    private InputManager inputManager;

    private final Camera camera;

    private Spatial target;

    private final Vector3f targetDir;
    private final Vector3f position;

    private final Vector3f targetLocation;
    private final Vector3f temp;

    private Vector3f initialUpVec;
    private Vector3f prevPos;
    private Vector3f lookAtOffset;

    private float minDistance = 1.0f;
    private float maxDistance = 40.0f;

    private float distance = 20;

    private float rotationSpeed = 1.0f;
    private float rotation = 0;

    private float trailingRotationInertia = 0.05f;

    private float zoomSensitivity = 2f;
    private float rotationSensitivity = 5f;
    private float chasingSensitivity = 5f;
    private float trailingSensitivity = 0.5f;

    private float verticalRotation = FastMath.PI / 6;

    private float rotationLerpFactor = 0;
    private float trailingLerpFactor = 0;

    /**
     * Целевой разворот камеры.
     */
    private float targetRotation = rotation;

    /**
     * Целевой разворот камеры по вертикали.
     */
    private float targetVRotation = verticalRotation;

    private float vRotationLerpFactor = 0;
    private float targetDistance = distance;
    private float distanceLerpFactor = 0;

    private float offsetDistance = 0.002f;

    private float previousTargetRotation;

    private boolean enabled = true;
    private boolean dragToRotate = true;
    private boolean trailingEnabled = true;
    private boolean hideCursorOnRotate = true;

    private boolean rotating = false;
    private boolean verticalRotating = false;
    private boolean smoothMotion = false;
    private boolean targetMoves = false;

    private boolean zooming = false;
    private boolean trailing = false;
    private boolean chasing = false;
    private boolean canRotate;
    private boolean zoomin;
    private boolean lockRotation;

    /**
     * Constructs the chase camera
     *
     * @param camera the application camera
     * @param target the spatial to follow
     */
    public EditorCamera(final Camera camera, final Spatial target) {
        this(camera);
        target.addControl(this);
    }

    /**
     * Constructs the chase camera if you use this constructor you have to attach the camera later to a spatial doing
     * spatial.addControl(chaseCamera);
     *
     * @param camera the application camera
     */
    public EditorCamera(final Camera camera) {
        this.camera = camera;
        this.initialUpVec = Vector3f.UNIT_Y;
        this.targetDir = new Vector3f();
        this.position = new Vector3f();
        this.targetLocation = new Vector3f(0, 0, 0);
        this.lookAtOffset = new Vector3f(0, 0, 0);
        this.temp = new Vector3f(0, 0, 0);
    }

    /**
     * Разворот камеры.
     *
     * @param direction the direction
     * @param value     the value
     */
    public void rotateTo(final Direction direction, final float value) {

        float targetRotation = radiansToDegree(getTargetRotation());
        float targetVRotation = radiansToDegree(getTargetVRotation());

        if (direction == Direction.LEFT) {
            targetRotation += value;
        } else if (direction == Direction.RIGHT) {
            targetRotation -= value;
        } else if (direction == Direction.TOP) {
            targetVRotation += value;
        } else if (direction == Direction.BOTTOM) {
            targetVRotation -= value;
        }

        setTargetRotation(degreeToRadians(targetRotation));
        setTargetVRotation(degreeToRadians(targetVRotation));
    }

    /**
     * Разворот камеры.
     *
     * @param perspective the perspective
     */
    public void rotateTo(final Perspective perspective) {

        float targetRotation = degreeToRadians(90);
        float targetVRotation = degreeToRadians(0);

        if (perspective == Perspective.BACK) {
            targetRotation = degreeToRadians(-90);
        } else if (perspective == Perspective.RIGHT) {
            targetRotation = degreeToRadians(180);
        } else if (perspective == Perspective.BOTTOM) {
            targetVRotation = degreeToRadians(90);
        } else if (perspective == Perspective.TOP) {
            targetVRotation = degreeToRadians(-90);
        }

        setTargetRotation(targetRotation);
        setTargetVRotation(targetVRotation);
    }

    /**
     * @return целевой разворот камеры.
     */
    private float getTargetRotation() {
        return targetRotation;
    }

    /**
     * Sets target rotation.
     *
     * @param targetRotation целевой разворот камеры.
     */
    public void setTargetRotation(final float targetRotation) {
        this.targetRotation = targetRotation;
    }

    /**
     * @return целевой разворот камеры по вертикали.
     */
    private float getTargetVRotation() {
        return targetVRotation;
    }

    /**
     * Sets target v rotation.
     *
     * @param targetVRotation целевой разворот камеры по вертикали.
     */
    public void setTargetVRotation(final float targetVRotation) {
        this.targetVRotation = targetVRotation;
    }

    @Override
    public void onAction(final String name, final boolean keyPressed, final float tpf) {

        if (!enabled || !dragToRotate) {
            return;
        } else if (!name.equals(CHASECAM_TOGGLEROTATE)) {
            return;
        }

        if (keyPressed) {
            canRotate = true;
            if (hideCursorOnRotate) inputManager.setCursorVisible(false);
        } else {
            canRotate = false;
            if (hideCursorOnRotate) inputManager.setCursorVisible(true);
        }
    }

    /**
     * Sets lock rotation.
     *
     * @param lockRotation the lock rotation
     */
    public void setLockRotation(boolean lockRotation) {
        this.lockRotation = lockRotation;
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
        if (!enabled) return;

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
     * Registers inputs with the input manager
     *
     * @param inputManager the input manager
     */
    public final void registerInput(final InputManager inputManager) {
        this.inputManager = inputManager;

        if (!inputManager.hasMapping(CHASECAM_DOWN)) {
            inputManager.addMapping(CHASECAM_DOWN, new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        }

        if (!inputManager.hasMapping(CHASECAM_UP)) {
            inputManager.addMapping(CHASECAM_UP, new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        }

        if (!inputManager.hasMapping(CHASECAM_ZOOMIN)) {
            inputManager.addMapping(CHASECAM_ZOOMIN, new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        }

        if (!inputManager.hasMapping(CHASECAM_ZOOMOUT)) {
            inputManager.addMapping(CHASECAM_ZOOMOUT, new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
        }

        if (!inputManager.hasMapping(CHASECAM_MOVELEFT)) {
            inputManager.addMapping(CHASECAM_MOVELEFT, new MouseAxisTrigger(MouseInput.AXIS_X, true));
        }

        if (!inputManager.hasMapping(CHASECAM_MOVERIGHT)) {
            inputManager.addMapping(CHASECAM_MOVERIGHT, new MouseAxisTrigger(MouseInput.AXIS_X, false));
        }

        if (!inputManager.hasMapping(CHASECAM_TOGGLEROTATE)) {
            inputManager.addMapping(CHASECAM_TOGGLEROTATE, new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE));
        }

        inputManager.addListener(this, ALL_INPUTS);
    }

    /**
     * Unregister input.
     *
     * @param inputManager the input manager
     */
    public void unregisterInput(final InputManager inputManager) {
        inputManager.removeListener(this);
    }

    /**
     * Compute position.
     */
    protected void computePosition() {

        float highDistance = (distance) * FastMath.sin((FastMath.PI / 2) - verticalRotation);

        position.set(highDistance * FastMath.cos(rotation), (distance) * FastMath.sin(verticalRotation), highDistance * FastMath.sin(rotation));
        position.addLocal(target.getWorldTranslation());
    }

    /**
     * Rotate camera.
     *
     * @param value the value
     */
    //rotate the camera around the target on the horizontal plane
    protected void rotateCamera(float value) {
        if (!canRotate || !enabled) return;
        rotating = true;
        targetRotation += value * rotationSpeed;
    }

    /**
     * Gets target distance.
     *
     * @return the target distance
     */
    public float getTargetDistance() {
        return targetDistance;
    }

    /**
     * Sets target distance.
     *
     * @param targetDistance the target distance
     */
    public void setTargetDistance(final float targetDistance) {
        this.targetDistance = max(min(targetDistance, maxDistance), minDistance);
    }

    /**
     * Zoom camera.
     *
     * @param value the value
     */
    //move the camera toward or away the target
    protected void zoomCamera(float value) {
        if (!enabled) return;

        zooming = true;
        targetDistance += value * zoomSensitivity * sqrt(targetDistance);
        targetDistance = max(min(targetDistance, maxDistance), minDistance);
    }

    /**
     * Vertical rotate camera.
     *
     * @param value the value
     */
    //rotate the camera around the target on the vertical plane
    protected void verticalRotateCamera(final float value) {
        if (!canRotate || !enabled) return;

        verticalRotating = true;
        targetVRotation += value * rotationSpeed;
    }

    /**
     * Updates the camera, should only be called internally
     *
     * @param tpf the tpf
     */
    protected void updateCamera(float tpf) {
        if (!enabled) return;

        targetLocation.set(target.getWorldTranslation()).addLocal(lookAtOffset);

        if (smoothMotion) {

            //computation of target direction
            targetDir.set(targetLocation).subtractLocal(prevPos);
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
                    if (targetRotation - rotation > trailingRotationInertia) {
                        targetRotation = rotation + trailingRotationInertia;
                    } else if (targetRotation - rotation < -trailingRotationInertia) {
                        targetRotation = rotation - trailingRotationInertia;
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
                        targetRotation = FastMath.TWO_PI - FastMath.acos(a.dot(b));
                    } else {
                        targetRotation = FastMath.acos(a.dot(b));
                    }
                    if (targetRotation - rotation > FastMath.PI || targetRotation - rotation < -FastMath.PI) {
                        targetRotation -= FastMath.TWO_PI;
                    }

                    //if there is an important change in the direction while trailing reset of the lerp factor to avoid jumpy movements
                    if (targetRotation != previousTargetRotation && FastMath.abs(targetRotation - previousTargetRotation) > FastMath.PI / 8) {
                        trailingLerpFactor = 0;
                    }
                    previousTargetRotation = targetRotation;
                }
                //computing lerp factor
                trailingLerpFactor = min(trailingLerpFactor + tpf * tpf * trailingSensitivity, 1);
                //computing rotation by linear interpolation
                rotation = FastMath.interpolateLinear(trailingLerpFactor, rotation, targetRotation);

                //if the rotation is near the target rotation we're good, that's over
                if (targetRotation + 0.01f >= rotation && targetRotation - 0.01f <= rotation) {
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
                rotationLerpFactor = min(rotationLerpFactor + tpf * tpf * rotationSensitivity, 1);
                rotation = FastMath.interpolateLinear(rotationLerpFactor, rotation, targetRotation);
                if (targetRotation + 0.01f >= rotation && targetRotation - 0.01f <= rotation) {
                    rotating = false;
                    rotationLerpFactor = 0;
                }
            }

            //linear interpolation of the rotation while rotating vertically
            if (verticalRotating) {
                vRotationLerpFactor = min(vRotationLerpFactor + tpf * tpf * rotationSensitivity, 1);
                verticalRotation = FastMath.interpolateLinear(vRotationLerpFactor, verticalRotation, targetVRotation);
                if (targetVRotation + 0.01f >= verticalRotation && targetVRotation - 0.01f <= verticalRotation) {
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
            verticalRotation = targetVRotation;
            rotation = targetRotation;
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

        final EditorCamera editorCamera = new EditorCamera(camera, spatial);
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
    public void setSpatial(Spatial spatial) {
        target = spatial;

        if (spatial == null) return;

        computePosition();

        prevPos = new Vector3f(target.getWorldTranslation());
        camera.setLocation(position);
    }

    /**
     * update the camera control, should only be used internally
     */
    public void update(float tpf) {
        updateCamera(tpf);
    }

    /**
     * renders the camera control, should only be used internally
     */
    public void render(RenderManager rm, ViewPort vp) {
        //nothing to render
    }

    /**
     * Write the camera
     *
     * @param ex the exporter
     */
    public void write(JmeExporter ex) throws IOException {
        throw new UnsupportedOperationException("remove ChaseCamera before saving");
    }

    /**
     * Read the camera
     */
    public void read(JmeImporter im) throws IOException {
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
        rotation = angleInRad;
        targetRotation = angleInRad;
    }

    /**
     * sets the default vertical rotation in radian of the camera at start of the application
     *
     * @param angleInRad the angle in rad
     */
    public void setDefaultVerticalRotation(float angleInRad) {
        verticalRotation = angleInRad;
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
        return rotation;
    }

    /**
     * returns the current vertical rotation around the target in radians.
     *
     * @return the vertical rotation
     */
    public float getVerticalRotation() {
        return verticalRotation;
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
        this.lookAtOffset = lookAtOffset;
    }

    /**
     * Sets the up vector of the camera used for the lookAt on the target
     *
     * @param up the up
     */
    public void setUpVector(Vector3f up) {
        initialUpVec = up;
    }

    /**
     * Returns the up vector of the camera used for the lookAt on the target
     *
     * @return the up vector
     */
    public Vector3f getUpVector() {
        return initialUpVec;
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