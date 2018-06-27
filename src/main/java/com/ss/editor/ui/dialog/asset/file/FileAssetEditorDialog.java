package com.ss.editor.ui.dialog.asset.file;

import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.component.asset.tree.resource.FolderResourceElement;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import javafx.scene.control.Label;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * The implementation of the {@link AssetEditorDialog} for choosing the {@link Path} from asset.
 *
 * @author JavaSaBr
 */
public class FileAssetEditorDialog extends AssetEditorDialog<Path> {

    public FileAssetEditorDialog(@NotNull Consumer<Path> consumer) {
        super(consumer);
    }

    public FileAssetEditorDialog(@NotNull Consumer<Path> consumer, @Nullable Validator<Path> validator) {
        super(consumer, validator);
    }

    @Override
    @FxThread
    protected void processOpen(@NotNull ResourceElement element) {
        super.processOpen(element);
        getConsumer().accept(element.getFile());
    }

    @Override
    @FxThread
    protected @Nullable Path getObject(@NotNull ResourceElement element) {
        return element.getFile();
    }

    @Override
    @FxThread
    protected void validate(@NotNull Label warningLabel, @Nullable ResourceElement element) {
        super.validate(warningLabel, element);

        var validator = getValidator();
        var visible = warningLabel.isVisible();

        if (!visible && element instanceof FolderResourceElement) {
            warningLabel.setText(Messages.ASSET_EDITOR_DIALOG_WARNING_SELECT_FILE);
            warningLabel.setVisible(true);
        } else if (validator == null) {
            warningLabel.setVisible(false);
        }
    }
}
