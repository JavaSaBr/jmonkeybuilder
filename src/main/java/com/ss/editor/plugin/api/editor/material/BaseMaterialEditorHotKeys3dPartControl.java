package com.ss.editor.plugin.api.editor.material;

import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.ss.editor.part3d.editor.ExtendableEditor3dPart;
import com.ss.editor.part3d.editor.control.impl.KeyEventRedirectEditor3dPartControl;
import com.ss.editor.ui.component.editor.FileEditor;
import com.ss.rlib.common.util.dictionary.ObjectDictionary;
import org.jetbrains.annotations.NotNull;

/**
 * The input control of {@link BaseMaterialEditor3dPart}
 *
 * @author JavaSaBr
 */
public class BaseMaterialEditorHotKeys3dPartControl extends KeyEventRedirectEditor3dPartControl {

    private static final String KEY_C = "jMB.baseMaterialEditorHotKeys.C";
    private static final String KEY_S = "jMB.baseMaterialEditorHotKeys.S";
    private static final String KEY_P = "jMB.baseMaterialEditorHotKeys.P";
    private static final String KEY_L = "jMB.baseMaterialEditorHotKeys.L";

    private static final ObjectDictionary<String, KeyTrigger> TRIGGERS =
            ObjectDictionary.ofType(String.class, KeyTrigger.class);

    private static final String[] MAPPINGS;

    static {

        TRIGGERS.put(KEY_C, new KeyTrigger(KeyInput.KEY_C));
        TRIGGERS.put(KEY_S, new KeyTrigger(KeyInput.KEY_S));
        TRIGGERS.put(KEY_P, new KeyTrigger(KeyInput.KEY_P));
        TRIGGERS.put(KEY_L, new KeyTrigger(KeyInput.KEY_L));

        MAPPINGS = TRIGGERS.keyArray(String.class)
                    .toArray(String.class);
    }

    protected BaseMaterialEditorHotKeys3dPartControl(@NotNull ExtendableEditor3dPart editor3dPart) {
        super(editor3dPart, TRIGGERS, MAPPINGS);
    }
}
