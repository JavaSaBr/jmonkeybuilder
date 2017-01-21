package com.ss.extension.scene.filter;

import com.jme3.export.Savable;
import com.jme3.post.Filter;
import com.jme3.util.clone.JmeCloneable;

import org.jetbrains.annotations.NotNull;

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
}
