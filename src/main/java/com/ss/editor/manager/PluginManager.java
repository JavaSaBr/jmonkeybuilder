package com.ss.editor.manager;

import static com.ss.rlib.common.plugin.impl.PluginSystemFactory.newBasePluginSystem;
import com.ss.editor.JmeApplication;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.config.Config;
import com.ss.editor.plugin.EditorPlugin;
import com.ss.editor.ui.event.impl.PluginsLoadedEvent;
import com.ss.editor.util.EditorUtil;
import com.ss.editor.util.TimeTracker;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.common.manager.InitializeManager;
import com.ss.rlib.common.plugin.ConfigurablePluginSystem;
import com.ss.rlib.common.plugin.exception.PreloadPluginException;
import com.ss.rlib.common.util.FileUtils;
import com.ss.rlib.common.util.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    private static final Logger LOGGER = LoggerManager.getLogger(PluginManager.class);
    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    @Nullable
    private static PluginManager instance;

    public static @NotNull PluginManager getInstance() {

        if (instance == null) {
            instance = new PluginManager();
            AsyncEventManager.getInstance()
                    .notify(new PluginsLoadedEvent());
        }

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

        if(true) {
            return;
        }

        var folderInUserHome = Config.getAppFolderInUserHome();
        var embeddedPath = System.getProperty("editor.embedded.plugins.path2");

        if (embeddedPath != null && Files.exists(Paths.get(embeddedPath))) {
            var embeddedPluginPath = Paths.get(embeddedPath);
            LOGGER.debug(this, "embedded plugin path: " + embeddedPluginPath);
            pluginSystem.configureEmbeddedPluginPath(embeddedPluginPath);
        } else {
            var rootFolder = Utils.getRootFolderFromClass(JmeApplication.class);
            var embeddedPluginPath = rootFolder.resolve("embedded-plugins");
            LOGGER.debug(this, "embedded plugin path: " + embeddedPluginPath);
            if (Files.exists(embeddedPluginPath)) {
                pluginSystem.configureEmbeddedPluginPath(embeddedPluginPath);
            } else {
                LOGGER.warning(this, "The embedded plugin folder doesn't exists.");
            }
        }

        var userPluginsFolder = folderInUserHome.resolve("plugins");

        LOGGER.debug(this, "installation plugin path: " + userPluginsFolder);

        if (!Files.exists(userPluginsFolder)) {
            FileUtils.createDirectories(userPluginsFolder);
        }

        pluginSystem.configureInstallationPluginsPath(userPluginsFolder);
        try {
            pluginSystem.preLoad();
        } catch (PreloadPluginException e) {
            FileUtils.delete(e.getPath());
            throw e;
        }

        pluginSystem.initialize();

        /*
        var initManager = InitializationManager.getInstance();
        initManager.addOnBeforeCreateJmeContext(this::onBeforeCreateJmeContext);
        initManager.addOnAfterCreateJmeContext(this::onAfterCreateJmeContext);
        initManager.addOnBeforeCreateJavaFxContext(this::onBeforeCreateJavaFxContext);
        initManager.addOnAfterCreateJavaFxContext(this::onAfterCreateJavaFxContext);
        initManager.addOnFinishLoading(this::onFinishLoading);*/
    }

    /**
     * Install a new plugin to the system.
     *
     * @param path the path to the plugin.
     */
    public void installPlugin(@NotNull Path path) {
        pluginSystem.installPlugin(path, false);
    }

    /**
     * Remove a plugin from this editor.
     *
     * @param plugin the plugin.
     */
    public void removePlugin(@NotNull EditorPlugin plugin) {
        pluginSystem.removePlugin(plugin);
    }

    /**
     * Do some things before when JME context will be created.
     */
    @JmeThread
    private void onBeforeCreateJmeContext() {
        pluginSystem.getPlugins().stream()
                .filter(EditorPlugin.class::isInstance)
                .map(EditorPlugin.class::cast)
                .forEach(editorPlugin -> editorPlugin.onBeforeCreateJmeContext(pluginSystem));
    }

    /**
     * Do some things after when JME context was created.
     */
    @JmeThread
    private void onAfterCreateJmeContext() {
        pluginSystem.getPlugins().stream()
                .filter(EditorPlugin.class::isInstance)
                .map(EditorPlugin.class::cast)
                .forEach(editorPlugin -> {

                    editorPlugin.onAfterCreateJmeContext(pluginSystem);

                    var container = editorPlugin.getContainer();
                    var classLoader = container.getClassLoader();
                    var assetManager = EditorUtil.getAssetManager();
                    assetManager.addClassLoader(classLoader);
                });
    }

    /**
     * Do some things before when JavaFX context will be created.
     */
    @FxThread
    private void onBeforeCreateJavaFxContext() {
        pluginSystem.getPlugins().stream()
                .filter(EditorPlugin.class::isInstance)
                .map(EditorPlugin.class::cast)
                .forEach(editorPlugin -> editorPlugin.onBeforeCreateJavaFxContext(pluginSystem));
    }

    /**
     * Do some things after when JavaFX context was created.
     */
    @FxThread
    private void onAfterCreateJavaFxContext() {
        pluginSystem.getPlugins().stream()
                .filter(EditorPlugin.class::isInstance)
                .map(EditorPlugin.class::cast)
                .forEach(editorPlugin -> editorPlugin.onAfterCreateJavaFxContext(pluginSystem));
    }

    /**
     * Do some things before when the editor is ready to work.
     */
    @FxThread
    private void onFinishLoading() {
        pluginSystem.getPlugins().stream()
                .filter(EditorPlugin.class::isInstance)
                .map(EditorPlugin.class::cast)
                .forEach(editorPlugin -> editorPlugin.onFinishLoading(pluginSystem));
    }

    /**
     * Handle each loaded plugin now.
     *
     * @param consumer the consumer.
     */
    @FromAnyThread
    public void handlePluginsNow(@NotNull Consumer<EditorPlugin> consumer) {
        handlePlugins(consumer);
    }

    /**
     * Handle each loaded plugin in background.
     *
     * @param consumer the consumer.
     */
    @FromAnyThread
    public void handlePluginsInBackground(@NotNull Consumer<EditorPlugin> consumer) {
        EXECUTOR_MANAGER.addBackgroundTask(() -> handlePlugins(consumer));
    }

    /**
     * Handle each loaded plugin in background and execute the result task in result.
     *
     * @param consumer the consumer.
     * @param result   the result task.
     */
    @FromAnyThread
    public void handlePluginsInBackground(@NotNull Consumer<EditorPlugin> consumer, @NotNull Runnable result) {
        EXECUTOR_MANAGER.addBackgroundTask(() -> {
            handlePlugins(consumer);
            result.run();
        });
    }

    @FromAnyThread
    private void handlePlugins(@NotNull Consumer<EditorPlugin> consumer) {
        pluginSystem.getPlugins().stream()
                .filter(EditorPlugin.class::isInstance)
                .map(EditorPlugin.class::cast)
                .forEach(consumer);
    }
}
