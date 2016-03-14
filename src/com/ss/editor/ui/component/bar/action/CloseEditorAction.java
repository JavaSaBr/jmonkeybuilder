package com.ss.editor.ui.component.bar.action;

import com.ss.editor.Editor;
import com.ss.editor.Messages;

import javafx.scene.control.MenuItem;

/**
 * Реализация действия по закрытию редактора.
 *
 * @author Ronn
 */
public class CloseEditorAction extends MenuItem {

    private static final Editor EDITOR = Editor.getInstance();

    public CloseEditorAction() {
        super(Messages.EDITOR_BAR_ASSET_CLOSE_EDITOR);
        setOnAction(event -> EDITOR.requestClose(true));
    }
}
