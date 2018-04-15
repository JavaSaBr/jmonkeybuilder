package com.ss.editor.ui.component.asset.tree.resource;

import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.common.util.array.Array;
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

    public ResourceElement(@NotNull Path file) {
        this.file = file;
    }

    /**
     * Create a tooltip to preview this element.
     *
     * @return the tooltip.
     */
    @FxThread
    public @Nullable Tooltip createToolTip() {
        return null;
    }

    /**
     * Get the file.
     *
     * @return the reference to the file.
     */
    @FromAnyThread
    public @NotNull Path getFile() {
        return file;
    }

    /**
     * Get children of this file.
     *
     * @param extensionFilter the extension filter.
     * @param onlyFolders     true if needs only folders.
     * @return list of children resource elements.
     */
    @FromAnyThread
    public @Nullable Array<ResourceElement> getChildren(@NotNull Array<String> extensionFilter, boolean onlyFolders) {
        return null;
    }

    /**
     * Return true if this element has children.
     *
     * @param extensionFilter the extension filter.
     * @param onlyFolders     true if needs only folders.
     * @return true if this element has children.
     */
    @FromAnyThread
    public boolean hasChildren(@NotNull Array<String> extensionFilter, boolean onlyFolders) {
        return false;
    }

    @Override
    public int compareTo(@Nullable ResourceElement other) {
        if (other == null) return -1;
        var file = getFile();
        var otherFile = other.getFile();
        return file.getNameCount() - otherFile.getNameCount();
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o instanceof Path) return file.equals(o);
        var that = (ResourceElement) o;
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
