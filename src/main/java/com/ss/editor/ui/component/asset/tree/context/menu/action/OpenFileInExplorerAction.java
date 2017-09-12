package com.ss.editor.ui.component.asset.tree.context.menu.action;

import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.manager.ResourceManager;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.util.UIUtils;
import com.ss.rlib.util.array.Array;
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

    @Override
    protected @Nullable Image getIcon() {
        return Icons.EXPLORER_16;
    }

    @Override
    protected @NotNull String getName() {
        return Messages.ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_OPEN_FILE_BY_SYSTEM_EXPLORER;
    }

    @Override
    protected void execute(@Nullable final ActionEvent event) {
        super.execute(event);

        final ResourceManager resourceManager = ResourceManager.getInstance();
        final Array<String> shaderNodes = resourceManager.getAvailableResources(FileExtensions.JME_SHADER_NODE);

        UIUtils.openResourceAssetDialog(s -> System.out.println(s), shaderNodes);

        //EditorUtil.openFileInSystemExplorer(getElement().getFile());
    }
}
