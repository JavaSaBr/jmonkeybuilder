package com.ss.editor.ui.action;

import com.ss.editor.annotation.FxThread;
import com.ss.editor.extension.action.ModifyingAction;
import com.ss.editor.extension.scene.app.state.EditableSceneAppState;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.model.undo.impl.ModifyingActionOperation;
import javafx.scene.control.MenuItem;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of {@link ModifyingAction} from {@link EditableSceneAppState}.
 *
 * @author JavaSaBr
 */
public class MenuModifyingAction extends MenuItem {

    /**
     * The action.
     */
    @NotNull
    private final ModifyingAction action;

    /**
     * The change consumer.
     */
    @NotNull
    private final ChangeConsumer changeConsumer;

    /**
     * The action's owner.
     */
    @NotNull
    private final Object owner;

    public MenuModifyingAction(
            @NotNull ModifyingAction action,
            @NotNull ChangeConsumer changeConsumer,
            @NotNull Object owner
    ) {
        this.action = action;
        this.changeConsumer = changeConsumer;
        this.owner = owner;
        setText(action.getName());
        setOnAction(event -> execute());
    }

    @FxThread
    private void execute() {
        changeConsumer.execute(new ModifyingActionOperation(action, owner));
    }
}
