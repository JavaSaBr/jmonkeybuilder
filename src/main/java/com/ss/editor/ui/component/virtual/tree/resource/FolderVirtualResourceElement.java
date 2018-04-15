package com.ss.editor.ui.component.virtual.tree.resource;

import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.component.virtual.tree.VirtualResourceTree;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of fake folder resource element.
 *
 * @author JavaSaBr
 */
public class FolderVirtualResourceElement extends VirtualResourceElement<String> {

    /**
     * The list of children.
     */
    @NotNull
    private final Array<VirtualResourceElement<?>> children;

    public FolderVirtualResourceElement(@NotNull final VirtualResourceTree<?> resourceTree, @NotNull final String path) {
        super(resourceTree, path);
        this.children = ArrayFactory.newArray(VirtualResourceElement.class);
    }

    @Override
    @FromAnyThread
    public void addChild(@NotNull final VirtualResourceElement<?> child) {
        this.children.add(child);
    }

    @Override
    @FromAnyThread
    public @Nullable Array<VirtualResourceElement<?>> getChildren() {
        return children;
    }

    @Override
    @FromAnyThread
    public boolean hasChildren() {
        return !children.isEmpty();
    }
}
