package com.ss.editor.state.editor.impl;

import static org.apache.commons.lang3.ArrayUtils.toArray;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.ss.editor.model.EditorCamera;
import com.ss.editor.ui.component.editor.FileEditor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import rlib.function.BooleanFloatConsumer;
import rlib.function.FloatFloatConsumer;
import rlib.util.dictionary.DictionaryFactory;
import rlib.util.dictionary.ObjectDictionary;
import tonegod.emitter.filter.TonegodTranslucentBucketFilter;

/**
 * The base implementation of the {@link com.jme3.app.state.AppState} for the editor.
 *
 * @author JavaSaBr
 */
public abstract class AdvancedAbstractEditorAppState<T extends FileEditor> extends AbstractEditorAppState<T> {

    protected static final ObjectDictionary<String, Trigger> TRIGGERS = DictionaryFactory.newObjectDictionary();
    protected static final ObjectDictionary<String, Trigger[]> MULTI_TRIGGERS = DictionaryFactory.newObjectDictionary();

    protected static final String MOUSE_RIGHT_CLICK = "SSEditor.editorState.mouseRightClick";
    protected static final String MOUSE_LEFT_CLICK = "SSEditor.editorState.mouseLeftClick";
    protected static final String MOUSE_MIDDLE_CLICK = "SSEditor.editorState.mouseMiddleClick";

    protected static final String MOUSE_X_AXIS = "SSEditor.editorState.mouseXAxis";
    protected static final String MOUSE_X_AXIS_NEGATIVE = "SSEditor.editorState.mouseXAxisNegative";
    protected static final String MOUSE_Y_AXIS = "SSEditor.editorState.mouseYAxis";
    protected static final String MOUSE_Y_AXIS_NEGATIVE = "SSEditor.editorState.mouseYAxisNegative";

    protected static final String KEY_CTRL = "SSEditor.editorState.keyCtrl";
    protected static final String KEY_ALT = "SSEditor.editorState.keyAlt";
    protected static final String KEY_SHIFT = "SSEditor.editorState.keyShift";
    protected static final String KEY_S = "SSEditor.editorState.S";
    protected static final String KEY_Z = "SSEditor.editorState.Z";
    protected static final String KEY_Y = "SSEditor.editorState.Y";

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

        TRIGGERS.put(MOUSE_RIGHT_CLICK, new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        TRIGGERS.put(MOUSE_LEFT_CLICK, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        TRIGGERS.put(MOUSE_MIDDLE_CLICK, new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE));

        MULTI_TRIGGERS.put(KEY_CTRL, toArray(new KeyTrigger(KeyInput.KEY_RCONTROL), new KeyTrigger(KeyInput.KEY_LCONTROL)));
        MULTI_TRIGGERS.put(KEY_SHIFT, toArray(new KeyTrigger(KeyInput.KEY_RSHIFT), new KeyTrigger(KeyInput.KEY_LSHIFT)));
        MULTI_TRIGGERS.put(KEY_ALT, toArray(new KeyTrigger(KeyInput.KEY_RMENU), new KeyTrigger(KeyInput.KEY_LMENU)));

        TRIGGERS.put(KEY_S, new KeyTrigger(KeyInput.KEY_S));
        TRIGGERS.put(KEY_Z, new KeyTrigger(KeyInput.KEY_Z));
        TRIGGERS.put(KEY_Y, new KeyTrigger(KeyInput.KEY_Y));

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
     * The scene listeners.
     */
    @NotNull
    private final ActionListener actionListener;

    @NotNull
    private final AnalogListener analogListener;

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

    public AdvancedAbstractEditorAppState(final T fileEditor) {
        super(fileEditor);
        this.editorCamera = needEditorCamera() ? createEditorCamera() : null;
        this.lightForCamera = needLightForCamera() ? createLightForCamera() : null;
        this.prevCameraLocation = new Vector3f();

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
     */
    protected void registerActionHandlers(@NotNull final ObjectDictionary<String, BooleanFloatConsumer> actionHandlers) {
        actionHandlers.put(KEY_ALT, (isPressed, tpf) -> setAltDown(isPressed));
        actionHandlers.put(KEY_CTRL, (isPressed, tpf) -> setControlDown(isPressed));
        actionHandlers.put(KEY_SHIFT, (isPressed, tpf) -> setShiftDown(isPressed));
        actionHandlers.put(MOUSE_LEFT_CLICK, (isPressed, tpf) -> setButtonLeftDown(isPressed));
        actionHandlers.put(MOUSE_MIDDLE_CLICK, (isPressed, tpf) -> setButtonMiddleDown(isPressed));
        actionHandlers.put(MOUSE_RIGHT_CLICK, (isPressed, tpf) -> setButtonRightDown(isPressed));
        actionHandlers.put(KEY_NUM_1, (isPressed, tpf) -> rotateTo(EditorCamera.Perspective.BACK, isPressed));
        actionHandlers.put(KEY_NUM_3, (isPressed, tpf) -> rotateTo(EditorCamera.Perspective.RIGHT, isPressed));
        actionHandlers.put(KEY_NUM_7, (isPressed, tpf) -> rotateTo(EditorCamera.Perspective.TOP, isPressed));
        actionHandlers.put(KEY_NUM_9, (isPressed, tpf) -> rotateTo(EditorCamera.Perspective.BOTTOM, isPressed));
        actionHandlers.put(KEY_NUM_2, (isPressed, tpf) -> rotateTo(EditorCamera.Direction.BOTTOM, isPressed));
        actionHandlers.put(KEY_NUM_8, (isPressed, tpf) -> rotateTo(EditorCamera.Direction.TOP, isPressed));
        actionHandlers.put(KEY_NUM_4, (isPressed, tpf) -> rotateTo(EditorCamera.Direction.LEFT, isPressed));
        actionHandlers.put(KEY_NUM_6, (isPressed, tpf) -> rotateTo(EditorCamera.Direction.RIGHT, isPressed));

        actionHandlers.put(KEY_Z, (isPressed, tpf) -> {
            if (!isPressed && isControlDown()) undo();
        });
        actionHandlers.put(KEY_Y, (isPressed, tpf) -> {
            if (!isPressed && isControlDown()) redo();
        });

        actionHandlers.put(KEY_S, (isPressed, tpf) -> {

            final FileEditor fileEditor = getFileEditor();

            if (isPressed && isControlDown() && fileEditor.isDirty()) {
                EXECUTOR_MANAGER.addFXTask(fileEditor::doSave);
            }
        });
    }

    /**
     * Register analog handlers.
     */
    protected void registerAnalogHandlers(@NotNull final ObjectDictionary<String, FloatFloatConsumer> analogHandlers) {
        analogHandlers.put(MOUSE_X_AXIS, (value, tpf) -> moveXCamera(value * 30F));
        analogHandlers.put(MOUSE_X_AXIS_NEGATIVE, (value, tpf) -> moveXCamera(-value * 30F));
        analogHandlers.put(MOUSE_Y_AXIS, (value, tpf) -> moveYCamera(-value * 30F));
        analogHandlers.put(MOUSE_Y_AXIS_NEGATIVE, (value, tpf) -> moveYCamera(value * 30F));
    }

    /**
     * Handle analog events.
     */
    protected void onAnalogImpl(@NotNull final String name, final float value, final float tpf) {
        final FloatFloatConsumer handler = analogHandlers.get(name);
        if (handler != null) handler.accept(value, tpf);
    }

    /**
     * Move a camera on X axis.
     *
     * @param value the value to move.
     */
    protected void moveXCamera(final float value) {
        if (!needMovableCamera() || !isShiftDown() || !isButtonMiddleDown()) return;

        final EditorCamera editorCamera = getEditorCamera();
        if (editorCamera == null) return;

        final Camera camera = EDITOR.getCamera();
        final Node nodeForCamera = getNodeForCamera();

        final Vector3f left = camera.getLeft();
        left.multLocal(value * (float) Math.sqrt(editorCamera.getTargetDistance()));
        left.addLocal(nodeForCamera.getLocalTranslation());

        nodeForCamera.setLocalTranslation(left);
    }

    /**
     * Move a camera on Y axis.
     *
     * @param value the value to move.
     */
    protected void moveYCamera(final float value) {
        if (!needMovableCamera() || !isShiftDown() || !isButtonMiddleDown()) return;

        final EditorCamera editorCamera = getEditorCamera();
        if (editorCamera == null) return;

        final Camera camera = EDITOR.getCamera();
        final Node nodeForCamera = getNodeForCamera();

        final Vector3f up = camera.getUp();
        up.multLocal(value * (float) Math.sqrt(editorCamera.getTargetDistance()));
        up.addLocal(nodeForCamera.getLocalTranslation());

        nodeForCamera.setLocalTranslation(up);
    }

    /**
     * @return true if camera can move.
     */
    protected boolean needMovableCamera() {
        return true;
    }

    /**
     * Handle action events.
     */
    protected void onActionImpl(final String name, final boolean isPressed, final float tpf) {

        final BooleanFloatConsumer handler = actionHandlers.get(name);
        if (handler != null) handler.accept(isPressed, tpf);

        final EditorCamera editorCamera = getEditorCamera();

        if (editorCamera != null && needMovableCamera()) {
            editorCamera.setLockRotation(isShiftDown() && isButtonMiddleDown());
        }
    }

    protected void rotateTo(final EditorCamera.Perspective perspective, final boolean isPressed) {
        final EditorCamera editorCamera = getEditorCamera();
        if (editorCamera != null && isPressed) editorCamera.rotateTo(perspective);
    }

    protected void rotateTo(final EditorCamera.Direction direction, final boolean isPressed) {
        final EditorCamera editorCamera = getEditorCamera();
        if (editorCamera != null && isPressed) editorCamera.rotateTo(direction, 10F);
    }

    /**
     * Redo last operation.
     */
    protected void redo() {
    }

    /**
     * Undo last operation.
     */
    protected void undo() {
    }

    /**
     * @return true if alt is pressed.
     */
    protected boolean isAltDown() {
        return altDown;
    }

    /**
     * @param altDown the alt is pressed.
     */
    protected void setAltDown(final boolean altDown) {
        this.altDown = altDown;
    }

    /**
     * @return true if control is pressed.
     */
    protected boolean isControlDown() {
        return controlDown;
    }

    /**
     * @param controlDown the control is pressed.
     */
    protected void setControlDown(final boolean controlDown) {
        this.controlDown = controlDown;
    }

    /**
     * @return true if shift is pressed.
     */
    protected boolean isShiftDown() {
        return shiftDown;
    }

    /**
     * @param shiftDown the shift is pressed.
     */
    protected void setShiftDown(final boolean shiftDown) {
        this.shiftDown = shiftDown;
    }

    /**
     * @param buttonLeftDown the left button is pressed.
     */
    protected void setButtonLeftDown(final boolean buttonLeftDown) {
        this.buttonLeftDown = buttonLeftDown;
    }

    /**
     * @param buttonMiddleDown the middle button is pressed.
     */
    protected void setButtonMiddleDown(final boolean buttonMiddleDown) {
        this.buttonMiddleDown = buttonMiddleDown;
    }

    /**
     * @param buttonRightDown the right button is pressed.
     */
    protected void setButtonRightDown(final boolean buttonRightDown) {
        this.buttonRightDown = buttonRightDown;
    }

    /**
     * @return true if left button is pressed.
     */
    protected boolean isButtonLeftDown() {
        return buttonLeftDown;
    }

    /**
     * @return true if middle button is pressed.
     */
    protected boolean isButtonMiddleDown() {
        return buttonMiddleDown;
    }

    /**
     * @return true if right button is pressed.
     */
    protected boolean isButtonRightDown() {
        return buttonRightDown;
    }

    /**
     * @return the editor camera.
     */
    @Nullable
    protected EditorCamera getEditorCamera() {
        return editorCamera;
    }

    @Override
    public void initialize(@NotNull final AppStateManager stateManager, @NotNull final Application application) {
        super.initialize(stateManager, application);

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
     */
    protected void checkAndAddMappings(final InputManager inputManager) {
        TRIGGERS.forEach(inputManager, AdvancedAbstractEditorAppState::addMapping);
        MULTI_TRIGGERS.forEach(inputManager, AdvancedAbstractEditorAppState::addMapping);
    }

    private static void addMapping(@NotNull final InputManager inputManager, @NotNull final String name,
                                   @NotNull final Trigger[] triggers) {
        if (!inputManager.hasMapping(name)) inputManager.addMapping(name, triggers);
    }

    private static void addMapping(@NotNull final InputManager inputManager, @NotNull final String name,
                                   @NotNull final Trigger trigger) {
        if (!inputManager.hasMapping(name)) inputManager.addMapping(name, trigger);
    }

    /**
     * Register the analog listener.
     */
    protected void registerAnalogListener(@NotNull final InputManager inputManager) {
        inputManager.addListener(analogListener, MOUSE_X_AXIS, MOUSE_X_AXIS_NEGATIVE, MOUSE_Y_AXIS, MOUSE_Y_AXIS_NEGATIVE);
    }

    /**
     * Register the action listener.
     */
    protected void registerActionListener(@NotNull final InputManager inputManager) {
        inputManager.addListener(actionListener, MOUSE_RIGHT_CLICK, MOUSE_LEFT_CLICK, MOUSE_MIDDLE_CLICK);
        inputManager.addListener(actionListener, KEY_CTRL, KEY_SHIFT, KEY_ALT, KEY_S, KEY_Z, KEY_Y, KEY_NUM_1,
                KEY_NUM_2, KEY_NUM_3, KEY_NUM_4, KEY_NUM_5, KEY_NUM_6, KEY_NUM_7, KEY_NUM_8, KEY_NUM_9);
    }

    @Override
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
     * @return true if need an editor camera.
     */
    protected boolean needEditorCamera() {
        return false;
    }

    /**
     * @return true if need a camera light.
     */
    protected boolean needLightForCamera() {
        return false;
    }

    /**
     * Create an editor camera.
     *
     * @return the new camera.
     */
    @NotNull
    protected EditorCamera createEditorCamera() {

        final Camera camera = EDITOR.getCamera();

        final EditorCamera editorCamera = new EditorCamera(camera, getNodeForCamera());
        editorCamera.setMaxDistance(10000);
        editorCamera.setSmoothMotion(false);
        editorCamera.setRotationSensitivity(1);
        editorCamera.setZoomSensitivity(0.2F);

        return editorCamera;
    }

    /**
     * @return the light for the camera.
     */
    @NotNull
    protected DirectionalLight createLightForCamera() {
        final DirectionalLight directionalLight = new DirectionalLight();
        directionalLight.setColor(ColorRGBA.White);
        return directionalLight;
    }

    /**
     * @return the node for the camera.
     */
    @NotNull
    protected Node getNodeForCamera() {
        return getStateNode();
    }

    /**
     * @return the light for the camera.
     */
    @Nullable
    protected DirectionalLight getLightForCamera() {
        return lightForCamera;
    }

    public float getPrevHRotation() {
        return prevHRotation;
    }

    public void setPrevHRotation(final float prevHRotation) {
        this.prevHRotation = prevHRotation;
    }

    public float getPrevTargetDistance() {
        return prevTargetDistance;
    }

    public void setPrevTargetDistance(final float prevTargetDistance) {
        this.prevTargetDistance = prevTargetDistance;
    }

    public float getPrevVRotation() {
        return prevVRotation;
    }

    public void setPrevVRotation(final float prevVRotation) {
        this.prevVRotation = prevVRotation;
    }

    @NotNull
    public Vector3f getPrevCameraLocation() {
        return prevCameraLocation;
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        final EditorCamera editorCamera = getEditorCamera();

        if (editorCamera != null) {
            checkCameraChanges(editorCamera);
        }

        final DirectionalLight lightForCamera = getLightForCamera();

        if (editorCamera != null && lightForCamera != null && needUpdateCameraLight()) {
            final Camera camera = EDITOR.getCamera();
            lightForCamera.setDirection(camera.getDirection().normalize());
        }
    }

    /**
     * Check camera changes.
     */
    protected void checkCameraChanges(@NotNull final EditorCamera editorCamera) {

        int changes = 0;

        final Node nodeForCamera = getNodeForCamera();
        final Vector3f prevCameraLocation = getPrevCameraLocation();
        final Vector3f cameraLocation = nodeForCamera.getLocalTranslation();

        if (!prevCameraLocation.equals(cameraLocation)) {
            changes++;
        }

        final float prevHRotation = getPrevHRotation();
        final float hRotation = editorCamera.getHorizontalRotation();

        final float prevVRotation = getPrevVRotation();
        final float vRotation = editorCamera.getVerticalRotation();

        if (prevHRotation != hRotation || prevVRotation != vRotation) {
            changes++;
        }

        final float prevTargetDistance = getPrevTargetDistance();
        final float targetDistance = editorCamera.getTargetDistance();

        if (prevTargetDistance != targetDistance) {
            changes++;
        }

        if (changes > 0) {
            notifyChangedCamera(cameraLocation, hRotation, vRotation, targetDistance);
        }

        prevCameraLocation.set(cameraLocation);

        setPrevHRotation(hRotation);
        setPrevVRotation(vRotation);
        setPrevTargetDistance(targetDistance);
    }

    /**
     * Notify about changed camera.
     */
    protected void notifyChangedCamera(@NotNull final Vector3f cameraLocation, final float hRotation,
                                       final float vRotation, final float targetDistance) {
    }

    /**
     * Update the editor camera.
     */
    public void updateCamera(@NotNull final Vector3f cameraLocation, final float hRotation,
                             final float vRotation, final float targetDistance) {

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

        editorCamera.update(1F);
    }

    /**
     * @return true if need to update the camera light.
     */
    protected boolean needUpdateCameraLight() {
        return false;
    }
}
