package com.ss.editor.manager;

import static com.ss.rlib.util.array.ArrayFactory.toArray;
import com.jme3.asset.AssetManager;
import com.ss.editor.JmeApplication;
import com.ss.editor.FileExtensions;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.classpath.ClassPathScanner;
import com.ss.rlib.classpath.ClassPathScannerFactory;
import com.ss.rlib.manager.InitializeManager;
import com.ss.rlib.plugin.PluginContainer;
import com.ss.rlib.util.FileUtils;
import com.ss.rlib.util.Utils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
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

/**
 * The class to manage classpath.
 *
 * @author JavaSaBr
 */
public class ClasspathManager {

    public enum Scope {
        CORE,
        CUSTOM,
        PLUGINS,
        LOCAL_LIBRARIES,
        LOCAL_CLASSES;

        @NotNull
        public static final Set<Scope> ONLY_CORE = EnumSet.of(CORE);

        @NotNull
        public static final Set<Scope> CORE_AND_CUSTOM_AND_LOCAL = EnumSet.of(CORE, CUSTOM, LOCAL_CLASSES, LOCAL_LIBRARIES);

        @NotNull
        public static final Set<Scope> ALL = EnumSet.allOf(Scope.class);
    }

    @NotNull
    private static final EditorConfig EDITOR_CONFIG = EditorConfig.getInstance();

    @NotNull
    private static final String[] JAR_EXTENSIONS = toArray(FileExtensions.JAVA_LIBRARY);

    @NotNull
    public static final Array<String> CORE_LIBRARIES_NAMES = ArrayFactory.asArray(
            "jme3-core", "jme3-terrain", "jme3-effects",
            "jme3-testdata", "jme3-plugins", "tonegod", "jmonkeybuilder"
    );

    @Nullable
    private static ClasspathManager instance;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    @NotNull
    public static ClasspathManager getInstance() {
        if (instance == null) instance = new ClasspathManager();
        return instance;
    }

    /**
     * The core classpath scanner.
     */
    @NotNull
    private final ClassPathScanner coreScanner;

    /**
     * The custom classpath scanner.
     */
    @Nullable
    private volatile ClassPathScanner customScanner;

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
     * The libraries class loader.
     */
    @Nullable
    private volatile URLClassLoader librariesLoader;

    /**
     * The classes class loader.
     */
    @Nullable
    private volatile URLClassLoader classesLoader;

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

        coreScanner = ClassPathScannerFactory.newManifestScanner(JmeApplication.class, "Class-Path");
        coreScanner.setUseSystemClasspath(true);
        coreScanner.scan(path -> {

            if (Files.isDirectory(Paths.get(path))) {
                return true;
            } else if (CORE_LIBRARIES_NAMES.search(path, (pattern, pth) -> pth.contains(pattern)) == null) {
                return false;
            } else if (path.contains("natives")) {
                return false;
            } else if (path.contains("sources") || path.contains("javadoc")) {
                return false;
            }

            return true;
        });

        final ExecutorManager executorManager = ExecutorManager.getInstance();
        executorManager.addJMETask(this::reload);
    }

    /**
     * Get all available resources from classpath.
     *
     * @return the list of resources.
     */
    public @NotNull Array<String> getAllResources() {
        final Array<String> result = ArrayFactory.newArray(String.class);
        coreScanner.getAllResources(result);
        return result;
    }

    /**
     * Reload custom classes and libraries.
     */
    public synchronized void reload() {
        updateLibraries();
        updateClasses();

        final URLClassLoader librariesLoader = getLibrariesLoader();
        final URLClassLoader classesLoader = getClassesLoader();

        if (librariesLoader == null && classesLoader == null) {
            this.customScanner = null;
            return;
        }

        final ClassLoader classLoader = classesLoader == null ? librariesLoader : classesLoader;
        final ClassPathScanner scanner = classesLoader == null ? ClassPathScannerFactory.newDefaultScanner() :
                ClassPathScannerFactory.newDefaultScanner(classLoader);

        final Array<URL> urls = ArrayFactory.newArray(URL.class);

        if (librariesLoader != null) {
            urls.addAll(librariesLoader.getURLs());
        }

        if (classesLoader != null) {
            urls.addAll(classesLoader.getURLs());
        }

        final String[] paths = urls.stream().map(url -> Utils.get(url, URL::toURI))
                .map(Paths::get)
                .map(Path::toString)
                .toArray(String[]::new);

        scanner.addAdditionalPaths(paths);
        scanner.setUseSystemClasspath(false);
        scanner.scan();

        this.customScanner = scanner;
    }

    /**
     * Update libraries loader.
     */
    @FromAnyThread
    private void updateLibraries() {

        final AssetManager assetManager = EditorUtil.getAssetManager();
        final URLClassLoader currentClassLoader = getLibrariesLoader();

        if (currentClassLoader != null) {
            assetManager.removeClassLoader(currentClassLoader);
            setLibrariesLoader(null);
        }

        final Path path = EDITOR_CONFIG.getLibrariesPath();
        if (path == null) {
            return;
        }

        final Array<Path> jars = FileUtils.getFiles(path, false, JAR_EXTENSIONS);
        final URL[] urls = jars.stream()
                .map(FileUtils::toUrl)
                .toArray(URL[]::new);

        final URLClassLoader classLoader = new URLClassLoader(urls, getClass().getClassLoader());

        assetManager.addClassLoader(classLoader);
        setLibrariesLoader(classLoader);
    }

    /**
     * Update compiled classes loader.
     */
    @FromAnyThread
    private void updateClasses() {

        final AssetManager assetManager = EditorUtil.getAssetManager();
        final URLClassLoader currentClassLoader = getClassesLoader();

        if (currentClassLoader != null) {
            assetManager.removeClassLoader(currentClassLoader);
            setClassesLoader(null);
        }

        final Path path = EDITOR_CONFIG.getClassesPath();
        if (path == null) {
            return;
        }

        final Array<Path> folders = ArrayFactory.newArray(Path.class);

        Utils.run(path, folders, (dir, toStore) -> {
            final DirectoryStream<Path> stream = Files.newDirectoryStream(dir);
            stream.forEach(subFile -> {
                if (Files.isDirectory(subFile)) {
                    toStore.add(subFile);
                }
            });
        });

        final URL[] urls = folders.stream()
                .map(FileUtils::toUrl)
                .toArray(URL[]::new);

        final ClassLoader librariesLoader = getLibrariesLoader();
        final ClassLoader parent = librariesLoader == null? getClass().getClassLoader() : librariesLoader;
        final URLClassLoader classLoader = new URLClassLoader(urls, parent);

        assetManager.addClassLoader(classLoader);
        setClassesLoader(classLoader);
    }

    /**
     * Load local libraries.
     */
    @FromAnyThread
    public synchronized void loadLocalLibraries(@NotNull final Array<Path> libraries) {

        final AssetManager assetManager = EditorUtil.getAssetManager();
        final URLClassLoader currentClassLoader = getLocalLibrariesLoader();

        if (currentClassLoader != null) {
            assetManager.removeClassLoader(currentClassLoader);
            setLocalLibrariesLoader(null);
        }

        if (libraries.isEmpty()) {
            this.localLibrariesScanner = null;
            return;
        }

        final URL[] urlArray = libraries.stream()
                .map(FileUtils::toUrl)
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
    }

    /**
     * Load local classes.
     */
    @FromAnyThread
    public synchronized void loadLocalClasses(@Nullable final Path output) {

        final AssetManager assetManager = EditorUtil.getAssetManager();
        final URLClassLoader currentClassLoader = getLocalClassesLoader();

        if (currentClassLoader != null) {
            assetManager.removeClassLoader(currentClassLoader);
            setLocalClassesLoader(null);
        }

        if (output == null || !Files.exists(output)) {
            this.localClassesScanner = null;
            return;
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
                .map(FileUtils::toUrl)
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
    }

    /**
     * @param librariesLoader the additional class loader.
     */
    @FromAnyThread
    private void setLibrariesLoader(@Nullable final URLClassLoader librariesLoader) {
        this.librariesLoader = librariesLoader;
    }

    /**
     * Get the library loader.
     *
     * @return the library loader.
     */
    @FromAnyThread
    public @Nullable URLClassLoader getLibrariesLoader() {
        return librariesLoader;
    }

    /**
     * Set the classes loader.
     *
     * @param classesLoader the classes loader.
     */
    @FromAnyThread
    private void setClassesLoader(@Nullable final URLClassLoader classesLoader) {
        this.classesLoader = classesLoader;
    }

    /**
     * Get classes loader.
     *
     * @return the classes loader.
     */
    @FromAnyThread
    public @Nullable URLClassLoader getClassesLoader() {
        return classesLoader;
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
        return customScanner;
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
    public @NotNull <T> Array<Class<T>> findImplements(@NotNull final Class<T> interfaceClass,
                                                       @NotNull final Set<Scope> scope) {

        final Array<Class<T>> result = ArrayFactory.newArray(Class.class);

        if (scope.contains(Scope.CORE)) {
            coreScanner.findImplements(result, interfaceClass);
        }

        final ClassPathScanner customScanner = getCustomScanner();
        if (customScanner != null && scope.contains(Scope.CUSTOM)) {
            customScanner.findImplements(result, interfaceClass);
        }

        final ClassPathScanner localLibrariesScanner = getLocalLibrariesScanner();
        if (localLibrariesScanner != null && scope.contains(Scope.LOCAL_LIBRARIES)) {
            localLibrariesScanner.findImplements(result, interfaceClass);
        }

        final ClassPathScanner localClassesScanner = getLocalClassesScanner();
        if (localClassesScanner != null && scope.contains(Scope.LOCAL_CLASSES)) {
            localClassesScanner.findImplements(result, interfaceClass);
        }

        if (scope.contains(Scope.PLUGINS)) {
            final PluginManager pluginManager = PluginManager.getInstance();
            pluginManager.handlePlugins(plugin -> {
                final PluginContainer container = plugin.getContainer();
                container.getScanner().findImplements(result, interfaceClass);
            });
        }

        return result;
    }
}
