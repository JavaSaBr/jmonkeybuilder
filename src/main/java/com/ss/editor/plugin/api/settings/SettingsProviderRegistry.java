package com.ss.editor.plugin.api.settings;

import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.config.DefaultSettingsProvider;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;

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
    private final Array<SettingsProvider> providers;

    private SettingsProviderRegistry() {
        this.providers = ArrayFactory.newArray(SettingsProvider.class);
        register(new DefaultSettingsProvider());
    }

    /**
     * Register the new settings provider.
     *
     * @param settingsProvider the new settings provider.
     */
    @FxThread
    public void register(@NotNull final SettingsProvider settingsProvider) {
        this.providers.add(settingsProvider);
    }

    /**
     * Get all available settings property definitions.
     *
     * @return all available settings property definitions.
     */
    @FxThread
    public @NotNull Array<SettingsPropertyDefinition> getDefinitions() {
        final Array<SettingsPropertyDefinition> result = ArrayFactory.newArray(SettingsPropertyDefinition.class);
        providers.forEach(result, (provider, definitions) -> definitions.addAll(provider.getDefinitions()));
        return result;
    }

    /**
     * Check the property if this property requires restarting of editor.
     *
     * @param propertyId the property id.
     * @return true if need to restart to apply changes for this property.
     */
    @FxThread
    public boolean isRequiredRestart(@NotNull final String propertyId) {
        return providers.search(propertyId, (provider, id) -> provider.isRequiredRestart(propertyId)) != null;
    }

    /**
     * Check the property if this property requires updating of classpath.
     *
     * @param propertyId the property id.
     * @return true if need to update classpath to apply changes for this property.
     */
    @FxThread
    public boolean isRequiredUpdateClasspath(@NotNull final String propertyId) {
        return providers.search(propertyId, (provider, id) -> provider.isRequiredRestart(propertyId)) != null;
    }

    /**
     * Check the property if this property requires reshaping of 3D view.
     *
     * @param propertyId the property id.
     * @return true if need to reshape 3D view to apply changes for this property.
     */
    @FxThread
    public boolean isRequiredReshape3DView(@NotNull final String propertyId) {
        return providers.search(propertyId, (provider, id) -> provider.isRequiredRestart(propertyId)) != null;
    }
}
