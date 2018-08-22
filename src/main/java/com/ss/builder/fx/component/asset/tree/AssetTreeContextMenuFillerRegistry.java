package com.ss.builder.fx.component.asset.tree;

import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.fx.component.asset.tree.context.menu.filler.AssetTreeMultiContextMenuFiller;
import com.ss.builder.fx.component.asset.tree.context.menu.filler.AssetTreeSingleContextMenuFiller;
import com.ss.builder.fx.component.asset.tree.context.menu.filler.impl.FileAssetTreeSingleContextMenuFiller;
import com.ss.builder.fx.component.asset.tree.context.menu.filler.impl.ResourceAssetTreeSingleContextMenuFiller;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.fx.component.asset.tree.context.menu.filler.AssetTreeMultiContextMenuFiller;
import com.ss.builder.fx.component.asset.tree.context.menu.filler.AssetTreeSingleContextMenuFiller;
import com.ss.builder.fx.component.asset.tree.context.menu.filler.impl.FileAssetTreeSingleContextMenuFiller;
import com.ss.builder.fx.component.asset.tree.context.menu.filler.impl.ResourceAssetTreeSingleContextMenuFiller;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.common.plugin.extension.ExtensionPoint;
import com.ss.rlib.common.plugin.extension.ExtensionPointManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The registry class to collect all available context menu filler.
 *
 * @author JavaSaBr
 */
public class AssetTreeContextMenuFillerRegistry {

    private static final Logger LOGGER = LoggerManager.getLogger(AssetTreeContextMenuFillerRegistry.class);

    /**
     * @see AssetTreeSingleContextMenuFiller
     */
    public static final String EP_SINGLE_FILLERS = "AssetTreeContextMenuFillerRegistry#singleFillers";

    /**
     * @see AssetTreeMultiContextMenuFiller
     */
    public static final String EP_MULTI_FILLERS = "AssetTreeContextMenuFillerRegistry#multiFillers";

    private static final ExtensionPoint<AssetTreeSingleContextMenuFiller> SINGLE_FILLERS =
            ExtensionPointManager.register(EP_SINGLE_FILLERS);

    private static final ExtensionPoint<AssetTreeMultiContextMenuFiller> MULTI_FILLERS =
            ExtensionPointManager.register(EP_MULTI_FILLERS);

    private static final AssetTreeContextMenuFillerRegistry INSTANCE =
            new AssetTreeContextMenuFillerRegistry();

    public static @NotNull AssetTreeContextMenuFillerRegistry getInstance() {
        return INSTANCE;
    }

    private AssetTreeContextMenuFillerRegistry() {

        SINGLE_FILLERS.register(new FileAssetTreeSingleContextMenuFiller())
                .register(new ResourceAssetTreeSingleContextMenuFiller());

        MULTI_FILLERS.register(new FileAssetTreeSingleContextMenuFiller())
                .register(new ResourceAssetTreeSingleContextMenuFiller());

        LOGGER.info("initialized.");
    }

    /**
     * Gets the list of available single context menu singleFillers.
     *
     * @return the list of single context menu filler.
     */
    @FromAnyThread
    public @NotNull List<AssetTreeSingleContextMenuFiller> getSingleFillers() {
        return SINGLE_FILLERS.getExtensions();
    }

    /**
     * Gets the list of available multiply context menu singleFillers.
     *
     * @return the list of multiply context menu filler.
     */
    @FromAnyThread
    public @NotNull List<AssetTreeMultiContextMenuFiller> getMultiFillers() {
        return MULTI_FILLERS.getExtensions();
    }
}
