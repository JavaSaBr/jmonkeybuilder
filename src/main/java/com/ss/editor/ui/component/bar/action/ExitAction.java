package com.ss.editor.ui.component.bar.action;

import com.ss.editor.Messages;
import javafx.application.Platform;
import javafx.scene.control.MenuItem;

/**
 * The action to exit from this Editor.
 *
 * @author JavaSaBr
 */
public class ExitAction extends MenuItem {

    /**
     * Instantiates a new ExitAction.
     */
    public ExitAction() {
        super(Messages.EDITOR_MENU_FILE_EXIT);
        setOnAction(event -> process());
    }

    /**
     * Exit.
     */
    private void process() {
        Platform.exit();
    }
}
