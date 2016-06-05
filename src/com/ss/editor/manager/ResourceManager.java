package com.ss.editor.manager;

import com.jme3.asset.AssetManager;
import com.ss.editor.Editor;
import com.ss.editor.FileExtensions;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.ChangedCurrentAssetFolderEvent;
import com.ss.editor.ui.event.impl.CreatedFileEvent;
import com.ss.editor.ui.event.impl.DeletedFileEvent;
import com.ss.editor.ui.event.impl.MovedFileEvent;
import com.ss.editor.ui.event.impl.RenamedFileEvent;
import com.ss.editor.ui.event.impl.RequestedRefreshAssetEvent;
import com.ss.editor.util.EditorUtil;
import com.ss.editor.util.SimpleFileVisitor;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;

import rlib.classpath.ClassPathScanner;
import rlib.classpath.ClassPathScannerFactory;
import rlib.logging.Logger;
import rlib.logging.LoggerManager;
import rlib.manager.InitializeManager;
import rlib.util.ArrayUtils;
import rlib.util.FileUtils;
import rlib.util.StringUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayComparator;
import rlib.util.array.ArrayFactory;

import static com.ss.editor.util.EditorUtil.toAssetPath;
import static rlib.util.ArrayUtils.contains;
import static rlib.util.ArrayUtils.move;
import static rlib.util.Util.safeExecute;
import static rlib.util.array.ArrayFactory.toGenericArray;

/**
 * Менеджер по работе с ресурсами.
 *
 * @author Ronn
 */
public class ResourceManager {

    private static final Logger LOGGER = LoggerManager.getLogger(ResourceManager.class);

    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    private static final ArrayComparator<String> STRING_ARRAY_COMPARATOR = StringUtils::compareIgnoreCase;

    private static ResourceManager instance;

    public static ResourceManager getInstance() {

        if (instance == null) {
            instance = new ResourceManager();
        }

        return instance;
    }

    /**
     * Список дополнительных класс лоадеров.
     */
    private final Array<URLClassLoader> classLoaders;

    /**
     * Список ресурсов в classpath.
     */
    private final Array<String> resourcesInClasspath;

    /**
     * Список доступных типов материалов из classpath.
     */
    private final Array<String> materialDefinitionsInClasspath;

    /**
     * Список доступных типов материалов.
     */
    private final Array<String> materialDefinitions;

    public ResourceManager() {
        InitializeManager.valid(getClass());

        this.classLoaders = ArrayFactory.newArray(URLClassLoader.class);
        this.resourcesInClasspath = ArrayFactory.newArray(String.class);
        this.materialDefinitionsInClasspath = ArrayFactory.newArray(String.class);
        this.materialDefinitions = ArrayFactory.newArray(String.class);

        final ClassPathScanner scanner = ClassPathScannerFactory.newManifestScanner(Editor.class, "Class-Path");
        scanner.scanning(path -> {

            if (!path.contains("jme3-core")) {
                return false;
            } else if (path.contains("natives")) {
                return false;
            } else if (path.contains("sources") || path.contains("javadoc")) {
                return false;
            }

            return true;
        });

        scanner.getAllResources(resourcesInClasspath);
        prepareClasspathResources();

        final ExecutorManager executorManager = ExecutorManager.getInstance();
        executorManager.addFXTask(() -> {
            final FXEventManager fxEventManager = FXEventManager.getInstance();
            fxEventManager.addEventHandler(ChangedCurrentAssetFolderEvent.EVENT_TYPE, event -> processChangeAsset());
            fxEventManager.addEventHandler(RequestedRefreshAssetEvent.EVENT_TYPE, event -> processRefreshAsset());
            fxEventManager.addEventHandler(CreatedFileEvent.EVENT_TYPE, event -> processEvent((CreatedFileEvent) event));
            fxEventManager.addEventHandler(DeletedFileEvent.EVENT_TYPE, event -> processEvent((DeletedFileEvent) event));
            fxEventManager.addEventHandler(RenamedFileEvent.EVENT_TYPE, event -> processEvent((RenamedFileEvent) event));
            fxEventManager.addEventHandler(MovedFileEvent.EVENT_TYPE, event -> processEvent((MovedFileEvent) event));
        });

        reload();
    }

    /**
     * Обработка переименования файла.
     */
    private synchronized void processEvent(final RenamedFileEvent event) {

        final Path prevFile = event.getPrevFile();
        final String extension = FileUtils.getExtension(prevFile);

        final Path prevAssetFile = EditorUtil.getAssetFile(prevFile);
        final String prevAssetPath = EditorUtil.toAssetPath(prevAssetFile);

        final Path newFile = event.getNewFile();
        final Path newAssetFile = EditorUtil.getAssetFile(newFile);
        final String newAssetPath = EditorUtil.toAssetPath(newAssetFile);

        if (extension.endsWith(FileExtensions.JME_MATERIAL_DEFINITION)) {
            replaceMD(prevAssetPath, newAssetPath);
        } else if (extension.endsWith(FileExtensions.JAVA_LIBRARY)) {
            replaceCL(prevFile, newFile);
        }
    }

    /**
     * Процесс замены класс лоадера.
     */
    private void replaceCL(Path prevAssetFile, Path newAssetFile) {

        final Editor editor = Editor.getInstance();
        final AssetManager assetManager = editor.getAssetManager();

        final URL prevURL = safeExecute(() -> prevAssetFile.toUri().toURL());
        final URL newURL = safeExecute(() -> newAssetFile.toUri().toURL());

        final Array<URLClassLoader> classLoaders = getClassLoaders();
        final URLClassLoader oldLoader = classLoaders.search(prevURL, (url, loader) -> contains(loader.getURLs(), url));
        final URLClassLoader newLoader = new URLClassLoader(toGenericArray(newURL), getClass().getClassLoader());

        if (oldLoader != null) {
            classLoaders.fastRemove(oldLoader);
            assetManager.removeClassLoader(oldLoader);
        }

        classLoaders.add(newLoader);
        assetManager.addClassLoader(newLoader);
    }

    /**
     * Обработка перемещения файла.
     */
    private synchronized void processEvent(final MovedFileEvent event) {

        final Path prevFile = event.getPrevFile();
        final String extension = FileUtils.getExtension(prevFile);

        final Path prevAssetFile = EditorUtil.getAssetFile(prevFile);
        final String prevAssetPath = EditorUtil.toAssetPath(prevAssetFile);

        final Path newFile = event.getNewFile();
        final Path newAssetFile = EditorUtil.getAssetFile(newFile);
        final String newAssetPath = EditorUtil.toAssetPath(newAssetFile);

        if (extension.endsWith(FileExtensions.JME_MATERIAL_DEFINITION)) {
            replaceMD(prevAssetPath, newAssetPath);
        } else if (extension.endsWith(FileExtensions.JAVA_LIBRARY)) {
            replaceCL(prevFile, newFile);
        }
    }

    /**
     * Процесс замены определения материала.
     */
    private void replaceMD(String prevAssetPath, String newAssetPath) {
        final Array<String> materialDefinitions = getMaterialDefinitions();
        materialDefinitions.fastRemove(prevAssetPath);
        materialDefinitions.add(newAssetPath);
    }

    /**
     * Обработка удаления файла из Asset.
     */
    private synchronized void processEvent(final DeletedFileEvent event) {

        final Path file = event.getFile();
        final String extension = FileUtils.getExtension(file);

        final Path assetFile = EditorUtil.getAssetFile(file);
        final String assetPath = toAssetPath(assetFile);

        if (extension.endsWith(FileExtensions.JME_MATERIAL_DEFINITION)) {
            final Array<String> materialDefinitions = getMaterialDefinitions();
            materialDefinitions.fastRemove(assetPath);
        } else if (extension.endsWith(FileExtensions.JAVA_LIBRARY)) {

            final Editor editor = Editor.getInstance();
            final AssetManager assetManager = editor.getAssetManager();

            final URL url = safeExecute(() -> file.toUri().toURL());

            final Array<URLClassLoader> classLoaders = getClassLoaders();
            final URLClassLoader oldLoader = classLoaders.search(url, (toCheck, loader) -> contains(loader.getURLs(), toCheck));

            if (oldLoader != null) {
                classLoaders.fastRemove(oldLoader);
                assetManager.removeClassLoader(oldLoader);
            }
        }
    }

    /**
     * Обработка созадния файла в Asset.
     */
    private synchronized void processEvent(final CreatedFileEvent event) {

        final Path file = event.getFile();
        final String extension = FileUtils.getExtension(file);

        final Path assetFile = EditorUtil.getAssetFile(file);
        final String assetPath = toAssetPath(assetFile);

        if (extension.endsWith(FileExtensions.JME_MATERIAL_DEFINITION)) {
            final Array<String> materialDefinitions = getMaterialDefinitions();
            if (!materialDefinitions.contains(assetPath)) materialDefinitions.add(assetPath);
        } else if (extension.endsWith(FileExtensions.JAVA_LIBRARY)) {

            final Editor editor = Editor.getInstance();
            final AssetManager assetManager = editor.getAssetManager();

            final URL url = safeExecute(() -> file.toUri().toURL());

            final Array<URLClassLoader> classLoaders = getClassLoaders();
            final URLClassLoader oldLoader = classLoaders.search(url, (toCheck, loader) -> contains(loader.getURLs(), toCheck));

            if (oldLoader != null) return;

            final URLClassLoader newLoader = new URLClassLoader(toGenericArray(url), getClass().getClassLoader());
            classLoaders.add(newLoader);
            assetManager.addClassLoader(newLoader);
        }
    }

    /**
     * @return список доступных типов материалов из classpath.
     */
    private Array<String> getMaterialDefinitionsInClasspath() {
        return materialDefinitionsInClasspath;
    }

    /**
     * @return список ресурсов в classpath.
     */
    private Array<String> getResourcesInClasspath() {
        return resourcesInClasspath;
    }

    /**
     * Подготовка перечня ресурсов в classpath.
     */
    private void prepareClasspathResources() {
        final Array<String> materialDefinitionsInClasspath = getMaterialDefinitionsInClasspath();
        final Array<String> resourcesInClasspath = getResourcesInClasspath();
        resourcesInClasspath.forEach(resource -> {
            if (resource.endsWith(FileExtensions.JME_MATERIAL_DEFINITION)) {
                materialDefinitionsInClasspath.add(resource);
            }
        });
    }

    /**
     * @return список доступных типов материалов.
     */
    private Array<String> getMaterialDefinitions() {
        return materialDefinitions;
    }

    /**
     * @return список класс лоадеров.
     */
    private Array<URLClassLoader> getClassLoaders() {
        return classLoaders;
    }

    /**
     * @return список доступных типов материалов.
     */
    public Array<String> getAvailableMaterialDefinitions() {

        final Array<String> result = ArrayFactory.newArray(String.class);
        final Array<String> materialDefinitions = getMaterialDefinitions();

        ArrayUtils.runInReadLock(materialDefinitions, result, (source, destination) -> move(source, destination, false));

        final Array<String> materialDefinitionsInClasspath = getMaterialDefinitionsInClasspath();
        materialDefinitionsInClasspath.forEach(result, (container, resource) -> {
            if (!container.contains(resource)) container.add(resource);
        });

        result.sort(STRING_ARRAY_COMPARATOR);
        return result;
    }

    /**
     * Перезагрузка доступных ресурсов.
     */
    private synchronized void reload() {

        final Editor editor = Editor.getInstance();
        final AssetManager assetManager = editor.getAssetManager();

        final Array<URLClassLoader> classLoaders = getClassLoaders();
        classLoaders.forEach(assetManager, AssetManager::removeClassLoader);
        classLoaders.clear();

        final Array<String> materialDefinitions = getMaterialDefinitions();
        materialDefinitions.clear();

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final Path currentAsset = editorConfig.getCurrentAsset();
        if (currentAsset == null) return;

        try {
            Files.walkFileTree(currentAsset, (SimpleFileVisitor) (file, attrs) -> handleFile(file));
        } catch (IOException e) {
            LOGGER.warning(e);
        }
    }

    private void handleFile(final Path file) {
        if (Files.isDirectory(file)) return;

        final String extension = FileUtils.getExtension(file);

        if (extension.endsWith(FileExtensions.JME_MATERIAL_DEFINITION)) {
            final Path assetFile = EditorUtil.getAssetFile(file);
            final Array<String> materialDefinitions = getMaterialDefinitions();
            materialDefinitions.add(toAssetPath(assetFile));
        } else if (extension.endsWith(FileExtensions.JAVA_LIBRARY)) {

            final Editor editor = Editor.getInstance();
            final AssetManager assetManager = editor.getAssetManager();

            final URL url = safeExecute(() -> file.toUri().toURL());

            final Array<URLClassLoader> classLoaders = getClassLoaders();
            final URLClassLoader oldLoader = classLoaders.search(url, (toCheck, loader) -> contains(loader.getURLs(), toCheck));

            if (oldLoader != null) return;

            final URLClassLoader newLoader = new URLClassLoader(toGenericArray(url), getClass().getClassLoader());
            classLoaders.add(newLoader);
            assetManager.addClassLoader(newLoader);
        }
    }

    /**
     * Обработка обновления Asset.
     */
    private void processRefreshAsset() {
        EXECUTOR_MANAGER.addBackgroundTask(this::reload);
    }

    /**
     * Обработка смены Asset.
     */
    private void processChangeAsset() {
        EXECUTOR_MANAGER.addBackgroundTask(this::reload);
    }
}
