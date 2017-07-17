package com.ss.editor.ui.component.bar.action;

import com.ss.editor.Messages;
import javafx.application.Platform;
import javafx.scene.control.MenuItem;

/**
 * The action for opening the dialog with settings.
 *
 * @author JavaSaBr
 */
public class ExitAction extends MenuItem {

    /**
     * Instantiates a new Open settings action.
     */
    public ExitAction() {
        super(Messages.EDITOR_MENU_FILE_EXIT);
        setOnAction(event -> process());
    }

    /**
     * The process of opening.
     */
    private void process() {
        Platform.exit();
    }
}
