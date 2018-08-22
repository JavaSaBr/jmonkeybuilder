package com.ss.builder.jme.editor.part3d.impl.material;

import com.ss.builder.plugin.api.editor.material.BaseMaterialEditor3dPart;
import com.ss.builder.editor.impl.material.MaterialFileEditor;
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
