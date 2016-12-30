package com.ss.editor.ui.component.asset.tree.context.menu.action;

import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.RequestedOpenFileEvent;

import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;

/**
 * The action to open a file.
 *
 * @author JavaSaBr
 */
public class OpenFileAction extends MenuItem {

    private static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();

    /**
     * The action element.
     */
    private final ResourceElement element;

    public OpenFileAction(final ResourceElement element) {
        this.element = element;
        setText(Messages.ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_OPEN_FILE);
        setOnAction(event -> processOpen());
        setGraphic(new ImageView(Icons.VIEW_16));
    }

    /**
     * Process of opening.
     */
    private void processOpen() {

        final RequestedOpenFileEvent event = new RequestedOpenFileEvent();
        event.setFile(element.getFile());

        FX_EVENT_MANAGER.notify(event);
    }
}
