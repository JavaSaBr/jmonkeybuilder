package com.ss.editor.plugin;

import com.ss.rlib.plugin.PluginContainer;
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
}
