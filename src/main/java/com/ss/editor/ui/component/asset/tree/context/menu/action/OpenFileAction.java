package com.ss.editor.ui.component.asset.tree.context.menu.action;

import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.event.impl.RequestedOpenFileEvent;
import javafx.event.ActionEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to open a file.
 *
 * @author JavaSaBr
 */
public class OpenFileAction extends FileAction {

    public OpenFileAction(@NotNull final ResourceElement element) {
        super(element);
    }

    @FXThread
    @Override
    protected @NotNull String getName() {
        return Messages.ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_OPEN_FILE;
    }

    @FXThread
    @Override
    protected void execute(@Nullable final ActionEvent event) {
        super.execute(event);

        final RequestedOpenFileEvent newEvent = new RequestedOpenFileEvent();
        newEvent.setFile(getElement().getFile());

        FX_EVENT_MANAGER.notify(newEvent);
    }
}
