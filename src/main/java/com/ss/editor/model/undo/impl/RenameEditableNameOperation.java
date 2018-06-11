package com.ss.editor.model.undo.impl;

import static com.ss.editor.model.undo.impl.RenameNodeOperation.PROPERTY_NAME;
import com.ss.editor.extension.EditableName;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import org.jetbrains.annotations.NotNull;

/**
 * The operation to rename an editable name object.
 *
 * @author JavaSaBr
 */
public class RenameEditableNameOperation extends AbstractEditorOperation<ChangeConsumer> {

    /**
     * The old name.
     */
    @NotNull
    private final String oldName;

    /**
     * The new name.
     */
    @NotNull
    private final String newName;

    /**
     * The object.
     */
    @NotNull
    private final EditableName object;

    public RenameEditableNameOperation(@NotNull String oldName, @NotNull String newName, @NotNull EditableName object) {
        this.oldName = oldName;
        this.newName = newName;
        this.object = object;
    }

    @Override
    protected void redoImpl(@NotNull ChangeConsumer editor) {
        EXECUTOR_MANAGER.addJmeTask(() -> {
            object.setName(newName);
            EXECUTOR_MANAGER.addFxTask(() -> editor.notifyFxChangeProperty(object, PROPERTY_NAME));
        });
    }

    @Override
    protected void undoImpl(@NotNull ChangeConsumer editor) {
        EXECUTOR_MANAGER.addJmeTask(() -> {
            object.setName(oldName);
            EXECUTOR_MANAGER.addFxTask(() -> editor.notifyFxChangeProperty(object, PROPERTY_NAME));
        });
    }
}
