package com.ss.editor.ui.component.bar.action;

import com.ss.editor.JFXApplication;
import com.ss.editor.Messages;
import com.ss.editor.ui.dialog.plugin.PluginsDialog;
import com.ss.editor.ui.scene.EditorFXScene;
import javafx.scene.control.MenuItem;
import org.jetbrains.annotations.NotNull;

/**
 * The action to open plugins dialog.
 *
 * @author JavaSaBr
 */
public class OpenPluginsAction extends MenuItem {

    @NotNull
    private static final JFXApplication JFX_APPLICATION = JFXApplication.getInstance();

    public OpenPluginsAction() {
        super(Messages.EDITOR_MENU_OTHER_PLUGINS);
        setOnAction(event -> process());
    }

    /**
     * The process of opening.
     */
    private void process() {

        final EditorFXScene scene = JFX_APPLICATION.getScene();

        final PluginsDialog dialog = new PluginsDialog();
        dialog.show(scene.getWindow());
    }
}
