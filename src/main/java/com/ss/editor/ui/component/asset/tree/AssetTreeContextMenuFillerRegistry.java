package com.ss.editor.ui.component.asset.tree;

import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.component.asset.tree.context.menu.filler.AssetTreeMultiContextMenuFiller;
import com.ss.editor.ui.component.asset.tree.context.menu.filler.AssetTreeSingleContextMenuFiller;
import com.ss.editor.ui.component.asset.tree.context.menu.filler.impl.FileAssetTreeSingleContextMenuFiller;
import com.ss.editor.ui.component.asset.tree.context.menu.filler.impl.ResourceAssetTreeSingleContextMenuFiller;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.common.util.array.ConcurrentArray;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * The registry class to collect all available context menu filler.
 *
 * @author JavaSaBr
 */
public class AssetTreeContextMenuFillerRegistry {

    private static final Logger LOGGER = LoggerManager.getLogger(AssetTreeContextMenuFillerRegistry.class);

    private static final AssetTreeContextMenuFillerRegistry INSTANCE = new AssetTreeContextMenuFillerRegistry();

    public static @NotNull AssetTreeContextMenuFillerRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * The list of single fillers.
     */
    @NotNull
    private final ConcurrentArray<AssetTreeSingleContextMenuFiller> singleFillers;

    /**
     * The list of multi fillers.
     */
    @NotNull
    private final ConcurrentArray<AssetTreeMultiContextMenuFiller> multiFillers;

    private AssetTreeContextMenuFillerRegistry() {
        this.singleFillers = ConcurrentArray.ofType(AssetTreeSingleContextMenuFiller.class);
        this.multiFillers = ConcurrentArray.ofType(AssetTreeMultiContextMenuFiller.class);
        registerSingle(new FileAssetTreeSingleContextMenuFiller());
        registerSingle(new ResourceAssetTreeSingleContextMenuFiller());
        registerMulti(new FileAssetTreeSingleContextMenuFiller());
        registerMulti(new ResourceAssetTreeSingleContextMenuFiller());
        LOGGER.info("initialized.");
    }

    /**
     * Register a new single context menu filler.
     *
     * @param filler the single context menu filler.
     */
    @FromAnyThread
    public void registerSingle(@NotNull AssetTreeSingleContextMenuFiller filler) {
        singleFillers.runInWriteLock(filler, Collection::add);
    }

    /**
     * Register a new multiply context menu filler.
     *
     * @param filler the multiply context menu filler.
     */
    @FromAnyThread
    public void registerMulti(@NotNull AssetTreeMultiContextMenuFiller filler) {
        multiFillers.runInWriteLock(filler, Collection::add);
    }

    /**
     * Gets the list of available single context menu singleFillers.
     *
     * @return the list of single context menu filler.
     */
    @FromAnyThread
    public @NotNull ConcurrentArray<AssetTreeSingleContextMenuFiller> getSingleFillers() {
        return singleFillers;
    }

    /**
     * Gets the list of available multiply context menu singleFillers.
     *
     * @return the list of multiply context menu filler.
     */
    @FromAnyThread
    public @NotNull ConcurrentArray<AssetTreeMultiContextMenuFiller> getMultiFillers() {
        return multiFillers;
    }
}
