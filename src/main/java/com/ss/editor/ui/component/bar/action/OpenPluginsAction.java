package com.ss.editor.ui.component.bar.action;

import com.ss.editor.Messages;
import com.ss.editor.ui.dialog.plugin.PluginsDialog;
import javafx.scene.control.MenuItem;

/**
 * The action to open plugins dialog.
 *
 * @author JavaSaBr
 */
public class OpenPluginsAction extends MenuItem {

    public OpenPluginsAction() {
        super(Messages.EDITOR_MENU_OTHER_PLUGINS);
        setOnAction(event -> process());
    }

    /**
     * Open the plugin dialog.
     */
    private void process() {
        final PluginsDialog dialog = new PluginsDialog();
        dialog.show();
    }
}
