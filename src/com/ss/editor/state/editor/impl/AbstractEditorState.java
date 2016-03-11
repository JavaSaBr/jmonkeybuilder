package com.ss.editor.state.editor.impl;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.ss.editor.Editor;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.state.editor.EditorState;

import rlib.logging.Logger;
import rlib.logging.LoggerManager;

/**
 * Базовая реализация.
 *
 * @author Ronn
 */
public abstract class AbstractEditorState extends AbstractAppState implements EditorState {

    protected static final Logger LOGGER = LoggerManager.getLogger(EditorState.class);

    protected static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();
    protected static final Editor EDITOR = Editor.getInstance();

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

    /**
     * Слушатели сцены.
     */
    private final ActionListener actionListener;
    private final AnalogListener analogListener;

    /**
     * Опциональная камера для сцены.
     */
    private final ChaseCamera chaseCamera;

    /**
     * Источник света для chase камеры.
     */
    private final DirectionalLight lightForChaseCamera;

    /**
     * Рутовый узел.
     */
    private final Node stateNode;

    /**
     * Нажат ли сейчас control.
     */
    private boolean controlDown;

    /**
     * Нажат ли сейчас alt.
     */
    private boolean altDown;

    /**
     * Нажат ли сейчас Shift.
     */
    private boolean shiftDown;

    public AbstractEditorState() {
        this.stateNode = new Node(getClass().getSimpleName());
        this.chaseCamera = needChaseCamera() ? createChaseCamera() : null;
        this.lightForChaseCamera = needLightForChaseCamera() ? createLightForChaseCamera() : null;

        if (lightForChaseCamera != null) {
            stateNode.addLight(lightForChaseCamera);
        }

        this.analogListener = new AnalogListener() {

            @Override
            public void onAnalog(final String name, final float value, final float tpf) {
                onAnalogImpl(name, value, tpf);
            }
        };

        this.actionListener = new ActionListener() {

            @Override
            public void onAction(final String name, final boolean isPressed, final float tpf) {
                onActionImpl(name, isPressed, tpf);
            }
        };
    }

    /**
     * Обработка перемещения мышки над 3D областью
     */
    protected void onAnalogImpl(final String name, final float value, final float tpf) {

    }

    /**
     * Обработка нажатий кнопок над областью редактора.
     */
    protected void onActionImpl(final String name, final boolean isPressed, final float tpf) {
        if (KEY_ALT.equals(name)) {
            setAltDown(isPressed);
        } else if (KEY_CTRL.equals(name)) {
            setControlDown(isPressed);
        } else if (KEY_SHIFT.equals(name)) {
            setShiftDown(isPressed);
        }
    }

    /**
     * @return нажат ли сейчас alt.
     */
    protected boolean isAltDown() {
        return altDown;
    }

    /**
     * @param altDown нажат ли сейчас alt.
     */
    protected void setAltDown(boolean altDown) {
        this.altDown = altDown;
    }

    /**
     * @return нажат ли сейчас control.
     */
    protected boolean isControlDown() {
        return controlDown;
    }

    /**
     * @param controlDown нажат ли сейчас control.
     */
    protected void setControlDown(boolean controlDown) {
        this.controlDown = controlDown;
    }

    /**
     * @return нажат ли сейчас Shift.
     */
    protected boolean isShiftDown() {
        return shiftDown;
    }

    /**
     * @param shiftDown нажат ли сейчас Shift.
     */
    protected void setShiftDown(boolean shiftDown) {
        this.shiftDown = shiftDown;
    }

    /**
     * @return рутовый узел.
     */
    protected Node getStateNode() {
        return stateNode;
    }

    /**
     * @return опциональная камера для сцены.
     */
    protected ChaseCamera getChaseCamera() {
        return chaseCamera;
    }

    @Override
    public void initialize(final AppStateManager stateManager, final Application application) {
        super.initialize(stateManager, application);

        final Node rootNode = EDITOR.getRootNode();
        rootNode.attachChild(getStateNode());

        final ChaseCamera chaseCamera = getChaseCamera();

        if (chaseCamera != null) {
            chaseCamera.setEnabled(true);
        }

        final InputManager inputManager = EDITOR.getInputManager();

        registerActionListener(inputManager);
        registerAnalogListener(inputManager);
    }

    private void registerAnalogListener(final InputManager inputManager) {

        if (!inputManager.hasMapping(MOUSE_X_AXIS)) {
            inputManager.addMapping(MOUSE_X_AXIS, new MouseAxisTrigger(MouseInput.AXIS_X, false));
        }

        if (!inputManager.hasMapping(MOUSE_X_AXIS_NEGATIVE)) {
            inputManager.addMapping(MOUSE_X_AXIS_NEGATIVE, new MouseAxisTrigger(MouseInput.AXIS_X, true));
        }

        if (!inputManager.hasMapping(MOUSE_Y_AXIS)) {
            inputManager.addMapping(MOUSE_Y_AXIS, new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        }

        if (!inputManager.hasMapping(MOUSE_Y_AXIS_NEGATIVE)) {
            inputManager.addMapping(MOUSE_Y_AXIS_NEGATIVE, new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        }

        inputManager.addListener(actionListener, MOUSE_X_AXIS, MOUSE_X_AXIS_NEGATIVE, MOUSE_Y_AXIS, MOUSE_Y_AXIS_NEGATIVE);
    }

    private void registerActionListener(final InputManager inputManager) {

        if (!inputManager.hasMapping(MOUSE_RIGHT_CLICK)) {
            inputManager.addMapping(MOUSE_RIGHT_CLICK, new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        }

        if (!inputManager.hasMapping(MOUSE_LEFT_CLICK)) {
            inputManager.addMapping(MOUSE_LEFT_CLICK, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        }

        if (!inputManager.hasMapping(MOUSE_MIDDLE_CLICK)) {
            inputManager.addMapping(MOUSE_MIDDLE_CLICK, new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE));
        }

        if (!inputManager.hasMapping(KEY_CTRL)) {
            inputManager.addMapping(KEY_CTRL, new KeyTrigger(KeyInput.KEY_RCONTROL), new KeyTrigger(KeyInput.KEY_LCONTROL));
        }

        if (!inputManager.hasMapping(KEY_SHIFT)) {
            inputManager.addMapping(KEY_SHIFT, new KeyTrigger(KeyInput.KEY_RSHIFT), new KeyTrigger(KeyInput.KEY_LSHIFT));
        }

        if (!inputManager.hasMapping(KEY_ALT)) {
            inputManager.addMapping(KEY_ALT, new KeyTrigger(KeyInput.KEY_RMENU), new KeyTrigger(KeyInput.KEY_LMENU));
        }

        inputManager.addListener(actionListener, MOUSE_LEFT_CLICK, MOUSE_LEFT_CLICK, MOUSE_MIDDLE_CLICK);
        inputManager.addListener(actionListener, KEY_CTRL, KEY_SHIFT, KEY_ALT);
    }

    @Override
    public void cleanup() {
        super.cleanup();

        final Node rootNode = EDITOR.getRootNode();
        rootNode.detachChild(getStateNode());

        final ChaseCamera chaseCamera = getChaseCamera();

        if (chaseCamera != null) {
            chaseCamera.setEnabled(false);
        }

        final InputManager inputManager = EDITOR.getInputManager();
        inputManager.removeListener(actionListener);
        inputManager.removeListener(analogListener);
    }

    /**
     * Нужна ли камера для этой части.
     */
    protected boolean needChaseCamera() {
        return false;
    }

    /**
     * Нужен ли источник света для chase камеры.
     */
    protected boolean needLightForChaseCamera() {
        return false;
    }

    protected ChaseCamera createChaseCamera() {

        final Camera camera = EDITOR.getCamera();

        final ChaseCamera chaser = new ChaseCamera(camera, getNodeForChaseCamera(), EDITOR.getInputManager());
        chaser.setDragToRotate(true);
        chaser.setMinVerticalRotation(-FastMath.HALF_PI);
        chaser.setMaxDistance(1000);
        chaser.setSmoothMotion(true);
        chaser.setRotationSensitivity(10);
        chaser.setZoomSensitivity(1);

        return chaser;
    }

    /**
     * @return источник света для chase камеры.
     */
    protected DirectionalLight createLightForChaseCamera() {

        final DirectionalLight directionalLight = new DirectionalLight();
        directionalLight.setColor(ColorRGBA.White);

        return directionalLight;
    }

    /**
     * @return узел на который должна смотреть камера.
     */
    protected Node getNodeForChaseCamera() {
        return stateNode;
    }

    /**
     * @return источник света для chase камеры.
     */
    protected DirectionalLight getLightForChaseCamera() {
        return lightForChaseCamera;
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        final ChaseCamera chaseCamera = getChaseCamera();
        final DirectionalLight lightForChaseCamera = getLightForChaseCamera();

        if (chaseCamera != null && lightForChaseCamera != null && needUpdateChaseCameraLight()) {
            final Camera camera = EDITOR.getCamera();
            lightForChaseCamera.setDirection(camera.getDirection());
        }
    }

    /**
     * @return нужно ли обновлять направление света.
     */
    protected boolean needUpdateChaseCameraLight() {
        return false;
    }
}
