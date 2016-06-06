package com.ss.editor.manager;

import com.jme3.asset.AssetManager;
import com.ss.editor.Editor;
import com.ss.editor.FileExtensions;
import com.ss.editor.config.EditorConfig;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

import rlib.logging.Logger;
import rlib.logging.LoggerManager;
import rlib.manager.InitializeManager;
import rlib.util.FileUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

import static rlib.util.Util.safeExecute;

/**
 * Менеджер по управлению classpath.
 *
 * @author Ronn
 */
public class ClasspathManager {

    private static final Logger LOGGER = LoggerManager.getLogger(ClasspathManager.class);

    private static final Editor EDITOR = Editor.getInstance();
    private static final AssetManager ASSET_MANAGER = EDITOR.getAssetManager();
    private static final EditorConfig EDITOR_CONFIG = EditorConfig.getInstance();

    private static final String[] EXTENSIONS = ArrayFactory.toGenericArray(FileExtensions.JAVA_LIBRARY);

    private static ClasspathManager instance;

    public static ClasspathManager getInstance() {
        if (instance == null) instance = new ClasspathManager();
        return instance;
    }

    /**
     * Дополнительный загрузчик классов.
     */
    private URLClassLoader additionalCL;

    public ClasspathManager() {
        InitializeManager.valid(getClass());
        updateAdditionalCL();
    }

    /**
     * Обновление дополнительного загрузчика классов.
     */
    public void updateAdditionalCL() {

        final URLClassLoader currentCL = getAdditionalCL();

        if (currentCL != null) {
            ASSET_MANAGER.removeClassLoader(currentCL);
            setAdditionalCL(null);
        }

        final Path path = EDITOR_CONFIG.getAdditionalClasspath();
        if (path == null) return;

        final Array<Path> jars = FileUtils.getFiles(path, false, EXTENSIONS);
        final URL[] urls = jars.stream().map(jar -> safeExecute(() -> jar.toUri().toURL())).toArray(URL[]::new);

        final URLClassLoader newCL = new URLClassLoader(urls, getClass().getClassLoader());

        ASSET_MANAGER.addClassLoader(newCL);

        setAdditionalCL(currentCL);
    }

    /**
     * @param additionalCL дополнительный загрузчик классов.
     */
    private void setAdditionalCL(final URLClassLoader additionalCL) {
        this.additionalCL = additionalCL;
    }

    /**
     * @return дополнительный загрузчик классов.
     */
    private URLClassLoader getAdditionalCL() {
        return additionalCL;
    }
}
