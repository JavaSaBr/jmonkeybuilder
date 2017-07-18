package com.ss.editor.manager;

import static com.ss.rlib.plugin.impl.PluginSystemFactory.newBasePluginSystem;
import com.ss.editor.Editor;
import com.ss.rlib.logging.Logger;
import com.ss.rlib.logging.LoggerManager;
import com.ss.rlib.manager.InitializeManager;
import com.ss.rlib.plugin.ConfigurablePluginSystem;
import com.ss.rlib.plugin.PluginSystem;
import com.ss.rlib.util.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The manager to work with plugins.
 *
 * @author JavaSaBr
 */
public class PluginManager {

    @NotNull
    private static final Logger LOGGER = LoggerManager.getLogger(PluginManager.class);

    @Nullable
    private static PluginManager instance;

    @NotNull
    public static PluginManager getInstance() {
        if (instance == null) instance = new PluginManager();
        return instance;
    }

    @NotNull
    private final PluginSystem pluginSystem;

    private PluginManager() {
        InitializeManager.valid(getClass());

        final ConfigurablePluginSystem configurablePluginSystem = newBasePluginSystem(getClass().getClassLoader());
        final String embeddedPath = System.getProperty("editor.embedded.plugins.path");

        if (embeddedPath != null) {
            final Path embeddedPluginPath = Paths.get(embeddedPath);
            configurablePluginSystem.configureEmbeddedPluginPath(embeddedPluginPath);
        } else {
            final Path rootFolder = Utils.getRootFolderFromClass(Editor.class);
            final Path embeddedPluginPath = rootFolder.resolve("embedded-plugins");
            if (Files.exists(embeddedPluginPath)) {
                configurablePluginSystem.configureEmbeddedPluginPath(embeddedPluginPath);
            } else {
                LOGGER.warning(this, "The embedded plugin folder doesn't exists.");
            }
        }

        configurablePluginSystem.preLoad();
        configurablePluginSystem.initialize();

        this.pluginSystem = configurablePluginSystem;
    }
}
