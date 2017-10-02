package com.ss.editor.ui.component.editor.impl;

import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.component.editor.EditorDescription;
import com.ss.editor.ui.control.code.BaseCodeArea;
import com.ss.editor.ui.control.code.GLSLCodeArea;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of editor to edit GLSL files.
 *
 * @author JavaSaBr
 */
public class GLSLFileEditor extends CodeAreaFileEditor {

    /**
     * The constant DESCRIPTION.
     */
    @NotNull
    public static final EditorDescription DESCRIPTION = new EditorDescription();

    static {
        DESCRIPTION.setConstructor(GLSLFileEditor::new);
        DESCRIPTION.setEditorName(Messages.GLSL_FILE_EDITOR_NAME);
        DESCRIPTION.setEditorId(GLSLFileEditor.class.getSimpleName());
        DESCRIPTION.setExtensions(FileExtensions.SHADER_EXTENSIONS);
    }

    @Override
    @FXThread
    protected @NotNull BaseCodeArea createCodeArea() {
        return new GLSLCodeArea();
    }

    @Override
    @FromAnyThread
    public @NotNull EditorDescription getDescription() {
        return DESCRIPTION;
    }
}
