package com.ss.editor.ui.component.asset.tree;

import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.component.asset.tree.context.menu.filler.AssetTreeMultiContextMenuFiller;
import com.ss.editor.ui.component.asset.tree.context.menu.filler.AssetTreeSingleContextMenuFiller;
import com.ss.editor.ui.component.asset.tree.context.menu.filler.impl.FileAssetTreeSingleContextMenuFiller;
import com.ss.editor.ui.component.asset.tree.context.menu.filler.impl.ResourceAssetTreeSingleContextMenuFiller;
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

    /**
     * The list of single fillers.
     */
    @NotNull
    private final Array<AssetTreeSingleContextMenuFiller> singleFillers;

    /**
     * The list of multi fillers.
     */
    @NotNull
    private final Array<AssetTreeMultiContextMenuFiller> multiFillers;

    private AssetTreeContextMenuFillerRegistry() {
        this.singleFillers = ArrayFactory.newArray(AssetTreeSingleContextMenuFiller.class);
        this.multiFillers = ArrayFactory.newArray(AssetTreeMultiContextMenuFiller.class);
        registerSingle(new FileAssetTreeSingleContextMenuFiller());
        registerSingle(new ResourceAssetTreeSingleContextMenuFiller());
        registerMulti(new FileAssetTreeSingleContextMenuFiller());
        registerMulti(new ResourceAssetTreeSingleContextMenuFiller());
    }

    /**
     * Register a new single context menu filler.
     *
     * @param filler the single context menu filler.
     */
    @FromAnyThread
    public void registerSingle(@NotNull final AssetTreeSingleContextMenuFiller filler) {
        this.singleFillers.add(filler);
    }

    /**
     * Register a new multiply context menu filler.
     *
     * @param filler the multiply context menu filler.
     */
    @FromAnyThread
    public void registerMulti(@NotNull final AssetTreeMultiContextMenuFiller filler) {
        this.multiFillers.add(filler);
    }

    /**
     * Gets the list of available single context menu singleFillers.
     *
     * @return the list of single context menu filler.
     */
    @FromAnyThread
    public @NotNull Array<AssetTreeSingleContextMenuFiller> getSingleFillers() {
        return singleFillers;
    }

    /**
     * Gets the list of available multiply context menu singleFillers.
     *
     * @return the list of multiply context menu filler.
     */
    @FromAnyThread
    public @NotNull Array<AssetTreeMultiContextMenuFiller> getMultiFillers() {
        return multiFillers;
    }
}
