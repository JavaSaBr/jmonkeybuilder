package com.ss.editor.ui.component.bar.action;

import com.ss.editor.JFXApplication;
import com.ss.editor.Messages;
import com.ss.editor.ui.dialog.GraphicSettingsDialog;
import com.ss.editor.ui.scene.EditorFXScene;

import javafx.scene.control.Button;

/**
 * The action for opening the dialog with graphics settings.
 *
 * @author JavaSaBr.
 */
public class OpenGraphicSettingsAction extends Button {

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

        final GraphicSettingsDialog dialog = new GraphicSettingsDialog();
        dialog.show(scene.getWindow());
    }
}
