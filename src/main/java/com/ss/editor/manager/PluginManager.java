package com.ss.editor.manager;

import static com.ss.rlib.plugin.impl.PluginSystemFactory.newBasePluginSystem;
import com.jme3.asset.AssetManager;
import com.ss.editor.JmeApplication;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.config.Config;
import com.ss.editor.plugin.EditorPlugin;
import com.ss.rlib.logging.Logger;
import com.ss.rlib.logging.LoggerManager;
import com.ss.rlib.manager.InitializeManager;
import com.ss.rlib.plugin.ConfigurablePluginSystem;
import com.ss.rlib.plugin.Plugin;
import com.ss.rlib.plugin.PluginContainer;
import com.ss.rlib.plugin.exception.PreloadPluginException;
import com.ss.rlib.util.FileUtils;
import com.ss.rlib.util.Utils;
import com.ss.rlib.util.array.Array;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

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

    /**
     * The plugin system.
     */
    @NotNull
    private final ConfigurablePluginSystem pluginSystem;

    private PluginManager() {
        InitializeManager.valid(getClass());

        this.pluginSystem = newBasePluginSystem(getClass().getClassLoader());
        this.pluginSystem.setAppVersion(Config.APP_VERSION);

        final Path folderInUserHome = Config.getAppFolderInUserHome();
        final String embeddedPath = System.getProperty("editor.embedded.plugins.path");

        if (embeddedPath != null) {
            final Path embeddedPluginPath = Paths.get(embeddedPath);
            LOGGER.debug(this, "embedded plugin path: " + embeddedPluginPath);
            pluginSystem.configureEmbeddedPluginPath(embeddedPluginPath);
        } else {
            final Path rootFolder = Utils.getRootFolderFromClass(JmeApplication.class);
            final Path embeddedPluginPath = rootFolder.resolve("embedded-plugins");
            LOGGER.debug(this, "embedded plugin path: " + embeddedPluginPath);
            if (Files.exists(embeddedPluginPath)) {
                pluginSystem.configureEmbeddedPluginPath(embeddedPluginPath);
            } else {
                LOGGER.warning(this, "The embedded plugin folder doesn't exists.");
            }
        }

        final Path userPluginsFolder = folderInUserHome.resolve("plugins");

        LOGGER.debug(this, "installation plugin path: " + userPluginsFolder);

        if (!Files.exists(userPluginsFolder)) {
            Utils.run(() -> Files.createDirectories(userPluginsFolder));
        }

        pluginSystem.configureInstallationPluginsPath(userPluginsFolder);

        try {
            pluginSystem.preLoad();
        } catch (final PreloadPluginException e) {
            FileUtils.delete(e.getPath());
            throw e;
        }

        pluginSystem.initialize();

        final InitializationManager initializationManager = InitializationManager.getInstance();
        initializationManager.addOnBeforeCreateJmeContext(this::onBeforeCreateJmeContext);
        initializationManager.addOnAfterCreateJmeContext(this::onAfterCreateJmeContext);
        initializationManager.addOnBeforeCreateJavaFxContext(this::onBeforeCreateJavaFxContext);
        initializationManager.addOnAfterCreateJavaFxContext(this::onAfterCreateJavaFxContext);
        initializationManager.addOnFinishLoading(this::onFinishLoading);
    }

    /**
     * Install a new plugin to the system.
     *
     * @param path the path to the plugin.
     */
    public void installPlugin(@NotNull final Path path) {
        pluginSystem.installPlugin(path, false);
    }

    /**
     * Remove a plugin from this editor.
     *
     * @param plugin the plugin.
     */
    public void removePlugin(@NotNull final EditorPlugin plugin) {
        pluginSystem.removePlugin(plugin);
    }

    /**
     * Do some things before when JME context will be created.
     */
    @JmeThread
    private void onBeforeCreateJmeContext() {
        final Array<Plugin> plugins = pluginSystem.getPlugins();
        plugins.stream().filter(EditorPlugin.class::isInstance)
                .map(EditorPlugin.class::cast)
                .forEach(editorPlugin -> editorPlugin.onBeforeCreateJmeContext(pluginSystem));
    }

    /**
     * Do some things after when JME context was created.
     */
    @JmeThread
    private void onAfterCreateJmeContext() {
        final Array<Plugin> plugins = pluginSystem.getPlugins();
        plugins.stream().filter(EditorPlugin.class::isInstance)
                .map(EditorPlugin.class::cast)
                .forEach(editorPlugin -> {
                    editorPlugin.onAfterCreateJmeContext(pluginSystem);

                    final PluginContainer container = editorPlugin.getContainer();
                    final URLClassLoader classLoader = container.getClassLoader();

                    final JmeApplication jmeApplication = JmeApplication.getInstance();
                    final AssetManager assetManager = jmeApplication.getAssetManager();
                    assetManager.addClassLoader(classLoader);
                });
    }

    /**
     * Do some things before when JavaFX context will be created.
     */
    @FxThread
    private void onBeforeCreateJavaFxContext() {
        final Array<Plugin> plugins = pluginSystem.getPlugins();
        plugins.stream().filter(EditorPlugin.class::isInstance)
                .map(EditorPlugin.class::cast)
                .forEach(editorPlugin -> editorPlugin.onBeforeCreateJavaFxContext(pluginSystem));
    }

    /**
     * Do some things after when JavaFX context was created.
     */
    @FxThread
    private void onAfterCreateJavaFxContext() {
        final Array<Plugin> plugins = pluginSystem.getPlugins();
        plugins.stream().filter(EditorPlugin.class::isInstance)
                .map(EditorPlugin.class::cast)
                .forEach(editorPlugin -> editorPlugin.onAfterCreateJavaFxContext(pluginSystem));
    }

    /**
     * Do some things before when the editor is ready to work.
     */
    @FxThread
    private void onFinishLoading() {
        final Array<Plugin> plugins = pluginSystem.getPlugins();
        plugins.stream().filter(EditorPlugin.class::isInstance)
                .map(EditorPlugin.class::cast)
                .forEach(editorPlugin -> editorPlugin.onFinishLoading(pluginSystem));
    }

    /**
     * Handle each loaded plugin.
     *
     * @param consumer the consumer.
     */
    public void handlePlugins(@NotNull final Consumer<EditorPlugin> consumer) {
        final Array<Plugin> plugins = pluginSystem.getPlugins();
        plugins.stream().filter(EditorPlugin.class::isInstance)
                .map(EditorPlugin.class::cast)
                .forEach(consumer);
    }
}
