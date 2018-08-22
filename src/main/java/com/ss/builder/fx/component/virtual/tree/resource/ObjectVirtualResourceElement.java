package com.ss.builder.ui.component.virtual.tree.resource;

import com.ss.editor.ui.component.virtual.tree.VirtualResourceTree;
import org.jetbrains.annotations.NotNull;

/**
 * The default implementation of the virtual resource element.
 *
 * @param <T> the type of presented object.
 * @author JavaSaBr
 */
public class ObjectVirtualResourceElement<T> extends VirtualResourceElement<T> {

    public ObjectVirtualResourceElement(@NotNull final VirtualResourceTree<T> resourceTree, @NotNull final T object) {
        super(resourceTree, object);
    }
}
