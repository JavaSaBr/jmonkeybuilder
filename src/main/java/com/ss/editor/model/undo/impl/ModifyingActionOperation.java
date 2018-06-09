package com.ss.editor.model.undo.impl;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FxThread;
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
    private Object owner;

    /**
     * The prev. state.
     */
    @Nullable
    private Object state;

    public ModifyingActionOperation(@NotNull ModifyingAction modifyingAction, @NotNull Object owner) {
        this.modifyingAction = modifyingAction;
        this.owner = owner;
    }

    @Override
    @FxThread
    protected void redoImpl(@NotNull ChangeConsumer editor) {
        EXECUTOR_MANAGER.addJmeTask(() -> {
            state = modifyingAction.redo(JmbEditorEnvoriment.getInstance(), owner);
            editor.notifyJmeObjectChanged(owner);
            EXECUTOR_MANAGER.addFxTask(() -> editor.notifyFxObjectChanged(owner));
        });
    }

    @Override
    @FxThread
    protected void undoImpl(@NotNull ChangeConsumer editor) {
        EXECUTOR_MANAGER.addJmeTask(() -> {
            modifyingAction.undo(JmbEditorEnvoriment.getInstance(), owner, notNull(state));
            editor.notifyJmeObjectChanged(owner);
            EXECUTOR_MANAGER.addFxTask(() -> editor.notifyFxObjectChanged(owner));
        });
    }
}
