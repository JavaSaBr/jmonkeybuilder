package com.ss.editor.ui.component.asset.tree.context.menu.action;

import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.asset.tree.resource.FileResourceElement;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.dialog.imports.model.ModelImportDialog;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to import an model to current asset folder.
 *
 * @author JavaSaBr
 */
public class ImportModelFileAction extends FileAction {

    public ImportModelFileAction(@NotNull final ResourceElement element) {
        super(element);
    }

    @FXThread
    @Override
    protected void execute(@Nullable final ActionEvent event) {
        super.execute(event);

        final ResourceElement element = getElement();

        final ModelImportDialog dialog = new ModelImportDialog();
        if (element instanceof FileResourceElement) {
            dialog.start(element.getFile().getParent());
        } else {
            dialog.start(element.getFile());
        }
    }

    @Override
    @FXThread
    protected @NotNull String getName() {
        return Messages.ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_IMPORT_MODEL;
    }

    @Override
    @FXThread
    protected @Nullable Image getIcon() {
        return Icons.IMPORT_16;
    }
}
