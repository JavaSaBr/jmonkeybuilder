package com.ss.editor.ui.component.bar.action;

import com.ss.editor.JFXApplication;
import com.ss.editor.Messages;
import com.ss.editor.ui.dialog.about.AboutDialog;
import com.ss.editor.ui.scene.EditorFXScene;
import javafx.scene.control.MenuItem;

/**
 * The action to open the About dialog.
 *
 * @author JavaSaBr
 */
public class AboutAction extends MenuItem {

    private static final JFXApplication JFX_APPLICATION = JFXApplication.getInstance();

    /**
     * Instantiates a new AboutAction.
     */
    public AboutAction() {
        super(Messages.EDITOR_MENU_HELP_ABOUT);
        setOnAction(event -> process());
    }

    /**
     * Open the dialog.
     */
    private void process() {

        final EditorFXScene scene = JFX_APPLICATION.getScene();

        final AboutDialog dialog = new AboutDialog();
        dialog.show(scene.getWindow());
    }
}
