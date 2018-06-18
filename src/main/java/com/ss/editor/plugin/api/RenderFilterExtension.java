package com.ss.editor.plugin.api;

import com.jme3.post.Filter;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.common.util.ClassUtils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import com.ss.rlib.common.util.dictionary.ConcurrentObjectDictionary;
import com.ss.rlib.common.util.dictionary.DictionaryFactory;
import com.ss.rlib.common.util.dictionary.ObjectDictionary;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * The class with some extensions to editor's render.
 *
 * @author JavaSaBr
 */
public class RenderFilterExtension {

    private static final RenderFilterExtension INSTANCE = new RenderFilterExtension();

    @FromAnyThread
    public static @NotNull RenderFilterExtension getInstance() {
        return INSTANCE;
    }

    /**
     * The map filter to its refresh action.
     */
    @NotNull
    private final ConcurrentObjectDictionary<Filter, Consumer<@NotNull ? extends Filter>> refreshActions;

    /**
     * The additional filters.
     */
    @NotNull
    private final Array<Filter> filters;

    private RenderFilterExtension() {
        this.filters = ArrayFactory.newCopyOnModifyArray(Filter.class);
        this.refreshActions = DictionaryFactory.newConcurrentAtomicObjectDictionary();
    }

    /**
     * Register the new additional filter.
     *
     * @param filter the filter.
     */
    @FromAnyThread
    public void register(@NotNull Filter filter) {
        this.filters.add(filter);

        var postProcessor = EditorUtil.getGlobalFilterPostProcessor();

        ExecutorManager.getInstance()
                .addJmeTask(() -> postProcessor.addFilter(filter));
    }

    /**
     * Set the handler to handle refresh events.
     *
     * @param filter  the filter.
     * @param handler the handler.
     * @param <T>     the filter's type.
     */
    @FromAnyThread
    public <T extends Filter> void setOnRefresh(@NotNull T filter, @NotNull Consumer<T> handler) {

        if (!filters.contains(filter)) {
            throw new IllegalArgumentException("The filter " + filter + "isn't registered.");
        }

        refreshActions.runInWriteLock(filter, handler, ObjectDictionary::put);
    }

    /**
     * Refresh all filters.
     */
    @JmeThread
    public void refreshFilters() {
        refreshActions.forEachInReadLock((filter, consumer) -> {
            var cast = ClassUtils.<Consumer<Filter>>unsafeCast(consumer);
            cast.accept(filter);
        });
    }

    @JmeThread
    public void enableFilters() {

    }

    @JmeThread
    public void disableFilters() {

    }
}
