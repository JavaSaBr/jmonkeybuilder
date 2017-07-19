package com.ss.editor.plugin;

import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.JMEThread;
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

    public EditorPlugin(@NotNull final PluginContainer pluginContainer) {
        super(pluginContainer);
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
