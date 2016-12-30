package com.ss.editor.ui.component.asset.tree.context.menu.action;

import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.component.creator.FileCreatorDescription;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.RequestedCreateFileEvent;

import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;

/**
 * The action for creating a new file.
 *
 * @author JavaSaBr
 */
public class NewFileByCreatorAction extends MenuItem {

    private static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();

    /**
     * The node element.
     */
    private final ResourceElement element;

    /**
     * The creator description.
     */
    private final FileCreatorDescription description;

    public NewFileByCreatorAction(final ResourceElement element, final FileCreatorDescription description) {
        this.element = element;
        this.description = description;
        setText(description.getFileDescription());
        setOnAction(event -> processCreate());
        setGraphic(new ImageView(Icons.NEW_FILE_16));
    }

    /**
     * Handle creating.
     */
    private void processCreate() {

        final RequestedCreateFileEvent event = new RequestedCreateFileEvent();
        event.setFile(element.getFile());
        event.setDescription(description);

        FX_EVENT_MANAGER.notify(event);
    }
}
