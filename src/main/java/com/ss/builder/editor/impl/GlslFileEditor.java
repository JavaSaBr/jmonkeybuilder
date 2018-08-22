package com.ss.builder.editor.impl;

import com.ss.builder.FileExtensions;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.editor.EditorDescriptor;
import com.ss.builder.fx.control.code.BaseCodeArea;
import com.ss.builder.fx.control.code.GLSLCodeArea;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of editor to edit GLSL files.
 *
 * @author JavaSaBr
 */
public class GlslFileEditor extends CodeAreaFileEditor {

    public static final EditorDescriptor DESCRIPTOR = new EditorDescriptor(
            GlslFileEditor::new,
            Messages.GLSL_FILE_EDITOR_NAME,
            GlslFileEditor.class.getSimpleName(),
            FileExtensions.SHADER_EXTENSIONS
    );

    @Override
    @FxThread
    protected @NotNull BaseCodeArea createCodeArea() {
        return new GLSLCodeArea();
    }

    @Override
    @FromAnyThread
    public @NotNull EditorDescriptor getDescriptor() {
        return DESCRIPTOR;
    }
}
