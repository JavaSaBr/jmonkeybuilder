package com.ss.builder.fx.component.bar.action;

import com.ss.builder.Messages;
import com.ss.builder.Messages;
import com.ss.builder.fx.dialog.plugin.PluginsDialog;
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
        new PluginsDialog().show();
    }
}
