package com.ss.editor.ui.component.asset.tree.context.menu.action;

import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.component.editor.EditorDescription;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.RequestedOpenFileEvent;

import org.jetbrains.annotations.NotNull;

import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * The action to open a file by an editor.
 *
 * @author JavaSaBr
 */
class OpenFileByEditorAction extends MenuItem {

    @NotNull
    private static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();

    /**
     * The action element.
     */
    @NotNull
    private final ResourceElement element;

    /**
     * The editor description.
     */
    @NotNull
    private final EditorDescription description;

    /**
     * Instantiates a new Open file by editor action.
     *
     * @param element     the element
     * @param description the description
     */
    OpenFileByEditorAction(@NotNull final ResourceElement element, @NotNull final EditorDescription description) {
        this.element = element;
        this.description = description;

        setText(description.getEditorName());
        setOnAction(event -> processOpen());

        final Image icon = description.getIcon();

        if (icon != null) {
            setGraphic(new ImageView(icon));
        }
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
