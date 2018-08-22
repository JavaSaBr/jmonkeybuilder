package com.ss.builder.ui.css;

import static com.ss.editor.config.DefaultSettingsProvider.Defaults.PREF_DEFAULT_THEME;
import static com.ss.editor.config.DefaultSettingsProvider.Preferences.PREF_UI_THEME;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.builder.ui.event.impl.CssAppliedEvent;
import com.ss.builder.ui.event.impl.FxSceneCreatedEvent;
import com.ss.builder.ui.event.impl.PluginCssLoadedEvent;
import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.manager.AsyncEventManager;
import com.ss.editor.manager.AsyncEventManager.CombinedAsyncEventHandlerBuilder;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.ui.builder.EditorFxSceneBuilder;
import com.ss.editor.ui.event.impl.CssAppliedEvent;
import com.ss.editor.ui.event.impl.FxSceneCreatedEvent;
import com.ss.editor.ui.event.impl.PluginCssLoadedEvent;
import com.ss.editor.util.EditorUtils;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.common.manager.InitializeManager;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;

import java.net.URL;

/**
 * The registry of available css files.
 *
 * @author JavaSaBr
 */
public class CssRegistry {

    private static final Logger LOGGER = LoggerManager.getLogger(CssRegistry.class);

    /**
     * The path to the base CSS styles.
     */
    public static final String CSS_FILE_BASE = "ui/css/base.css";

    /**
     * The path to the external CSS styles.
     */
    public static final String CSS_FILE_EXTERNAL = "ui/css/external.css";

    /**
     * The path to the custom ids CSS styles.
     */
    public static final String CSS_FILE_CUSTOM_IDS = "ui/css/custom_ids.css";

    /**
     * The path to the custom classes CSS styles.
     */
    public static final String CSS_FILE_CUSTOM_CLASSES = "ui/css/custom_classes.css";

    private static final CssRegistry INSTANCE = new CssRegistry();

    @FromAnyThread
    public static @NotNull CssRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * The list of available css files.
     */
    @NotNull
    private final Array<String> availableCssFiles;

    private CssRegistry() {
        InitializeManager.valid(getClass());

        this.availableCssFiles = ArrayFactory.newCopyOnModifyArray(String.class);

        var classLoader = EditorFxSceneBuilder.class.getClassLoader();

        register(CSS_FILE_BASE, classLoader);
        register(CSS_FILE_EXTERNAL, classLoader);
        register(CSS_FILE_CUSTOM_IDS, classLoader);
        register(CSS_FILE_CUSTOM_CLASSES, classLoader);

        // if a scene was created before when we loaded plugin's css.
        CombinedAsyncEventHandlerBuilder.of(this::applyCssToScene)
                .add(FxSceneCreatedEvent.EVENT_TYPE)
                .buildAndRegister();

        CombinedAsyncEventHandlerBuilder.of(this::applyCssToScene)
                .add(PluginCssLoadedEvent.EVENT_TYPE)
                .add(FxSceneCreatedEvent.EVENT_TYPE)
                .buildAndRegister();

        LOGGER.info("initialized.");
    }

    /**
     * Apply all loaded CSS files to the main scene.
     */
    @BackgroundThread
    private void applyCssToScene() {
        var executorManager = ExecutorManager.getInstance();
        executorManager.addFxTask(() -> {

            var editorConfig = EditorConfig.getInstance();
            var theme = editorConfig.getEnum(PREF_UI_THEME, PREF_DEFAULT_THEME);

            var scene = EditorUtils.getFxScene();
            var stylesheets = scene.getStylesheets();

            // check if we have nothing new
            if (stylesheets.size() == getAvailableCssFiles().size() + 1) {
                return;
            }

            stylesheets.clear();
            stylesheets.addAll(getAvailableCssFiles());
            stylesheets.add(theme.getCssFile());

            AsyncEventManager.getInstance()
                    .notify(new CssAppliedEvent());

            LOGGER.info("applied CSS to the main scene.");
        });
    }

    /**
     * Add the CSS file to this registry.
     *
     * @param cssFile the URL to the CSS file.
     */
    @FromAnyThread
    public void register(@NotNull URL cssFile) {
        availableCssFiles.add(cssFile.toExternalForm());
    }

    /**
     * Add the CSS file to this registry.
     *
     * @param cssFile     the path to CSS file.
     * @param classLoader the class loader which can load this path.
     */
    @FromAnyThread
    public void register(@NotNull String cssFile, @NotNull ClassLoader classLoader) {
        register(notNull(classLoader.getResource(cssFile)));
    }

    /**
     * Get a list of available CSS files.
     *
     * @return the list of available css files.
     */
    @FromAnyThread
    public @NotNull Array<String> getAvailableCssFiles() {
        return availableCssFiles;
    }
}
