package com.ss.builder.plugin.api;

import com.jme3.post.Filter;
import com.ss.builder.annotation.BackgroundThread;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.manager.AsyncEventManager;
import com.ss.builder.manager.ExecutorManager;
import com.ss.builder.fx.event.impl.AllPluginsExtensionsRegisteredEvent;
import com.ss.builder.fx.event.impl.JmeContextCreatedEvent;
import com.ss.builder.util.EditorUtils;
import com.ss.builder.annotation.BackgroundThread;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.manager.AsyncEventManager.CombinedAsyncEventHandlerBuilder;
import com.ss.builder.manager.ExecutorManager;
import com.ss.builder.fx.event.impl.AllPluginsExtensionsRegisteredEvent;
import com.ss.builder.fx.event.impl.JmeContextCreatedEvent;
import com.ss.builder.util.EditorUtils;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.common.plugin.extension.ExtensionPoint;
import com.ss.rlib.common.plugin.extension.ExtensionPointManager;
import org.jetbrains.annotations.NotNull;

/**
 * The registry of some additional render filters.
 *
 * @author JavaSaBr
 */
public class RenderFilterRegistry {

    private static final Logger LOGGER = LoggerManager.getLogger(RenderFilterRegistry.class);

    public interface FilterExtension {

        @FromAnyThread
        @NotNull Filter getFilter();

        @JmeThread
        default void refresh() {
        }
    }

    /**
     * @see FilterExtension
     */
    public static final String EP_FILTERS = "RenderFilterExtension#filters";

    private static final ExtensionPoint<FilterExtension> EXTENSIONS =
            ExtensionPointManager.register(EP_FILTERS);

    private static final RenderFilterRegistry INSTANCE = new RenderFilterRegistry();

    @FromAnyThread
    public static @NotNull RenderFilterRegistry getInstance() {
        return INSTANCE;
    }

    private RenderFilterRegistry() {

        AsyncEventManager.CombinedAsyncEventHandlerBuilder.of(this::applyExtensions)
                .add(AllPluginsExtensionsRegisteredEvent.EVENT_TYPE)
                .add(JmeContextCreatedEvent.EVENT_TYPE)
                .buildAndRegister();

        LOGGER.info("initialized.");
    }

    @BackgroundThread
    private void applyExtensions() {
        ExecutorManager.getInstance()
                .addJmeTask(this::applyExtensionsInJme);
    }

    @BackgroundThread
    private void applyExtensionsInJme() {

        var postProcessor = EditorUtils.getGlobalFilterPostProcessor();

        for (var extension : EXTENSIONS) {
            postProcessor.addFilter(extension.getFilter());
        }

        LOGGER.info("applied additional filter extensions.");
    }

    /**
     * Refresh all filters.
     */
    @JmeThread
    public void refreshFilters() {
        EXTENSIONS.forEach(FilterExtension::refresh);
    }

    @JmeThread
    public void enableFilters() {
    }

    @JmeThread
    public void disableFilters() {
    }
}
