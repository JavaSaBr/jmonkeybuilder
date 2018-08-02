package com.ss.editor.part3d.editor.impl;

import static com.ss.rlib.common.util.array.ArrayFactory.toArray;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.config.Config;
import com.ss.editor.model.EditorCamera;
import com.ss.editor.part3d.editor.Editor3dPart;
import com.ss.editor.part3d.editor.control.InputEditor3dPartControl;
import com.ss.editor.part3d.editor.control.impl.CameraEditor3dPartControl;
import com.ss.editor.part3d.editor.control.impl.InputStateEditor3dPartControl;
import com.ss.editor.plugin.api.RenderFilterRegistry;
import com.ss.editor.ui.component.editor.FileEditor;
import com.ss.editor.util.EditorUtils;
import com.ss.rlib.common.function.BooleanFloatConsumer;
import com.ss.rlib.common.function.FloatFloatConsumer;
import com.ss.rlib.common.logging.LoggerLevel;
import com.ss.rlib.common.util.dictionary.ObjectDictionary;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The base implementation of the {@link Editor3dPart} for a file editor.
 *
 * @param <T> the type of a file editor.
 * @author JavaSaBr
 */
public abstract class AdvancedAbstractEditor3dPart<T extends FileEditor> extends AbstractEditor3dPart<T>
        implements CameraSupportEditor3dPart {

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
     * The current state manager.
     */
    @Nullable
    protected AppStateManager stateManager;

    public AdvancedAbstractEditor3dPart(@NotNull T fileEditor) {
        super(fileEditor);
        this.editorCamera = needEditorCamera() ? createEditorCamera() : null;
        this.lightForCamera = needLightForCamera() ? createLightForCamera() : null;
        this.prevCameraLocation = new Vector3f();

        if (lightForCamera != null) {
            stateNode.addLight(lightForCamera);
        }
    }

    @Override
    @BackgroundThread
    protected void initControls() {
        super.initControls();

        controls.add(new InputStateEditor3dPartControl(this));

        if (needMovableCamera()) {
            controls.add(new CameraEditor3dPartControl(this));
        }
    }

    /**
     * Register action handlers.
     *
     * @param actionHandlers the action handlers
     */
    @JmeThread
    protected void registerActionHandlers(@NotNull ObjectDictionary<String, BooleanFloatConsumer> actionHandlers) {

        actionHandlers.put(KEY_CTRL_Z, (isPressed, tpf) -> {
            var control = requireControl(InputStateEditor3dPartControl.class);
            if (!isPressed && control.isControlDown()) {
                undo();
            }
        });

        actionHandlers.put(KEY_CTRL_Y, (isPressed, tpf) -> {
            var control = requireControl(InputStateEditor3dPartControl.class);
            if (!isPressed && control.isControlDown()) {
                redo();
            }
        });

        actionHandlers.put(KEY_CTRL_S, (isPressed, tpf) -> {
            var control = requireControl(InputStateEditor3dPartControl.class);
            if (isPressed && control.isControlDown() && fileEditor.isDirty()) {
                fileEditor.save();
            }
        });
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

    @Override
    @JmeThread
    public @Nullable EditorCamera getEditorCamera() {
        return editorCamera;
    }

    @Override
    @JmeThread
    public void initialize(@NotNull AppStateManager stateManager, @NotNull Application application) {
        super.initialize(stateManager, application);
        this.stateManager = stateManager;

        var rootNode = EditorUtils.getGlobalRootNode();
        rootNode.attachChild(stateNode);

        var filterRegistry = RenderFilterRegistry.getInstance();
        filterRegistry.enableFilters();

        var editorCamera = getEditorCamera();
        var inputManager = EditorUtils.getInputManager();

        controls.stream()
                .filter(InputEditor3dPartControl.class::isInstance)
                .map(InputEditor3dPartControl.class::cast)
                .forEach(inputEditor3dPartControl -> inputEditor3dPartControl.register(inputManager));

        if (editorCamera != null) {
            editorCamera.setEnabled(true);
            editorCamera.registerInput(inputManager);
        }
    }

    @Override
    @JmeThread
    public void cleanup() {
        super.cleanup();

        var filterRegistry = RenderFilterRegistry.getInstance();
        filterRegistry.disableFilters();

        var rootNode = EditorUtils.getGlobalRootNode();
        rootNode.detachChild(stateNode);

        var editorCamera = getEditorCamera();
        var inputManager = EditorUtils.getInputManager();

        controls.stream()
                .filter(InputEditor3dPartControl.class::isInstance)
                .map(InputEditor3dPartControl.class::cast)
                .forEach(inputEditor3dPartControl -> inputEditor3dPartControl.unregister(inputManager));

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

        var camera = EditorUtils.getGlobalCamera();

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
     * Get the previous camera location.
     *
     * @return the previous camera location
     */
    @FromAnyThread
    public @NotNull Vector3f getPrevCameraLocation() {
        return prevCameraLocation;
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
            var camera = EditorUtils.getGlobalCamera();
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

    @JmeThread
    protected void preCameraUpdate(float tpf) {
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

        var hRotation = editorCamera.getHorizontalRotation();
        var vRotation = editorCamera.getvRotation();
        var targetDistance = editorCamera.getTargetDistance();


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

        var editorCamera = getEditorCamera();

        if (editorCamera == null) {
            return;
        }

        editorCamera.setTargetHRotation(hRotation);
        editorCamera.setTargetVRotation(vRotation);
        editorCamera.setTargetDistance(targetDistance);

        getNodeForCamera().setLocalTranslation(cameraLocation);
        getPrevCameraLocation().set(cameraLocation);

        this.prevHRotation = hRotation;
        this.prevVRotation = vRotation;
        this.prevTargetDistance = targetDistance;
        this.prevCameraFlySpeed = cameraFlySpeed;
        this.cameraFlySpeed = cameraFlySpeed;

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
