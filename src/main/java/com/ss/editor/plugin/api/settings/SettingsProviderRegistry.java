package com.ss.editor.plugin.api.settings;

import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.config.DefaultSettingsProvider;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import com.ss.rlib.common.util.array.ConcurrentArray;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * The registry of all settings providers.
 *
 * @author JavaSaBr
 */
public class SettingsProviderRegistry {

    private static final SettingsProviderRegistry INSTANCE = new SettingsProviderRegistry();

    @FromAnyThread
    public static @NotNull SettingsProviderRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * The list of settings providers.
     */
    @NotNull
    private final ConcurrentArray<SettingsProvider> providers;

    private SettingsProviderRegistry() {
        this.providers = ConcurrentArray.of(SettingsProvider.class);
        register(new DefaultSettingsProvider());
    }

    /**
     * Register the new settings provider.
     *
     * @param settingsProvider the new settings provider.
     */
    @FromAnyThread
    public void register(@NotNull SettingsProvider settingsProvider) {
        providers.runInWriteLock(settingsProvider, Collection::add);
    }

    /**
     * Get all available settings property definitions.
     *
     * @return all available settings property definitions.
     */
    @FxThread
    public @NotNull Array<SettingsPropertyDefinition> getDefinitions() {

        var result = Array.<SettingsPropertyDefinition>of(SettingsPropertyDefinition.class);

        var stamp = providers.readLock();
        try {

            providers.forEach(result, (provider, definitions) ->
                    definitions.addAll(provider.getDefinitions()));

        } finally {
            providers.readUnlock(stamp);
        }

        return result;
    }

    /**
     * Check the property if this property requires restarting of editor.
     *
     * @param propertyId the property id.
     * @return true if need to restart to apply changes for this property.
     */
    @FxThread
    public boolean isRequiredRestart(@NotNull String propertyId) {
        var stamp = providers.readLock();
        try {

            return providers.search(propertyId,
                    (provider, id) -> provider.isRequiredRestart(propertyId)) != null;

        } finally {
            providers.readUnlock(stamp);
        }
    }

    /**
     * Check the property if this property requires updating of classpath.
     *
     * @param propertyId the property id.
     * @return true if need to update classpath to apply changes for this property.
     */
    @FxThread
    public boolean isRequiredUpdateClasspath(@NotNull String propertyId) {
        var stamp = providers.readLock();
        try {

            return providers.search(propertyId,
                    (provider, id) -> provider.isRequiredUpdateClasspath(propertyId)) != null;

        } finally {
            providers.readUnlock(stamp);
        }
    }

    /**
     * Check the property if this property requires reshaping of 3D view.
     *
     * @param propertyId the property id.
     * @return true if need to reshape 3D view to apply changes for this property.
     */
    @FxThread
    public boolean isRequiredReshape3DView(@NotNull String propertyId) {
        var stamp = providers.readLock();
        try {

            return providers.search(propertyId,
                    (provider, id) -> provider.isRequiredReshape3DView(propertyId)) != null;

        } finally {
            providers.readUnlock(stamp);
        }
    }
}
