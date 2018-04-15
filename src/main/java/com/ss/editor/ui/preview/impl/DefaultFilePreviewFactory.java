package com.ss.editor.ui.preview.impl;

import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.preview.FilePreview;
import com.ss.editor.ui.preview.FilePreviewFactory;
import com.ss.rlib.common.util.array.Array;
import org.jetbrains.annotations.NotNull;

/**
 * The default implementation of {@link FilePreviewFactory} with default file previews.
 *
 * @author JavaSaBr
 */
public class DefaultFilePreviewFactory implements FilePreviewFactory {

    private static final DefaultFilePreviewFactory INSTANCE = new DefaultFilePreviewFactory();

    public static @NotNull DefaultFilePreviewFactory getInstance() {
        return INSTANCE;
    }

    @Override
    @FxThread
    public void createFilePreviews(@NotNull final Array<FilePreview> result) {
        result.add(new TextFilePreview());
        result.add(new ImageFilePreview());
        result.add(new JmeObjectFilePreview());
    }
}
