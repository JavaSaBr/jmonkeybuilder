package com.ss.editor.ui.component.asset.tree.context.menu.action;

import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.component.creator.FileCreatorDescription;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.RequestedCreateFileEvent;

import org.jetbrains.annotations.NotNull;

import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * The action for creating a new file.
 *
 * @author JavaSaBr
 */
class NewFileByCreatorAction extends MenuItem {

    @NotNull
    private static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();

    /**
     * The node element.
     */
    @NotNull
    private final ResourceElement element;

    /**
     * The creator description.
     */
    @NotNull
    private final FileCreatorDescription description;

    /**
     * Instantiates a new New file by creator action.
     *
     * @param element     the element
     * @param description the description
     */
    NewFileByCreatorAction(@NotNull final ResourceElement element, @NotNull final FileCreatorDescription description) {
        this.element = element;
        this.description = description;
        final Image icon = description.getIcon();
        setText(description.getFileDescription());
        setOnAction(event -> processCreate());
        setGraphic(new ImageView(icon == null ? Icons.NEW_FILE_16 : icon));
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
