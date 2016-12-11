package com.ss.editor.ui.component.bar.action;

import com.ss.editor.JFXApplication;
import com.ss.editor.Messages;

import javafx.scene.control.Button;

/**
 * The action for closing the Editor.
 *
 * @author JavaSaBr.
 */
public class CloseEditorAction extends Button {

    private static final JFXApplication APPLICATION = JFXApplication.getInstance();

    public CloseEditorAction() {
        super(Messages.EDITOR_BAR_ASSET_CLOSE_EDITOR);
        setOnAction(event -> APPLICATION.onExit());
    }
}
