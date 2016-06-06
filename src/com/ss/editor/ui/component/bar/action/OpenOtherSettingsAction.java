package com.ss.editor.ui.component.bar.action;

import com.ss.editor.Editor;
import com.ss.editor.Messages;
import com.ss.editor.ui.dialog.OtherSettingsDialog;
import com.ss.editor.ui.scene.EditorFXScene;

import javafx.scene.control.MenuItem;

/**
 * Реализация действия по открытию настроек программы.
 *
 * @author Ronn
 */
public class OpenOtherSettingsAction extends MenuItem {

    private static final Editor EDITOR = Editor.getInstance();

    public OpenOtherSettingsAction() {
        super(Messages.EDITOR_BAR_SETTINGS_OTHER);
        setOnAction(event -> process());
    }

    /**
     * Процесс выбора папки Asset.
     */
    private void process() {

        final EditorFXScene scene = EDITOR.getScene();

        final OtherSettingsDialog dialog = new OtherSettingsDialog();
        dialog.show(scene.getWindow());
    }
}
