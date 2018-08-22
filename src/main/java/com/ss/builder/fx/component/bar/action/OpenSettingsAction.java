package com.ss.builder.ui.component.bar.action;

import com.ss.builder.Messages;
import com.ss.builder.ui.dialog.SettingsDialog;
import com.ss.editor.Messages;
import com.ss.editor.ui.dialog.SettingsDialog;
import javafx.scene.control.MenuItem;

/**
 * The action to open the dialog with settings.
 *
 * @author JavaSaBr
 */
public class OpenSettingsAction extends MenuItem {

    public OpenSettingsAction() {
        super(Messages.EDITOR_MENU_OTHER_SETTINGS);
        setOnAction(event -> process());
    }

    /**
     * Open the dialog.
     */
    private void process() {
        new SettingsDialog().show();
    }
}
