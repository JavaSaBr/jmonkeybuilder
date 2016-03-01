package com.ss.editor.ui.component.bar.action;

import com.ss.editor.Editor;
import com.ss.editor.Messages;
import com.ss.editor.ui.dialog.GraphicsDialog;
import com.ss.editor.ui.scene.EditorFXScene;

import javafx.scene.control.MenuItem;

/**
 * Реализация действия по открытию настроек программы.
 *
 * @author Ronn
 */
public class OpenSettingsAction extends MenuItem {

    private static final Editor EDITOR = Editor.getInstance();

    public OpenSettingsAction() {
        super(Messages.EDITOR_BAR_SETTINGS_GRAPHICS);
        setOnAction(event -> process());
    }

    /**
     * Процесс выбора папки Asset.
     */
    private void process() {

        final EditorFXScene scene = EDITOR.getScene();

        final GraphicsDialog dialog = new GraphicsDialog();
        dialog.show(scene.getWindow());
    }
}
