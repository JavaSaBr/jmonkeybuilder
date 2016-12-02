package com.ss.editor.manager;

import static com.ss.editor.util.EditorUtil.toAssetPath;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static rlib.util.ArrayUtils.contains;
import static rlib.util.ArrayUtils.move;
import static rlib.util.Util.safeGet;
import static rlib.util.array.ArrayFactory.toArray;

import com.jme3.asset.AssetManager;
import com.ss.editor.Editor;
import com.ss.editor.EditorThread;
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
import com.ss.editor.util.SimpleFolderVisitor;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

import rlib.classpath.ClassPathScanner;
import rlib.classpath.ClassPathScannerFactory;
import rlib.concurrent.util.ThreadUtils;
import rlib.logging.Logger;
import rlib.logging.LoggerManager;
import rlib.manager.InitializeManager;
import rlib.util.FileUtils;
import rlib.util.StringUtils;
import rlib.util.Util;
import rlib.util.array.Array;
import rlib.util.array.ArrayComparator;
import rlib.util.array.ArrayFactory;

/**
 * Менеджер по работе с ресурсами.
 *
 * @author Ronn
 */
public class ResourceManager extends EditorThread {

    private static final Logger LOGGER = LoggerManager.getLogger(ResourceManager.class);

    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();
    private static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();

    private static final ArrayComparator<String> STRING_ARRAY_COMPARATOR = StringUtils::compareIgnoreCase;

    private static final WatchService WATCH_SERVICE;

    static {
        try {
            WATCH_SERVICE = FileSystems.getDefault().newWatchService();
        } catch (final IOException e) {
            LOGGER.warning(e);
            throw new RuntimeException(e);
        }
    }

    private static ResourceManager instance;

    public static ResourceManager getInstance() {
        if (instance == null) instance = new ResourceManager();
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

    /**
     * Ключи для слежения за изменением папок.
     */
    private final Array<WatchKey> watchKeys;

    public ResourceManager() {
        InitializeManager.valid(getClass());

        this.watchKeys = ArrayFactory.newArray(WatchKey.class);
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
        start();
    }

    /**
     * Обработка переименования файла.
     */
    private synchronized void processEvent(final RenamedFileEvent event) {

        final Path prevFile = event.getPrevFile();
        final Path newFile = event.getNewFile();

        final String extension = FileUtils.getExtension(prevFile);

        final Path prevAssetFile = EditorUtil.getAssetFile(prevFile);
        final String prevAssetPath = EditorUtil.toAssetPath(prevAssetFile);

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
    private void replaceCL(final Path prevAssetFile, final Path newAssetFile) {

        final Editor editor = Editor.getInstance();
        final AssetManager assetManager = editor.getAssetManager();

        final URL prevURL = safeGet(prevAssetFile, file -> file.toUri().toURL());
        final URL newURL = safeGet(newAssetFile, file -> file.toUri().toURL());

        final Array<URLClassLoader> classLoaders = getClassLoaders();
        final URLClassLoader oldLoader = classLoaders.search(prevURL, (loader, url) -> contains(loader.getURLs(), url));
        final URLClassLoader newLoader = new URLClassLoader(toArray(newURL), getClass().getClassLoader());

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
        final Path newFile = event.getNewFile();

        final String extension = FileUtils.getExtension(prevFile);

        final Path prevAssetFile = EditorUtil.getAssetFile(prevFile);
        final String prevAssetPath = EditorUtil.toAssetPath(prevAssetFile);

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
    private void replaceMD(final String prevAssetPath, final String newAssetPath) {
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

            final URL url = safeGet(file, toUri -> toUri.toUri().toURL());

            final Array<URLClassLoader> classLoaders = getClassLoaders();
            final URLClassLoader oldLoader = classLoaders.search(url, (loader, toCheck) -> contains(loader.getURLs(), toCheck));

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

            final URL url = safeGet(file, FileUtils::toUrl);

            final Array<URLClassLoader> classLoaders = getClassLoaders();
            final URLClassLoader oldLoader = classLoaders.search(url, (loader, toCheck) -> contains(loader.getURLs(), toCheck));

            if (oldLoader != null) return;

            final URLClassLoader newLoader = new URLClassLoader(toArray(url), getClass().getClassLoader());
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
    public synchronized Array<String> getAvailableMaterialDefinitions() {

        final Array<String> result = ArrayFactory.newArray(String.class);
        final Array<String> materialDefinitions = getMaterialDefinitions();

        move(materialDefinitions, result, false);

        final Array<String> materialDefinitionsInClasspath = getMaterialDefinitionsInClasspath();
        materialDefinitionsInClasspath.forEach(result, (resource, container) -> {
            if (!container.contains(resource)) container.add(resource);
        });

        result.sort(STRING_ARRAY_COMPARATOR);
        return result;
    }

    /**
     * Перезагрузка доступных ресурсов.
     */
    private synchronized void reload() {

        final Array<WatchKey> watchKeys = getWatchKeys();
        watchKeys.forEach(WatchKey::cancel);
        watchKeys.clear();

        final Editor editor = Editor.getInstance();
        final AssetManager assetManager = editor.getAssetManager();

        final Array<URLClassLoader> classLoaders = getClassLoaders();
        classLoaders.forEach(assetManager, (loader, manager) -> manager.removeClassLoader(loader));
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

        try {
            watchKeys.add(currentAsset.register(WATCH_SERVICE, ENTRY_CREATE, ENTRY_DELETE));
            Files.walkFileTree(currentAsset, (SimpleFolderVisitor) (file, attrs) -> registerFolder(watchKeys, file));
        } catch (IOException e) {
            LOGGER.warning(e);
        }
    }

    private static void registerFolder(final Array<WatchKey> watchKeys, final Path file) {
        watchKeys.add(Util.safeGet(file, first -> {
            return first.register(WATCH_SERVICE, ENTRY_CREATE, ENTRY_DELETE);
        }));
    }

    /**
     * Обработка файла в папаке asset.
     */
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

            final URL url = safeGet(file, FileUtils::toUrl);

            final Array<URLClassLoader> classLoaders = getClassLoaders();
            final URLClassLoader oldLoader = classLoaders.search(url, (loader, toCheck) -> contains(loader.getURLs(), toCheck));

            if (oldLoader != null) return;

            final URLClassLoader newLoader = new URLClassLoader(toArray(url), getClass().getClassLoader());
            classLoaders.add(newLoader);
            assetManager.addClassLoader(newLoader);
        }
    }

    @Override
    public void run() {
        super.run();
        while (true) {
            ThreadUtils.sleep(200);

            final Array<WatchKey> watchKeys = getWatchKeys();

            List<WatchEvent<?>> watchEvents = null;
            WatchKey watchKey = null;

            synchronized (this) {
                for (final WatchKey key : watchKeys) {
                    watchKey = key;
                    watchEvents = key.pollEvents();
                    if (!watchEvents.isEmpty()) break;
                }
            }

            if (watchEvents == null || watchEvents.isEmpty()) continue;

            for (final WatchEvent<?> watchEvent : watchEvents) {

                final Path file = (Path) watchEvent.context();
                final Path folder = (Path) watchKey.watchable();
                final Path realFile = folder.resolve(file);

                if (watchEvent.kind() == ENTRY_CREATE) {

                    final CreatedFileEvent event = new CreatedFileEvent();
                    event.setFile(realFile);
                    event.setNeedSelect(false);

                    if (Files.isDirectory(realFile)) {
                        registerWatchKey(realFile);
                    }

                    FX_EVENT_MANAGER.notify(event);

                } else if (watchEvent.kind() == ENTRY_DELETE) {

                    final DeletedFileEvent event = new DeletedFileEvent();
                    event.setFile(realFile);

                    removeWatchKeyFor(realFile);

                    FX_EVENT_MANAGER.notify(event);
                }
            }
        }
    }

    private synchronized WatchKey findWatchKey(final Path path) {
        final Array<WatchKey> watchKeys = getWatchKeys();
        return watchKeys.search(path, (watchKey, toCheck) -> watchKey.watchable().equals(toCheck));
    }

    private synchronized void removeWatchKeyFor(final Path path) {

        final WatchKey watchKey = findWatchKey(path);

        if (watchKey != null) {
            getWatchKeys().fastRemove(watchKey);
            watchKey.cancel();
        }
    }

    private synchronized void registerWatchKey(final Path dir) {
        Util.safeExecute(() -> getWatchKeys().add(dir.register(WATCH_SERVICE, ENTRY_CREATE, ENTRY_DELETE)));
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

    /**
     * @return ключи для слежения за изменением папок.
     */
    private Array<WatchKey> getWatchKeys() {
        return watchKeys;
    }
}
