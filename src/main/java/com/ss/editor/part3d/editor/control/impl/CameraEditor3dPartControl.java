package com.ss.editor.part3d.editor.control.impl;

import com.jme3.app.Application;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.*;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.config.Config;
import com.ss.editor.control.transform.EditorTransformSupport;
import com.ss.editor.control.transform.EditorTransformSupport.PickedAxis;
import com.ss.editor.model.EditorCamera;
import com.ss.editor.model.EditorCamera.Direction;
import com.ss.editor.model.EditorCamera.Perspective;
import com.ss.editor.part3d.editor.ExtendableEditor3dPart;
import com.ss.editor.part3d.editor.control.InputEditor3dPartControl;
import com.ss.editor.util.EditorUtils;
import com.ss.editor.util.JmeUtils;
import com.ss.editor.util.LocalObjects;
import com.ss.rlib.common.logging.LoggerLevel;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.dictionary.ObjectDictionary;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static com.ss.rlib.common.util.array.ArrayFactory.toArray;

/**
 * The editor's camera control.
 *
 * @author JavaSaBr
 */
public class CameraEditor3dPartControl extends BaseInputEditor3dPartControl<ExtendableEditor3dPart> implements
        InputEditor3dPartControl, ActionListener, AnalogListener {

    protected static final ObjectDictionary<String, Trigger> ACTION_TRIGGERS =
            ObjectDictionary.ofType(String.class, Trigger.class);

    protected static final ObjectDictionary<String, Trigger> ANALOG_TRIGGERS =
            ObjectDictionary.ofType(String.class, Trigger.class);

    protected static final ObjectDictionary<String, Trigger[]> ACTION_MULTI_TRIGGERS =
            ObjectDictionary.ofType(String.class, Trigger[].class);

    protected static final String MOUSE_RIGHT_CLICK = "jMB.editorCamera.rightClick";
    protected static final String MOUSE_LEFT_CLICK = "jMB.editorCamera.leftClick";
    protected static final String MOUSE_MIDDLE_CLICK = "jMB.editorCamera.middleClick";

    protected static final String MOUSE_X_AXIS = "jMB.editorCamera.mouseXAxis";
    protected static final String MOUSE_X_AXIS_NEGATIVE = "jMB.editorCamera.mouseXAxisNegative";
    protected static final String MOUSE_Y_AXIS = "jMB.baseEditor.editorCamera";
    protected static final String MOUSE_Y_AXIS_NEGATIVE = "jMB.editorCamera.mouseYAxisNegative";

    protected static final String KEY_W = "jMB.editorCamera.W";
    protected static final String KEY_S = "jMB.editorCamera.S";
    protected static final String KEY_A = "jMB.editorCamera.A";
    protected static final String KEY_D = "jMB.editorCamera.D";
    protected static final String KEY_ALT = "jMB.editorCamera.Alt";
    protected static final String KEY_CTRL = "jMB.editorCamera.Ctrl";

    protected static final String KEY_NUM_1 = "jMB.editorCamera.num1";
    protected static final String KEY_NUM_2 = "jMB.editorCamera.num2";
    protected static final String KEY_NUM_3 = "jMB.editorCamera.num3";
    protected static final String KEY_NUM_4 = "jMB.editorCamera.num4";
    protected static final String KEY_NUM_6 = "jMB.editorCamera.num6";
    protected static final String KEY_NUM_7 = "jMB.editorCamera.num7";
    protected static final String KEY_NUM_8 = "jMB.editorCamera.num8";
    protected static final String KEY_NUM_9 = "jMB.editorCamera.num9";

    private static final String[] ACTION_MAPPINGS;
    private static final String[] ANALOG_MAPPINGS;

    static {

        ACTION_TRIGGERS.put(MOUSE_RIGHT_CLICK, new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        ACTION_TRIGGERS.put(MOUSE_LEFT_CLICK, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        ACTION_TRIGGERS.put(MOUSE_MIDDLE_CLICK, new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE));

        ACTION_TRIGGERS.put(KEY_W, new KeyTrigger(KeyInput.KEY_W));
        ACTION_TRIGGERS.put(KEY_S, new KeyTrigger(KeyInput.KEY_S));
        ACTION_TRIGGERS.put(KEY_A, new KeyTrigger(KeyInput.KEY_A));
        ACTION_TRIGGERS.put(KEY_D, new KeyTrigger(KeyInput.KEY_D));

        ACTION_TRIGGERS.put(KEY_NUM_1, new KeyTrigger(KeyInput.KEY_NUMPAD1));
        ACTION_TRIGGERS.put(KEY_NUM_2, new KeyTrigger(KeyInput.KEY_NUMPAD2));
        ACTION_TRIGGERS.put(KEY_NUM_3, new KeyTrigger(KeyInput.KEY_NUMPAD3));
        ACTION_TRIGGERS.put(KEY_NUM_4, new KeyTrigger(KeyInput.KEY_NUMPAD4));
        ACTION_TRIGGERS.put(KEY_NUM_6, new KeyTrigger(KeyInput.KEY_NUMPAD6));
        ACTION_TRIGGERS.put(KEY_NUM_7, new KeyTrigger(KeyInput.KEY_NUMPAD7));
        ACTION_TRIGGERS.put(KEY_NUM_8, new KeyTrigger(KeyInput.KEY_NUMPAD8));
        ACTION_TRIGGERS.put(KEY_NUM_9, new KeyTrigger(KeyInput.KEY_NUMPAD9));

        ACTION_MULTI_TRIGGERS.put(KEY_CTRL, toArray(new KeyTrigger(KeyInput.KEY_RCONTROL), new KeyTrigger(KeyInput.KEY_LCONTROL)));
        ACTION_MULTI_TRIGGERS.put(KEY_ALT, toArray(new KeyTrigger(KeyInput.KEY_RMENU), new KeyTrigger(KeyInput.KEY_LMENU)));

        ANALOG_TRIGGERS.put(MOUSE_X_AXIS, new MouseAxisTrigger(MouseInput.AXIS_X, false));
        ANALOG_TRIGGERS.put(MOUSE_X_AXIS, new MouseAxisTrigger(MouseInput.AXIS_X, false));
        ANALOG_TRIGGERS.put(MOUSE_X_AXIS, new MouseAxisTrigger(MouseInput.AXIS_X, false));
        ANALOG_TRIGGERS.put(MOUSE_X_AXIS, new MouseAxisTrigger(MouseInput.AXIS_X, false));

        Array<String> mappings = ACTION_TRIGGERS.keyArray(String.class);
        mappings.addAll(ACTION_MULTI_TRIGGERS.keyArray(String.class));

        ACTION_MAPPINGS = mappings.toArray(String.class);
        ANALOG_MAPPINGS = ANALOG_TRIGGERS.keyArray(String.class)
                .toArray(String.class);
    }

    /**
     * The editor camera.
     */
    @NotNull
    private final EditorCamera editorCamera;

    /**
     * The light of the camera.
     */
    @NotNull
    private final DirectionalLight light;

    /**
     * The previous camera location.
     */
    @NotNull
    private final Vector3f prevCameraLocation;

    /**
     * The node on which the camera is looking.
     */
    @NotNull
    private final Node cameraNode;

    /**
     * The flag of flying camera.
     */
    @NotNull
    private final AtomicInteger cameraFlying;

    /**
     * The flag of moving camera.
     */
    @NotNull
    private final AtomicInteger cameraMoving;

    /**
     * The state of camera keys.
     */
    @NotNull
    private final boolean[] cameraKeysState;

    /**
     * The previous camera zoom.
     */
    private float prevTargetDistance;

    /**
     * The previous vertical camera rotation.
     */
    private float prevVRotation;

    /**
     * The previous horizontal camera rotation.
     */
    private float prevHRotation;

    /**
     * The previous camera speed.
     */
    private float prevCameraFlySpeed;

    /**
     * The camera fly speed.
     */
    private float cameraFlySpeed;

    /**
     * True of need to add light for the camera.
     */
    private boolean needLight;

    /**
     * True if need to update light to follow the camera.
     */
    private boolean needUpdateLight;

    /**
     * TRue if need to allow the camera to be moved.
     */
    private boolean needMovableCamera;

    public CameraEditor3dPartControl(@NotNull ExtendableEditor3dPart editor3dPart, @NotNull Camera camera) {
        this(editor3dPart, camera, true, true, true);
    }

    public CameraEditor3dPartControl(
            @NotNull ExtendableEditor3dPart editor3dPart,
            @NotNull Camera camera,
            boolean needLight,
            boolean needUpdateLight,
            boolean needMovableCamera
    ) {
        super(editor3dPart);
        this.cameraFlying = new AtomicInteger();
        this.cameraMoving = new AtomicInteger();
        this.prevCameraLocation = new Vector3f();
        this.cameraKeysState = new boolean[4];
        this.cameraFlySpeed = 1F;
        this.cameraNode = new Node("CameraNode");
        this.editorCamera = createEditorCamera(camera);
        this.light = createLight();
        this.needLight = needLight;
        this.needUpdateLight = needUpdateLight;
        this.needMovableCamera = needMovableCamera;

        actionHandlers.put(KEY_NUM_1, (isPressed, tpf) -> rotateTo(Perspective.BACK, isPressed));
        actionHandlers.put(KEY_NUM_3, (isPressed, tpf) -> rotateTo(Perspective.RIGHT, isPressed));
        actionHandlers.put(KEY_NUM_7, (isPressed, tpf) -> rotateTo(Perspective.TOP, isPressed));
        actionHandlers.put(KEY_NUM_9, (isPressed, tpf) -> rotateTo(Perspective.BOTTOM, isPressed));
        actionHandlers.put(KEY_NUM_2, (isPressed, tpf) -> rotateTo(Direction.BOTTOM, isPressed));
        actionHandlers.put(KEY_NUM_8, (isPressed, tpf) -> rotateTo(Direction.TOP, isPressed));
        actionHandlers.put(KEY_NUM_4, (isPressed, tpf) -> rotateTo(Direction.LEFT, isPressed));
        actionHandlers.put(KEY_NUM_6, (isPressed, tpf) -> rotateTo(Direction.RIGHT, isPressed));

        actionHandlers.put(MOUSE_MIDDLE_CLICK, (isPressed, tpf) -> {
            if (isCameraFlying() && !isPressed) {
                finishCameraMoving(0, true);
            }
        });

        actionHandlers.put(KEY_CTRL, (isPressed, tpf) -> {
            if (isCameraFlying() && isPressed && cameraFlySpeed > 0) {
                cameraFlySpeed = Math.max(cameraFlySpeed - 0.4F, 0.1F);
            }
        });

        actionHandlers.put(KEY_ALT, (isPressed, tpf) -> {
            if (isCameraFlying() && isPressed && cameraFlySpeed > 0) {
                cameraFlySpeed += 0.4F;
            }
        });

        actionHandlers.put(KEY_A, (isPressed, tpf) -> moveSideCamera(tpf, true, isPressed, 0));
        actionHandlers.put(KEY_D, (isPressed, tpf) -> moveSideCamera(-tpf, true, isPressed, 1));
        actionHandlers.put(KEY_W, (isPressed, tpf) -> moveDirectionCamera(tpf, true, isPressed, 2));
        actionHandlers.put(KEY_S, (isPressed, tpf) -> moveDirectionCamera(-tpf, true, isPressed, 3));

        analogHandlers.put(MOUSE_X_AXIS, (value, tpf) -> moveXMouse(value));
        analogHandlers.put(MOUSE_X_AXIS_NEGATIVE, (value, tpf) -> moveXMouse(-value));
        analogHandlers.put(MOUSE_Y_AXIS, (value, tpf) -> moveYMouse(-value));
        analogHandlers.put(MOUSE_Y_AXIS_NEGATIVE, (value, tpf) -> moveYMouse(value));
    }

    /**
     * Create light for camera directional light.
     *
     * @return the light for the camera.
     */
    @FromAnyThread
    protected @NotNull DirectionalLight createLight() {

        var light = new DirectionalLight();
        light.setColor(ColorRGBA.White);

        return light;
    }

    /**
     * sets the default horizontal rotation in radian of the camera at start of the application
     *
     * @param angleInRad the angle in rad
     */
    @FromAnyThread
    public void setDefaultHorizontalRotation(float angleInRad) {
        editorCamera.setDefaultHorizontalRotation(angleInRad);
    }

    /**
     * sets the default vertical rotation in radian of the camera at start of the application
     *
     * @param angleInRad the angle in rad
     */
    @FromAnyThread
    public void setDefaultVerticalRotation(float angleInRad) {
        editorCamera.setDefaultVerticalRotation(angleInRad);
    }

    /**
     * Create an editor camera.
     *
     * @param camera the camera.
     * @return the new editor camera.
     */
    @FromAnyThread
    protected @NotNull EditorCamera createEditorCamera(@NotNull Camera camera) {

        var editorCamera = new EditorCamera(camera, cameraNode);
        editorCamera.setMaxDistance(10000);
        editorCamera.setMinDistance(0.01F);
        editorCamera.setSmoothMotion(false);
        editorCamera.setRotationSensitivity(1);
        editorCamera.setZoomSensitivity(0.2F);

        return editorCamera;
    }

    /**
     * Set the light's direction.
     *
     * @param direction the light's direction.
     */
    @FromAnyThread
    public void setLightDirection(@NotNull Vector3f direction) {
        this.light.setDirection(direction);
    }

    @Override
    @JmeThread
    public void initialize(@NotNull Application application) {
        super.initialize(application);

        var rootNode = editor3dPart.getRootNode();
        rootNode.attachChild(cameraNode);

        if (needLight) {
            rootNode.addLight(light);
        }
    }

    @Override
    @JmeThread
    public void cleanup(@NotNull Application application) {
        super.cleanup(application);

        var rootNode = editor3dPart.getRootNode();
        rootNode.detachChild(cameraNode);

        if (needLight) {
            rootNode.removeLight(light);
        }
    }

    /**
     * Enable camera's light.
     */
    @JmeThread
    public void enableLight() {

        if (needLight) {
            return;
        }

        editor3dPart.getRootNode()
                .addLight(light);

        needLight = true;
    }

    /**
     * Disable camera's light.
     */
    @JmeThread
    public void disableLight() {

        if (!needLight) {
            return;
        }

        editor3dPart.getRootNode()
                .removeLight(light);

        needLight = false;
    }

    @Override
    @JmeThread
    public void register(@NotNull InputManager inputManager) {

        ACTION_TRIGGERS.forEach(inputManager, JmeUtils::addMapping);
        ACTION_MULTI_TRIGGERS.forEach(inputManager, JmeUtils::addMapping);
        ANALOG_TRIGGERS.forEach(inputManager, JmeUtils::addMapping);

        inputManager.addListener(this, ACTION_MAPPINGS);
    }

    @Override
    @JmeThread
    public void onAction(@NotNull String name, boolean isPressed, float tpf) {
        super.onAction(name, isPressed, tpf);

        var inputState = editor3dPart.requireControl(InputStateEditor3dPartControl.class);

        if (needMovableCamera) {
            //FIXME
            // editorCamera.setLockRotation(inputState.isShiftDown() && inputState.isButtonMiddleDown());
        }
    }

    @Override
    @JmeThread
    public void cameraUpdate(float tpf) {
        editorCamera.updateCamera(tpf);

        if (isCameraFlying()) {
            if (cameraKeysState[0]) {
                moveSideCamera(tpf * 30, false, false, 0);
            }
            if (cameraKeysState[1]) {
                moveSideCamera(-tpf * 30, false, false, 1);
            }
            if (cameraKeysState[2]) {
                moveDirectionCamera(tpf * 30, false, false, 2);
            }
            if (cameraKeysState[3]) {
                moveDirectionCamera(-tpf * 30, false, false, 3);
            }
        }

        checkCameraChanges(editorCamera);
    }

    @Override
    @JmeThread
    public void postCameraUpdate(float tpf) {
        if (needLight && needUpdateLight) {
            var direction = LocalObjects.get().nextVector();
            light.setDirection(editorCamera.getDirection(direction));
        }
    }

    /**
     * Rotate to the perspective.
     *
     * @param perspective the perspective.
     * @param isPressed   true if a key is pressed.
     */
    @JmeThread
    protected void rotateTo(@NotNull Perspective perspective, boolean isPressed) {
        if (isPressed) {
            editorCamera.rotateTo(perspective);
        }
    }

    /**
     * Rotate to the direction.
     *
     * @param direction the direction.
     * @param isPressed true if a key is pressed.
     */
    @JmeThread
    protected void rotateTo(@NotNull Direction direction, boolean isPressed) {
        if (isPressed) {
            editorCamera.rotateTo(direction, 10F);
        }
    }

    /**
     * Return true if the camera is flying now.
     *
     * @return true if the camera is flying now.
     */
    @JmeThread
    public boolean isCameraFlying() {
        return cameraFlying.get() != 0;
    }

    /**
     * Return true if the camera is moving now.
     *
     * @return true if the camera is moving now.
     */
    @JmeThread
    public boolean isCameraMoving() {
        return cameraMoving.get() != 0;
    }

    /**
     * Start to move the camera.
     */
    @JmeThread
    private void startCameraFlying(int key) {

        if (Config.DEV_CAMERA_DEBUG && LOGGER.isEnabled(LoggerLevel.DEBUG)) {
            LOGGER.debug(this, "start camera moving[" + cameraFlying + "] for key " + key);
        }

        if (cameraFlying.get() == 0) {

            var camera = EditorUtils.getGlobalCamera();

            cameraNode.setLocalTranslation(camera.getLocation());
            editorCamera.setTargetDistance(0);
        }

        if (!cameraKeysState[key]) {
            cameraKeysState[key] = true;
            cameraFlying.incrementAndGet();
        }
    }


    /**
     * Finish to move the camera.
     */
    @JmeThread
    private void finishCameraMoving(int key, boolean force) {

        if (Config.DEV_CAMERA_DEBUG && LOGGER.isEnabled(LoggerLevel.DEBUG)) {
            LOGGER.debug(this, "finish camera moving[" + cameraFlying + "] for key " + key + ", force = " + force);
        }

        cameraKeysState[key] = false;

        if (cameraFlying.get() == 0) {
            return;
        }

        if (force) {
            cameraFlying.set(0);
            for (int i = 0; i < cameraKeysState.length; i++) {
                cameraKeysState[i] = false;
            }
        } else {
            cameraFlying.decrementAndGet();
        }
    }


    /**
     * Move a camera to direction.
     *
     * @param value the value to move.
     */
    @JmeThread
    private void moveDirectionCamera(float value, boolean isAction, boolean isPressed, int key) {

        if (canCameraFly()) {
            return;
        }  else if (isAction && isPressed) {
            startCameraFlying(key);
        } else if (isAction) {
            finishCameraMoving(key, false);
        } else if (!isCameraFlying() || isAction) {
            return;
        }

        var local = LocalObjects.get();
        var direction = editorCamera.getDirection(local.nextVector());
        direction.multLocal(value * cameraFlySpeed);
        direction.addLocal(cameraNode.getLocalTranslation());

        cameraNode.setLocalTranslation(direction);
    }

    /**
     * Move a camera to side.
     *
     * @param value the value to move.
     */
    @JmeThread
    private void moveSideCamera(float value, boolean isAction, boolean isPressed, int key) {

        if (canCameraFly()) {
            return;
        }  else if (isAction && isPressed) {
            startCameraFlying(key);
        } else if (isAction) {
            finishCameraMoving(key, false);
        } else if (!isCameraFlying() || isAction) {
            return;
        }

        var local = LocalObjects.get();
        var left = editorCamera.getLeft(local.nextVector());
        left.multLocal(value * cameraFlySpeed);
        left.addLocal(cameraNode.getLocalTranslation());

        cameraNode.setLocalTranslation(left);
    }


    /**
     * Move a mouse on X axis.
     *
     * @param value the value to move.
     */
    @JmeThread
    protected void moveXMouse(float value) {

        /*final EditorCamera editorCamera = getEditorCamera();
        final Camera camera = EDITOR.getCamera();
        final Node nodeForCamera = getNodeForCamera();

        final Vector3f left = camera.getLeft();
        left.multLocal(value * (float) Math.sqrt(editorCamera.getTargetDistance()));
        left.addLocal(nodeForCamera.getLocalTranslation());

        nodeForCamera.setLocalTranslation(left);*/
    }

    /**
     * Move a mouse on Y axis.
     *
     * @param value the value to move.
     */
    @JmeThread
    protected void moveYMouse(float value) {

        /*final EditorCamera editorCamera = getEditorCamera();
        final Camera camera = EDITOR.getCamera();
        final Node nodeForCamera = getNodeForCamera();

        final Vector3f up = camera.getUp();
        up.multLocal(value * (float) Math.sqrt(editorCamera.getTargetDistance()));
        up.addLocal(nodeForCamera.getLocalTranslation());

        nodeForCamera.setLocalTranslation(up);*/
    }

    @JmeThread
    private boolean canCameraFly() {
       /** var inputState = editor3dPart.requireControl(InputStateEditor3dPartControl.class);
        return inputState.isButtonMiddleDown() &&
                !inputState.isShiftDown() &&
                !isCameraMoving();**/
       return false;
    }

    @JmeThread
    private boolean canCameraMoveOrFly() {
        /*return editor3dPart.requireControl(InputStateEditor3dPartControl.class)
                .isButtonMiddleDown();*/
        return false;
    }


    /**
     * Check camera changes.
     *
     * @param editorCamera the editor's camera.
     */
    @JmeThread
    protected void checkCameraChanges(@NotNull EditorCamera editorCamera) {

        int changes = 0;

        var cameraLocation = cameraNode.getLocalTranslation();

        var hRotation = editorCamera.getHorizontalRotation();
        var vRotation = editorCamera.getvRotation();
        var targetDistance = editorCamera.getTargetDistance();

        if (!prevCameraLocation.equals(cameraLocation)) {
            changes++;
        } else if (prevHRotation != hRotation || prevVRotation != vRotation) {
            changes++;
        } else if (prevTargetDistance != targetDistance) {
            changes++;
        } else if (cameraFlySpeed != prevCameraFlySpeed) {
            changes++;
        }

        if (changes > 0) {
            notifyChangedCameraSettings(cameraLocation, hRotation, vRotation, targetDistance, cameraFlySpeed);
        }

        prevCameraLocation.set(cameraLocation);

        this.prevHRotation = hRotation;
        this.prevVRotation = vRotation;
        this.prevTargetDistance = targetDistance;
        this.prevCameraFlySpeed = cameraFlySpeed;
    }

    /**
     * Notify about changed camera's settings.
     *
     * @param cameraLocation the camera location.
     * @param hRotation      the h rotation.
     * @param vRotation      the v rotation.
     * @param targetDistance the target distance.
     * @param cameraFlySpeed the camera fly speed.
     */
    @JmeThread
    protected void notifyChangedCameraSettings(
        @NotNull Vector3f cameraLocation,
        float hRotation,
        float vRotation,
        float targetDistance,
        float cameraFlySpeed
    ) {

      //  ExecutorManager.getInstance()
       //         .addFxTask(() -> fileEditor.notifyChangedCameraSettings(cameraLocation, hRotation, vRotation, targetDistance, cameraFlySpeed));
    }

    /**
     * Update the editor's camera settings.
     *
     * @param cameraLocation the camera location.
     * @param hRotation      the h rotation.
     * @param vRotation      the v rotation.
     * @param targetDistance the target distance.
     * @param cameraFlySpeed the camera fly speed.
     */
    @JmeThread
    public void updateCameraSettings(
        @NotNull Vector3f cameraLocation,
        float hRotation,
        float vRotation,
        float targetDistance,
        float cameraFlySpeed
    ) {
        
      /*  editorCamera.setTargetHRotation(hRotation);
        editorCamera.setTargetVRotation(vRotation);
        editorCamera.setTargetDistance(targetDistance);

        getNodeForCamera().setLocalTranslation(cameraLocation);
        getPrevCameraLocation().set(cameraLocation);

        this.prevHRotation = hRotation;
        this.prevVRotation = vRotation;
        this.prevTargetDistance = targetDistance;
        this.prevCameraFlySpeed = cameraFlySpeed;
        this.cameraFlySpeed = cameraFlySpeed;

        editorCamera.update(1F);*/
    }
}
