package com.ss.builder.model.undo.impl;

import com.ss.editor.extension.EditableName;
import org.jetbrains.annotations.NotNull;

/**
 * The operation to rename an editable name object.
 *
 * @author JavaSaBr
 */
public class RenameEditableNameOperation extends RenameObjectOperation<EditableName> {

    public RenameEditableNameOperation(
            @NotNull String oldName,
            @NotNull String newName,
            @NotNull EditableName object
    ) {
        super(oldName, newName, object, EditableName::setName);
    }
}
