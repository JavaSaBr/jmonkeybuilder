package com.ss.editor.state.editor.impl;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
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
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.ss.editor.Editor;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.EditorCamera;
import com.ss.editor.state.editor.EditorState;
import com.ss.editor.ui.component.editor.FileEditor;

import rlib.logging.Logger;
import rlib.logging.LoggerManager;

/**
 * Базовая реализация.
 *
 * @author Ronn
 */
public abstract class AbstractEditorState<T extends FileEditor> extends AbstractAppState implements EditorState {

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
    protected static final String KEY_S = "SSEditor.editorState.S";
    protected static final String KEY_Z = "SSEditor.editorState.Z";
    protected static final String KEY_Y = "SSEditor.editorState.Y";

    /**
     * Слушатели сцены.
     */
    private final ActionListener actionListener;
    private final AnalogListener analogListener;

    /**
     * Редактор использующий этот стейт.
     */
    private final T fileEditor;

    /**
     * Опциональная камера для сцены.
     */
    private final EditorCamera editorCamera;

    /**
     * Источник света для chase камеры.
     */
    private final DirectionalLight lightForCamera;

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

    /**
     * Нажата ли сейчас левая кнопка мыши.
     */
    private boolean buttonLeftDown;

    /**
     * Нажата ли сейчас правая кнопка мыши.
     */
    private boolean buttonRightDown;

    /**
     * Нажат ли сейчас колесик.
     */
    private boolean buttonMiddleDown;

    public AbstractEditorState(final T fileEditor) {
        this.fileEditor = fileEditor;
        this.stateNode = new Node(getClass().getSimpleName());
        this.editorCamera = needEditorCamera() ? createEditorCamera() : null;
        this.lightForCamera = needLightForCamera() ? createLightForCamera() : null;

        if (lightForCamera != null) {
            stateNode.addLight(lightForCamera);
        }

        this.analogListener = this::onAnalogImpl;
        this.actionListener = this::onActionImpl;
    }

    /**
     * @return редактор использующий этот стейт.
     */
    protected T getFileEditor() {
        return fileEditor;
    }

    /**
     * Обработка перемещения мышки над 3D областью
     */
    protected void onAnalogImpl(final String name, final float value, final float tpf) {

        if (!needMovableCamera() || !isShiftDown() || !isButtonMiddleDown()) {
            return;
        }

        if (MOUSE_X_AXIS.equals(name)) {
            moveXCamera(value * 30);
        } else if (MOUSE_X_AXIS_NEGATIVE.equals(name)) {
            moveXCamera(-value * 30);
        }

        if (MOUSE_Y_AXIS.equals(name)) {
            moveYCamera(-value * 30);
        } else if (MOUSE_Y_AXIS_NEGATIVE.equals(name)) {
            moveYCamera(value * 30);
        }
    }

    protected void moveXCamera(final float value) {

        final Camera camera = EDITOR.getCamera();
        final Node nodeForCamera = getNodeForCamera();

        final Vector3f left = camera.getLeft();
        left.multLocal(value);
        left.addLocal(nodeForCamera.getLocalTranslation());

        nodeForCamera.setLocalTranslation(left);
        final EditorCamera editorCamera = getEditorCamera();
    }

    protected void moveYCamera(final float value) {

        final Camera camera = EDITOR.getCamera();
        final Node nodeForCamera = getNodeForCamera();

        final Vector3f up = camera.getUp();
        up.multLocal(value);
        up.addLocal(nodeForCamera.getLocalTranslation());

        nodeForCamera.setLocalTranslation(up);
    }

    protected boolean needMovableCamera() {
        return true;
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
        } else if (MOUSE_LEFT_CLICK.equals(name)) {
            setButtonLeftDown(isPressed);
        } else if (MOUSE_MIDDLE_CLICK.equals(name)) {
            setButtonMiddleDown(isPressed);
        } else if (MOUSE_RIGHT_CLICK.equals(name)) {
            setButtonRightDown(isPressed);
        } else if (KEY_S.equals(name)) {

            final FileEditor fileEditor = getFileEditor();

            if (isControlDown() && fileEditor.isDirty()) {
                EXECUTOR_MANAGER.addFXTask(fileEditor::doSave);
            }

        } else if (!isPressed && KEY_Z.equals(name) && isControlDown()) {
            undo();
        } else if (!isPressed && KEY_Y.equals(name) && isControlDown()) {
            redo();
        }

        final EditorCamera editorCamera = getEditorCamera();

        if (editorCamera != null && needMovableCamera()) {
            editorCamera.setLockRotation(isShiftDown() && isButtonMiddleDown());
        }
    }

    /**
     * Повторонение отмененной операции.
     */
    protected void redo() {
    }


    /**
     * отмена последней операции.
     */
    protected void undo() {
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
    protected void setAltDown(final boolean altDown) {
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
    protected void setControlDown(final boolean controlDown) {
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
    protected void setShiftDown(final boolean shiftDown) {
        this.shiftDown = shiftDown;
    }

    /**
     * @param buttonLeftDown нажата ли сейчас левая кнопка мыши.
     */
    protected void setButtonLeftDown(final boolean buttonLeftDown) {
        this.buttonLeftDown = buttonLeftDown;
    }

    /**
     * @param buttonMiddleDown нажат ли сейчас колесик.
     */
    protected void setButtonMiddleDown(final boolean buttonMiddleDown) {
        this.buttonMiddleDown = buttonMiddleDown;
    }

    /**
     * @param buttonRightDown нажата ли сейчас правая кнопка мыши.
     */
    protected void setButtonRightDown(final boolean buttonRightDown) {
        this.buttonRightDown = buttonRightDown;
    }

    /**
     * @return нажата ли сейчас левая кнопка мыши.
     */
    protected boolean isButtonLeftDown() {
        return buttonLeftDown;
    }

    /**
     * @return нажат ли сейчас колесик.
     */
    protected boolean isButtonMiddleDown() {
        return buttonMiddleDown;
    }

    /**
     * @return нажата ли сейчас правая кнопка мыши.
     */
    protected boolean isButtonRightDown() {
        return buttonRightDown;
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
    protected EditorCamera getEditorCamera() {
        return editorCamera;
    }

    @Override
    public void initialize(final AppStateManager stateManager, final Application application) {
        super.initialize(stateManager, application);

        final Node rootNode = EDITOR.getRootNode();
        rootNode.attachChild(getStateNode());

        final EditorCamera editorCamera = getEditorCamera();
        final InputManager inputManager = EDITOR.getInputManager();

        registerActionListener(inputManager);
        registerAnalogListener(inputManager);

        if (editorCamera != null) {
            editorCamera.setEnabled(true);
            editorCamera.registerInput(inputManager);
        }
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

        inputManager.addListener(analogListener, MOUSE_X_AXIS, MOUSE_X_AXIS_NEGATIVE, MOUSE_Y_AXIS, MOUSE_Y_AXIS_NEGATIVE);
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

        if (!inputManager.hasMapping(KEY_S)) {
            inputManager.addMapping(KEY_S, new KeyTrigger(KeyInput.KEY_S));
        }

        if (!inputManager.hasMapping(KEY_Z)) {
            inputManager.addMapping(KEY_Z, new KeyTrigger(KeyInput.KEY_Z));
        }

        if (!inputManager.hasMapping(KEY_Y)) {
            inputManager.addMapping(KEY_Y, new KeyTrigger(KeyInput.KEY_Y));
        }

        inputManager.addListener(actionListener, MOUSE_RIGHT_CLICK, MOUSE_LEFT_CLICK, MOUSE_MIDDLE_CLICK);
        inputManager.addListener(actionListener, KEY_CTRL, KEY_SHIFT, KEY_ALT, KEY_S, KEY_Z, KEY_Y);
    }

    @Override
    public void cleanup() {
        super.cleanup();

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
     * Нужна ли камера для этой части.
     */
    protected boolean needEditorCamera() {
        return false;
    }

    /**
     * Нужен ли источник света для камеры.
     */
    protected boolean needLightForCamera() {
        return false;
    }

    protected EditorCamera createEditorCamera() {

        final Camera camera = EDITOR.getCamera();

        final EditorCamera editorCamera = new EditorCamera(camera, getNodeForCamera());
        editorCamera.setMinVerticalRotation(-FastMath.HALF_PI);
        editorCamera.setMaxDistance(1000);
        editorCamera.setSmoothMotion(false);
        editorCamera.setRotationSensitivity(1);
        editorCamera.setZoomSensitivity(1);
        editorCamera.setDownRotateOnCloseViewOnly(false);

        return editorCamera;
    }

    /**
     * @return источник света для камеры.
     */
    protected DirectionalLight createLightForCamera() {

        final DirectionalLight directionalLight = new DirectionalLight();
        directionalLight.setColor(ColorRGBA.White);

        return directionalLight;
    }

    /**
     * @return узел на который должна смотреть камера.
     */
    protected Node getNodeForCamera() {
        return stateNode;
    }

    /**
     * @return источник света для камеры.
     */
    protected DirectionalLight getLightForCamera() {
        return lightForCamera;
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        final EditorCamera editorCamera = getEditorCamera();
        final DirectionalLight lightForCamera = getLightForCamera();

        if (editorCamera != null && lightForCamera != null && needUpdateCameraLight()) {
            final Camera camera = EDITOR.getCamera();
            lightForCamera.setDirection(camera.getDirection().normalize());
        }
    }

    /**
     * @return нужно ли обновлять направление света.
     */
    protected boolean needUpdateCameraLight() {
        return false;
    }
}
