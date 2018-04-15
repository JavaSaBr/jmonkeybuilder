package com.ss.editor.ui.dialog.asset.virtual;

import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.rlib.common.util.FileUtils;
import com.ss.rlib.common.util.StringUtils;
import com.ss.rlib.common.util.array.Array;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The implementation to work with string resources.
 *
 * @author JavaSaBr
 */
public class StringVirtualAssetEditorDialog extends VirtualAssetEditorDialog<String> {

    @NotNull
    public static final Function<@NotNull String, @Nullable String> DEFAULT_VALIDATOR = resource -> {

        final String extension = FileUtils.getExtension(resource);

        if (StringUtils.isEmpty(extension)) {
            return Messages.ASSET_EDITOR_DIALOG_WARNING_SELECT_FILE;
        }

        return null;
    };

    public StringVirtualAssetEditorDialog(@NotNull final Consumer<String> consumer,
                                          @NotNull final Array<String> resources) {
        this(consumer, DEFAULT_VALIDATOR, resources);
    }

    public StringVirtualAssetEditorDialog(@NotNull final Consumer<String> consumer,
                                          @Nullable final Function<@NotNull String, @Nullable String> validator,
                                          @NotNull final Array<String> resources) {
        super(consumer, validator, resources);
    }

    @Override
    @FromAnyThread
    protected @NotNull Class<String> getObjectsType() {
        return String.class;
    }
}
