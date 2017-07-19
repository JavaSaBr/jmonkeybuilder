package com.ss.editor.manager;

import static com.ss.rlib.plugin.impl.PluginSystemFactory.newBasePluginSystem;
import com.ss.editor.Editor;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.JMEThread;
import com.ss.editor.plugin.EditorPlugin;
import com.ss.rlib.logging.Logger;
import com.ss.rlib.logging.LoggerManager;
import com.ss.rlib.manager.InitializeManager;
import com.ss.rlib.plugin.ConfigurablePluginSystem;
import com.ss.rlib.plugin.Plugin;
import com.ss.rlib.plugin.PluginSystem;
import com.ss.rlib.util.Utils;
import com.ss.rlib.util.array.Array;
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

    /**
     * Do some things before when JME context will be created.
     */
    @JMEThread
    public void onBeforeCreateJMEContext() {
        final Array<Plugin> plugins = pluginSystem.getPlugins();
        plugins.stream().filter(EditorPlugin.class::isInstance)
                .map(EditorPlugin.class::cast)
                .forEach(editorPlugin -> editorPlugin.onBeforeCreateJMEContext(pluginSystem));
    }

    /**
     * Do some things after when JME context was created.
     */
    @JMEThread
    public void onAfterCreateJMEContext() {
        final Array<Plugin> plugins = pluginSystem.getPlugins();
        plugins.stream().filter(EditorPlugin.class::isInstance)
                .map(EditorPlugin.class::cast)
                .forEach(editorPlugin -> editorPlugin.onAfterCreateJMEContext(pluginSystem));
    }

    /**
     * Do some things before when JavaFX context will be created.
     */
    @FXThread
    public void onBeforeCreateJavaFXContext() {
        final Array<Plugin> plugins = pluginSystem.getPlugins();
        plugins.stream().filter(EditorPlugin.class::isInstance)
                .map(EditorPlugin.class::cast)
                .forEach(editorPlugin -> editorPlugin.onBeforeCreateJavaFXContext(pluginSystem));
    }

    /**
     * Do some things after when JavaFX context was created.
     */
    @FXThread
    public void onAfterCreateJavaFXContext() {
        final Array<Plugin> plugins = pluginSystem.getPlugins();
        plugins.stream().filter(EditorPlugin.class::isInstance)
                .map(EditorPlugin.class::cast)
                .forEach(editorPlugin -> editorPlugin.onAfterCreateJavaFXContext(pluginSystem));
    }

    /**
     * Do some things before when the editor is ready to work.
     */
    @FXThread
    public void onFinishLoading() {
        final Array<Plugin> plugins = pluginSystem.getPlugins();
        plugins.stream().filter(EditorPlugin.class::isInstance)
                .map(EditorPlugin.class::cast)
                .forEach(editorPlugin -> editorPlugin.onFinishLoading(pluginSystem));
    }
}
