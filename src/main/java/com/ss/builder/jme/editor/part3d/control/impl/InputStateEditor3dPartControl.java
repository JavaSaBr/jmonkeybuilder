package com.ss.builder.editor.part3d.control.impl;

import static com.ss.rlib.common.util.array.ArrayFactory.toArray;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.editor.part3d.ExtendableEditor3dPart;
import com.ss.builder.util.JmeUtils;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.editor.part3d.ExtendableEditor3dPart;
import com.ss.editor.util.JmeUtils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.dictionary.ObjectDictionary;
import org.jetbrains.annotations.NotNull;

/**
 * The control to store input state of the editor 3d part.
 *
 * @author JavaSaBr
 */
public class InputStateEditor3dPartControl extends BaseInputEditor3dPartControl<ExtendableEditor3dPart>
        implements ActionListener {

    public static final String PROP_IS_CONTROL_DOWN = "inputStateEditor.isControlDown";
    public static final String PROP_IS_ALT_DOWN = "inputStateEditor.isAltDown";
    public static final String PROP_IS_SHIFT_DOWN = "inputStateEditor.isShiftDown";
    public static final String PROP_IS_BUTTON_LEFT_DOWN = "inputStateEditor.isButtonLeftDown";
    public static final String PROP_IS_BUTTON_MIDDLE_DOWN = "inputStateEditor.isButtonMiddleDown";
    public static final String PROP_IS_BUTTON_RIGHT_DOWN = "inputStateEditor.isButtonRightDown";

    private static final String[] PROPERTIES = {
            PROP_IS_CONTROL_DOWN,
            PROP_IS_ALT_DOWN,
            PROP_IS_SHIFT_DOWN,
            PROP_IS_BUTTON_LEFT_DOWN,
            PROP_IS_BUTTON_MIDDLE_DOWN,
            PROP_IS_BUTTON_RIGHT_DOWN
    };

    private static final ObjectDictionary<String, Trigger> TRIGGERS =
            ObjectDictionary.ofType(String.class, Trigger.class);

    private static final ObjectDictionary<String, Trigger[]> MULTI_TRIGGERS =
            ObjectDictionary.ofType(String.class, Trigger[].class);

    private static final String MOUSE_RIGHT_CLICK = "jMB.inputStateEditor.mouseRightClick";
    private static final String MOUSE_LEFT_CLICK = "jMB.inputStateEditor.mouseLeftClick";
    private static final String MOUSE_MIDDLE_CLICK = "jMB.inputStateEditor.mouseMiddleClick";

    private static final String KEY_CTRL = "jMB.inputStateEditor.keyCtrl";
    private static final String KEY_ALT = "jMB.inputStateEditor.keyAlt";
    private static final String KEY_SHIFT = "jMB.inputStateEditor.keyShift";

    private static final String[] MAPPINGS;

    static {

        TRIGGERS.put(MOUSE_RIGHT_CLICK, new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        TRIGGERS.put(MOUSE_LEFT_CLICK, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        TRIGGERS.put(MOUSE_MIDDLE_CLICK, new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE));

        MULTI_TRIGGERS.put(KEY_CTRL, toArray(new KeyTrigger(KeyInput.KEY_RCONTROL), new KeyTrigger(KeyInput.KEY_LCONTROL)));
        MULTI_TRIGGERS.put(KEY_SHIFT, toArray(new KeyTrigger(KeyInput.KEY_RSHIFT), new KeyTrigger(KeyInput.KEY_LSHIFT)));
        MULTI_TRIGGERS.put(KEY_ALT, toArray(new KeyTrigger(KeyInput.KEY_RMENU), new KeyTrigger(KeyInput.KEY_LMENU)));

        Array<String> mappings = TRIGGERS.keyArray(String.class);
        mappings.addAll(MULTI_TRIGGERS.keyArray(String.class));

        MAPPINGS = mappings.toArray(String.class);
    }

    @NotNull
    private final ObjectDictionary<String, Boolean> state;

    public InputStateEditor3dPartControl(@NotNull ExtendableEditor3dPart editor3dPart) {
        super(editor3dPart);
        this.state = ObjectDictionary.ofType(String.class, Boolean.class);

        actionHandlers.put(MOUSE_LEFT_CLICK, (isPressed, tpf) -> state.put(PROP_IS_BUTTON_LEFT_DOWN, isPressed));
        actionHandlers.put(MOUSE_RIGHT_CLICK, (isPressed, tpf) -> state.put(PROP_IS_BUTTON_RIGHT_DOWN, isPressed));
        actionHandlers.put(MOUSE_MIDDLE_CLICK, (isPressed, tpf) -> state.put(PROP_IS_BUTTON_MIDDLE_DOWN, isPressed));
        actionHandlers.put(KEY_ALT, (isPressed, tpf) -> state.put(PROP_IS_ALT_DOWN, isPressed));
        actionHandlers.put(KEY_CTRL, (isPressed, tpf) -> state.put(PROP_IS_CONTROL_DOWN, isPressed));
        actionHandlers.put(KEY_SHIFT, (isPressed, tpf) -> state.put(PROP_IS_SHIFT_DOWN, isPressed));

        for (var property : PROPERTIES) {
            state.put(property, false);
        }
    }

    @Override
    @JmeThread
    public void register(@NotNull InputManager inputManager) {

        TRIGGERS.forEach(inputManager, JmeUtils::addMapping);
        MULTI_TRIGGERS.forEach(inputManager, JmeUtils::addMapping);

        inputManager.addListener(this, MAPPINGS);
    }

    @Override
    @JmeThread
    public boolean hasProperty(@NotNull String propertyId) {
        return state.containsKey(propertyId);
    }

    @Override
    @JmeThread
    public boolean getBooleanProperty(@NotNull String propertyId) {
        return Boolean.TRUE.equals(state.get(propertyId));
    }
}
