package com.ss.builder.plugin.api.settings;

import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.FxThread;
import com.ss.rlib.common.util.array.Array;
import org.jetbrains.annotations.NotNull;

/**
 * The interface to implement a settings provider.
 *
 * @author JavaSaBr
 */
public interface SettingsProvider {

    /**
     * Get the list of property definitions.
     *
     * @return the list of property definitions.
     */
    @FxThread
    @NotNull Array<SettingsPropertyDefinition> getDefinitions();

    /**
     * Check the property id.
     *
     * @param propertyId the property id.
     * @return true if need restart to apply changes for this property.
     */
    @FxThread
    default boolean isRequiredRestart(@NotNull String propertyId) {
        return false;
    }

    /**
     * Check the property if this property requires updating of classpath.
     *
     * @param propertyId the property id.
     * @return true if need to update classpath to apply changes for this property.
     */
    @FxThread
    default boolean isRequiredUpdateClasspath(@NotNull String propertyId) {
        return false;
    }

    /**
     * Check the property if this property requires reshaping of 3D view.
     *
     * @param propertyId the property id.
     * @return true if need to reshape 3D view to apply changes for this property.
     */
    @FxThread
    default boolean isRequiredReshape3DView(@NotNull String propertyId) {
        return false;
    }
}
