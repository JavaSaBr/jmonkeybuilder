package com.ss.extension.scene.filter;

import com.jme3.export.Savable;
import com.jme3.post.Filter;
import com.jme3.util.clone.JmeCloneable;
import com.ss.extension.scene.app.state.SceneAppState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import rlib.util.array.Array;

/**
 * The interface to implement a scene filter.
 *
 * @author JavaSaBr
 */
public interface SceneFilter<T extends Filter> extends Savable, JmeCloneable, Cloneable {

    /**
     * Get a filter. It is usually return this.
     *
     * @return the filter.
     */
    T get();

    /**
     * @return the name of this filter.
     */
    @NotNull
    String getName();

    /**
     * Enable or disable this filter
     *
     * @param enabled true to enable
     */
    void setEnabled(boolean enabled);

    /**
     * returns true if the filter is enabled
     *
     * @return enabled
     */
    boolean isEnabled();

    /**
     * Check state dependencies.
     *
     * @param exists the current exists states.
     * @return null of can create or message with description.
     */
    @Nullable
    default String checkStates(@NotNull final Array<SceneAppState> exists) {
        return null;
    }

    /**
     * Check filter dependencies.
     *
     * @param exists the current exists filters.
     * @return null of can create or message with description.
     */
    @Nullable
    default String checkFilters(@NotNull final Array<SceneFilter<?>> exists) {
        return null;
    }
}
