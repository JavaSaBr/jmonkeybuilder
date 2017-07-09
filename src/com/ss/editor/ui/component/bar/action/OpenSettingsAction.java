package com.ss.editor.ui.component.bar.action;

import com.ss.editor.JFXApplication;
import com.ss.editor.Messages;
import com.ss.editor.ui.dialog.SettingsDialog;
import com.ss.editor.ui.scene.EditorFXScene;
import javafx.scene.control.MenuItem;

/**
 * The action for opening the dialog with settings.
 *
 * @author JavaSaBr
 */
public class OpenSettingsAction extends MenuItem {

    private static final JFXApplication JFX_APPLICATION = JFXApplication.getInstance();

    /**
     * Instantiates a new Open settings action.
     */
    public OpenSettingsAction() {
        super(Messages.EDITOR_MENU_OTHER_SETTINGS);
        setOnAction(event -> process());
    }

    /**
     * The process of opening.
     */
    private void process() {

        final EditorFXScene scene = JFX_APPLICATION.getScene();

        final SettingsDialog dialog = new SettingsDialog();
        dialog.show(scene.getWindow());
    }
}
