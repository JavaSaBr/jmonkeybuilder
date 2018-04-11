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
import java.util.function.Function;

/**
 * The implementation of the {@link AssetEditorDialog} for choosing the {@link Path} from asset.
 *
 * @author JavaSaBr
 */
public class FileAssetEditorDialog extends AssetEditorDialog<Path> {

    public FileAssetEditorDialog(@NotNull final Consumer<Path> consumer) {
        super(consumer);
    }

    public FileAssetEditorDialog(@NotNull final Consumer<Path> consumer, @Nullable final Function<Path, String> validator) {
        super(consumer, validator);
    }

    @Override
    @FxThread
    protected void processOpen(@NotNull final ResourceElement element) {
        super.processOpen(element);
        final Consumer<Path> consumer = getConsumer();
        consumer.accept(element.getFile());
    }

    @Override
    @FxThread
    protected @Nullable Path getObject(@NotNull final ResourceElement element) {
        return element.getFile();
    }

    @Override
    @FxThread
    protected void validate(@NotNull final Label warningLabel, @Nullable final ResourceElement element) {
        super.validate(warningLabel, element);

        final Function<@NotNull Path, @Nullable String> validator = getValidator();
        final boolean visible = warningLabel.isVisible();

        if (!visible && element instanceof FolderResourceElement) {
            warningLabel.setText(Messages.ASSET_EDITOR_DIALOG_WARNING_SELECT_FILE);
            warningLabel.setVisible(true);
        } else if (validator == null) {
            warningLabel.setVisible(false);
        }
    }
}
