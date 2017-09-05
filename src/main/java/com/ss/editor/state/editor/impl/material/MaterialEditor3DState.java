package com.ss.editor.state.editor.impl.material;

import com.ss.editor.plugin.api.editor.material.BaseMaterialEditor3DState;
import com.ss.editor.ui.component.editor.impl.material.MaterialFileEditor;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation the 3D part of the {@link MaterialFileEditor} but it can be reused in other cases.
 *
 * @author JavaSaBr
 */
public class MaterialEditor3DState extends BaseMaterialEditor3DState<MaterialFileEditor> {

    public MaterialEditor3DState(@NotNull final MaterialFileEditor fileEditor) {
        super(fileEditor);
    }
}
