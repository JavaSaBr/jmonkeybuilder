package com.ss.editor.plugin;

import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.file.converter.FileConverterRegistry;
import com.ss.editor.manager.FileIconManager;
import com.ss.editor.plugin.api.settings.SettingsProviderRegistry;
import com.ss.editor.ui.component.asset.tree.AssetTreeContextMenuFillerRegistry;
import com.ss.editor.ui.component.creator.FileCreatorRegistry;
import com.ss.editor.ui.component.editor.EditorRegistry;
import com.ss.editor.ui.component.painting.PaintingComponentRegistry;
import com.ss.editor.ui.control.property.builder.PropertyBuilderRegistry;
import com.ss.editor.ui.control.tree.node.factory.TreeNodeFactoryRegistry;
import com.ss.editor.ui.css.CssRegistry;
import com.ss.editor.ui.preview.FilePreviewFactoryRegistry;
import com.ss.rlib.plugin.PluginContainer;
import com.ss.rlib.plugin.PluginSystem;
import com.ss.rlib.plugin.impl.BasePlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;

/**
 * The base implementation of a plugin for this editor.
 *
 * @author JavaSaBr
 */
public class EditorPlugin extends BasePlugin {

    public EditorPlugin(@NotNull final PluginContainer pluginContainer) {
        super(pluginContainer);
    }

    /**
     * Register this plugin's own CSS files.
     *
     * @param registry the CSS registry.
     */
    @FromAnyThread
    public void register(@NotNull final CssRegistry registry) {
    }

    /**
     * Register this plugin's own file creators.
     *
     * @param registry the file creator registry.
     */
    @FromAnyThread
    public void register(@NotNull final FileCreatorRegistry registry) {
    }

    /**
     * Register this plugin's own file editors.
     *
     * @param registry the file creator registry.
     */
    @FromAnyThread
    public void register(@NotNull final EditorRegistry registry) {
    }

    /**
     * Register this plugin's own file icon finders.
     *
     * @param iconManager the icon manager.
     */
    @FromAnyThread
    public void register(@NotNull final FileIconManager iconManager) {
    }

    /**
     * Register this plugin's own file converters.
     *
     * @param registry the converters registry.
     */
    @FromAnyThread
    public void register(@NotNull final FileConverterRegistry registry) {
    }

    /**
     * Register this plugin's own asset tree context menu fillers.
     *
     * @param registry the menu fillers registry.
     */
    @FromAnyThread
    public void register(@NotNull final AssetTreeContextMenuFillerRegistry registry) {
    }

    /**
     * Register this plugin's own tree node factories.
     *
     * @param registry the registry of tree node factories.
     */
    @FromAnyThread
    public void register(@NotNull final TreeNodeFactoryRegistry registry) {
    }

    /**
     * Register this plugin's own property builders.
     *
     * @param registry the registry of property builders.
     */
    @FromAnyThread
    public void register(@NotNull final PropertyBuilderRegistry registry) {
    }

    /**
     * Register this plugin's own file preview factories.
     *
     * @param registry the registry of file preview factories.
     */
    @FromAnyThread
    public void register(@NotNull final FilePreviewFactoryRegistry registry) {
    }

    /**
     * Register this plugin's own settings providers.
     *
     * @param registry the registry of settings providers.
     */
    @FxThread
    public void register(@NotNull final SettingsProviderRegistry registry) {
    }

    /**
     * Register this plugin's own painting component's constructors.
     *
     * @param registry the registry of painting component's constructors.
     */
    @FxThread
    public void register(@NotNull final PaintingComponentRegistry registry) {
    }

    /**
     * Do some things before when jME context will be created.
     *
     * @param pluginSystem the plugin system.
     */
    @JmeThread
    public void onBeforeCreateJmeContext(@NotNull final PluginSystem pluginSystem) {
    }

    /**
     * Do some things after when jME context was created.
     *
     * @param pluginSystem the plugin system.
     */
    @JmeThread
    public void onAfterCreateJmeContext(@NotNull final PluginSystem pluginSystem) {
    }

    /**
     * Do some things before when JavaFX context will be created.
     *
     * @param pluginSystem the plugin system.
     */
    @FxThread
    public void onBeforeCreateJavaFxContext(@NotNull final PluginSystem pluginSystem) {
    }

    /**
     * Do some things after when JavaFX context was created.
     *
     * @param pluginSystem the plugin system.
     */
    @FxThread
    public void onAfterCreateJavaFxContext(@NotNull final PluginSystem pluginSystem) {
    }

    /**
     * Do some things before when the editor is ready to work.
     *
     * @param pluginSystem the plugin system.
     */
    @FxThread
    public void onFinishLoading(@NotNull final PluginSystem pluginSystem) {
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
