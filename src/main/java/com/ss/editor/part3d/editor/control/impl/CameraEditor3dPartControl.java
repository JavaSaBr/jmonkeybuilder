package com.ss.editor.part3d.editor.control.impl;

import com.jme3.input.InputManager;
import com.jme3.input.controls.Trigger;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.config.Config;
import com.ss.editor.model.EditorCamera;
import com.ss.editor.model.EditorCamera.Direction;
import com.ss.editor.model.EditorCamera.Perspective;
import com.ss.editor.part3d.editor.control.InputEditor3dPartControl;
import com.ss.editor.part3d.editor.impl.CameraSupportEditor3dPart;
import com.ss.editor.util.EditorUtils;
import com.ss.editor.util.JmeUtils;
import com.ss.editor.util.LocalObjects;
import com.ss.rlib.common.logging.LoggerLevel;
import com.ss.rlib.common.util.ObjectUtils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.dictionary.ObjectDictionary;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The editor's camera control.
 *
 * @author JavaSaBr
 */
public class CameraEditor3dPartControl extends BaseInputEditor3dPartControl<CameraSupportEditor3dPart> implements
        InputEditor3dPartControl {

    protected static final ObjectDictionary<String, Trigger> TRIGGERS =
            ObjectDictionary.ofType(String.class, Trigger.class);

    protected static final ObjectDictionary<String, Trigger[]> MULTI_TRIGGERS =
            ObjectDictionary.ofType(String.class, Trigger[].class);

    protected static final String MOUSE_RIGHT_CLICK = "jMB.baseEditor.mouseRightClick";
    protected static final String MOUSE_LEFT_CLICK = "jMB.baseEditor.mouseLeftClick";
    protected static final String MOUSE_MIDDLE_CLICK = "jMB.baseEditor.mouseMiddleClick";

    protected static final String MOUSE_X_AXIS = "jMB.baseEditor.mouseXAxis";
    protected static final String MOUSE_X_AXIS_NEGATIVE = "jMB.baseEditor.mouseXAxisNegative";
    protected static final String MOUSE_Y_AXIS = "jMB.baseEditor.mouseYAxis";
    protected static final String MOUSE_Y_AXIS_NEGATIVE = "jMB.baseEditor.mouseYAxisNegative";

    protected static final String MOUSE_MOVE_CAMERA_X_AXIS = "jMB.baseEditor.mouseMoveCameraXAxis";
    protected static final String MOUSE_MOVE_CAMERA_X_AXIS_NEGATIVE = "jMB.baseEditor.mouseMoveCameraXAxisNegative";
    protected static final String MOUSE_MOVE_CAMERA_Y_AXIS = "jMB.baseEditor.mouseMoveCameraYAxis";
    protected static final String MOUSE_MOVE_CAMERA_Y_AXIS_NEGATIVE = "jMB.baseEditor.mouseMoveCameraYAxisNegative";

    protected static final String KEY_CTRL = "jMB.baseEditor.keyCtrl";
    protected static final String KEY_ALT = "jMB.baseEditor.keyAlt";
    protected static final String KEY_SHIFT = "jMB.baseEditor.keyShift";

    protected static final String KEY_CTRL_S = "jMB.baseEditor.Ctrl.S";
    protected static final String KEY_CTRL_Z = "jMB.baseEditor.Ctrl.Z";
    protected static final String KEY_CTRL_Y = "jMB.baseEditor.Ctrl.Y";

    protected static final String KEY_FLY_CAMERA_W = "jMB.baseEditor.keyFlyCameraW";
    protected static final String KEY_FLY_CAMERA_S = "jMB.baseEditor.keyFlyCameraS";
    protected static final String KEY_FLY_CAMERA_A = "jMB.baseEditor.keyFlyCameraA";
    protected static final String KEY_FLY_CAMERA_D = "jMB.baseEditor.keyFlyCameraD";

    protected static final String KEY_NUM_1 = "jMB.baseEditor.num1";
    protected static final String KEY_NUM_2 = "jMB.baseEditor.num2";
    protected static final String KEY_NUM_3 = "jMB.baseEditor.num3";
    protected static final String KEY_NUM_4 = "jMB.baseEditor.num4";
    protected static final String KEY_NUM_5 = "jMB.baseEditor.num5";
    protected static final String KEY_NUM_6 = "jMB.baseEditor.num6";
    protected static final String KEY_NUM_7 = "jMB.baseEditor.num7";
    protected static final String KEY_NUM_8 = "jMB.baseEditor.num8";
    protected static final String KEY_NUM_9 = "jMB.baseEditor.num9";

    private static final String[] MAPPINGS;

    static {

        Array<String> mappings = TRIGGERS.keyArray(String.class);
        mappings.addAll(MULTI_TRIGGERS.keyArray(String.class));

        MAPPINGS = mappings.toArray(String.class);
    }

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

    public CameraEditor3dPartControl(@NotNull CameraSupportEditor3dPart editor3dPart) {
        super(editor3dPart);
        this.cameraFlying = new AtomicInteger();
        this.cameraMoving = new AtomicInteger();
        this.cameraKeysState = new boolean[4];
        this.cameraFlySpeed = 1F;

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

        actionHandlers.put(KEY_FLY_CAMERA_A, (isPressed, tpf) -> moveSideCamera(tpf, true, isPressed, 0));
        actionHandlers.put(KEY_FLY_CAMERA_D, (isPressed, tpf) -> moveSideCamera(-tpf, true, isPressed, 1));
        actionHandlers.put(KEY_FLY_CAMERA_W, (isPressed, tpf) -> moveDirectionCamera(tpf, true, isPressed, 2));
        actionHandlers.put(KEY_FLY_CAMERA_S, (isPressed, tpf) -> moveDirectionCamera(-tpf, true, isPressed, 3));

        analogHandlers.put(MOUSE_X_AXIS, (value, tpf) -> moveXMouse(value));
        analogHandlers.put(MOUSE_X_AXIS_NEGATIVE, (value, tpf) -> moveXMouse(-value));
        analogHandlers.put(MOUSE_Y_AXIS, (value, tpf) -> moveYMouse(-value));
        analogHandlers.put(MOUSE_Y_AXIS_NEGATIVE, (value, tpf) -> moveYMouse(value));
    }

    @Override
    @JmeThread
    public void register(@NotNull InputManager inputManager) {

        TRIGGERS.forEach(inputManager, JmeUtils::addMapping);
        MULTI_TRIGGERS.forEach(inputManager, JmeUtils::addMapping);

        inputManager.addListener(this, MAPPINGS);
    }

    /**
     * Rotate to the perspective.
     *
     * @param perspective the perspective.
     * @param isPressed   true if a key is pressed.
     */
    @JmeThread
    protected void rotateTo(@NotNull Perspective perspective, boolean isPressed) {

        var editorCamera = editor3dPart.requireEditorCamera();

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

        var editorCamera = editor3dPart.requireEditorCamera();

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

            getNodeForCamera().setLocalTranslation(camera.getLocation());
            requireEditorCamera().setTargetDistance(0);
        }

        var cameraKeysState = getCameraKeysState();

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

        var cameraKeysState = getCameraKeysState();

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

        var editorCamera = getEditorCamera();

        if (editorCamera == null) {
            return;
        }

        var camera = EditorUtils.getGlobalCamera();
        var nodeForCamera = getNodeForCamera();

        var local = LocalObjects.get();

        var direction = camera.getDirection(local.nextVector());
        direction.multLocal(value * cameraFlySpeed);
        direction.addLocal(nodeForCamera.getLocalTranslation());

        nodeForCamera.setLocalTranslation(direction);
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

        var editorCamera = getEditorCamera();

        if (editorCamera == null) {
            return;
        }

        var camera = EditorUtils.getGlobalCamera();
        var nodeForCamera = getNodeForCamera();

        var local = LocalObjects.get();
        var left = camera.getLeft(local.nextVector());
        left.multLocal(value * cameraFlySpeed);
        left.addLocal(nodeForCamera.getLocalTranslation());

        nodeForCamera.setLocalTranslation(left);
    }


    /**
     * Move a mouse on X axis.
     *
     * @param value the value to move.
     */
    @JmeThread
    protected void moveXMouse(float value) {

        final EditorCamera editorCamera = getEditorCamera();
        final Camera camera = EDITOR.getCamera();
        final Node nodeForCamera = getNodeForCamera();

        final Vector3f left = camera.getLeft();
        left.multLocal(value * (float) Math.sqrt(editorCamera.getTargetDistance()));
        left.addLocal(nodeForCamera.getLocalTranslation());

        nodeForCamera.setLocalTranslation(left);
    }

    /**
     * Move a mouse on Y axis.
     *
     * @param value the value to move.
     */
    @JmeThread
    protected void moveYMouse(float value) {

        final EditorCamera editorCamera = getEditorCamera();
        final Camera camera = EDITOR.getCamera();
        final Node nodeForCamera = getNodeForCamera();

        final Vector3f up = camera.getUp();
        up.multLocal(value * (float) Math.sqrt(editorCamera.getTargetDistance()));
        up.addLocal(nodeForCamera.getLocalTranslation());

        nodeForCamera.setLocalTranslation(up);
    }

    @JmeThread
    private boolean canCameraFly() {
        var inputState = editor3dPart.requireControl(InputStateEditor3dPartControl.class);
        return inputState.isButtonMiddleDown() &&
                !inputState.isShiftDown() &&
                !isCameraMoving();
    }

    @JmeThread
    private boolean canCameraMoveOrFly() {
        return editor3dPart.requireControl(InputStateEditor3dPartControl.class)
                .isButtonMiddleDown();
    }
}
