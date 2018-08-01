package com.ss.editor.manager;

import static com.ss.editor.config.DefaultSettingsProvider.Preferences.PREF_USER_CLASSES_FOLDER;
import static com.ss.editor.config.DefaultSettingsProvider.Preferences.PREF_USER_LIBRARY_FOLDER;

import com.ss.editor.FileExtensions;
import com.ss.editor.JmeApplication;
import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.manager.AsyncEventManager.CombinedAsyncEventHandlerBuilder;
import com.ss.editor.ui.event.impl.ClasspathReloadedEvent;
import com.ss.editor.ui.event.impl.CoreClassesScannedEvent;
import com.ss.editor.ui.event.impl.JmeContextCreatedEvent;
import com.ss.editor.ui.event.impl.ManagersInitializedEvent;
import com.ss.editor.util.EditorUtils;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
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

    private static final String[] JAR_EXTENSIONS =
            ArrayFactory.toArray(FileExtensions.JAVA_LIBRARY);

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
     * The local libraries scanner.
     */
    @NotNull
    private final AtomicReference<ClassPathScanner> localLibrariesScanner;

    /**
     * The local classes scanner.
     */
    @NotNull
    private final AtomicReference<ClassPathScanner> localClassesScanner;

    /**
     * The local libraries class loader.
     */
    @NotNull
    private final AtomicReference<URLClassLoader> localLibrariesLoader;

    /**
     * The local classes class loader.
     */
    @NotNull
    private final AtomicReference<URLClassLoader> localClassesLoader;

    /**
     * The core classpath scanner.
     */
    @NotNull
    private volatile ClassPathScanner coreScanner;

    private ClasspathManager() {
        InitializeManager.valid(getClass());

        this.coreScanner = ClassPathScannerFactory.newDefaultScanner();
        this.librariesLoader = new AtomicReference<>();
        this.classesLoader = new AtomicReference<>();
        this.customScanner = new AtomicReference<>();
        this.localLibrariesScanner = new AtomicReference<>();
        this.localClassesScanner = new AtomicReference<>();
        this.localLibrariesLoader = new AtomicReference<>();
        this.localClassesLoader = new AtomicReference<>();

        CombinedAsyncEventHandlerBuilder.of(this::scanCoreClasses)
                .add(ManagersInitializedEvent.EVENT_TYPE)
                .buildAndRegister();

        CombinedAsyncEventHandlerBuilder.of(this::reload)
                .add(CoreClassesScannedEvent.EVENT_TYPE)
                .add(JmeContextCreatedEvent.EVENT_TYPE)
                .buildAndRegister();

        LOGGER.info("initialized.");
    }

    /**
     * Scan core classes in background.
     */
    @BackgroundThread
    private void scanCoreClasses() {

        var coreScanner = ClassPathScannerFactory.newManifestScanner(JmeApplication.class, "Class-Path");
        coreScanner.setUseSystemClasspath(true);
        coreScanner.scan(this::filterCoreLibraries);

        this.coreScanner = coreScanner;

        AsyncEventManager.getInstance()
                .notify(new CoreClassesScannedEvent());

        LOGGER.info("scanned core classes.");
    }

    @BackgroundThread
    private boolean filterCoreLibraries(@NotNull String path) {

        if (Files.isDirectory(Paths.get(path))) {
            return true;
        } else if (!CORE_LIBRARIES_NAMES.anyMatch(path, (pattern, pth) -> pth.contains(pattern))) {
            return false;
        } else if (path.contains("natives")) {
            return false;
        } else {
            return !path.contains("sources") && !path.contains("javadoc");
        }
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
            var urls = Array.ofType(URL.class);

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
            updateClassLoader(userLibrariesLoader, getLibrariesLoader(), librariesLoader);
            updateClassLoader(userClassesLoader, getClassesLoader(), classesLoader);

            AsyncEventManager.getInstance()
                    .notify(new ClasspathReloadedEvent());

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
        ExecutorManager.getInstance()
                .addBackgroundTask(this::reloadInBackground);
    }

    /**
     * Prepare a relevant user's libraries loader.
     */
    @BackgroundThread
    private @Nullable URLClassLoader createUserLibrariesLoader() {

        var path = EditorConfig.getInstance()
                .getFile(PREF_USER_LIBRARY_FOLDER);

        if (path == null) {
            return null;
        }

        var urls = FileUtils.getFiles(path, false, JAR_EXTENSIONS)
                .stream()
                .map(FileUtils::getUrl)
                .toArray(URL[]::new);

        return new URLClassLoader(urls, getClass().getClassLoader());
    }

    /**
     * Update compiled classes loader.
     */
    @FromAnyThread
    private @Nullable URLClassLoader createUserClassesLoader(@Nullable ClassLoader userLibrariesLoader) {

        var path = EditorConfig.getInstance()
                .getFile(PREF_USER_CLASSES_FOLDER);

        if (path == null) {
            return null;
        }

        var folders = Array.ofType(Path.class);

        FileUtils.forEachR(path, folders,
                Files::isDirectory,
                Collection::add);

        var urls = folders.stream()
                .map(FileUtils::getUrl)
                .toArray(URL[]::new);

        var parent = userLibrariesLoader == null ?
                getClass().getClassLoader() : userLibrariesLoader;

        return new URLClassLoader(urls, parent);
    }

    /**
     * Update class loader.
     */
    @BackgroundThread
    private void updateClassLoader(
            @Nullable URLClassLoader newLoader,
            @Nullable URLClassLoader currentLoader,
            @NotNull AtomicReference<URLClassLoader> reference
    ) {

        if (!reference.compareAndSet(currentLoader, newLoader)) {
            return;
        }

        var assetManager = EditorUtils.getAssetManager();

        if (currentLoader != null) {
            ExecutorManager.getInstance()
                .addJmeTask(() -> assetManager.removeClassLoader(currentLoader));
        }

        if (newLoader != null) {
            ExecutorManager.getInstance()
                .addJmeTask(() -> assetManager.addClassLoader(newLoader));
        }
    }

    /**
     * Load local libraries.
     */
    @FromAnyThread
    public @NotNull ClasspathManager loadLocalLibraries(@NotNull Array<Path> libraries) {

        var currentClassLoader = getLocalLibrariesLoader();
        var newClassLoader = ClassPathScanner.NULL_CLASS_LOADER;
        var currentScanner = getLocalLibrariesScanner();
        var newScanner = ClassPathScanner.NULL_SCANNER;

        try {

            if (libraries.isEmpty()) {
                return this;
            }

            var urlArray = libraries.stream()
                    .map(FileUtils::getUrl)
                    .toArray(URL[]::new);

            newClassLoader = new URLClassLoader(urlArray, getClass().getClassLoader());

            var paths = Arrays.stream(newClassLoader.getURLs())
                    .map(url -> Utils.get(url, URL::toURI))
                    .map(Paths::get)
                    .map(Path::toString)
                    .toArray(String[]::new);

            var scanner = ClassPathScannerFactory.newDefaultScanner(newClassLoader);
            scanner.addAdditionalPaths(paths);
            scanner.setUseSystemClasspath(false);
            scanner.scan();

        } finally {
            updateClassLoader(newClassLoader, currentClassLoader, localLibrariesLoader);
            localLibrariesScanner.compareAndSet(currentScanner, newScanner);
        }

        return this;
    }

    /**
     * Load local classes.
     */
    @FromAnyThread
    public @NotNull ClasspathManager loadLocalClasses(@Nullable Path output) {

        var currentClassLoader = getLocalClassesLoader();
        var newClassLoader = ClassPathScanner.NULL_CLASS_LOADER;
        var currentScanner = getLocalClassesScanner();
        var newScanner = ClassPathScanner.NULL_SCANNER;

        try {

            if (output == null || !Files.exists(output)) {
                return this;
            }

            var folders = Array.ofType(Path.class);

            FileUtils.forEachR(output, folders,
                    Files::isDirectory,
                    Collection::add);

            var urlArray = folders.stream()
                    .map(FileUtils::getUrl)
                    .toArray(URL[]::new);

            var librariesLoader = getLocalLibrariesLoader();
            var parent = librariesLoader == null? getClass().getClassLoader() : librariesLoader;

            newClassLoader = new URLClassLoader(urlArray, parent);

            var paths = Arrays.stream(newClassLoader.getURLs())
                    .map(url -> Utils.get(url, URL::toURI))
                    .map(Paths::get)
                    .map(Path::toString)
                    .toArray(String[]::new);

            var scanner = ClassPathScannerFactory.newDefaultScanner(newClassLoader);
            scanner.addAdditionalPaths(paths);
            scanner.setUseSystemClasspath(false);
            scanner.scan();

        } finally {
            updateClassLoader(newClassLoader, currentClassLoader, localClassesLoader);
            localClassesScanner.compareAndSet(currentScanner, newScanner);
        }

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
        return localLibrariesLoader.get();
    }

    /**
     * Get the local classes class loader.
     *
     * @return the local classes class loader.
     */
    @FromAnyThread
    private @Nullable URLClassLoader getLocalClassesLoader() {
        return localClassesLoader.get();
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
        return localLibrariesScanner.get();
    }

    /**
     * Get the local classes scanner.
     *
     * @return the local classes scanner.
     */
    @FromAnyThread
    private @Nullable ClassPathScanner getLocalClassesScanner() {
        return localClassesScanner.get();
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

        var result = Array.<Class<T>>ofType(Class.class);

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
            PluginManager.getInstance()
                    .handlePluginsNow(plugin -> plugin.getContainer()
                            .getScanner()
                            .findImplements(result, interfaceClass));
        }

        return result;
    }
}
