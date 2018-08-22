package com.ss.builder.fx.preview;

import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.FxThread;
import com.ss.rlib.common.util.array.Array;
import org.jetbrains.annotations.NotNull;

/**
 * The factory of file previews.
 *
 * @author JavaSaBr
 */
public interface FilePreviewFactory {

    /**
     * Add to the result list new file previews implementations.
     *
     * @param result the list to store new implementations.
     */
    @FxThread
    void createFilePreviews(@NotNull Array<FilePreview> result);
}
