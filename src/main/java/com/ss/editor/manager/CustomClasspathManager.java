package com.ss.editor.manager;

import static com.ss.rlib.util.array.ArrayFactory.toArray;
import com.jme3.asset.AssetManager;
import com.ss.editor.Editor;
import com.ss.editor.FileExtensions;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.config.EditorConfig;
import com.ss.rlib.manager.InitializeManager;
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

/**
 * The class to manage custom classpath.
 *
 * @author JavaSaBr
 */
public class CustomClasspathManager {

    @NotNull
    private static final EditorConfig EDITOR_CONFIG = EditorConfig.getInstance();

    @NotNull
    private static final String[] JAR_EXTENSIONS = toArray(FileExtensions.JAVA_LIBRARY);

    @NotNull
    private static final String[] CLASSES_EXTENSIONS = toArray(FileExtensions.JAVA_CLASS);

    @Nullable
    private static CustomClasspathManager instance;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    @NotNull
    public static CustomClasspathManager getInstance() {
        if (instance == null) instance = new CustomClasspathManager();
        return instance;
    }

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


    private CustomClasspathManager() {
        InitializeManager.valid(getClass());

        final ExecutorManager executorManager = ExecutorManager.getInstance();
        executorManager.addJMETask(this::updateLibraries);
        executorManager.addJMETask(this::updateClasses);
    }

    /**
     * Update libraries loader.
     */
    @FromAnyThread
    public synchronized void updateLibraries() {

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
    public synchronized void updateClasses() {

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
}
