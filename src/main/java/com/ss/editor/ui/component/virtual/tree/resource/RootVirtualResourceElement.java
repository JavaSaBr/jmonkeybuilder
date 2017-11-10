package com.ss.editor.ui.component.virtual.tree.resource;

import com.ss.editor.ui.component.virtual.tree.VirtualResourceTree;
import org.jetbrains.annotations.NotNull;

/**
 * The root implementation of the virtual resource element.
 *
 * @author JavaSaBr
 */
public class RootVirtualResourceElement extends FolderVirtualResourceElement {

    public RootVirtualResourceElement(@NotNull final VirtualResourceTree resourceTree) {
        super(resourceTree, "/");
    }
}
