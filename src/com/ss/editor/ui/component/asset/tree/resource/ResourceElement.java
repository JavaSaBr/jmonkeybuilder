package com.ss.editor.ui.component.asset.tree.resource;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

import rlib.logging.Logger;
import rlib.logging.LoggerManager;
import rlib.util.array.Array;

/**
 * The base implementation of a resource.
 *
 * @author JavaSaBr
 */
public abstract class ResourceElement implements Comparable<ResourceElement> {

    protected static final Logger LOGGER = LoggerManager.getLogger(ResourceElement.class);

    /**
     * The reference to the file.
     */
    protected final Path file;

    public ResourceElement(final Path file) {
        this.file = file;
    }

    /**
     * @return the reference to the file.
     */
    public Path getFile() {
        return file;
    }

    /**
     * @return list of children resource elements.
     */
    public Array<ResourceElement> getChildren(@NotNull final Array<String> extensionFilter) {
        return null;
    }

    /**
     * @return true if this element has children.
     */
    public boolean hasChildren(@NotNull final Array<String> extensionFilter) {
        return false;
    }

    @Override
    public int compareTo(@Nullable final ResourceElement other) {

        final Path file = getFile();
        final Path otherFile = other.getFile();

        return file.getNameCount() - otherFile.getNameCount();
    }

    @Override
    public boolean equals(final Object other) {

        if (this == other) return true;
        if (other == null || !(other instanceof ResourceElement)) return false;

        ResourceElement that = (ResourceElement) other;

        return !(file != null ? !file.equals(that.file) : that.file != null);
    }

    @Override
    public int hashCode() {
        return file != null ? file.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ResourceElement{" +
                "file=" + file +
                '}';
    }
}
