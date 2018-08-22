package com.ss.builder.editor.impl;

import com.ss.builder.FileExtensions;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.editor.EditorDescriptor;
import com.ss.builder.fx.control.code.BaseCodeArea;
import com.ss.builder.fx.control.code.MaterialDefinitionCodeArea;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of editor to edit material definition files.
 *
 * @author JavaSaBr
 */
public class MaterialDefinitionFileEditor extends CodeAreaFileEditor {

    public static final EditorDescriptor DESCRIPTOR = new EditorDescriptor(
            MaterialDefinitionFileEditor::new,
            Messages.MATERIAL_DEFINITION_FILE_EDITOR_NAME,
            MaterialDefinitionFileEditor.class.getSimpleName(),
            FileExtensions.JME_MATERIAL_DEFINITION
    );

    @Override
    @FxThread
    protected @NotNull BaseCodeArea createCodeArea() {
        return new MaterialDefinitionCodeArea();
    }

    @Override
    @FromAnyThread
    public @NotNull EditorDescriptor getDescriptor() {
        return DESCRIPTOR;
    }
}
