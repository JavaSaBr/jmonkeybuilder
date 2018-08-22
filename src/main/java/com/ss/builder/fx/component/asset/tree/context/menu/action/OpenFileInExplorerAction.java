package com.ss.builder.ui.component.asset.tree.context.menu.action;

import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.ui.Icons;
import com.ss.builder.ui.component.asset.tree.resource.ResourceElement;
import com.ss.builder.util.EditorUtils;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.util.EditorUtils;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to open a file in an system explorer.
 *
 * @author JavaSaBr
 */
public class OpenFileInExplorerAction extends FileAction {

    public OpenFileInExplorerAction(@NotNull final ResourceElement element) {
        super(element);
    }

    @FxThread
    @Override
    protected @Nullable Image getIcon() {
        return Icons.EXPLORER_16;
    }

    @FxThread
    @Override
    protected @NotNull String getName() {
        return Messages.ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_OPEN_FILE_BY_SYSTEM_EXPLORER;
    }

    @FxThread
    @Override
    protected void execute(@Nullable final ActionEvent event) {
        super.execute(event);
        EditorUtils.openFileInSystemExplorer(getElement().getFile());
    }
}
