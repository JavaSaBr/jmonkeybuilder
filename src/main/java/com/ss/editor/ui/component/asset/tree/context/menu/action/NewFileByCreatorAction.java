package com.ss.editor.ui.component.asset.tree.context.menu.action;

import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.component.creator.FileCreatorDescription;
import com.ss.editor.ui.event.impl.RequestedCreateFileEvent;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action for creating a new file.
 *
 * @author JavaSaBr
 */
class NewFileByCreatorAction extends FileAction {

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
        super(element);
        this.description = description;
        final Image icon = description.getIcon();
        setText(description.getFileDescription());
        setGraphic(new ImageView(icon == null ? Icons.NEW_FILE_16 : icon));
    }

    @Override
    protected void execute(@Nullable final ActionEvent event) {
        super.execute(event);

        final RequestedCreateFileEvent newEvent = new RequestedCreateFileEvent();
        newEvent.setFile(getElement().getFile());
        newEvent.setDescription(description);

        FX_EVENT_MANAGER.notify(newEvent);
    }
}
