package com.ss.editor.ui.component.asset.tree.resource;

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

    /**
     * Instantiates a new ImageResourceElement.
     *
     * @param file the file
     */
    ImageResourceElement(@NotNull final Path file) {
        super(file);
    }

    @Override
    @Nullable
    public Tooltip createToolTip() {
        return new ImagePreview(getFile());
    }
}
