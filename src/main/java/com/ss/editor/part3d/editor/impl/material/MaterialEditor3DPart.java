package com.ss.editor.part3d.editor.impl.material;

import com.ss.editor.plugin.api.editor.material.BaseMaterialEditor3DPart;
import com.ss.editor.ui.component.editor.impl.material.MaterialFileEditor;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation the 3D part of the {@link MaterialFileEditor} but it can be reused in other cases.
 *
 * @author JavaSaBr
 */
public class MaterialEditor3DPart extends BaseMaterialEditor3DPart<MaterialFileEditor> {

    public MaterialEditor3DPart(@NotNull final MaterialFileEditor fileEditor) {
        super(fileEditor);
    }
}
