package com.ss.editor.ui.control.app.state.list;

import com.ss.editor.annotation.FxThread;
import com.ss.editor.extension.action.ModifyingAction;
import com.ss.editor.extension.scene.app.state.EditableSceneAppState;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import javafx.scene.control.MenuItem;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of {@link ModifyingAction} from {@link EditableSceneAppState}.
 *
 * @author JavaSaBr
 */
public class AppStateAction extends MenuItem {

    @NotNull
    private final ModifyingAction action;

    @NotNull
    private final ChangeConsumer changeConsumer;

    public AppStateAction(@NotNull ModifyingAction action, @NotNull ChangeConsumer changeConsumer) {
        this.action = action;
        this.changeConsumer = changeConsumer;
        setOnAction(event -> execute());
    }

    @FxThread
    private void execute() {

    }
}
