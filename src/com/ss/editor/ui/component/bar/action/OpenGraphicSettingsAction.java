package com.ss.editor.ui.component.bar.action;

import com.ss.editor.JFXApplication;
import com.ss.editor.Messages;
import com.ss.editor.ui.dialog.GraphicSetingsDialog;
import com.ss.editor.ui.scene.EditorFXScene;

import javafx.scene.control.MenuItem;

/**
 * The action for opening the dialog with graphics settings.
 *
 * @author JavaSaBr.
 */
public class OpenGraphicSettingsAction extends MenuItem {

    private static final JFXApplication JFX_APPLICATION = JFXApplication.getInstance();

    public OpenGraphicSettingsAction() {
        super(Messages.EDITOR_BAR_SETTINGS_GRAPHICS);
        setOnAction(event -> process());
    }

    /**
     * The process of opening.
     */
    private void process() {

        final EditorFXScene scene = JFX_APPLICATION.getScene();

        final GraphicSetingsDialog dialog = new GraphicSetingsDialog();
        dialog.show(scene.getWindow());
    }
}
