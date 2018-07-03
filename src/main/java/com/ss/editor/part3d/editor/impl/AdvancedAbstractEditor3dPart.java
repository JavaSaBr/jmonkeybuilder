package com.ss.editor.part3d.editor.impl;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import static com.ss.rlib.common.util.array.ArrayFactory.toArray;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.*;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.config.Config;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.EditorCamera;
import com.ss.editor.part3d.editor.Editor3dPart;
import com.ss.editor.plugin.api.RenderFilterRegistry;
import com.ss.editor.ui.component.editor.FileEditor;
import com.ss.editor.util.EditorUtil;
import com.ss.editor.util.LocalObjects;
import com.ss.rlib.common.function.BooleanFloatConsumer;
import com.ss.rlib.common.function.FloatFloatConsumer;
import com.ss.rlib.common.logging.LoggerLevel;
import com.ss.rlib.common.util.dictionary.DictionaryFactory;
import com.ss.rlib.common.util.dictionary.ObjectDictionary;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The base implementation of the {@link Editor3dPart} for a file editor.
 *
 * @param <T> the type of a file editor.
 * @author JavaSaBr
 */
public abstract class AdvancedAbstractEditor3dPart<T extends FileEditor> extends AbstractEditor3dPart<T> {

    protected static final ObjectDictionary<String, Trigger> TRIGGERS =
            DictionaryFactory.newObjectDictionary();

    protected static final ObjectDictionary<String, Trigger[]> MULTI_TRIGGERS =
            DictionaryFactory.newObjectDictionary();

    protected static final String MOUSE_RIGHT_CLICK = "SSEditor.editorState.mouseRightClick";
    protected static final String MOUSE_LEFT_CLICK = "SSEditor.editorState.mouseLeftClick";
    protected static final String MOUSE_MIDDLE_CLICK = "SSEditor.editorState.mouseMiddleClick";

    protected static final String MOUSE_X_AXIS = "SSEditor.editorState.mouseXAxis";
    protected static final String MOUSE_X_AXIS_NEGATIVE = "SSEditor.editorState.mouseXAxisNegative";
    protected static final String MOUSE_Y_AXIS = "SSEditor.editorState.mouseYAxis";
    protected static final String MOUSE_Y_AXIS_NEGATIVE = "SSEditor.editorState.mouseYAxisNegative";

    protected static final String MOUSE_MOVE_CAMERA_X_AXIS = "SSEditor.editorState.mouseMoveCameraXAxis";
    protected static final String MOUSE_MOVE_CAMERA_X_AXIS_NEGATIVE = "SSEditor.editorState.mouseMoveCameraXAxisNegative";
    protected static final String MOUSE_MOVE_CAMERA_Y_AXIS = "SSEditor.editorState.mouseMoveCameraYAxis";
    protected static final String MOUSE_MOVE_CAMERA_Y_AXIS_NEGATIVE = "SSEditor.editorState.mouseMoveCameraYAxisNegative";

    protected static final String KEY_CTRL = "SSEditor.editorState.keyCtrl";
    protected static final String KEY_ALT = "SSEditor.editorState.keyAlt";
    protected static final String KEY_SHIFT = "SSEditor.editorState.keyShift";

    protected static final String KEY_CTRL_S = "SSEditor.editorState.Ctrl.S";
    protected static final String KEY_CTRL_Z = "SSEditor.editorState.Ctrl.Z";
    protected static final String KEY_CTRL_Y = "SSEditor.editorState.Ctrl.Y";

    protected static final String KEY_FLY_CAMERA_W = "SSEditor.editorState.keyFlyCameraW";
    protected static final String KEY_FLY_CAMERA_S = "SSEditor.editorState.keyFlyCameraS";
    protected static final String KEY_FLY_CAMERA_A = "SSEditor.editorState.keyFlyCameraA";
    protected static final String KEY_FLY_CAMERA_D = "SSEditor.editorState.keyFlyCameraD";

    protected static final String KEY_NUM_1 = "SSEditor.editorState.num1";
    protected static final String KEY_NUM_2 = "SSEditor.editorState.num2";
    protected static final String KEY_NUM_3 = "SSEditor.editorState.num3";
    protected static final String KEY_NUM_4 = "SSEditor.editorState.num4";
    protected static final String KEY_NUM_5 = "SSEditor.editorState.num5";
    protected static final String KEY_NUM_6 = "SSEditor.editorState.num6";
    protected static final String KEY_NUM_7 = "SSEditor.editorState.num7";
    protected static final String KEY_NUM_8 = "SSEditor.editorState.num8";
    protected static final String KEY_NUM_9 = "SSEditor.editorState.num9";

    static {
        TRIGGERS.put(MOUSE_X_AXIS, new MouseAxisTrigger(MouseInput.AXIS_X, false));
        TRIGGERS.put(MOUSE_X_AXIS_NEGATIVE, new MouseAxisTrigger(MouseInput.AXIS_X, true));
        TRIGGERS.put(MOUSE_Y_AXIS, new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        TRIGGERS.put(MOUSE_Y_AXIS_NEGATIVE, new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        TRIGGERS.put(MOUSE_MOVE_CAMERA_X_AXIS, new MouseAxisTrigger(MouseInput.AXIS_X, false));
        TRIGGERS.put(MOUSE_MOVE_CAMERA_X_AXIS_NEGATIVE, new MouseAxisTrigger(MouseInput.AXIS_X, true));
        TRIGGERS.put(MOUSE_MOVE_CAMERA_Y_AXIS, new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        TRIGGERS.put(MOUSE_MOVE_CAMERA_Y_AXIS_NEGATIVE, new MouseAxisTrigger(MouseInput.AXIS_Y, true));

        TRIGGERS.put(MOUSE_RIGHT_CLICK, new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        TRIGGERS.put(MOUSE_LEFT_CLICK, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        TRIGGERS.put(MOUSE_MIDDLE_CLICK, new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE));

        MULTI_TRIGGERS.put(KEY_CTRL, toArray(new KeyTrigger(KeyInput.KEY_RCONTROL), new KeyTrigger(KeyInput.KEY_LCONTROL)));
        MULTI_TRIGGERS.put(KEY_SHIFT, toArray(new KeyTrigger(KeyInput.KEY_RSHIFT), new KeyTrigger(KeyInput.KEY_LSHIFT)));
        MULTI_TRIGGERS.put(KEY_ALT, toArray(new KeyTrigger(KeyInput.KEY_RMENU), new KeyTrigger(KeyInput.KEY_LMENU)));

        TRIGGERS.put(KEY_CTRL_S, new KeyTrigger(KeyInput.KEY_S));
        TRIGGERS.put(KEY_CTRL_Z, new KeyTrigger(KeyInput.KEY_Z));
        TRIGGERS.put(KEY_CTRL_Y, new KeyTrigger(KeyInput.KEY_Y));

        TRIGGERS.put(KEY_FLY_CAMERA_W, new KeyTrigger(KeyInput.KEY_W));
        TRIGGERS.put(KEY_FLY_CAMERA_S, new KeyTrigger(KeyInput.KEY_S));
        TRIGGERS.put(KEY_FLY_CAMERA_A, new KeyTrigger(KeyInput.KEY_A));
        TRIGGERS.put(KEY_FLY_CAMERA_D, new KeyTrigger(KeyInput.KEY_D));

        TRIGGERS.put(KEY_NUM_1, new KeyTrigger(KeyInput.KEY_NUMPAD1));
        TRIGGERS.put(KEY_NUM_2, new KeyTrigger(KeyInput.KEY_NUMPAD2));
        TRIGGERS.put(KEY_NUM_3, new KeyTrigger(KeyInput.KEY_NUMPAD3));
        TRIGGERS.put(KEY_NUM_4, new KeyTrigger(KeyInput.KEY_NUMPAD4));
        TRIGGERS.put(KEY_NUM_5, new KeyTrigger(KeyInput.KEY_NUMPAD5));
        TRIGGERS.put(KEY_NUM_6, new KeyTrigger(KeyInput.KEY_NUMPAD6));
        TRIGGERS.put(KEY_NUM_7, new KeyTrigger(KeyInput.KEY_NUMPAD7));
        TRIGGERS.put(KEY_NUM_8, new KeyTrigger(KeyInput.KEY_NUMPAD8));
        TRIGGERS.put(KEY_NUM_9, new KeyTrigger(KeyInput.KEY_NUMPAD9));
    }

    /**
     * The table of action handlers.
     */
    @NotNull
    private final ObjectDictionary<String, BooleanFloatConsumer> actionHandlers;

    /**
     * The table of analog handlers.
     */
    @NotNull
    private final ObjectDictionary<String, FloatFloatConsumer> analogHandlers;

    /**
     * The action scene listeners.
     */
    @NotNull
    protected final ActionListener actionListener;

    /**
     * The analog scene listeners.
     */
    @NotNull
    protected final AnalogListener analogListener;

    /**
     * The editor camera.
     */
    @Nullable
    private final EditorCamera editorCamera;

    /**
     * The light of the camera.
     */
    @Nullable
    private final DirectionalLight lightForCamera;

    /**
     * The previous camera location.
     */
    @NotNull
    private final Vector3f prevCameraLocation;

    /**
     * The state of camera keys.
     */
    @NotNull
    private final boolean[] cameraKeysState;

    /**
     * The flag of moving camera.
     */
    @NotNull
    private AtomicInteger cameraMoving;

    /**
     * The current state manager.
     */
    @Nullable
    protected AppStateManager stateManager;

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
    private float prevCameraSpeed;

    /**
     * The camera speed.
     */
    private float cameraSpeed;

    /**
     * Is control pressed.
     */
    private boolean controlDown;

    /**
     * Is alt pressed.
     */
    private boolean altDown;

    /**
     * Is shift pressed.
     */
    private boolean shiftDown;

    /**
     * Is left button pressed.
     */
    private boolean buttonLeftDown;

    /**
     * Is right button pressed.
     */
    private boolean buttonRightDown;

    /**
     * Is middle button pressed.
     */
    private boolean buttonMiddleDown;

    public AdvancedAbstractEditor3dPart(@NotNull T fileEditor) {
        super(fileEditor);
        this.cameraMoving = new AtomicInteger();
        this.editorCamera = needEditorCamera() ? createEditorCamera() : null;
        this.lightForCamera = needLightForCamera() ? createLightForCamera() : null;
        this.prevCameraLocation = new Vector3f();
        this.cameraKeysState = new boolean[4];
        this.cameraSpeed = 1F;

        if (lightForCamera != null) {
            stateNode.addLight(lightForCamera);
        }

        this.analogListener = this::onAnalogImpl;
        this.actionListener = this::onActionImpl;
        this.actionHandlers = DictionaryFactory.newObjectDictionary();
        this.analogHandlers = DictionaryFactory.newObjectDictionary();

        registerActionHandlers(actionHandlers);
        registerAnalogHandlers(analogHandlers);
    }

    /**
     * Register action handlers.
     *
     * @param actionHandlers the action handlers
     */
    @JmeThread
    protected void registerActionHandlers(@NotNull ObjectDictionary<String, BooleanFloatConsumer> actionHandlers) {

        actionHandlers.put(KEY_ALT, (isPressed, tpf) -> setAltDown(isPressed));
        actionHandlers.put(MOUSE_LEFT_CLICK, (isPressed, tpf) -> setButtonLeftDown(isPressed));
        actionHandlers.put(MOUSE_RIGHT_CLICK, (isPressed, tpf) -> setButtonRightDown(isPressed));
        actionHandlers.put(KEY_NUM_1, (isPressed, tpf) -> rotateTo(EditorCamera.Perspective.BACK, isPressed));
        actionHandlers.put(KEY_NUM_3, (isPressed, tpf) -> rotateTo(EditorCamera.Perspective.RIGHT, isPressed));
        actionHandlers.put(KEY_NUM_7, (isPressed, tpf) -> rotateTo(EditorCamera.Perspective.TOP, isPressed));
        actionHandlers.put(KEY_NUM_9, (isPressed, tpf) -> rotateTo(EditorCamera.Perspective.BOTTOM, isPressed));
        actionHandlers.put(KEY_NUM_2, (isPressed, tpf) -> rotateTo(EditorCamera.Direction.BOTTOM, isPressed));
        actionHandlers.put(KEY_NUM_8, (isPressed, tpf) -> rotateTo(EditorCamera.Direction.TOP, isPressed));
        actionHandlers.put(KEY_NUM_4, (isPressed, tpf) -> rotateTo(EditorCamera.Direction.LEFT, isPressed));
        actionHandlers.put(KEY_NUM_6, (isPressed, tpf) -> rotateTo(EditorCamera.Direction.RIGHT, isPressed));

        actionHandlers.put(MOUSE_MIDDLE_CLICK, (isPressed, tpf) -> {
            setButtonMiddleDown(isPressed);
            if (isCameraMoving() && !isPressed) finishCameraMoving(0, true);
        });

        actionHandlers.put(KEY_CTRL, (isPressed, tpf) -> {
            setControlDown(isPressed);
            if (isCameraMoving() && isPressed && cameraSpeed > 0) {
                cameraSpeed = Math.max(cameraSpeed - 0.4F, 0.1F);
            }
        });

        actionHandlers.put(KEY_SHIFT, (isPressed, tpf) -> {
            setShiftDown(isPressed);
            if (isCameraMoving() && isPressed && cameraSpeed > 0) {
                cameraSpeed += 0.4F;
            }
        });

        actionHandlers.put(KEY_CTRL_Z, (isPressed, tpf) -> {
            if (!isPressed && isControlDown()) undo();
        });
        actionHandlers.put(KEY_CTRL_Y, (isPressed, tpf) -> {
            if (!isPressed && isControlDown()) redo();
        });

        actionHandlers.put(KEY_FLY_CAMERA_A, (isPressed, tpf) -> {
            if (isButtonMiddleDown()) moveSideCamera(tpf, true, isPressed, 0);
        });
        actionHandlers.put(KEY_FLY_CAMERA_D, (isPressed, tpf) -> {
            if (isButtonMiddleDown()) moveSideCamera(-tpf, true, isPressed, 1);
        });
        actionHandlers.put(KEY_FLY_CAMERA_W, (isPressed, tpf) -> {
            if (isButtonMiddleDown()) moveDirectionCamera(tpf, true, isPressed, 2);
        });
        actionHandlers.put(KEY_FLY_CAMERA_S, (isPressed, tpf) -> {
            if (isButtonMiddleDown()) moveDirectionCamera(-tpf, true, isPressed, 3);
        });

        actionHandlers.put(KEY_CTRL_S, (isPressed, tpf) -> {
            if (isPressed && isControlDown() && fileEditor.isDirty()) {
                ExecutorManager.getInstance()
                        .addFxTask(fileEditor::save);
            }
        });
    }

    /**
     * Register analog handlers.
     *
     * @param analogHandlers the analog handlers
     */
    @JmeThread
    protected void registerAnalogHandlers(@NotNull ObjectDictionary<String, FloatFloatConsumer> analogHandlers) {
        analogHandlers.put(MOUSE_X_AXIS, (value, tpf) -> moveXMouse(value));
        analogHandlers.put(MOUSE_X_AXIS_NEGATIVE, (value, tpf) -> moveXMouse(-value));
        analogHandlers.put(MOUSE_Y_AXIS, (value, tpf) -> moveYMouse(-value));
        analogHandlers.put(MOUSE_Y_AXIS_NEGATIVE, (value, tpf) -> moveYMouse(value));
    }

    /**
     * Handle analog events.
     */
    @JmeThread
    private void onAnalogImpl(@NotNull String name, float value, float tpf) {
        var handler = analogHandlers.get(name);
        if (handler != null) {
            handler.accept(value, tpf);
        }
    }

    /**
     * Move a mouse on X axis.
     *
     * @param value the value to move.
     */
    @JmeThread
    protected void moveXMouse(float value) {
    }

    /**
     * Move a mouse on Y axis.
     *
     * @param value the value to move.
     */
    @JmeThread
    protected void moveYMouse(float value) {
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
     * Get the state of camera keys.
     *
     * @return the state of camera keys.
     */
    @JmeThread
    private @NotNull boolean[] getCameraKeysState() {
        return cameraKeysState;
    }

    /**
     * Start to move the camera.
     */
    @JmeThread
    private void startCameraMoving(int key) {

        if (Config.DEV_CAMERA_DEBUG && LOGGER.isEnabled(LoggerLevel.DEBUG)) {
            LOGGER.debug(this, "start camera moving[" + cameraMoving + "] for key " + key);
        }

        if (cameraMoving.get() == 0) {

            var camera = EditorUtil.getGlobalCamera();
            var nodeForCamera = getNodeForCamera();
            nodeForCamera.setLocalTranslation(camera.getLocation());

            var editorCamera = notNull(getEditorCamera());
            editorCamera.setTargetDistance(0);
        }

        var cameraKeysState = getCameraKeysState();

        if (!cameraKeysState[key]) {
            cameraKeysState[key] = true;
            cameraMoving.incrementAndGet();
        }
    }

    /**
     * Finish to move the camera.
     */
    @JmeThread
    private void finishCameraMoving(int key, boolean force) {

        var cameraKeysState = getCameraKeysState();

        if (Config.DEV_CAMERA_DEBUG && LOGGER.isEnabled(LoggerLevel.DEBUG)) {
            LOGGER.debug(this, "finish camera moving[" + cameraMoving + "] for key " + key + ", force = " + force);
        }

        cameraKeysState[key] = false;

        if (cameraMoving.get() == 0) {
            return;
        }

        if (force) {
            cameraMoving.set(0);
            for (int i = 0; i < cameraKeysState.length; i++) {
                cameraKeysState[i] = false;
            }
        } else {
            cameraMoving.decrementAndGet();
        }
    }

    /**
     * Move a camera to direction.
     *
     * @param value the value to move.
     */
    @JmeThread
    private void moveDirectionCamera(float value, boolean isAction, boolean isPressed, int key) {

        if (!canCameraMove()) {
            return;
        } else if (isAction && isPressed) {
            startCameraMoving(key);
        } else if (isAction) {
            finishCameraMoving(key, false);
        }

        if (!isCameraMoving() || isAction) {
            return;
        }

        var editorCamera = getEditorCamera();

        if (editorCamera == null) {
            return;
        }

        var camera = EditorUtil.getGlobalCamera();
        var nodeForCamera = getNodeForCamera();

        var local = LocalObjects.get();

        var direction = camera.getDirection(local.nextVector());
        direction.multLocal(value * cameraSpeed);
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

        if (!canCameraMove()) {
            return;
        } else if (isAction && isPressed) {
            startCameraMoving(key);
        } else if (isAction) {
            finishCameraMoving(key, false);
        }

        if (!isCameraMoving() || isAction) {
            return;
        }

        var editorCamera = getEditorCamera();

        if (editorCamera == null) {
            return;
        }

        var camera = EditorUtil.getGlobalCamera();
        var nodeForCamera = getNodeForCamera();

        var local = LocalObjects.get();
        var left = camera.getLeft(local.nextVector());
        left.multLocal(value * cameraSpeed);
        left.addLocal(nodeForCamera.getLocalTranslation());

        nodeForCamera.setLocalTranslation(left);
    }

    @JmeThread
    private boolean canCameraMove() {
        return needMovableCamera() && isButtonMiddleDown();
    }

    /**
     * Return true if camera can move.
     *
     * @return true if camera can move.
     */
    @JmeThread
    protected boolean needMovableCamera() {
        return true;
    }

    /**
     * Handle action events.
     *
     * @param name      the name.
     * @param isPressed true if action is pressed.
     * @param tpf       the tpf.
     */
    @JmeThread
    protected void onActionImpl(@NotNull String name, boolean isPressed, float tpf) {

        var handler = actionHandlers.get(name);

        if (handler != null) {
            handler.accept(isPressed, tpf);
        }

        var editorCamera = getEditorCamera();

        if (editorCamera != null && needMovableCamera()) {
            editorCamera.setLockRotation(isShiftDown() && isButtonMiddleDown());
        }
    }

    /**
     * Rotate to the perspective.
     *
     * @param perspective the perspective.
     * @param isPressed   true if a key is pressed.
     */
    @JmeThread
    protected void rotateTo(@NotNull EditorCamera.Perspective perspective, boolean isPressed) {
        var editorCamera = getEditorCamera();
        if (editorCamera != null && isPressed) {
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
    protected void rotateTo(@NotNull EditorCamera.Direction direction, boolean isPressed) {
        var editorCamera = getEditorCamera();
        if (editorCamera != null && isPressed) {
            editorCamera.rotateTo(direction, 10F);
        }
    }

    /**
     * Redo last operation.
     */
    @JmeThread
    protected void redo() {
    }

    /**
     * Undo last operation.
     */
    @JmeThread
    protected void undo() {
    }

    /**
     * Return true if alt is pressed.
     *
     * @return true if alt is pressed.
     */
    @JmeThread
    protected boolean isAltDown() {
        return altDown;
    }

    /**
     * Set true if the alt is pressed.
     *
     * @param altDown true if the alt is pressed.
     */
    @JmeThread
    protected void setAltDown(boolean altDown) {
        this.altDown = altDown;
    }

    /**
     * Return true if control is pressed.
     *
     * @return true if control is pressed.
     */
    @JmeThread
    protected boolean isControlDown() {
        return controlDown;
    }

    /**
     * Set true if the control is pressed.
     *
     * @param controlDown true if the control is pressed.
     */
    @JmeThread
    protected void setControlDown(boolean controlDown) {
        this.controlDown = controlDown;
    }

    /**
     * Return true if shift is pressed.
     *
     * @return true if shift is pressed.
     */
    @JmeThread
    protected boolean isShiftDown() {
        return shiftDown;
    }

    /**
     * Set true if the shift is pressed.
     *
     * @param shiftDown true if the shift is pressed.
     */
    @JmeThread
    protected void setShiftDown(boolean shiftDown) {
        this.shiftDown = shiftDown;
    }

    /**
     * Set true if the left button is pressed.
     *
     * @param buttonLeftDown true if the left button is pressed.
     */
    @JmeThread
    protected void setButtonLeftDown(boolean buttonLeftDown) {
        this.buttonLeftDown = buttonLeftDown;
    }

    /**
     * Set true if the middle button is pressed.
     *
     * @param buttonMiddleDown true if the middle button is pressed.
     */
    @JmeThread
    protected void setButtonMiddleDown(boolean buttonMiddleDown) {
        this.buttonMiddleDown = buttonMiddleDown;
    }

    /**
     * Set true if the right button is pressed.
     *
     * @param buttonRightDown true if the right button is pressed.
     */
    @JmeThread
    protected void setButtonRightDown(boolean buttonRightDown) {
        this.buttonRightDown = buttonRightDown;
    }

    /**
     * Return true if left button is pressed.
     *
     * @return true if left button is pressed.
     */
    @JmeThread
    protected boolean isButtonLeftDown() {
        return buttonLeftDown;
    }

    /**
     * Return true if middle button is pressed.
     *
     * @return true if middle button is pressed.
     */
    @JmeThread
    protected boolean isButtonMiddleDown() {
        return buttonMiddleDown;
    }

    /**
     * Return true if right button is pressed.
     *
     * @return true if right button is pressed.
     */
    @JmeThread
    protected boolean isButtonRightDown() {
        return buttonRightDown;
    }

    /**
     * Get the editor camera.
     *
     * @return the editor camera.
     */
    @JmeThread
    protected @Nullable EditorCamera getEditorCamera() {
        return editorCamera;
    }

    @Override
    @JmeThread
    public void initialize(@NotNull AppStateManager stateManager, @NotNull Application application) {
        super.initialize(stateManager, application);
        this.stateManager = stateManager;

        var rootNode = EditorUtil.getGlobalRootNode();
        rootNode.attachChild(stateNode);

        var filterRegistry = RenderFilterRegistry.getInstance();
        filterRegistry.enableFilters();

        var editorCamera = getEditorCamera();
        var inputManager = EditorUtil.getInputManager();

        checkAndAddMappings(inputManager);
        registerActionListener(inputManager);
        registerAnalogListener(inputManager);

        if (editorCamera != null) {
            editorCamera.setEnabled(true);
            editorCamera.registerInput(inputManager);
        }
    }

    /**
     * Check and update all triggers.
     *
     * @param inputManager the input manager
     */
    @JmeThread
    protected void checkAndAddMappings(@NotNull InputManager inputManager) {
        TRIGGERS.forEach(inputManager, AdvancedAbstractEditor3dPart::addMapping);
        MULTI_TRIGGERS.forEach(inputManager, AdvancedAbstractEditor3dPart::addMapping);
    }

    @JmeThread
    private static void addMapping(
            @NotNull InputManager inputManager,
            @NotNull String name,
            @NotNull Trigger[] triggers
    ) {
        if (!inputManager.hasMapping(name)) {
            inputManager.addMapping(name, triggers);
        }
    }

    @JmeThread
    private static void addMapping(
            @NotNull InputManager inputManager,
            @NotNull String name,
            @NotNull Trigger trigger
    ) {
        if (!inputManager.hasMapping(name)) {
            inputManager.addMapping(name, trigger);
        }
    }

    /**
     * Register the analog listener.
     *
     * @param inputManager the input manager
     */
    @JmeThread
    protected void registerAnalogListener(@NotNull InputManager inputManager) {
        inputManager.addListener(analogListener, MOUSE_X_AXIS, MOUSE_X_AXIS_NEGATIVE, MOUSE_Y_AXIS,
                MOUSE_Y_AXIS_NEGATIVE, MOUSE_MOVE_CAMERA_X_AXIS, MOUSE_MOVE_CAMERA_X_AXIS_NEGATIVE,
                MOUSE_MOVE_CAMERA_Y_AXIS, MOUSE_MOVE_CAMERA_Y_AXIS_NEGATIVE);
    }

    /**
     * Register the action listener.
     *
     * @param inputManager the input manager
     */
    @JmeThread
    protected void registerActionListener(@NotNull InputManager inputManager) {
        inputManager.addListener(actionListener, MOUSE_RIGHT_CLICK, MOUSE_LEFT_CLICK, MOUSE_MIDDLE_CLICK);
        inputManager.addListener(actionListener, KEY_CTRL, KEY_SHIFT, KEY_ALT, KEY_CTRL_S, KEY_CTRL_Z, KEY_CTRL_Y, KEY_NUM_1,
                KEY_NUM_2, KEY_NUM_3, KEY_NUM_4, KEY_NUM_5, KEY_NUM_6, KEY_NUM_7, KEY_NUM_8, KEY_NUM_9, KEY_FLY_CAMERA_W,
                KEY_FLY_CAMERA_S, KEY_FLY_CAMERA_A, KEY_FLY_CAMERA_D);
    }

    @Override
    @JmeThread
    public void cleanup() {
        super.cleanup();

        var filterRegistry = RenderFilterRegistry.getInstance();
        filterRegistry.disableFilters();

        var rootNode = EditorUtil.getGlobalRootNode();
        rootNode.detachChild(stateNode);

        var editorCamera = getEditorCamera();

        var inputManager = EditorUtil.getInputManager();
        inputManager.removeListener(actionListener);
        inputManager.removeListener(analogListener);

        if (editorCamera != null) {
            editorCamera.setEnabled(false);
            editorCamera.unregisterInput(inputManager);
        }
    }

    /**
     * Need editor camera boolean.
     *
     * @return true if need an editor camera.
     */
    @JmeThread
    protected boolean needEditorCamera() {
        return false;
    }

    /**
     * Need light for camera boolean.
     *
     * @return true if need a camera light.
     */
    @JmeThread
    protected boolean needLightForCamera() {
        return false;
    }

    /**
     * Create an editor camera.
     *
     * @return the new camera.
     */
    @JmeThread
    protected @NotNull EditorCamera createEditorCamera() {

        var camera = EditorUtil.getGlobalCamera();

        var editorCamera = new EditorCamera(camera, getNodeForCamera());
        editorCamera.setMaxDistance(10000);
        editorCamera.setMinDistance(0.01F);
        editorCamera.setSmoothMotion(false);
        editorCamera.setRotationSensitivity(1);
        editorCamera.setZoomSensitivity(0.2F);

        return editorCamera;
    }

    /**
     * Create light for camera directional light.
     *
     * @return the light for the camera.
     */
    @JmeThread
    protected @NotNull DirectionalLight createLightForCamera() {
        var directionalLight = new DirectionalLight();
        directionalLight.setColor(ColorRGBA.White);
        return directionalLight;
    }

    /**
     * Get the node for the camera.
     *
     * @return the node for the camera.
     */
    @JmeThread
    protected @NotNull Node getNodeForCamera() {
        return stateNode;
    }

    /**
     * Get the light for the camera.
     *
     * @return the light for the camera.
     */
    @JmeThread
    protected @Nullable DirectionalLight getLightForCamera() {
        return lightForCamera;
    }

    /**
     * Get the previous horizontal camera rotation.
     *
     * @return the previous horizontal camera rotation.
     */
    @JmeThread
    protected float getPrevHRotation() {
        return prevHRotation;
    }

    /**
     * Set the previous horizontal camera rotation.
     *
     * @param prevHRotation the previous horizontal camera rotation.
     */
    @JmeThread
    protected void setPrevHRotation(float prevHRotation) {
        this.prevHRotation = prevHRotation;
    }

    /**
     * Get the previous camera zoom.
     *
     * @return the previous camera zoom.
     */
    @JmeThread
    protected float getPrevTargetDistance() {
        return prevTargetDistance;
    }

    /**
     * Set the previous camera zoom.
     *
     * @param prevTargetDistance the previous camera zoom.
     */
    @JmeThread
    protected void setPrevTargetDistance(float prevTargetDistance) {
        this.prevTargetDistance = prevTargetDistance;
    }

    /**
     * Get the previous vertical camera rotation.
     *
     * @return the previous vertical camera rotation.
     */
    @JmeThread
    protected float getPrevVRotation() {
        return prevVRotation;
    }

    /**
     * Set the previous vertical camera rotation.
     *
     * @param prevVRotation the previous vertical camera rotation.
     */
    @JmeThread
    protected void setPrevVRotation(float prevVRotation) {
        this.prevVRotation = prevVRotation;
    }

    /**
     * Get the previous camera location.
     *
     * @return the previous camera location
     */
    @FromAnyThread
    public @NotNull Vector3f getPrevCameraLocation() {
        return prevCameraLocation;
    }

    /**
     * Get the camera speed.
     *
     * @return the camera speed.
     */
    @FromAnyThread
    private float getCameraSpeed() {
        return cameraSpeed;
    }

    /**
     * Set the camera speed.
     *
     * @param cameraSpeed the camera speed.
     */
    @FromAnyThread
    private void setCameraSpeed(float cameraSpeed) {
        this.cameraSpeed = cameraSpeed;
    }

    /**
     * Get the previous camera speed.
     *
     * @return the previous camera speed.
     */
    @FromAnyThread
    private float getPrevCameraSpeed() {
        return prevCameraSpeed;
    }

    /**
     * Set the previous camera speed.
     *
     * @param prevCameraSpeed the previous camera speed.
     */
    @FromAnyThread
    private void setPrevCameraSpeed(float prevCameraSpeed) {
        this.prevCameraSpeed = prevCameraSpeed;
    }

    @Override
    @JmeThread
    public void update(float tpf) {
        super.update(tpf);
        preCameraUpdate(tpf);
        cameraUpdate(tpf);
        postCameraUpdate(tpf);
    }

    @JmeThread
    protected void postCameraUpdate(float tpf) {

        var lightForCamera = getLightForCamera();
        var editorCamera = getEditorCamera();

        if (editorCamera != null && lightForCamera != null && needUpdateCameraLight()) {
            var camera = EditorUtil.getGlobalCamera();
            lightForCamera.setDirection(camera.getDirection().normalize());
        }
    }

    @JmeThread
    protected void cameraUpdate(float tpf) {

        var editorCamera = getEditorCamera();

        if (editorCamera == null) {
            return;
        }

        editorCamera.updateCamera(tpf);

        var cameraKeysState = getCameraKeysState();

        if (isCameraMoving()) {
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

    @JmeThread
    protected void preCameraUpdate(final float tpf) {
    }

    /**
     * Check camera changes.
     *
     * @param editorCamera the editor's camera.
     */
    @JmeThread
    protected void checkCameraChanges(@NotNull EditorCamera editorCamera) {

        int changes = 0;

        var nodeForCamera = getNodeForCamera();
        var prevCameraLocation = getPrevCameraLocation();
        var cameraLocation = nodeForCamera.getLocalTranslation();

        var prevHRotation = getPrevHRotation();
        var hRotation = editorCamera.getHorizontalRotation();

        var prevVRotation = getPrevVRotation();
        var vRotation = editorCamera.getVerticalRotation();

        var prevTargetDistance = getPrevTargetDistance();
        var targetDistance = editorCamera.getTargetDistance();

        var cameraSpeed = getCameraSpeed();
        var prevCameraSpeed = getPrevCameraSpeed();

        if (!prevCameraLocation.equals(cameraLocation)) {
            changes++;
        } else if (prevHRotation != hRotation || prevVRotation != vRotation) {
            changes++;
        } else if (prevTargetDistance != targetDistance) {
            changes++;
        } else if (cameraSpeed != prevCameraSpeed) {
            changes++;
        }

        if (changes > 0) {
            notifyChangedCameraSettings(cameraLocation, hRotation, vRotation, targetDistance, cameraSpeed);
        }

        prevCameraLocation.set(cameraLocation);

        setPrevHRotation(hRotation);
        setPrevVRotation(vRotation);
        setPrevTargetDistance(targetDistance);
        setPrevCameraSpeed(cameraSpeed);
    }

    /**
     * Notify about changed camera's settings.
     *
     * @param cameraLocation the camera location.
     * @param hRotation      the h rotation.
     * @param vRotation      the v rotation.
     * @param targetDistance the target distance.
     * @param cameraSpeed    the camera speed.
     */
    @JmeThread
    protected void notifyChangedCameraSettings(
            @NotNull Vector3f cameraLocation,
            float hRotation,
            float vRotation,
            float targetDistance,
            float cameraSpeed
    ) {
    }

    /**
     * Update the editor's camera settings.
     *
     * @param cameraLocation the camera location.
     * @param hRotation      the h rotation.
     * @param vRotation      the v rotation.
     * @param targetDistance the target distance.
     * @param cameraSpeed    the camera speed.
     */
    @JmeThread
    public void updateCameraSettings(
            @NotNull Vector3f cameraLocation,
            float hRotation,
            float vRotation,
            float targetDistance,
            float cameraSpeed
    ) {

        var editorCamera = getEditorCamera();

        if (editorCamera == null) {
            return;
        }

        editorCamera.setTargetRotation(hRotation);
        editorCamera.setTargetVRotation(vRotation);
        editorCamera.setTargetDistance(targetDistance);

        getNodeForCamera().setLocalTranslation(cameraLocation);
        getPrevCameraLocation().set(cameraLocation);

        setPrevHRotation(hRotation);
        setPrevVRotation(vRotation);
        setPrevTargetDistance(targetDistance);
        setPrevCameraSpeed(cameraSpeed);
        setCameraSpeed(cameraSpeed);

        editorCamera.update(1F);
    }

    /**
     * Return true if need to update the camera light.
     *
     * @return true if need to update the camera light.
     */
    @JmeThread
    protected boolean needUpdateCameraLight() {
        return false;
    }
}
