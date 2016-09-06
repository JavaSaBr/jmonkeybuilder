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
import com.jme3.input.controls.Trigger;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.ss.editor.Editor;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.EditorCamera;
import com.ss.editor.state.editor.EditorState;
import com.ss.editor.ui.component.editor.FileEditor;

import rlib.function.BooleanFloatConsumer;
import rlib.function.FloatFloatConsumer;
import rlib.logging.Logger;
import rlib.logging.LoggerManager;
import rlib.util.dictionary.DictionaryFactory;
import rlib.util.dictionary.ObjectDictionary;

import static org.apache.commons.lang3.ArrayUtils.toArray;

/**
 * The base implementation of the {@link com.jme3.app.state.AppState} for the editor.
 *
 * @author Ronn
 */
public abstract class AbstractEditorState<T extends FileEditor> extends AbstractAppState implements EditorState {

    protected static final Logger LOGGER = LoggerManager.getLogger(EditorState.class);

    protected static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();
    protected static final Editor EDITOR = Editor.getInstance();

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
     * Обработчики акшен событий.
     */
    private final ObjectDictionary<String, BooleanFloatConsumer> actionHandlers;

    /**
     * Обработчики аналоговых событий.
     */
    private final ObjectDictionary<String, FloatFloatConsumer> analogHandlers;

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
        this.actionHandlers = DictionaryFactory.newObjectDictionary();
        this.analogHandlers = DictionaryFactory.newObjectDictionary();

        registerActionHandlers(actionHandlers);
        registerAnalogHandlers(analogHandlers);
    }

    /**
     * Регистрация обработчиков акшен событий.
     */
    protected void registerActionHandlers(final ObjectDictionary<String, BooleanFloatConsumer> actionHandlers) {
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
     * Регистрация обработчиков аналоговых событий.
     */
    protected void registerAnalogHandlers(final ObjectDictionary<String, FloatFloatConsumer> analogHandlers) {
        analogHandlers.put(MOUSE_X_AXIS, (value, tpf) -> moveXCamera(value * 30F));
        analogHandlers.put(MOUSE_X_AXIS_NEGATIVE, (value, tpf) -> moveXCamera(-value * 30F));
        analogHandlers.put(MOUSE_Y_AXIS, (value, tpf) -> moveYCamera(-value * 30F));
        analogHandlers.put(MOUSE_Y_AXIS_NEGATIVE, (value, tpf) -> moveYCamera(value * 30F));
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
        final FloatFloatConsumer handler = analogHandlers.get(name);
        if (handler != null) handler.accept(value, tpf);
    }

    protected void moveXCamera(final float value) {
        if (!needMovableCamera() || !isShiftDown() || !isButtonMiddleDown()) return;

        final EditorCamera editorCamera = getEditorCamera();
        final Camera camera = EDITOR.getCamera();
        final Node nodeForCamera = getNodeForCamera();

        final Vector3f left = camera.getLeft();
        left.multLocal(value * (float) Math.sqrt(editorCamera.getTargetDistance()));
        left.addLocal(nodeForCamera.getLocalTranslation());

        nodeForCamera.setLocalTranslation(left);
    }

    protected void moveYCamera(final float value) {
        if (!needMovableCamera() || !isShiftDown() || !isButtonMiddleDown()) return;

        final EditorCamera editorCamera = getEditorCamera();
        final Camera camera = EDITOR.getCamera();
        final Node nodeForCamera = getNodeForCamera();

        final Vector3f up = camera.getUp();
        up.multLocal(value * (float) Math.sqrt(editorCamera.getTargetDistance()));
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

        checkAndAddMappings(inputManager);
        registerActionListener(inputManager);
        registerAnalogListener(inputManager);

        if (editorCamera != null) {
            editorCamera.setEnabled(true);
            editorCamera.registerInput(inputManager);
        }
    }

    /**
     * Проверка и в случае отсутствия, добавление необходимых триггеров.
     */
    protected void checkAndAddMappings(final InputManager inputManager) {
        TRIGGERS.forEach(inputManager, AbstractEditorState::addMapping);
        MULTI_TRIGGERS.forEach(inputManager, AbstractEditorState::addMapping);
    }

    private static void addMapping(final InputManager inputManager, final String name, final Trigger[] triggers) {
        if (!inputManager.hasMapping(name)) inputManager.addMapping(name, triggers);
    }

    private static void addMapping(final InputManager inputManager, final String name, final Trigger trigger) {
        if (!inputManager.hasMapping(name)) inputManager.addMapping(name, trigger);
    }

    /**
     * Регистрация аналогового слушателя.
     */
    protected void registerAnalogListener(final InputManager inputManager) {
        inputManager.addListener(analogListener, MOUSE_X_AXIS, MOUSE_X_AXIS_NEGATIVE, MOUSE_Y_AXIS, MOUSE_Y_AXIS_NEGATIVE);
    }

    /**
     * Регистрация слушателя нажатий.
     */
    protected void registerActionListener(final InputManager inputManager) {
        inputManager.addListener(actionListener, MOUSE_RIGHT_CLICK, MOUSE_LEFT_CLICK, MOUSE_MIDDLE_CLICK);
        inputManager.addListener(actionListener, KEY_CTRL, KEY_SHIFT, KEY_ALT, KEY_S, KEY_Z, KEY_Y, KEY_NUM_1,
                KEY_NUM_2, KEY_NUM_3, KEY_NUM_4, KEY_NUM_5, KEY_NUM_6, KEY_NUM_7, KEY_NUM_8, KEY_NUM_9);
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
        editorCamera.setMaxDistance(10000);
        editorCamera.setSmoothMotion(false);
        editorCamera.setRotationSensitivity(1);
        editorCamera.setZoomSensitivity(0.5F);

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
