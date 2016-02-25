package com.ss.editor.ui.component.asset.tree.resource;

import java.nio.file.Path;

import rlib.logging.Logger;
import rlib.logging.LoggerManager;
import rlib.util.array.Array;

/**
 * Базовая реализация элемента ресурса в дереве.
 *
 * @author Ronn
 */
public abstract class ResourceElement implements Comparable<ResourceElement> {

    protected static final Logger LOGGER = LoggerManager.getLogger(ResourceElement.class);

    /**
     * Ссылка на файл.
     */
    protected final Path file;

    public ResourceElement(final Path file) {
        this.file = file;
    }

    /**
     * @return ссылка на файл.
     */
    public Path getFile() {
        return file;
    }

    /**
     * @return список элементов, содержащихся в этом.
     */
    public Array<ResourceElement> getChildren() {
        return null;
    }

    /**
     * @return есть ли у этого элемента дочерние.
     */
    public boolean hasChildren() {
        return false;
    }

    @Override
    public int compareTo(final ResourceElement other) {

        final Path file = getFile();
        final Path otherFile = other.getFile();

        return file.getNameCount() - otherFile.getNameCount();
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResourceElement that = (ResourceElement) o;

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
