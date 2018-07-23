package com.ss.editor.model.undo.impl;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.extension.action.ModifyingAction;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.util.JmbEditorEnvoriment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link AbstractEditorOperation} to execute {@link ModifyingAction}.
 *
 * @author JavaSaBr
 */
public class ModifyingActionOperation extends AbstractEditorOperation<ChangeConsumer> {

    /**
     * The action.
     */
    @NotNull
    private final ModifyingAction modifyingAction;

    /**
     * The action's owner.
     */
    @NotNull
    private final Object owner;

    /**
     * The prev. state.
     */
    @Nullable
    private volatile Object state;

    public ModifyingActionOperation(@NotNull ModifyingAction modifyingAction, @NotNull Object owner) {
        this.modifyingAction = modifyingAction;
        this.owner = owner;
    }

    @Override
    @JmeThread
    protected void endInJme(@NotNull ChangeConsumer editor) {
        super.endInJme(editor);
        editor.notifyJmeObjectChanged(owner);
    }

    @Override
    @FxThread
    protected void endInFx(@NotNull ChangeConsumer editor) {
        super.endInFx(editor);
        editor.notifyFxObjectChanged(owner);
    }

    @Override
    @JmeThread
    protected void redoInJme(@NotNull ChangeConsumer editor) {
        super.redoInJme(editor);
        state = modifyingAction.redo(JmbEditorEnvoriment.getInstance(), owner);
    }

    @Override
    @FxThread
    protected void undoInJme(@NotNull ChangeConsumer editor) {
        super.undoInJme(editor);
        modifyingAction.undo(JmbEditorEnvoriment.getInstance(), owner, notNull(state));
    }
}
