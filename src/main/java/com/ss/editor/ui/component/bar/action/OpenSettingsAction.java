package com.ss.editor.ui.component.bar.action;

import com.ss.editor.Messages;
import com.ss.editor.ui.dialog.SettingsDialog;
import javafx.scene.control.MenuItem;

/**
 * The action to open the dialog with settings.
 *
 * @author JavaSaBr
 */
public class OpenSettingsAction extends MenuItem {

    /**
     * Instantiates a new OpenSettingsAction.
     */
    public OpenSettingsAction() {
        super(Messages.EDITOR_MENU_OTHER_SETTINGS);
        setOnAction(event -> process());
    }

    /**
     * Open the dialog.
     */
    private void process() {
        final SettingsDialog dialog = new SettingsDialog();
        dialog.show();
    }
}
