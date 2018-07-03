package com.ss.editor.ui.component.editor.impl;

import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.component.editor.EditorDescriptor;
import com.ss.editor.ui.control.code.BaseCodeArea;
import com.ss.editor.ui.control.code.MaterialDefinitionCodeArea;
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
