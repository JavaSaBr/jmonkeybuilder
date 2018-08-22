package com.ss.builder.editor.part3d.control.impl;

import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.ss.builder.editor.part3d.ExtendableEditor3dPart;
import com.ss.builder.plugin.api.editor.material.BaseMaterialEditor3dPart;
import com.ss.editor.editor.part3d.ExtendableEditor3dPart;
import com.ss.editor.plugin.api.editor.material.BaseMaterialEditor3dPart;
import com.ss.rlib.common.util.dictionary.ObjectDictionary;
import org.jetbrains.annotations.NotNull;

/**
 * The input control of {@link BaseMaterialEditor3dPart}
 *
 * @author JavaSaBr
 */
public class AbstractSceneEditorHotKeys3dPartControl extends KeyEventRedirectEditor3dPartControl {

    private static final String KEY_S = "jMB.abstractSceneEditorHotKeys.S";
    private static final String KEY_G = "jMB.abstractSceneEditorHotKeys.G";
    private static final String KEY_R = "jMB.abstractSceneEditorHotKeys.R";
    private static final String KEY_DEL = "jMB.abstractSceneEditorHotKeys.Del";

    private static final ObjectDictionary<String, KeyTrigger> TRIGGERS =
            ObjectDictionary.ofType(String.class, KeyTrigger.class);

    private static final String[] MAPPINGS;

    static {

        TRIGGERS.put(KEY_G, new KeyTrigger(KeyInput.KEY_G));
        TRIGGERS.put(KEY_S, new KeyTrigger(KeyInput.KEY_S));
        TRIGGERS.put(KEY_R, new KeyTrigger(KeyInput.KEY_R));
        TRIGGERS.put(KEY_DEL, new KeyTrigger(KeyInput.KEY_DELETE));

        MAPPINGS = TRIGGERS.keyArray(String.class)
                    .toArray(String.class);
    }

    public AbstractSceneEditorHotKeys3dPartControl(@NotNull ExtendableEditor3dPart editor3dPart) {
        super(editor3dPart, TRIGGERS, MAPPINGS);
    }
}
