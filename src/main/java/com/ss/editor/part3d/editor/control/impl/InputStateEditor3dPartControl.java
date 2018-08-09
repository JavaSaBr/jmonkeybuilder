package com.ss.editor.part3d.editor.control.impl;

import static com.ss.rlib.common.util.array.ArrayFactory.toArray;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.part3d.editor.ExtendableEditor3dPart;
import com.ss.editor.part3d.editor.control.InputEditor3dPartControl;
import com.ss.editor.util.JmeUtils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.dictionary.ObjectDictionary;
import org.jetbrains.annotations.NotNull;

/**
 * The control to store input state of the editor 3d part.
 *
 * @author JavaSaBr
 */
public class InputStateEditor3dPartControl extends BaseInputEditor3dPartControl<ExtendableEditor3dPart> implements
        InputEditor3dPartControl {

    public static final String PROP_IS_CONTROL_DOWN = "inputStateEditor.isControlDown";
    public static final String PROP_IS_ALT_DOWN = "inputStateEditor.isAltDown";

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

    /**
     * True if control is pressed.
     */
    private boolean controlDown;

    /**
     * True if alt is pressed.
     */
    private boolean altDown;

    /**
     * True if shift is pressed.
     */
    private boolean shiftDown;

    /**
     * True if left button is pressed.
     */
    private boolean buttonLeftDown;

    /**
     * True if right button is pressed.
     */
    private boolean buttonRightDown;

    /**
     * True if middle button is pressed.
     */
    private boolean buttonMiddleDown;

    public InputStateEditor3dPartControl(@NotNull ExtendableEditor3dPart editor3dPart) {
        super(editor3dPart);
        actionHandlers.put(MOUSE_LEFT_CLICK, (isPressed, tpf) -> buttonLeftDown = isPressed);
        actionHandlers.put(MOUSE_RIGHT_CLICK, (isPressed, tpf) -> buttonRightDown = isPressed);
        actionHandlers.put(MOUSE_MIDDLE_CLICK, (isPressed, tpf) -> buttonMiddleDown = isPressed);
        actionHandlers.put(KEY_ALT, (isPressed, tpf) -> altDown = isPressed);
        actionHandlers.put(KEY_CTRL, (isPressed, tpf) -> controlDown = isPressed);
        actionHandlers.put(KEY_SHIFT, (isPressed, tpf) -> shiftDown = isPressed);
    }

    @Override
    @JmeThread
    public void register(@NotNull InputManager inputManager) {

        TRIGGERS.forEach(inputManager, JmeUtils::addMapping);
        MULTI_TRIGGERS.forEach(inputManager, JmeUtils::addMapping);

        inputManager.addListener(this, MAPPINGS);
    }
}
