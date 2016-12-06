package com.ss.editor.ui.component.bar.action;

import com.ss.editor.JFXApplication;
import com.ss.editor.Messages;
import com.ss.editor.ui.dialog.OtherSettingsDialog;
import com.ss.editor.ui.scene.EditorFXScene;

import javafx.scene.control.MenuItem;

/**
 * The action for opening the dialog with other settings.
 *
 * @author JavaSaBr.
 */
public class OpenOtherSettingsAction extends MenuItem {

    private static final JFXApplication JFX_APPLICATION = JFXApplication.getInstance();

    public OpenOtherSettingsAction() {
        super(Messages.EDITOR_BAR_SETTINGS_OTHER);
        setOnAction(event -> process());
    }

    /**
     * The process of opening.
     */
    private void process() {

        final EditorFXScene scene = JFX_APPLICATION.getScene();

        final OtherSettingsDialog dialog = new OtherSettingsDialog();
        dialog.show(scene.getWindow());
    }
}
