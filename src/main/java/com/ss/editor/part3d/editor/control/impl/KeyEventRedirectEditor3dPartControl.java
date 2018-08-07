package com.ss.editor.part3d.editor.control.impl;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;
import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.part3d.editor.Editor3dPart;
import com.ss.editor.plugin.api.editor.material.BaseMaterialEditor3dPart;
import com.ss.editor.ui.component.editor.FileEditor;
import com.ss.editor.util.JmeUtils;
import com.ss.rlib.common.util.ObjectUtils;
import com.ss.rlib.common.util.dictionary.ObjectDictionary;
import javafx.scene.input.KeyCode;
import org.jetbrains.annotations.NotNull;

/**
 * The control to redirect some input events from {@link Editor3dPart} to {@link FileEditor}.
 *
 * @author JavaSaBr
 */
public class KeyEventRedirectEditor3dPartControl extends BaseInputEditor3dPartControl<Editor3dPart> {

    private static final KeyCode[] KEY_CODES = new KeyCode[Byte.MAX_VALUE * 3];

    static {
        KEY_CODES[KeyInput.KEY_C] = KeyCode.C;
    }

    @NotNull
    private final ObjectDictionary<String, KeyTrigger> triggers;

    @NotNull
    private final String[] mappings;

    protected KeyEventRedirectEditor3dPartControl(
            @NotNull BaseMaterialEditor3dPart<?> editor3dPart,
            @NotNull ObjectDictionary<String, KeyTrigger> triggers,
            @NotNull String[] mappings
    ) {
        super(editor3dPart);
        this.triggers = triggers;
        this.mappings = mappings;

        triggers.forEach(actionHandlers, (handlers, mapping, trigger) ->
                handlers.put(mapping, (isPressed, tpf) ->
                        makeHandler(editor3dPart, KEY_CODES[trigger.getKeyCode()], isPressed)));
    }

    @BackgroundThread
    private void makeHandler(@NotNull Editor3dPart editor3dPart, @NotNull KeyCode keyCode, boolean isPressed) {

        var inputState = editor3dPart.requireControl(InputStateEditor3dPartControl.class);
        var fileEditor = editor3dPart.getFileEditor();
        var isControlDown = inputState.isControlDown();
        var isShiftDown = inputState.isShiftDown();
        var isButtonMiddleDown = inputState.isButtonMiddleDown();

        fileEditor.handleKeyAction(ObjectUtils.notNull(keyCode), isPressed,
                isControlDown, isShiftDown, isButtonMiddleDown);
    }

    @Override
    @JmeThread
    public void register(@NotNull InputManager inputManager) {
        triggers.forEach(inputManager, JmeUtils::addMapping);
        inputManager.addListener(this, mappings);
    }
}
