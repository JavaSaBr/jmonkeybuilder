package com.ss.builder.ui.component.virtual.tree.resource;

import static com.ss.rlib.common.util.ClassUtils.unsafeCast;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.component.virtual.tree.VirtualResourceTree;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.common.util.array.Array;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The base implementation of a virtual resource.
 *
 * @author JavaSaBr
 */
public abstract class VirtualResourceElement<T> implements Comparable<VirtualResourceElement<?>> {

    /**
     * The logger.
     */
    @NotNull
    protected static final Logger LOGGER = LoggerManager.getLogger(VirtualResourceElement.class);

    /**
     * The virtual resource tree.
     */
    @NotNull
    protected final VirtualResourceTree resourceTree;

    /**
     * The resource object.
     */
    @NotNull
    protected final T object;

    public VirtualResourceElement(@NotNull final VirtualResourceTree resourceTree, @NotNull final T object) {
        this.resourceTree = resourceTree;
        this.object = object;
    }

    /**
     * Get the type of this resource element.
     *
     * @return the type of this resource element.
     */
    @FromAnyThread
    public @NotNull Class<T> getType() {
        return unsafeCast(object.getClass());
    }

    /**
     * Get the path of this resource.
     *
     * @return the path of this resource.
     */
    @FromAnyThread
    public @NotNull String getPath() {
        return resourceTree.getPath(object);
    }

    /**
     * Get the name of this resource.
     *
     * @return the name of this resource.
     */
    @FromAnyThread
    public @NotNull String getName() {
        return resourceTree.getName(object);
    }

    /**
     * Get the resource object.
     *
     * @return the resource object.
     */
    @FromAnyThread
    public @NotNull T getObject() {
        return object;
    }

    /**
     * Add a child to this element.
     *
     * @param child the new child.
     */
    @FromAnyThread
    public void addChild(@NotNull final VirtualResourceElement<?> child) {

    }

    /**
     * Get the list of children of this element.
     *
     * @return list of children resource elements.
     */
    @FromAnyThread
    public @Nullable Array<VirtualResourceElement<?>> getChildren() {
        return null;
    }

    /**
     * Check this element to has children.
     *
     * @return true if this element has children.
     */
    @FromAnyThread
    public boolean hasChildren() {
        return false;
    }

    @Override
    public int compareTo(@NotNull final VirtualResourceElement<?> o) {
        return 0;
    }

    @Override
    public String toString() {
        return object.toString();
    }
}
