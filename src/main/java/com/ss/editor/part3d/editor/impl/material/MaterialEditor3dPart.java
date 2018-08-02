package com.ss.editor.part3d.editor.impl.material;

import com.ss.editor.plugin.api.editor.material.BaseMaterialEditor3dPart;
import com.ss.editor.ui.component.editor.impl.material.MaterialFileEditor;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation the 3D part of the {@link MaterialFileEditor} but it can be reused in other cases.
 *
 * @author JavaSaBr
 */
public class MaterialEditor3dPart extends BaseMaterialEditor3dPart<MaterialFileEditor> {

    public MaterialEditor3dPart(@NotNull MaterialFileEditor fileEditor) {
        super(fileEditor);
    }
}
