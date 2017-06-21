package com.ss.editor.ui.dialog.asset;

import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import javafx.scene.control.Label;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The implementation of the {@link AssetEditorDialog} to choose the {@link Path} from asset.
 *
 * @author JavaSaBr
 */
public class FolderAssetEditorDialog extends FileAssetEditorDialog {

    /**
     * Instantiates a new Folder asset editor dialog.
     *
     * @param consumer the consumer
     */
    public FolderAssetEditorDialog(@NotNull final Consumer<Path> consumer) {
        super(consumer);
        setOnlyFolders(true);
    }

    /**
     * Instantiates a new Folder asset editor dialog.
     *
     * @param consumer  the consumer
     * @param validator the validator
     */
    public FolderAssetEditorDialog(@NotNull final Consumer<Path> consumer,
                                   @Nullable final Function<Path, String> validator) {
        super(consumer, validator);
        setOnlyFolders(true);
    }
}
