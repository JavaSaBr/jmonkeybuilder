package com.ss.editor.manager;

import static com.ss.rlib.util.array.ArrayFactory.toArray;
import com.jme3.asset.AssetManager;
import com.ss.editor.Editor;
import com.ss.editor.FileExtensions;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.config.EditorConfig;
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

/**
 * The class to manage classpath.
 *
 * @author JavaSaBr
 */
public class ClasspathManager {

    @NotNull
    private static final EditorConfig EDITOR_CONFIG = EditorConfig.getInstance();

    @NotNull
    private static final String[] JAR_EXTENSIONS = toArray(FileExtensions.JAVA_LIBRARY);

    @NotNull
    public static final Array<String> CORE_LIBRARIES_NAMES = ArrayFactory.asArray(
            "jme3-core", "jme3-terrain", "jme3-effects",
            "jme3-testdata", "jme3-plugins", "tonegod"
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
     * The libraries class loader.
     */
    @Nullable
    private volatile URLClassLoader librariesLoader;

    /**
     * The classes class loader.
     */
    @Nullable
    private volatile URLClassLoader classesLoader;

    private ClasspathManager() {
        InitializeManager.valid(getClass());

        coreScanner = ClassPathScannerFactory.newManifestScanner(Editor.class, "Class-Path");
        coreScanner.setUseSystemClasspath(true);
        coreScanner.scan(path -> {

            if (CORE_LIBRARIES_NAMES.search(path, (pattern, pth) -> pth.contains(pattern)) == null) {
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
    @NotNull
    public Array<String> getAllResources() {
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
        final ClassLoader classLoader = classesLoader == null ? librariesLoader : classesLoader;

        final ClassPathScanner scanner = classesLoader == null ? ClassPathScannerFactory.newDefaultScanner() :
                ClassPathScannerFactory.newDefaultScanner(classLoader);

        if (librariesLoader == null && classesLoader == null) {
            this.customScanner = scanner;
            return;
        }

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
        scanner.scan(path -> true);

        this.customScanner = scanner;
    }

    /**
     * Update libraries loader.
     */
    @FromAnyThread
    private void updateLibraries() {

        final Editor editor = Editor.getInstance();
        final AssetManager assetManager = editor.getAssetManager();
        final URLClassLoader currentClassLoader = getLibrariesLoader();

        if (currentClassLoader != null) {
            assetManager.removeClassLoader(currentClassLoader);
            setLibrariesLoader(null);
        }

        final Path path = EDITOR_CONFIG.getLibrariesPath();
        if (path == null) return;

        final Array<Path> jars = FileUtils.getFiles(path, false, JAR_EXTENSIONS);
        final URL[] urls = jars.stream().map(FileUtils::toUrl)
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

        final Editor editor = Editor.getInstance();
        final AssetManager assetManager = editor.getAssetManager();
        final URLClassLoader currentClassLoader = getClassesLoader();

        if (currentClassLoader != null) {
            assetManager.removeClassLoader(currentClassLoader);
            setLibrariesLoader(null);
        }

        final Path path = EDITOR_CONFIG.getClassesPath();
        if (path == null) return;

        final Array<Path> folders = ArrayFactory.newArray(Path.class);

        Utils.run(path, folders, (dir, toStore) -> {
            final DirectoryStream<Path> stream = Files.newDirectoryStream(dir);
            stream.forEach(subFile -> {
                if (Files.isDirectory(subFile)) {
                    toStore.add(subFile);
                }
            });
        });

        final URL[] urls = folders.stream().map(FileUtils::toUrl)
                .toArray(URL[]::new);

        final ClassLoader librariesLoader = getLibrariesLoader();
        final ClassLoader parent = librariesLoader == null? getClass().getClassLoader() : librariesLoader;
        final URLClassLoader classLoader = new URLClassLoader(urls, parent);

        assetManager.addClassLoader(classLoader);

        setClassesLoader(classLoader);
    }

    /**
     * @param librariesLoader the additional class loader.
     */
    private void setLibrariesLoader(@Nullable final URLClassLoader librariesLoader) {
        this.librariesLoader = librariesLoader;
    }

    /**
     * Get the library loader.
     *
     * @return the library loader.
     */
    @Nullable
    @FromAnyThread
    public URLClassLoader getLibrariesLoader() {
        return librariesLoader;
    }

    /**
     * Set the classes loader.
     *
     * @param classesLoader the classes loader.
     */
    private void setClassesLoader(@Nullable final URLClassLoader classesLoader) {
        this.classesLoader = classesLoader;
    }

    /**
     * Get classes loader.
     *
     * @return the classes loader.
     */
    @Nullable
    @FromAnyThread
    private URLClassLoader getClassesLoader() {
        return classesLoader;
    }

    /**
     * Find all implementations of the interface class.
     *
     * @return the list of all available implementations.
     */
    @NotNull
    public <T> Array<Class<T>> findImplements(@NotNull final Class<T> interfaceClass) {

        final Array<Class<T>> result = ArrayFactory.newArray(Class.class);

        coreScanner.findImplements(result, interfaceClass);
        customScanner.findImplements(result, interfaceClass);

        final PluginManager pluginManager = PluginManager.getInstance();
        pluginManager.handlePlugins(plugin -> {
            final PluginContainer container = plugin.getContainer();
            container.getScanner().findImplements(result, interfaceClass);
        });

        return result;
    }
}
