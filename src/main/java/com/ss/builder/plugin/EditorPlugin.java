package com.ss.builder.plugin;

import com.ss.builder.annotation.BackgroundThread;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.fx.css.CssRegistry;
import com.ss.builder.manager.ResourceManager;
import com.ss.rlib.common.plugin.PluginContainer;
import com.ss.rlib.common.plugin.PluginSystem;
import com.ss.rlib.common.plugin.extension.ExtensionPointManager;
import com.ss.rlib.common.plugin.impl.BasePlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;

/**
 * The base implementation of a plugin for this editor.
 *
 * @author JavaSaBr
 */
public class EditorPlugin extends BasePlugin {

    public EditorPlugin(@NotNull PluginContainer pluginContainer) {
        super(pluginContainer);
    }

    /**
     * Register this plugin's specific resources in the manager.
     *
     * @param resourceManager the resource manager.
     */
    @BackgroundThread
    public void register(@NotNull ResourceManager resourceManager) {
    }

    /**
     * Register this plugin's own CSS files.
     *
     * @param registry the CSS registry.
     */
    @BackgroundThread
    public void register(@NotNull CssRegistry registry) {
    }

    /**
     * Register all necessary extensions.
     *
     * @param manager the extension point's manager.
     */
    @BackgroundThread
    public void register(@NotNull ExtensionPointManager manager) {
    }

    /**
     * Do some things after when jME context was created.
     *
     * @param pluginSystem the plugin system.
     */
    @BackgroundThread
    public void onAfterCreateJmeContext(@NotNull PluginSystem pluginSystem) {
    }

    /**
     * Do some things after when JavaFX context was created.
     *
     * @param pluginSystem the plugin system.
     */
    @BackgroundThread
    public void onAfterCreateJavaFxContext(@NotNull PluginSystem pluginSystem) {
    }

    /**
     * Do some things before when the editor is ready to work.
     *
     * @param pluginSystem the plugin system.
     */
    @BackgroundThread
    public void onFinishLoading(@NotNull PluginSystem pluginSystem) {
    }

    @Override
    @FromAnyThread
    public @NotNull PluginContainer getContainer() {
        return super.getContainer();
    }

    /**
     * Get the URL to a home page of this plugin.
     *
     * @return the URL of a home page of this plugin or null.
     */
    @FromAnyThread
    public @Nullable URL getHomePageUrl() {
        return null;
    }

    /**
     * Get the HTML presentation of plugin's dependencies as gradle dependencies.
     *
     * @return the HTML presentation of plugin's dependencies as gradle dependencies.
     */
    @FromAnyThread
    public @Nullable String getUsedGradleDependencies() {
        return null;
    }

    /**
     * Get the HTML presentation of plugin's dependencies as maven dependencies.
     *
     * @return the HTML presentation of plugin's dependencies as maven dependencies.
     */
    @FromAnyThread
    public @Nullable String getUsedMavenDependencies() {
        return null;
    }
}
