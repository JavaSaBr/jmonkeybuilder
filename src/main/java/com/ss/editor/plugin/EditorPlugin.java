package com.ss.editor.plugin;

import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.file.converter.FileConverterRegistry;
import com.ss.editor.manager.FileIconManager;
import com.ss.editor.manager.ResourceManager;
import com.ss.editor.plugin.api.settings.SettingsProviderRegistry;
import com.ss.editor.ui.component.asset.tree.AssetTreeContextMenuFillerRegistry;
import com.ss.editor.ui.component.creator.FileCreatorRegistry;
import com.ss.editor.ui.component.editor.EditorRegistry;
import com.ss.editor.ui.component.painting.PaintingComponentRegistry;
import com.ss.editor.ui.control.property.builder.PropertyBuilderRegistry;
import com.ss.editor.ui.control.tree.node.factory.TreeNodeFactoryRegistry;
import com.ss.editor.ui.css.CssRegistry;
import com.ss.editor.ui.preview.FilePreviewFactoryRegistry;
import com.ss.rlib.common.plugin.PluginContainer;
import com.ss.rlib.common.plugin.PluginSystem;
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
     * Register this plugin's own file creators.
     *
     * @param registry the file creator registry.
     */
    @BackgroundThread
    public void register(@NotNull FileCreatorRegistry registry) {
    }

    /**
     * Register this plugin's own file editors.
     *
     * @param registry the file creator registry.
     */
    @BackgroundThread
    public void register(@NotNull EditorRegistry registry) {
    }

    /**
     * Register this plugin's own file icon finders.
     *
     * @param iconManager the icon manager.
     */
    @BackgroundThread
    public void register(@NotNull FileIconManager iconManager) {
    }

    /**
     * Register this plugin's own file converters.
     *
     * @param registry the converters registry.
     */
    @BackgroundThread
    public void register(@NotNull FileConverterRegistry registry) {
    }

    /**
     * Register this plugin's own asset tree context menu fillers.
     *
     * @param registry the menu fillers registry.
     */
    @BackgroundThread
    public void register(@NotNull AssetTreeContextMenuFillerRegistry registry) {
    }

    /**
     * Register this plugin's own tree node factories.
     *
     * @param registry the registry of tree node factories.
     */
    @BackgroundThread
    public void register(@NotNull TreeNodeFactoryRegistry registry) {
    }

    /**
     * Register this plugin's own property builders.
     *
     * @param registry the registry of property builders.
     */
    @BackgroundThread
    public void register(@NotNull PropertyBuilderRegistry registry) {
    }

    /**
     * Register this plugin's own file preview factories.
     *
     * @param registry the registry of file preview factories.
     */
    @BackgroundThread
    public void register(@NotNull FilePreviewFactoryRegistry registry) {
    }

    /**
     * Register this plugin's own settings providers.
     *
     * @param registry the registry of settings providers.
     */
    @BackgroundThread
    public void register(@NotNull SettingsProviderRegistry registry) {
    }

    /**
     * Register this plugin's own painting component's constructors.
     *
     * @param registry the registry of painting component's constructors.
     */
    @BackgroundThread
    public void register(@NotNull PaintingComponentRegistry registry) {
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
