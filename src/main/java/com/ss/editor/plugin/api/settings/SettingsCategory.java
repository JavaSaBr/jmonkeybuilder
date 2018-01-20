package com.ss.editor.plugin.api.settings;

import com.ss.editor.annotation.FromAnyThread;
import org.jetbrains.annotations.NotNull;

/**
 * The class to describe a settings category.
 *
 * @author JavaSaBr
 */
public class SettingsCategory implements Comparable<SettingsCategory> {

    /**
     * The category id.
     */
    @NotNull
    private final String id;

    /**
     * THe label.
     */
    @NotNull
    private final String label;

    /**
     * The order.
     */
    private final int order;

    public SettingsCategory(@NotNull final String id, @NotNull final String label, final int order) {
        this.id = id;
        this.label = label;
        this.order = order;
    }

    /**
     * Get the category id.
     *
     * @return the category id.
     */
    @FromAnyThread
    public @NotNull String getId() {
        return id;
    }

    /**
     * Get the category label.
     *
     * @return the category label.
     */
    @FromAnyThread
    public @NotNull String getLabel() {
        return label;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final SettingsCategory that = (SettingsCategory) o;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public int compareTo(@NotNull final SettingsCategory o) {
        return order - o.order;
    }
}
