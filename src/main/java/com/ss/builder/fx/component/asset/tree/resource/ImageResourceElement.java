package com.ss.builder.fx.component.asset.tree.resource;

import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.tooltip.ImagePreview;
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
    @FxThread
    public @Nullable Tooltip createToolTip() {
        return new ImagePreview(getFile());
    }
}
