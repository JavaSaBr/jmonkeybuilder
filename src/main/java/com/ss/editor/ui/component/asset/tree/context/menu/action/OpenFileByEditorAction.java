package com.ss.editor.ui.component.asset.tree.context.menu.action;

import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.component.editor.EditorDescription;
import com.ss.editor.ui.event.impl.RequestedOpenFileEvent;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to open a file by an editor.
 *
 * @author JavaSaBr
 */
class OpenFileByEditorAction extends FileAction {

    /**
     * The editor description.
     */
    @NotNull
    private final EditorDescription description;

    public OpenFileByEditorAction(@NotNull final ResourceElement element,
                                  @NotNull final EditorDescription description) {
        super(element);
        this.description = description;

        setText(description.getEditorName());

        final Image icon = description.getIcon();

        if (icon != null) {
            setGraphic(new ImageView(icon));
        }
    }

    @Override
    protected void execute(@Nullable final ActionEvent event) {
        super.execute(event);

        final RequestedOpenFileEvent newEvent = new RequestedOpenFileEvent();
        newEvent.setFile(getElement().getFile());
        newEvent.setDescription(description);

        FX_EVENT_MANAGER.notify(newEvent);
    }
}
