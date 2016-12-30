package com.ss.editor.ui.component.asset.tree.context.menu.action;

import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.component.editor.EditorDescription;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.RequestedOpenFileEvent;

import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;

/**
 * The action to open a file by an editor.
 *
 * @author JavaSaBr
 */
public class OpenFileByEditorAction extends MenuItem {

    private static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();

    /**
     * The action element.
     */
    private final ResourceElement element;

    /**
     * The editor description.
     */
    private final EditorDescription description;

    public OpenFileByEditorAction(final ResourceElement element, final EditorDescription description) {
        this.element = element;
        this.description = description;
        setText(description.getEditorName());
        setOnAction(event -> processOpen());
        setGraphic(new ImageView(Icons.VIEW_16));
    }

    /**
     * Process of opening.
     */
    private void processOpen() {

        final RequestedOpenFileEvent event = new RequestedOpenFileEvent();
        event.setFile(element.getFile());
        event.setDescription(description);

        FX_EVENT_MANAGER.notify(event);
    }
}
