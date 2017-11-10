package com.ss.editor.ui.component.asset.tree.resource;

import com.ss.editor.annotation.FXThread;
import com.ss.editor.ui.tooltip.ImagePreview;
import javafx.scene.control.Tooltip;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

/**
 * The presentation of an image.
 *
 * @author JavaSaBr
 */
public class ImageResourceElement extends FileResourceElement {

    public ImageResourceElement(@NotNull final Path file) {
        super(file);
    }

    @Override
    @FXThread
    public @Nullable Tooltip createToolTip() {
        return new ImagePreview(getFile());
    }
}
