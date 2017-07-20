package com.ss.editor.ui.component.asset.tree;

import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.component.asset.tree.context.menu.filler.AssetTreeContextMenuFiller;
import com.ss.editor.ui.component.asset.tree.context.menu.filler.impl.FileAssetTreeContextMenuFiller;
import com.ss.editor.ui.component.asset.tree.context.menu.filler.impl.ResourceAssetTreeContextMenuFiller;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;

/**
 * The registry class to collect all available context menu filler.
 *
 * @author JavaSaBr
 */
public class AssetTreeContextMenuFillerRegistry {

    @NotNull
    private static final AssetTreeContextMenuFillerRegistry INSTANCE = new AssetTreeContextMenuFillerRegistry();

    @NotNull
    public static AssetTreeContextMenuFillerRegistry getInstance() {
        return INSTANCE;
    }

    @NotNull
    private final Array<AssetTreeContextMenuFiller> fillers;

    private AssetTreeContextMenuFillerRegistry() {
        this.fillers = ArrayFactory.newArray(AssetTreeContextMenuFiller.class);
        register(new FileAssetTreeContextMenuFiller());
        register(new ResourceAssetTreeContextMenuFiller());
    }

    /**
     * Register a new context menu filler.
     *
     * @param assetTreeContextMenuFiller the context menu filler.
     */
    @FromAnyThread
    public void register(@NotNull final AssetTreeContextMenuFiller assetTreeContextMenuFiller) {
        this.fillers.add(assetTreeContextMenuFiller);
    }

    /**
     * Gets the list of available context menu fillers.
     *
     * @return the list of context menu filler.
     */
    @NotNull
    public Array<AssetTreeContextMenuFiller> getFillers() {
        return fillers;
    }
}
