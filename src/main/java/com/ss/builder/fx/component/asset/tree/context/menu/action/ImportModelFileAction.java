package com.ss.builder.fx.component.asset.tree.context.menu.action;

import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.Icons;
import com.ss.builder.fx.component.asset.tree.resource.FileResourceElement;
import com.ss.builder.fx.component.asset.tree.resource.ResourceElement;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.Icons;
import com.ss.builder.fx.component.asset.tree.resource.FileResourceElement;
import com.ss.builder.fx.component.asset.tree.resource.ResourceElement;
import com.ss.builder.fx.dialog.imports.model.ModelImportDialog;
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

    @FxThread
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
    @FxThread
    protected @NotNull String getName() {
        return Messages.ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_IMPORT_MODEL;
    }

    @Override
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.IMPORT_16;
    }
}
