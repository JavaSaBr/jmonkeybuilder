package com.ss.editor.plugin.api;

import com.jme3.post.Filter;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import com.ss.rlib.util.dictionary.DictionaryFactory;
import com.ss.rlib.util.dictionary.ObjectDictionary;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * The class with some extensions to editor's render.
 *
 * @author JavaSaBr
 */
public class RenderFilterExtension {

    @NotNull
    private static final RenderFilterExtension INSTANCE = new RenderFilterExtension();

    @FromAnyThread
    public static @NotNull RenderFilterExtension getInstance() {
        return INSTANCE;
    }

    /**
     * The map filter to its refresh action.
     */
    @NotNull
    private final ObjectDictionary<Filter, Consumer<@NotNull ? extends Filter>> refreshActions;

    /**
     * The additional filters.
     */
    @NotNull
    private final Array<Filter> filters;

    private RenderFilterExtension() {
        this.filters = ArrayFactory.newArray(Filter.class);
        this.refreshActions = DictionaryFactory.newObjectDictionary();
    }

    /**
     * Register the new additional filter.
     *
     * @param filter the filter.
     */
    @JmeThread
    public void register(@NotNull final Filter filter) {
        this.filters.add(filter);
    }

    /**
     * Set the handler to handle refresh events.
     *
     * @param filter  the filter.
     * @param handler the handler.
     * @param <T>     the filter's type.
     */
    @JmeThread
    public <T extends Filter> void setOnRefresh(@NotNull final T filter, @NotNull final Consumer<@NotNull T> handler) {

        if (filters.contains(filter)) {
            throw new IllegalArgumentException("The filter " + filter + "isn't registered.");
        }

        refreshActions.put(filter, handler);
    }

    /**
     * Refresh all filters.
     */
    @JmeThread
    public void refreshFilters() {
        filters.forEach(refreshActions, (filter, map) -> {
            final Consumer<@NotNull Filter> consumer = (Consumer<Filter>) map.get(filter);
            if (consumer != null) {
                consumer.accept(filter);
            }
        });
    }

    @JmeThread
    public void enableFilters() {

    }

    @JmeThread
    public void disableFilters() {

    }
}
