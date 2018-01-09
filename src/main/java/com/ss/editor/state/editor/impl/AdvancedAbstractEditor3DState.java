package com.ss.editor.state.editor.impl;

import static com.ss.rlib.util.ObjectUtils.notNull;
import static com.ss.rlib.util.array.ArrayFactory.toArray;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
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
import com.ss.editor.annotation.JMEThread;
import com.ss.editor.config.Config;
import com.ss.editor.model.EditorCamera;
import com.ss.editor.ui.component.editor.FileEditor;
import com.ss.editor.util.LocalObjects;
import com.ss.rlib.function.BooleanFloatConsumer;
import com.ss.rlib.function.FloatFloatConsumer;
import com.ss.rlib.logging.LoggerLevel;
import com.ss.rlib.util.dictionary.DictionaryFactory;
import com.ss.rlib.util.dictionary.ObjectDictionary;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tonegod.emitter.filter.TonegodTranslucentBucketFilter;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The base implementation of the {@link com.ss.editor.state.editor.Editor3DState} for a file editor.
 *
 * @param <T> the type of a file editor.
 * @author JavaSaBr
 */
public abstract class AdvancedAbstractEditor3DState<T extends FileEditor> extends AbstractEditor3DState<T> {

    /**
     * The constant TRIGGERS.
     */
    @NotNull
    protected static final ObjectDictionary<String, Trigger> TRIGGERS = DictionaryFactory.newObjectDictionary();

    /**
     * The constant MULTI_TRIGGERS.
     */
    @NotNull
    protected static final ObjectDictionary<String, Trigger[]> MULTI_TRIGGERS = DictionaryFactory.newObjectDictionary();

    /**
     * The constant MOUSE_RIGHT_CLICK.
     */
    @NotNull
    protected static final String MOUSE_RIGHT_CLICK = "SSEditor.editorState.mouseRightClick";

    /**
     * The constant MOUSE_LEFT_CLICK.
     */
    @NotNull
    protected static final String MOUSE_LEFT_CLICK = "SSEditor.editorState.mouseLeftClick";

    /**
     * The constant MOUSE_MIDDLE_CLICK.
     */
    @NotNull
    protected static final String MOUSE_MIDDLE_CLICK = "SSEditor.editorState.mouseMiddleClick";

    /**
     * The constant MOUSE_X_AXIS.
     */
    @NotNull
    protected static final String MOUSE_X_AXIS = "SSEditor.editorState.mouseXAxis";

    /**
     * The constant MOUSE_X_AXIS_NEGATIVE.
     */
    @NotNull
    protected static final String MOUSE_X_AXIS_NEGATIVE = "SSEditor.editorState.mouseXAxisNegative";

    /**
     * The constant MOUSE_Y_AXIS.
     */
    @NotNull
    protected static final String MOUSE_Y_AXIS = "SSEditor.editorState.mouseYAxis";

    /**
     * The constant MOUSE_Y_AXIS_NEGATIVE.
     */
    @NotNull
    protected static final String MOUSE_Y_AXIS_NEGATIVE = "SSEditor.editorState.mouseYAxisNegative";

    /**
     * The constant MOUSE_MOVE_CAMERA_X_AXIS.
     */
    @NotNull
    protected static final String MOUSE_MOVE_CAMERA_X_AXIS = "SSEditor.editorState.mouseMoveCameraXAxis";

    /**
     * The constant MOUSE_MOVE_CAMERA_X_AXIS_NEGATIVE.
     */
    @NotNull
    protected static final String MOUSE_MOVE_CAMERA_X_AXIS_NEGATIVE = "SSEditor.editorState.mouseMoveCameraXAxisNegative";

    /**
     * The constant MOUSE_MOVE_CAMERA_Y_AXIS.
     */
    @NotNull
    protected static final String MOUSE_MOVE_CAMERA_Y_AXIS = "SSEditor.editorState.mouseMoveCameraYAxis";

    /**
     * The constant MOUSE_MOVE_CAMERA_Y_AXIS_NEGATIVE.
     */
    @NotNull
    protected static final String MOUSE_MOVE_CAMERA_Y_AXIS_NEGATIVE = "SSEditor.editorState.mouseMoveCameraYAxisNegative";

    /**
     * The constant KEY_CTRL.
     */
    @NotNull
    protected static final String KEY_CTRL = "SSEditor.editorState.keyCtrl";

    /**
     * The constant KEY_ALT.
     */
    @NotNull
    protected static final String KEY_ALT = "SSEditor.editorState.keyAlt";

    /**
     * The constant KEY_SHIFT.
     */
    @NotNull
    protected static final String KEY_SHIFT = "SSEditor.editorState.keyShift";

    /**
     * The constant KEY_CTRL_S.
     */
    @NotNull
    protected static final String KEY_CTRL_S = "SSEditor.editorState.Ctrl.S";

    /**
     * The constant KEY_CTRL_Z.
     */
    @NotNull
    protected static final String KEY_CTRL_Z = "SSEditor.editorState.Ctrl.Z";

    /**
     * The constant KEY_CTRL_Y.
     */
    @NotNull
    protected static final String KEY_CTRL_Y = "SSEditor.editorState.Ctrl.Y";

    /**
     * The constant KEY_FLY_CAMERA_W.
     */
    @NotNull
    protected static final String KEY_FLY_CAMERA_W = "SSEditor.editorState.keyFlyCameraW";

    /**
     * The constant KEY_FLY_CAMERA_S.
     */
    @NotNull
    protected static final String KEY_FLY_CAMERA_S = "SSEditor.editorState.keyFlyCameraS";

    /**
     * The constant KEY_FLY_CAMERA_A.
     */
    @NotNull
    protected static final String KEY_FLY_CAMERA_A = "SSEditor.editorState.keyFlyCameraA";

    /**
     * The constant KEY_FLY_CAMERA_D.
     */
    @NotNull
    protected static final String KEY_FLY_CAMERA_D = "SSEditor.editorState.keyFlyCameraD";

    /**
     * The constant KEY_NUM_1.
     */
    @NotNull
    protected static final String KEY_NUM_1 = "SSEditor.editorState.num1";

    /**
     * The constant KEY_NUM_2.
     */
    @NotNull
    protected static final String KEY_NUM_2 = "SSEditor.editorState.num2";

    /**
     * The constant KEY_NUM_3.
     */
    @NotNull
    protected static final String KEY_NUM_3 = "SSEditor.editorState.num3";

    /**
     * The constant KEY_NUM_4.
     */
    @NotNull
    protected static final String KEY_NUM_4 = "SSEditor.editorState.num4";

    /**
     * The constant KEY_NUM_5.
     */
    @NotNull
    protected static final String KEY_NUM_5 = "SSEditor.editorState.num5";

    /**
     * The constant KEY_NUM_6.
     */
    @NotNull
    protected static final String KEY_NUM_6 = "SSEditor.editorState.num6";

    /**
     * The constant KEY_NUM_7.
     */
    @NotNull
    protected static final String KEY_NUM_7 = "SSEditor.editorState.num7";

    /**
     * The constant KEY_NUM_8.
     */
    @NotNull
    protected static final String KEY_NUM_8 = "SSEditor.editorState.num8";

    /**
     * The constant KEY_NUM_9.
     */
    @NotNull
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

    /**
     * Instantiates a new Advanced abstract editor app state.
     *
     * @param fileEditor the file editor
     */
    public AdvancedAbstractEditor3DState(@NotNull final T fileEditor) {
        super(fileEditor);
        this.cameraMoving = new AtomicInteger();
        this.editorCamera = needEditorCamera() ? createEditorCamera() : null;
        this.lightForCamera = needLightForCamera() ? createLightForCamera() : null;
        this.prevCameraLocation = new Vector3f();
        this.cameraKeysState = new boolean[4];
        this.cameraSpeed = 1F;

        if (lightForCamera != null) {
            final Node stateNode = getStateNode();
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
    @JMEThread
    protected void registerActionHandlers(@NotNull final ObjectDictionary<String, BooleanFloatConsumer> actionHandlers) {
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

            final FileEditor fileEditor = getFileEditor();

            if (isPressed && isControlDown() && fileEditor.isDirty()) {
                EXECUTOR_MANAGER.addFXTask(fileEditor::save);
            }
        });
    }

    /**
     * Register analog handlers.
     *
     * @param analogHandlers the analog handlers
     */
    @JMEThread
    protected void registerAnalogHandlers(@NotNull final ObjectDictionary<String, FloatFloatConsumer> analogHandlers) {
        analogHandlers.put(MOUSE_X_AXIS, (value, tpf) -> moveXMouse(value));
        analogHandlers.put(MOUSE_X_AXIS_NEGATIVE, (value, tpf) -> moveXMouse(-value));
        analogHandlers.put(MOUSE_Y_AXIS, (value, tpf) -> moveYMouse(-value));
        analogHandlers.put(MOUSE_Y_AXIS_NEGATIVE, (value, tpf) -> moveYMouse(value));
    }

    /**
     * Handle analog events.
     */
    @JMEThread
    private void onAnalogImpl(@NotNull final String name, final float value, final float tpf) {
        final FloatFloatConsumer handler = analogHandlers.get(name);
        if (handler != null) handler.accept(value, tpf);
    }

    /**
     * Move a mouse on X axis.
     *
     * @param value the value to move.
     */
    @JMEThread
    protected void moveXMouse(final float value) {
    }

    /**
     * Move a mouse on Y axis.
     *
     * @param value the value to move.
     */
    @JMEThread
    protected void moveYMouse(final float value) {
    }

    /**
     * @return true if the camera is moving now.
     */
    @JMEThread
    public boolean isCameraMoving() {
        return cameraMoving.get() != 0;
    }

    /**
     * @return the state of camera keys.
     */
    @JMEThread
    private @NotNull boolean[] getCameraKeysState() {
        return cameraKeysState;
    }

    /**
     * Start to move the camera.
     */
    @JMEThread
    private void startCameraMoving(final int key) {

        if (Config.DEV_CAMERA_DEBUG && LOGGER.isEnabled(LoggerLevel.DEBUG)) {
            LOGGER.debug(this, "start camera moving[" + cameraMoving + "] for key " + key);
        }

        if (cameraMoving.get() == 0) {

            final Camera camera = EDITOR.getCamera();
            final Node nodeForCamera = getNodeForCamera();
            nodeForCamera.setLocalTranslation(camera.getLocation());

            final EditorCamera editorCamera = notNull(getEditorCamera());
            editorCamera.setTargetDistance(0);
        }

        final boolean[] cameraKeysState = getCameraKeysState();

        if (!cameraKeysState[key]) {
            cameraKeysState[key] = true;
            cameraMoving.incrementAndGet();
        }
    }

    /**
     * Finish to move the camera.
     */
    @JMEThread
    private void finishCameraMoving(final int key, final boolean force) {

        final boolean[] cameraKeysState = getCameraKeysState();

        if (Config.DEV_CAMERA_DEBUG && LOGGER.isEnabled(LoggerLevel.DEBUG)) {
            LOGGER.debug(this, "finish camera moving[" + cameraMoving + "] for key " + key + ", force = " + force);
        }

        cameraKeysState[key] = false;

        if (cameraMoving.get() == 0) return;

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
    @JMEThread
    private void moveDirectionCamera(final float value, final boolean isAction, final boolean isPressed, final int key) {
        if (!canCameraMove()) return;
        if (isAction && isPressed) startCameraMoving(key);
        else if (isAction) finishCameraMoving(key, false);
        if (!isCameraMoving() || isAction) return;

        final EditorCamera editorCamera = getEditorCamera();
        if (editorCamera == null) return;

        final Camera camera = EDITOR.getCamera();
        final Node nodeForCamera = getNodeForCamera();

        final LocalObjects local = LocalObjects.get();
        final Vector3f direction = camera.getDirection(local.nextVector());
        direction.multLocal(value * cameraSpeed);
        direction.addLocal(nodeForCamera.getLocalTranslation());

        nodeForCamera.setLocalTranslation(direction);
    }

    /**
     * Move a camera to side.
     *
     * @param value the value to move.
     */
    @JMEThread
    private void moveSideCamera(final float value, final boolean isAction, final boolean isPressed, final int key) {
        if (!canCameraMove()) return;
        if (isAction && isPressed) startCameraMoving(key);
        else if (isAction) finishCameraMoving(key, false);
        if (!isCameraMoving() || isAction) return;

        final EditorCamera editorCamera = getEditorCamera();
        if (editorCamera == null) return;

        final Camera camera = EDITOR.getCamera();
        final Node nodeForCamera = getNodeForCamera();

        final LocalObjects local = LocalObjects.get();
        final Vector3f left = camera.getLeft(local.nextVector());
        left.multLocal(value * cameraSpeed);
        left.addLocal(nodeForCamera.getLocalTranslation());

        nodeForCamera.setLocalTranslation(left);
    }

    @JMEThread
    private boolean canCameraMove() {
        return needMovableCamera() && isButtonMiddleDown();
    }

    /**
     * Need movable camera boolean.
     *
     * @return true if camera can move.
     */
    @JMEThread
    protected boolean needMovableCamera() {
        return true;
    }

    /**
     * Handle action events.
     *
     * @param name      the name
     * @param isPressed the is pressed
     * @param tpf       the tpf
     */
    @JMEThread
    protected void onActionImpl(final String name, final boolean isPressed, final float tpf) {

        final BooleanFloatConsumer handler = actionHandlers.get(name);
        if (handler != null) handler.accept(isPressed, tpf);

        final EditorCamera editorCamera = getEditorCamera();

        if (editorCamera != null && needMovableCamera()) {
            editorCamera.setLockRotation(isShiftDown() && isButtonMiddleDown());
        }
    }

    /**
     * Rotate to.
     *
     * @param perspective the perspective
     * @param isPressed   the is pressed
     */
    @JMEThread
    protected void rotateTo(@NotNull final EditorCamera.Perspective perspective, final boolean isPressed) {
        final EditorCamera editorCamera = getEditorCamera();
        if (editorCamera != null && isPressed) editorCamera.rotateTo(perspective);
    }

    /**
     * Rotate to.
     *
     * @param direction the direction
     * @param isPressed the is pressed
     */
    @JMEThread
    protected void rotateTo(@NotNull final EditorCamera.Direction direction, final boolean isPressed) {
        final EditorCamera editorCamera = getEditorCamera();
        if (editorCamera != null && isPressed) editorCamera.rotateTo(direction, 10F);
    }

    /**
     * Redo last operation.
     */
    @JMEThread
    protected void redo() {
    }

    /**
     * Undo last operation.
     */
    @JMEThread
    protected void undo() {
    }

    /**
     * Is alt down boolean.
     *
     * @return true if alt is pressed.
     */
    @JMEThread
    protected boolean isAltDown() {
        return altDown;
    }

    /**
     * Sets alt down.
     *
     * @param altDown the alt is pressed.
     */
    @JMEThread
    protected void setAltDown(final boolean altDown) {
        this.altDown = altDown;
    }

    /**
     * Is control down boolean.
     *
     * @return true if control is pressed.
     */
    @JMEThread
    protected boolean isControlDown() {
        return controlDown;
    }

    /**
     * Sets control down.
     *
     * @param controlDown the control is pressed.
     */
    @JMEThread
    protected void setControlDown(final boolean controlDown) {
        this.controlDown = controlDown;
    }

    /**
     * Is shift down boolean.
     *
     * @return true if shift is pressed.
     */
    @JMEThread
    protected boolean isShiftDown() {
        return shiftDown;
    }

    /**
     * Sets shift down.
     *
     * @param shiftDown the shift is pressed.
     */
    @JMEThread
    protected void setShiftDown(final boolean shiftDown) {
        this.shiftDown = shiftDown;
    }

    /**
     * Sets button left down.
     *
     * @param buttonLeftDown the left button is pressed.
     */
    @JMEThread
    protected void setButtonLeftDown(final boolean buttonLeftDown) {
        this.buttonLeftDown = buttonLeftDown;
    }

    /**
     * Sets button middle down.
     *
     * @param buttonMiddleDown the middle button is pressed.
     */
    @JMEThread
    protected void setButtonMiddleDown(final boolean buttonMiddleDown) {
        this.buttonMiddleDown = buttonMiddleDown;
    }

    /**
     * Sets button right down.
     *
     * @param buttonRightDown the right button is pressed.
     */
    @JMEThread
    protected void setButtonRightDown(final boolean buttonRightDown) {
        this.buttonRightDown = buttonRightDown;
    }

    /**
     * Is button left down boolean.
     *
     * @return true if left button is pressed.
     */
    @JMEThread
    protected boolean isButtonLeftDown() {
        return buttonLeftDown;
    }

    /**
     * Is button middle down boolean.
     *
     * @return true if middle button is pressed.
     */
    @JMEThread
    protected boolean isButtonMiddleDown() {
        return buttonMiddleDown;
    }

    /**
     * Is button right down boolean.
     *
     * @return true if right button is pressed.
     */
    @JMEThread
    protected boolean isButtonRightDown() {
        return buttonRightDown;
    }

    /**
     * Gets editor camera.
     *
     * @return the editor camera.
     */
    @JMEThread
    protected @Nullable EditorCamera getEditorCamera() {
        return editorCamera;
    }

    @Override
    @JMEThread
    public void initialize(@NotNull final AppStateManager stateManager, @NotNull final Application application) {
        super.initialize(stateManager, application);
        this.stateManager = stateManager;

        final Node rootNode = EDITOR.getRootNode();
        rootNode.attachChild(getStateNode());

        final TonegodTranslucentBucketFilter translucentBucketFilter = EDITOR.getTranslucentBucketFilter();
        translucentBucketFilter.setEnabled(true);

        final EditorCamera editorCamera = getEditorCamera();
        final InputManager inputManager = EDITOR.getInputManager();

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
    @JMEThread
    protected void checkAndAddMappings(@NotNull final InputManager inputManager) {
        TRIGGERS.forEach(inputManager, AdvancedAbstractEditor3DState::addMapping);
        MULTI_TRIGGERS.forEach(inputManager, AdvancedAbstractEditor3DState::addMapping);
    }

    @JMEThread
    private static void addMapping(@NotNull final InputManager inputManager, @NotNull final String name,
                                   @NotNull final Trigger[] triggers) {
        if (!inputManager.hasMapping(name)) inputManager.addMapping(name, triggers);
    }

    @JMEThread
    private static void addMapping(@NotNull final InputManager inputManager, @NotNull final String name,
                                   @NotNull final Trigger trigger) {
        if (!inputManager.hasMapping(name)) inputManager.addMapping(name, trigger);
    }

    /**
     * Register the analog listener.
     *
     * @param inputManager the input manager
     */
    @JMEThread
    protected void registerAnalogListener(@NotNull final InputManager inputManager) {
        inputManager.addListener(analogListener, MOUSE_X_AXIS, MOUSE_X_AXIS_NEGATIVE, MOUSE_Y_AXIS,
                MOUSE_Y_AXIS_NEGATIVE, MOUSE_MOVE_CAMERA_X_AXIS, MOUSE_MOVE_CAMERA_X_AXIS_NEGATIVE,
                MOUSE_MOVE_CAMERA_Y_AXIS, MOUSE_MOVE_CAMERA_Y_AXIS_NEGATIVE);
    }

    /**
     * Register the action listener.
     *
     * @param inputManager the input manager
     */
    @JMEThread
    protected void registerActionListener(@NotNull final InputManager inputManager) {
        inputManager.addListener(actionListener, MOUSE_RIGHT_CLICK, MOUSE_LEFT_CLICK, MOUSE_MIDDLE_CLICK);
        inputManager.addListener(actionListener, KEY_CTRL, KEY_SHIFT, KEY_ALT, KEY_CTRL_S, KEY_CTRL_Z, KEY_CTRL_Y, KEY_NUM_1,
                KEY_NUM_2, KEY_NUM_3, KEY_NUM_4, KEY_NUM_5, KEY_NUM_6, KEY_NUM_7, KEY_NUM_8, KEY_NUM_9, KEY_FLY_CAMERA_W,
                KEY_FLY_CAMERA_S, KEY_FLY_CAMERA_A, KEY_FLY_CAMERA_D);
    }

    @Override
    @JMEThread
    public void cleanup() {
        super.cleanup();

        final TonegodTranslucentBucketFilter translucentBucketFilter = EDITOR.getTranslucentBucketFilter();
        translucentBucketFilter.setEnabled(false);

        final Node rootNode = EDITOR.getRootNode();
        rootNode.detachChild(getStateNode());

        final EditorCamera editorCamera = getEditorCamera();
        final InputManager inputManager = EDITOR.getInputManager();
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
    @JMEThread
    protected boolean needEditorCamera() {
        return false;
    }

    /**
     * Need light for camera boolean.
     *
     * @return true if need a camera light.
     */
    @JMEThread
    protected boolean needLightForCamera() {
        return false;
    }

    /**
     * Create an editor camera.
     *
     * @return the new camera.
     */
    @JMEThread
    protected @NotNull EditorCamera createEditorCamera() {

        final Camera camera = EDITOR.getCamera();
        final EditorCamera editorCamera = new EditorCamera(camera, getNodeForCamera());
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
    @JMEThread
    protected @NotNull DirectionalLight createLightForCamera() {
        final DirectionalLight directionalLight = new DirectionalLight();
        directionalLight.setColor(ColorRGBA.White);
        return directionalLight;
    }

    /**
     * Gets node for camera.
     *
     * @return the node for the camera.
     */
    @JMEThread
    protected @NotNull Node getNodeForCamera() {
        return getStateNode();
    }

    /**
     * Gets light for camera.
     *
     * @return the light for the camera.
     */
    @JMEThread
    protected @Nullable DirectionalLight getLightForCamera() {
        return lightForCamera;
    }

    /**
     * Gets prev h rotation.
     *
     * @return the previous horizontal camera rotation.
     */
    @JMEThread
    protected float getPrevHRotation() {
        return prevHRotation;
    }

    /**
     * Sets prev h rotation.
     *
     * @param prevHRotation the previous horizontal camera rotation.
     */
    @JMEThread
    protected void setPrevHRotation(final float prevHRotation) {
        this.prevHRotation = prevHRotation;
    }

    /**
     * Gets prev target distance.
     *
     * @return the previous camera zoom.
     */
    @JMEThread
    protected float getPrevTargetDistance() {
        return prevTargetDistance;
    }

    /**
     * Sets prev target distance.
     *
     * @param prevTargetDistance the previous camera zoom.
     */
    @JMEThread
    protected void setPrevTargetDistance(final float prevTargetDistance) {
        this.prevTargetDistance = prevTargetDistance;
    }

    /**
     * Gets prev v rotation.
     *
     * @return the previous vertical camera rotation.
     */
    @JMEThread
    protected float getPrevVRotation() {
        return prevVRotation;
    }

    /**
     * Sets prev v rotation.
     *
     * @param prevVRotation the previous vertical camera rotation.
     */
    @JMEThread
    protected void setPrevVRotation(final float prevVRotation) {
        this.prevVRotation = prevVRotation;
    }

    /**
     * Gets prev camera location.
     *
     * @return the prev camera location
     */
    @FromAnyThread
    public @NotNull Vector3f getPrevCameraLocation() {
        return prevCameraLocation;
    }

    /**
     * @return the camera speed.
     */
    @FromAnyThread
    private float getCameraSpeed() {
        return cameraSpeed;
    }

    /**
     * @param cameraSpeed the camera speed.
     */
    @FromAnyThread
    private void setCameraSpeed(final float cameraSpeed) {
        this.cameraSpeed = cameraSpeed;
    }

    /**
     * @return the prev camera speed.
     */
    @FromAnyThread
    private float getPrevCameraSpeed() {
        return prevCameraSpeed;
    }

    /**
     * @param prevCameraSpeed the prev camera speed.
     */
    @FromAnyThread
    private void setPrevCameraSpeed(final float prevCameraSpeed) {
        this.prevCameraSpeed = prevCameraSpeed;
    }

    @Override
    @JMEThread
    public void update(float tpf) {
        super.update(tpf);
        preCameraUpdate(tpf);
        cameraUpdate(tpf);
        postCameraUpdate(tpf);
    }

    @JMEThread
    protected void postCameraUpdate(final float tpf) {

        final DirectionalLight lightForCamera = getLightForCamera();
        final EditorCamera editorCamera = getEditorCamera();

        if (editorCamera != null && lightForCamera != null && needUpdateCameraLight()) {
            final Camera camera = EDITOR.getCamera();
            lightForCamera.setDirection(camera.getDirection().normalize());
        }
    }

    @JMEThread
    protected void cameraUpdate(final float tpf) {

        final EditorCamera editorCamera = getEditorCamera();
        if (editorCamera == null) {
            return;
        }

        editorCamera.updateCamera(tpf);

        final boolean[] cameraKeysState = getCameraKeysState();

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

    @JMEThread
    protected void preCameraUpdate(final float tpf) {

    }

    /**
     * Check camera changes.
     *
     * @param editorCamera the editor camera
     */
    @JMEThread
    protected void checkCameraChanges(@NotNull final EditorCamera editorCamera) {

        int changes = 0;

        final Node nodeForCamera = getNodeForCamera();
        final Vector3f prevCameraLocation = getPrevCameraLocation();
        final Vector3f cameraLocation = nodeForCamera.getLocalTranslation();

        final float prevHRotation = getPrevHRotation();
        final float hRotation = editorCamera.getHorizontalRotation();

        final float prevVRotation = getPrevVRotation();
        final float vRotation = editorCamera.getVerticalRotation();

        final float prevTargetDistance = getPrevTargetDistance();
        final float targetDistance = editorCamera.getTargetDistance();

        final float cameraSpeed = getCameraSpeed();
        final float prevCameraSpeed = getPrevCameraSpeed();

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
    @JMEThread
    protected void notifyChangedCameraSettings(@NotNull final Vector3f cameraLocation, final float hRotation,
                                               final float vRotation, final float targetDistance,
                                               final float cameraSpeed) {
    }

    /**
     * Update the editor camera settings.
     *
     * @param cameraLocation the camera location.
     * @param hRotation      the h rotation.
     * @param vRotation      the v rotation.
     * @param targetDistance the target distance.
     * @param cameraSpeed    the camera speed.
     */
    @JMEThread
    public void updateCameraSettings(@NotNull final Vector3f cameraLocation, final float hRotation,
                                     final float vRotation, final float targetDistance, final float cameraSpeed) {

        final EditorCamera editorCamera = getEditorCamera();
        if (editorCamera == null) return;

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
     * Need update camera light boolean.
     *
     * @return true if need to update the camera light.
     */
    @JMEThread
    protected boolean needUpdateCameraLight() {
        return false;
    }
}
