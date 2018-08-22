package com.ss.builder.fx.dialog.asset.file;

import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.component.asset.tree.resource.ResourceElement;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.component.asset.tree.resource.ResourceElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * The implementation of the {@link AssetEditorDialog} to choose the {@link Path} from asset.
 *
 * @author JavaSaBr
 */
public class FolderAssetEditorDialog extends AssetEditorDialog<Path> {

    public FolderAssetEditorDialog(@NotNull Consumer<Path> consumer) {
        super(consumer);
        setOnlyFolders(true);
    }

    public FolderAssetEditorDialog(@NotNull Consumer<Path> consumer, @Nullable Validator<Path> validator) {
        super(consumer, validator);
        setOnlyFolders(true);
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
}
