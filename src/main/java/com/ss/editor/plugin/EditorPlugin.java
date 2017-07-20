package com.ss.editor.plugin;

import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.JMEThread;
import com.ss.editor.file.converter.FileConverterRegistry;
import com.ss.editor.manager.FileIconManager;
import com.ss.editor.ui.component.asset.tree.AssetTreeContextMenuFillerRegistry;
import com.ss.editor.ui.component.creator.FileCreatorRegistry;
import com.ss.editor.ui.component.editor.EditorRegistry;
import com.ss.editor.ui.control.tree.node.TreeNodeFactoryRegistry;
import com.ss.editor.ui.css.CSSRegistry;
import com.ss.rlib.plugin.PluginContainer;
import com.ss.rlib.plugin.PluginSystem;
import com.ss.rlib.plugin.impl.BasePlugin;
import org.jetbrains.annotations.NotNull;

/**
 * The base implementation of a plugin for this editor.
 *
 * @author JavaSaBr
 */
public class EditorPlugin extends BasePlugin {

    /**
     * @param pluginContainer the plugin container.
     */
    public EditorPlugin(@NotNull final PluginContainer pluginContainer) {
        super(pluginContainer);
    }

    /**
     * Register this plugin's own CSS files.
     *
     * @param registry the CSS registry.
     */
    @FromAnyThread
    public void registerCSS(@NotNull final CSSRegistry registry) {
    }

    /**
     * Register this plugin's own file creators.
     *
     * @param registry the file creator registry.
     */
    @FromAnyThread
    public void registerFileCreators(@NotNull final FileCreatorRegistry registry) {
    }

    /**
     * Register this plugin's own file editors.
     *
     * @param registry the file creator registry.
     */
    @FromAnyThread
    public void registerFileEditors(@NotNull final EditorRegistry registry) {
    }

    /**
     * Register this plugin's own file icon finders.
     *
     * @param iconManager the icon manager.
     */
    @FromAnyThread
    public void registerFileIconFinders(@NotNull final FileIconManager iconManager) {
    }

    /**
     * Register this plugin's own file converters.
     *
     * @param converterRegistry the converters registry.
     */
    @FromAnyThread
    public void registerFileConverters(@NotNull final FileConverterRegistry converterRegistry) {
    }

    /**
     * Register this plugin's own asset tree context menu fillers.
     *
     * @param fillerRegistry the menu fillers registry.
     */
    @FromAnyThread
    public void registerContextMenuFillers(@NotNull final AssetTreeContextMenuFillerRegistry fillerRegistry) {
    }

    /**
     * Register this plugin's own tree node factories.
     *
     * @param factoryRegistry the registry of tree node factories.
     */
    @FromAnyThread
    public void registerTreeNodeFactories(@NotNull final TreeNodeFactoryRegistry factoryRegistry) {
    }

    /**
     * Do some things before when JME context will be created.
     *
     * @param pluginSystem the plugin system.
     */
    @JMEThread
    public void onBeforeCreateJMEContext(@NotNull final PluginSystem pluginSystem) {
    }

    /**
     * Do some things after when JME context was created.
     *
     * @param pluginSystem the plugin system.
     */
    @JMEThread
    public void onAfterCreateJMEContext(@NotNull final PluginSystem pluginSystem) {
    }

    /**
     * Do some things before when JavaFX context will be created.
     *
     * @param pluginSystem the plugin system.
     */
    @FXThread
    public void onBeforeCreateJavaFXContext(@NotNull final PluginSystem pluginSystem) {
    }

    /**
     * Do some things after when JavaFX context was created.
     *
     * @param pluginSystem the plugin system.
     */
    @FXThread
    public void onAfterCreateJavaFXContext(@NotNull final PluginSystem pluginSystem) {
    }

    /**
     * Do some things before when the editor is ready to work.
     *
     * @param pluginSystem the plugin system.
     */
    @FXThread
    public void onFinishLoading(@NotNull final PluginSystem pluginSystem) {
    }
}
