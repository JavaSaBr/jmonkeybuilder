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
import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.part3d.editor.Editor3dPart;
import com.ss.editor.part3d.editor.control.Editor3dPartControl;
import com.ss.editor.part3d.editor.control.impl.CameraEditor3dPartControl;
import com.ss.editor.part3d.editor.control.impl.InputStateEditor3dPartControl;
import com.ss.editor.plugin.api.RenderFilterRegistry;
import com.ss.editor.ui.component.editor.FileEditor;
import com.ss.editor.util.EditorUtils;
import com.ss.rlib.common.function.BooleanFloatConsumer;
import com.ss.rlib.common.util.dictionary.ObjectDictionary;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * The base implementation of the {@link Editor3dPart} for a file editor.
 *
 * @param <T> the type of a file editor.
 * @author JavaSaBr
 */
public abstract class AdvancedAbstractEditor3dPart<T extends FileEditor> extends AbstractEditor3dPart<T> {

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

    public AdvancedAbstractEditor3dPart(@NotNull T fileEditor) {
        super(fileEditor);
        controls.add(new InputStateEditor3dPartControl(this));
        createCameraControl().ifPresent(controls::add);
    }

    @BackgroundThread
    protected @NotNull Optional<CameraEditor3dPartControl> createCameraControl() {
        return Optional.of(new CameraEditor3dPartControl(this, EditorUtils.getGlobalCamera()));
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
    public void initialize(@NotNull AppStateManager stateManager, @NotNull Application application) {
        super.initialize(stateManager, application);

        var rootNode = EditorUtils.getRootNode(application);
        rootNode.attachChild(stateNode);

        var filterRegistry = RenderFilterRegistry.getInstance();
        filterRegistry.enableFilters();

    }

    @Override
    @JmeThread
    public void cleanup() {

        var filterRegistry = RenderFilterRegistry.getInstance();
        filterRegistry.disableFilters();

        var rootNode = EditorUtils.getGlobalRootNode();
        rootNode.detachChild(stateNode);

        super.cleanup();
    }

    @Override
    @JmeThread
    public void update(float tpf) {
        super.update(tpf);
        controls.forEach(tpf, Editor3dPartControl::update);
        controls.forEach(tpf, Editor3dPartControl::preCameraUpdate);
        controls.forEach(tpf, Editor3dPartControl::cameraUpdate);
        controls.forEach(tpf, Editor3dPartControl::postCameraUpdate);
    }
}
