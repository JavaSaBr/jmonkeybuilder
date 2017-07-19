package com.ss.editor.manager;

import static com.ss.rlib.util.array.ArrayFactory.toArray;
import com.jme3.asset.AssetManager;
import com.ss.editor.Editor;
import com.ss.editor.FileExtensions;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.config.EditorConfig;
import com.ss.rlib.manager.InitializeManager;
import com.ss.rlib.util.FileUtils;
import com.ss.rlib.util.array.Array;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.net.URLClassLoader;
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
    private static final String[] EXTENSIONS = toArray(FileExtensions.JAVA_LIBRARY);

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
     * The additional class loader.
     */
    @Nullable
    private volatile URLClassLoader additionalCL;

    private CustomClasspathManager() {
        InitializeManager.valid(getClass());

        ExecutorManager executorManager = ExecutorManager.getInstance();
        executorManager.addJMETask(this::updateAdditionalCL);
    }

    /**
     * Update additional classpath.
     */
    @FromAnyThread
    public synchronized void updateAdditionalCL() {

        final Editor editor = Editor.getInstance();
        final AssetManager assetManager = editor.getAssetManager();
        final URLClassLoader currentCL = getAdditionalCL();

        if (currentCL != null) {
            assetManager.removeClassLoader(currentCL);
            setAdditionalCL(null);
        }

        final Path path = EDITOR_CONFIG.getAdditionalClasspath();
        if (path == null) return;

        final Array<Path> jars = FileUtils.getFiles(path, false, EXTENSIONS);
        final URL[] urls = jars.stream().map(FileUtils::toUrl)
                .toArray(URL[]::new);

        final URLClassLoader newCL = new URLClassLoader(urls, getClass().getClassLoader());

        assetManager.addClassLoader(newCL);

        setAdditionalCL(newCL);
    }

    /**
     * @param additionalCL the additional class loader.
     */
    private void setAdditionalCL(@Nullable final URLClassLoader additionalCL) {
        this.additionalCL = additionalCL;
    }

    /**
     * Gets additional cl.
     *
     * @return the additional class loader.
     */
    @Nullable
    @FromAnyThread
    public URLClassLoader getAdditionalCL() {
        return additionalCL;
    }
}
