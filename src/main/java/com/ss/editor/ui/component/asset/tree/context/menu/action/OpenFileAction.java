package com.ss.editor.ui.component.asset.tree.context.menu.action;

import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.event.FxEventManager;
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

    public OpenFileAction(@NotNull ResourceElement element) {
        super(element);
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_OPEN_FILE;
    }

    @Override
    @FxThread
    protected void execute(@Nullable ActionEvent event) {
        super.execute(event);

        FxEventManager.getInstance()
                .notify(new RequestedOpenFileEvent(getFile()));
    }
}
