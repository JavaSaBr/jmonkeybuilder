package com.ss.editor.manager;

import static com.ss.editor.config.DefaultSettingsProvider.Preferences.PREF_USER_CLASSES_FOLDER;
import static com.ss.editor.config.DefaultSettingsProvider.Preferences.PREF_USER_LIBRARY_FOLDER;
import static com.ss.rlib.common.util.array.ArrayFactory.toArray;
import com.jme3.asset.AssetManager;
import com.ss.editor.FileExtensions;
import com.ss.editor.JmeApplication;
import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.manager.AsyncEventManager.CombinedAsyncEventHandlerBuilder;
import com.ss.editor.ui.event.impl.ManagersInitializedEvent;
import com.ss.editor.ui.event.impl.ClasspathReloadedEvent;
import com.ss.editor.ui.event.impl.CoreClassesScannedEvent;
import com.ss.editor.ui.event.impl.JmeContextCreatedEvent;
import com.ss.editor.util.EditorUtil;
import com.ss.editor.util.TimeTracker;
import com.ss.rlib.common.classpath.ClassPathScanner;
import com.ss.rlib.common.classpath.ClassPathScannerFactory;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.common.manager.InitializeManager;
import com.ss.rlib.common.util.FileUtils;
import com.ss.rlib.common.util.Utils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The class to manage classpath.
 *
 * @author JavaSaBr
 */
public class ClasspathManager {

    private static final Logger LOGGER = LoggerManager.getLogger(ClasspathManager.class);

    private static final EditorConfig EDITOR_CONFIG = EditorConfig.getInstance();
    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();
    private static final AsyncEventManager ASYNC_EVENT_MANAGER = AsyncEventManager.getInstance();

    public enum Scope {
        CORE,
        CUSTOM,
        PLUGINS,
        LOCAL_LIBRARIES,
        LOCAL_CLASSES;

        public static final Set<Scope> ONLY_CORE = EnumSet.of(CORE);
        public static final Set<Scope> ALL = EnumSet.allOf(Scope.class);
        public static final Set<Scope> CORE_AND_CUSTOM_AND_LOCAL =
            EnumSet.of(CORE, CUSTOM, LOCAL_CLASSES, LOCAL_LIBRARIES);
    }

    private static final String[] JAR_EXTENSIONS = toArray(FileExtensions.JAVA_LIBRARY);

    public static final Array<String> CORE_LIBRARIES_NAMES = Array.of(
            "jme3-core",
            "jme3-terrain",
            "jme3-effects",
            "jme3-testdata",
            "jme3-plugins",
            "tonegod",
            "jmonkeybuilder"
    );

    @Nullable
    private static ClasspathManager instance;

    @FromAnyThread
    public static @NotNull ClasspathManager getInstance() {
        if (instance == null) instance = new ClasspathManager();
        return instance;
    }

    /**
     * The libraries class loader.
     */
    @NotNull
    private final AtomicReference<URLClassLoader> librariesLoader;

    /**
     * The classes class loader.
     */
    @NotNull
    private final AtomicReference<URLClassLoader> classesLoader;

    /**
     * The custom classpath scanner.
     */
    @NotNull
    private final AtomicReference<ClassPathScanner> customScanner;

    /**
     * The core classpath scanner.
     */
    @NotNull
    private volatile ClassPathScanner coreScanner;


    /**
     * The local libraries scanner.
     */
    @Nullable
    private volatile ClassPathScanner localLibrariesScanner;

    /**
     * The local classes scanner.
     */
    @Nullable
    private volatile ClassPathScanner localClassesScanner;


    /**
     * The local libraries class loader.
     */
    @Nullable
    private volatile URLClassLoader localLibrariesLoader;

    /**
     * The local classes class loader.
     */
    @Nullable
    private volatile URLClassLoader localClassesLoader;

    private ClasspathManager() {
        InitializeManager.valid(getClass());

        TimeTracker.getStartupTracker(TimeTracker.STARTPUL_LEVEL_5)
                .start();

        this.coreScanner = ClassPathScannerFactory.newDefaultScanner();
        this.librariesLoader = new AtomicReference<>();
        this.classesLoader = new AtomicReference<>();
        this.customScanner = new AtomicReference<>();

        CombinedAsyncEventHandlerBuilder.of(this::scanCoreClasses)
                .add(ManagersInitializedEvent.EVENT_TYPE)
                .buildAndRegister();

        CombinedAsyncEventHandlerBuilder.of(this::reload)
                .add(CoreClassesScannedEvent.EVENT_TYPE)
                .add(JmeContextCreatedEvent.EVENT_TYPE)
                .buildAndRegister();

        TimeTracker.getStartupTracker(TimeTracker.STARTPUL_LEVEL_5)
                .finish(() -> "Initialized ClasspathManager");

        LOGGER.info("initialized.");
    }

    /**
     * Scan core classes in background.
     */
    @BackgroundThread
    private void scanCoreClasses() {

        var coreScanner = ClassPathScannerFactory.newManifestScanner(JmeApplication.class, "Class-Path");
        coreScanner.setUseSystemClasspath(true);
        coreScanner.scan(path -> {

            if (Files.isDirectory(Paths.get(path))) {
                return true;
            } else if (CORE_LIBRARIES_NAMES.search(path, (pattern, pth) -> pth.contains(pattern)) == null) {
                return false;
            } else if (path.contains("natives")) {
                return false;
            } else {
                return !path.contains("sources") && !path.contains("javadoc");
            }
        });

        this.coreScanner = coreScanner;

        ASYNC_EVENT_MANAGER.notify(new CoreClassesScannedEvent());

        LOGGER.info("scanned core classes.");
    }

    /**
     * Reload custom classes and libraries.
     */
    @BackgroundThread
    private void reloadInBackground() {

        var userLibrariesLoader = createUserLibrariesLoader();
        var userClassesLoader = createUserClassesLoader(userLibrariesLoader);
        var currentScanner = getCustomScanner();
        try {

            if (userLibrariesLoader == null && userClassesLoader == null) {
                customScanner.compareAndSet(currentScanner, null);
                return;
            }

            var classLoader = userClassesLoader == null ? userLibrariesLoader : userClassesLoader;
            var scanner = ClassPathScannerFactory.newDefaultScanner(classLoader);
            var urls = ArrayFactory.<URL>newArray(URL.class);

            if (userLibrariesLoader != null) {
                urls.addAll(userLibrariesLoader.getURLs());
            }

            if (userClassesLoader != null) {
                urls.addAll(userClassesLoader.getURLs());
            }

            var paths = urls.stream()
                    .map(url -> Utils.get(url, URL::toURI))
                    .map(Paths::get)
                    .map(Path::toString)
                    .toArray(String[]::new);

            scanner.addAdditionalPaths(paths);
            scanner.setUseSystemClasspath(false);
            scanner.scan();

            customScanner.compareAndSet(currentScanner, scanner);

        } finally {
            updateLibrariesLoader(userLibrariesLoader);
            updateClassesLoader(userClassesLoader);
            ASYNC_EVENT_MANAGER.notify(new ClasspathReloadedEvent());
            LOGGER.info("reloaded.");
        }
    }

    /**
     * Get all available resources from classpath.
     *
     * @return the list of resources.
     */
    public @NotNull Array<String> getAllResources() {
        return coreScanner.getFoundResources();
    }

    /**
     * Reload custom classes and libraries.
     */
    @FromAnyThread
    public void reload() {
        EXECUTOR_MANAGER.addBackgroundTask(this::reloadInBackground);
    }

    /**
     * Prepare a relevant user's libraries loader.
     */
    @BackgroundThread
    private @Nullable URLClassLoader createUserLibrariesLoader() {

        var path = EDITOR_CONFIG.getFile(PREF_USER_LIBRARY_FOLDER);
        if (path == null) {
            return null;
        }

        var jars = FileUtils.getFiles(path, false, JAR_EXTENSIONS);
        var urls = jars.stream()
                .map(FileUtils::getUrl)
                .toArray(URL[]::new);

        return new URLClassLoader(urls, getClass().getClassLoader());
    }

    /**
     * Update compiled classes loader.
     */
    @FromAnyThread
    private @Nullable URLClassLoader createUserClassesLoader(@Nullable ClassLoader userLibrariesLoader) {

        var path = EDITOR_CONFIG.getFile(PREF_USER_CLASSES_FOLDER);
        if (path == null) {
            return null;
        }

        var folders = ArrayFactory.<Path>newArray(Path.class);

        Utils.run(path, folders, (dir, toStore) -> {
            var stream = Files.newDirectoryStream(dir);
            stream.forEach(subFile -> {
                if (Files.isDirectory(subFile)) {
                    toStore.add(subFile);
                }
            });
        });

        var urls = folders.stream()
                .map(FileUtils::getUrl)
                .toArray(URL[]::new);

        var parent = userLibrariesLoader == null ?
                getClass().getClassLoader() : userLibrariesLoader;

        return new URLClassLoader(urls, parent);
    }

    /**
     * Update libraries loader.
     */
    @BackgroundThread
    private void updateLibrariesLoader(@Nullable URLClassLoader newLoader) {

        var currentLoader = getLibrariesLoader();

        if (!librariesLoader.compareAndSet(currentLoader, newLoader)) {
            return;
        }

        var assetManager = EditorUtil.getAssetManager();

        if (currentLoader != null) {
            EXECUTOR_MANAGER.addJmeTask(() ->
                    assetManager.removeClassLoader(currentLoader));
        }

        EXECUTOR_MANAGER.addJmeTask(() ->
                assetManager.addClassLoader(newLoader));
    }

    /**
     * Update compiled classes loader.
     */
    @BackgroundThread
    private void updateClassesLoader(@Nullable URLClassLoader newLoader) {

        var currentLoader = getClassesLoader();

        if (!classesLoader.compareAndSet(currentLoader, newLoader)) {
            return;
        }

        var assetManager = EditorUtil.getAssetManager();

        if (currentLoader != null) {
            EXECUTOR_MANAGER.addJmeTask(() ->
                    assetManager.removeClassLoader(currentLoader));
        }

        EXECUTOR_MANAGER.addJmeTask(() ->
                assetManager.addClassLoader(newLoader));
    }

    /**
     * Load local libraries.
     */
    @FromAnyThread
    public synchronized @NotNull ClasspathManager loadLocalLibraries(@NotNull Array<Path> libraries) {

        final AssetManager assetManager = EditorUtil.getAssetManager();
        final URLClassLoader currentClassLoader = getLocalLibrariesLoader();

        if (currentClassLoader != null) {
            assetManager.removeClassLoader(currentClassLoader);
            setLocalLibrariesLoader(null);
        }

        if (libraries.isEmpty()) {
            this.localLibrariesScanner = null;
            return this;
        }

        final URL[] urlArray = libraries.stream()
                .map(FileUtils::getUrl)
                .toArray(URL[]::new);

        final URLClassLoader classLoader = new URLClassLoader(urlArray, getClass().getClassLoader());

        assetManager.addClassLoader(classLoader);
        setLocalLibrariesLoader(classLoader);

        final ClassPathScanner scanner = ClassPathScannerFactory.newDefaultScanner(classLoader);
        final Array<URL> urls = ArrayFactory.asArray(classLoader.getURLs());
        final String[] paths = urls.stream().map(url -> Utils.get(url, URL::toURI))
                .map(Paths::get)
                .map(Path::toString)
                .toArray(String[]::new);

        scanner.addAdditionalPaths(paths);
        scanner.setUseSystemClasspath(false);
        scanner.scan();

        this.localLibrariesScanner = scanner;
        return this;
    }

    /**
     * Load local classes.
     */
    @FromAnyThread
    public synchronized @NotNull ClasspathManager loadLocalClasses(@Nullable final Path output) {

        final AssetManager assetManager = EditorUtil.getAssetManager();
        final URLClassLoader currentClassLoader = getLocalClassesLoader();

        if (currentClassLoader != null) {
            assetManager.removeClassLoader(currentClassLoader);
            setLocalClassesLoader(null);
        }

        if (output == null || !Files.exists(output)) {
            this.localClassesScanner = null;
            return this;
        }

        final Array<Path> folders = ArrayFactory.newArray(Path.class);

        Utils.run(output, folders, (dir, toStore) -> {
            final DirectoryStream<Path> stream = Files.newDirectoryStream(dir);
            stream.forEach(subFile -> {
                if (Files.isDirectory(subFile)) {
                    toStore.add(subFile);
                }
            });
        });

        final URL[] urlArray = folders.stream()
                .map(FileUtils::getUrl)
                .toArray(URL[]::new);

        final ClassLoader librariesLoader = getLocalLibrariesLoader();
        final ClassLoader parent = librariesLoader == null? getClass().getClassLoader() : librariesLoader;
        final URLClassLoader classLoader = new URLClassLoader(urlArray, parent);

        assetManager.addClassLoader(classLoader);
        setLocalClassesLoader(classLoader);

        final ClassPathScanner scanner = ClassPathScannerFactory.newDefaultScanner(classLoader);
        final Array<URL> urls = ArrayFactory.asArray(classLoader.getURLs());
        final String[] paths = urls.stream().map(url -> Utils.get(url, URL::toURI))
                .map(Paths::get)
                .map(Path::toString)
                .toArray(String[]::new);

        scanner.addAdditionalPaths(paths);
        scanner.setUseSystemClasspath(false);
        scanner.scan();

        this.localClassesScanner = scanner;
        return this;
    }

    /**
     * Get the library loader.
     *
     * @return the library loader.
     */
    @FromAnyThread
    public @Nullable URLClassLoader getLibrariesLoader() {
        return librariesLoader.get();
    }

    /**
     * Get the classes loader.
     *
     * @return the classes loader.
     */
    @FromAnyThread
    public @Nullable URLClassLoader getClassesLoader() {
        return classesLoader.get();
    }

    /**
     * Get the local libraries class loader.
     *
     * @return the local libraries class loader.
     */
    @FromAnyThread
    private @Nullable URLClassLoader getLocalLibrariesLoader() {
        return localLibrariesLoader;
    }

    /**
     * Set the local libraries class loader.
     *
     * @param localLibrariesLoader the local libraries class loader.
     */
    @FromAnyThread
    private void setLocalLibrariesLoader(@Nullable final URLClassLoader localLibrariesLoader) {
        this.localLibrariesLoader = localLibrariesLoader;
    }

    /**
     * Get the local classes class loader.
     *
     * @return the local classes class loader.
     */
    @FromAnyThread
    private @Nullable URLClassLoader getLocalClassesLoader() {
        return localClassesLoader;
    }

    /**
     * Set the local classes class loader.
     *
     * @param localClassesLoader the local classes class loader.
     */
    @FromAnyThread
    private void setLocalClassesLoader(@Nullable final URLClassLoader localClassesLoader) {
        this.localClassesLoader = localClassesLoader;
    }

    /**
     * Find all implementations of the interface class.
     *
     * @param <T> the type of an interface.
     * @param interfaceClass the interface class.
     * @return the list of all available implementations.
     */
    @FromAnyThread
    public @NotNull <T> Array<Class<T>> findImplements(@NotNull final Class<T> interfaceClass) {
        return findImplements(interfaceClass, Scope.ONLY_CORE);
    }

    /**
     * Get the custom scanner.
     *
     * @return the custom scanner.
     */
    @FromAnyThread
    private @Nullable ClassPathScanner getCustomScanner() {
        return customScanner.get();
    }

    /**
     * Get the local libraries scanner.
     *
     * @return the local libraries scanner.
     */
    @FromAnyThread
    private @Nullable ClassPathScanner getLocalLibrariesScanner() {
        return localLibrariesScanner;
    }

    /**
     * Get the local classes scanner.
     *
     * @return the local classes scanner.
     */
    @FromAnyThread
    private @Nullable ClassPathScanner getLocalClassesScanner() {
        return localClassesScanner;
    }

    /**
     * Find all implementations of the interface class.
     *
     * @param <T>            the type of an interface.
     * @param interfaceClass the interface class.
     * @param scope          the scope.
     * @return the list of all available implementations.
     */
    @FromAnyThread
    public @NotNull <T> Array<Class<T>> findImplements(
            @NotNull Class<T> interfaceClass,
            @NotNull Set<Scope> scope
    ) {

        var result = ArrayFactory.<Class<T>>newArray(Class.class);

        if (scope.contains(Scope.CORE)) {
            coreScanner.findImplements(result, interfaceClass);
        }

        var customScanner = getCustomScanner();
        if (customScanner != null && scope.contains(Scope.CUSTOM)) {
            customScanner.findImplements(result, interfaceClass);
        }

        var localLibrariesScanner = getLocalLibrariesScanner();
        if (localLibrariesScanner != null && scope.contains(Scope.LOCAL_LIBRARIES)) {
            localLibrariesScanner.findImplements(result, interfaceClass);
        }

        var localClassesScanner = getLocalClassesScanner();
        if (localClassesScanner != null && scope.contains(Scope.LOCAL_CLASSES)) {
            localClassesScanner.findImplements(result, interfaceClass);
        }

        if (scope.contains(Scope.PLUGINS)) {
            var pluginManager = PluginManager.getInstance();
            pluginManager.handlePluginsNow(plugin -> {
                var container = plugin.getContainer();
                container.getScanner().findImplements(result, interfaceClass);
            });
        }

        return result;
    }
}
