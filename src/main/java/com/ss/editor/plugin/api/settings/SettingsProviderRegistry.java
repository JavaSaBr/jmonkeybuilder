package com.ss.editor.plugin.api.settings;

import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.config.DefaultSettingsProvider;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.common.plugin.extension.ExtensionPoint;
import com.ss.rlib.common.plugin.extension.ExtensionPointManager;
import com.ss.rlib.common.util.array.Array;
import org.jetbrains.annotations.NotNull;

/**
 * The registry of all settings providers.
 *
 * @author JavaSaBr
 */
public class SettingsProviderRegistry {

    private static final Logger LOGGER = LoggerManager.getLogger(SettingsProviderRegistry.class);

    /**
     * @see SettingsProvider
     */
    public static final String EP_PROVIDERS = "SettingsProviderRegistry#providers";

    private static final ExtensionPoint<SettingsProvider> PROVIDERS =
            ExtensionPointManager.register(EP_PROVIDERS);

    private static final SettingsProviderRegistry INSTANCE = new SettingsProviderRegistry();

    @FromAnyThread
    public static @NotNull SettingsProviderRegistry getInstance() {
        return INSTANCE;
    }

    private SettingsProviderRegistry() {
        PROVIDERS.register(new DefaultSettingsProvider());
        LOGGER.info("initialized.");
    }

    /**
     * Get all available settings property definitions.
     *
     * @return all available settings property definitions.
     */
    @FxThread
    public @NotNull Array<SettingsPropertyDefinition> getDefinitions() {

        var result = Array.<SettingsPropertyDefinition>ofType(SettingsPropertyDefinition.class);

        for (var settingsProvider : PROVIDERS.getExtensions()) {
            result.addAll(settingsProvider.getDefinitions());
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
        return PROVIDERS.getExtensions().stream()
                .anyMatch(settingsProvider -> settingsProvider.isRequiredRestart(propertyId));
    }

    /**
     * Check the property if this property requires updating of classpath.
     *
     * @param propertyId the property id.
     * @return true if need to update classpath to apply changes for this property.
     */
    @FxThread
    public boolean isRequiredUpdateClasspath(@NotNull String propertyId) {
        return PROVIDERS.getExtensions().stream()
                .anyMatch(settingsProvider -> settingsProvider.isRequiredUpdateClasspath(propertyId));
    }

    /**
     * Check the property if this property requires reshaping of 3D view.
     *
     * @param propertyId the property id.
     * @return true if need to reshape 3D view to apply changes for this property.
     */
    @FxThread
    public boolean isRequiredReshape3DView(@NotNull String propertyId) {
        return PROVIDERS.getExtensions().stream()
                .anyMatch(settingsProvider -> settingsProvider.isRequiredReshape3DView(propertyId));
    }
}
