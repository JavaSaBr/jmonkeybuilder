package com.ss.editor.ui.control.app.state.list;

import com.ss.extension.scene.app.state.EditableSceneAppState;

import org.jetbrains.annotations.NotNull;

import javafx.scene.control.cell.TextFieldListCell;

/**
 * The implementation of list cell to present EditableSceneAppState.
 *
 * @author JavaSaBr
 */
public class AppStateListCell extends TextFieldListCell<EditableSceneAppState> {

    @NotNull
    private final AppStateList stateList;

    public AppStateListCell(final @NotNull AppStateList stateList) {
        this.stateList = stateList;
    }
}
