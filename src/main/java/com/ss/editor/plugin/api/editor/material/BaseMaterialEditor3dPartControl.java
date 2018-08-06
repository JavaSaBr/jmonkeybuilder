package com.ss.editor.plugin.api.editor.material;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.part3d.editor.control.impl.BaseInputEditor3dPartControl;
import com.ss.editor.util.JmeUtils;
import com.ss.rlib.common.util.dictionary.ObjectDictionary;
import javafx.scene.input.KeyCode;
import org.jetbrains.annotations.NotNull;

/**
 * The input control of {@link BaseMaterialEditor3dPart}
 *
 * @author JavaSaBr
 */
public class BaseMaterialEditor3dPartControl extends BaseInputEditor3dPartControl<BaseMaterialEditor3dPart<?>> {

    private static final String KEY_C = "jMB.baseMaterialEditor.C";
    private static final String KEY_S = "jMB.baseMaterialEditor.S";
    private static final String KEY_P = "jMB.baseMaterialEditor.P";
    private static final String KEY_L = "jMB.baseMaterialEditor.L";

    private static final ObjectDictionary<String, Trigger> TRIGGERS =
            ObjectDictionary.ofType(String.class, Trigger.class);

    private static final String[] MAPPINGS;

    static {
        TRIGGERS.put(KEY_C, new KeyTrigger(KeyInput.KEY_C));
        TRIGGERS.put(KEY_S, new KeyTrigger(KeyInput.KEY_S));
        TRIGGERS.put(KEY_P, new KeyTrigger(KeyInput.KEY_P));
        TRIGGERS.put(KEY_L, new KeyTrigger(KeyInput.KEY_L));

        MAPPINGS = TRIGGERS.keyArray(String.class)
                    .toArray(String.class);
    }

    protected BaseMaterialEditor3dPartControl(@NotNull BaseMaterialEditor3dPart<?> editor3dPart) {
        super(editor3dPart);

        BaseMaterialFileEditor fileEditor = editor3dPart.getFileEditor();

        actionHandlers.put(KEY_S, (isPressed, tpf) ->
                fileEditor.handleKeyAction(KeyCode.S, isPressed, isControlDown(), isShiftDown(), isButtonMiddleDown()));

        actionHandlers.put(KEY_C, (isPressed, tpf) ->
                fileEditor.handleKeyAction(KeyCode.C, isPressed, isControlDown(), isShiftDown(), isButtonMiddleDown()));

        actionHandlers.put(KEY_P, (isPressed, tpf) ->
                fileEditor.handleKeyAction(KeyCode.P, isPressed, isControlDown(), isShiftDown(), isButtonMiddleDown()));

        actionHandlers.put(KEY_L, (isPressed, tpf) ->
                fileEditor.handleKeyAction(KeyCode.L, isPressed, isControlDown(), isShiftDown(), isButtonMiddleDown()));
    }

    @Override
    @JmeThread
    public void register(@NotNull InputManager inputManager) {
        TRIGGERS.forEach(inputManager, JmeUtils::addMapping);
        inputManager.addListener(this, MAPPINGS);
    }
}
