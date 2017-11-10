package com.ss.editor.ui.component.asset.tree.resource;

import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.rlib.logging.Logger;
import com.ss.rlib.logging.LoggerManager;
import com.ss.rlib.util.array.Array;
import javafx.scene.control.Tooltip;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

/**
 * The base implementation of resource.
 *
 * @author JavaSaBr
 */
public abstract class ResourceElement implements Comparable<ResourceElement> {

    /**
     * The logger.
     */
    @NotNull
    protected static final Logger LOGGER = LoggerManager.getLogger(ResourceElement.class);

    /**
     * The reference to the file.
     */
    @NotNull
    protected final Path file;

    public ResourceElement(@NotNull final Path file) {
        this.file = file;
    }

    /**
     * Create tooltip to preview this element.
     *
     * @return the tooltip.
     */
    @FXThread
    public @Nullable Tooltip createToolTip() {
        return null;
    }

    /**
     * Gets file.
     *
     * @return the reference to the file.
     */
    @FromAnyThread
    public @NotNull Path getFile() {
        return file;
    }

    /**
     * Gets children.
     *
     * @param extensionFilter the extension filter
     * @param onlyFolders     the only folders
     * @return list of children resource elements.
     */
    @FromAnyThread
    public @Nullable Array<ResourceElement> getChildren(@NotNull final Array<String> extensionFilter, final boolean onlyFolders) {
        return null;
    }

    /**
     * Has children boolean.
     *
     * @param extensionFilter the extension filter
     * @param onlyFolders     the only folders
     * @return true if this element has children.
     */
    @FromAnyThread
    public boolean hasChildren(@NotNull final Array<String> extensionFilter, final boolean onlyFolders) {
        return false;
    }

    @Override
    public int compareTo(@Nullable final ResourceElement other) {
        if (other == null) return -1;

        final Path file = getFile();
        final Path otherFile = other.getFile();

        return file.getNameCount() - otherFile.getNameCount();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o instanceof Path) return file.equals(o);
        final ResourceElement that = (ResourceElement) o;
        return file.equals(that.file);
    }

    @Override
    public int hashCode() {
        return file.hashCode();
    }

    @Override
    public String toString() {
        return "ResourceElement{" + "file=" + file + '}';
    }
}
