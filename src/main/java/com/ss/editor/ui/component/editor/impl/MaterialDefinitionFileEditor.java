package com.ss.editor.ui.component.editor.impl;

import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.component.editor.EditorDescription;
import com.ss.editor.ui.control.code.BaseCodeArea;
import com.ss.editor.ui.control.code.MaterialDefinitionCodeArea;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of editor to edit material definition files.
 *
 * @author JavaSaBr
 */
public class MaterialDefinitionFileEditor extends CodeAreaFileEditor {

    /**
     * The constant DESCRIPTION.
     */
    @NotNull
    public static final EditorDescription DESCRIPTION = new EditorDescription();

    static {
        DESCRIPTION.setConstructor(MaterialDefinitionFileEditor::new);
        DESCRIPTION.setEditorName(Messages.MATERIAL_DEFINITION_FILE_EDITOR_NAME);
        DESCRIPTION.setEditorId(MaterialDefinitionFileEditor.class.getSimpleName());
        DESCRIPTION.addExtension(FileExtensions.JME_MATERIAL_DEFINITION);
    }

    @Override
    @FXThread
    protected @NotNull BaseCodeArea createCodeArea() {
        return new MaterialDefinitionCodeArea();
    }

    @Override
    @FromAnyThread
    public @NotNull EditorDescription getDescription() {
        return DESCRIPTION;
    }
}
