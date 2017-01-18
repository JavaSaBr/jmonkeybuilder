package com.ss.editor.manager;

import static com.ss.editor.FileExtensions.IMAGE_HDR;
import static com.ss.editor.FileExtensions.IMAGE_PNG;
import static com.ss.editor.FileExtensions.IMAGE_TGA;
import static com.ss.editor.util.EditorUtil.getAssetFile;
import static com.ss.editor.util.EditorUtil.toAssetPath;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static rlib.util.ArrayUtils.contains;
import static rlib.util.ArrayUtils.move;
import static rlib.util.FileUtils.getFiles;
import static rlib.util.FileUtils.toUrl;
import static rlib.util.Util.get;
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
import com.ss.editor.ui.event.impl.FileChangedEvent;
import com.ss.editor.ui.event.impl.RequestedRefreshAssetEvent;
import com.ss.editor.util.SimpleFileVisitor;
import com.ss.editor.util.SimpleFolderVisitor;

import org.jetbrains.annotations.NotNull;

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
import java.util.Objects;

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
 * The manager for working with resources.
 *
 * @author JavaSaBr
 */
public class ResourceManager extends EditorThread {

    private static final Logger LOGGER = LoggerManager.getLogger(ResourceManager.class);

    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();
    private static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();

    @NotNull
    private static final ArrayComparator<String> STRING_ARRAY_COMPARATOR = StringUtils::compareIgnoreCase;

    @NotNull
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

    @NotNull
    public static ResourceManager getInstance() {
        if (instance == null) instance = new ResourceManager();
        return instance;
    }

    /**
     * The list of additional ENVs.
     */
    @NotNull
    private final Array<Path> additionalEnvs;

    /**
     * The list of an additional classpath.
     */
    @NotNull
    private final Array<URLClassLoader> classLoaders;

    /**
     * The list of resources in the classpath.
     */
    @NotNull
    private final Array<String> resourcesInClasspath;

    /**
     * The list of material definitions in the classpath.
     */
    @NotNull
    private final Array<String> materialDefinitionsInClasspath;

    /**
     * The list of available material definitions in the classpath.
     */
    @NotNull
    private final Array<String> materialDefinitions;

    /**
     * The list of keys for watching to folders.
     */
    @NotNull
    private final Array<WatchKey> watchKeys;

    public ResourceManager() {
        InitializeManager.valid(getClass());

        this.additionalEnvs = ArrayFactory.newArray(Path.class);
        this.watchKeys = ArrayFactory.newArray(WatchKey.class);
        this.classLoaders = ArrayFactory.newArray(URLClassLoader.class);
        this.resourcesInClasspath = ArrayFactory.newArray(String.class);
        this.materialDefinitionsInClasspath = ArrayFactory.newArray(String.class);
        this.materialDefinitions = ArrayFactory.newArray(String.class);

        final ClassPathScanner scanner = ClassPathScannerFactory.newManifestScanner(Editor.class, "Class-Path");
        scanner.scanning(path -> {

            if (!(path.contains("jme3-core") || path.contains("jme3-effects") || path.contains("tonegod"))) {
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
        });

        updateAdditionalEnvs();
        reload();
        start();
    }

    /**
     * Update the list with additional ENVs.
     */
    public synchronized void updateAdditionalEnvs() {

        final EditorConfig editorConfig = EditorConfig.getInstance();

        final Array<Path> additionalEnvs = getAdditionalEnvs();
        additionalEnvs.clear();

        final Path folder = editorConfig.getAdditionalEnvs();
        if (folder == null) return;

        additionalEnvs.addAll(getFiles(folder, IMAGE_HDR, IMAGE_TGA, IMAGE_PNG));
    }

    /**
     * Get a list with additional ENVs.
     *
     * @return the list.
     */
    @NotNull
    public Array<Path> getAdditionalEnvs() {
        return additionalEnvs;
    }

    /**
     * Handle a removed file.
     */
    private synchronized void processEvent(@NotNull final DeletedFileEvent event) {
        if (event.isDirectory()) return;

        final Path file = event.getFile();
        final String extension = FileUtils.getExtension(file);

        final Path assetFile = Objects.requireNonNull(getAssetFile(file), "Not found asset file for " + file);
        final String assetPath = toAssetPath(assetFile);

        if (extension.endsWith(FileExtensions.JME_MATERIAL_DEFINITION)) {
            final Array<String> materialDefinitions = getMaterialDefinitions();
            materialDefinitions.fastRemove(assetPath);
        } else if (extension.endsWith(FileExtensions.JAVA_LIBRARY)) {

            final Editor editor = Editor.getInstance();
            final AssetManager assetManager = editor.getAssetManager();

            final URL url = toUrl(file);
            final Array<URLClassLoader> classLoaders = getClassLoaders();

            final URLClassLoader oldLoader = classLoaders.search(url, (loader, toCheck) ->
                    contains(loader.getURLs(), toCheck));

            if (oldLoader != null) {
                classLoaders.fastRemove(oldLoader);
                assetManager.removeClassLoader(oldLoader);
            }
        }
    }

    /**
     * Handle a created file.
     */
    private synchronized void processEvent(@NotNull final CreatedFileEvent event) {
        if (event.isDirectory()) return;

        final Path file = event.getFile();
        final String extension = FileUtils.getExtension(file);

        final Path assetFile = Objects.requireNonNull(getAssetFile(file), "Not found asset file for " + file);
        final String assetPath = toAssetPath(assetFile);

        if (extension.endsWith(FileExtensions.JME_MATERIAL_DEFINITION)) {
            final Array<String> materialDefinitions = getMaterialDefinitions();
            if (!materialDefinitions.contains(assetPath)) materialDefinitions.add(assetPath);
        } else if (extension.endsWith(FileExtensions.JAVA_LIBRARY)) {

            final Editor editor = Editor.getInstance();
            final AssetManager assetManager = editor.getAssetManager();

            final URL url = get(file, FileUtils::toUrl);

            final Array<URLClassLoader> classLoaders = getClassLoaders();
            final URLClassLoader oldLoader = classLoaders.search(url, (loader, toCheck) -> contains(loader.getURLs(), toCheck));

            if (oldLoader != null) return;

            final URLClassLoader newLoader = new URLClassLoader(toArray(url), getClass().getClassLoader());
            classLoaders.add(newLoader);
            assetManager.addClassLoader(newLoader);
        }
    }

    /**
     * @return the list of material definitions in the classpath.
     */
    @NotNull
    private Array<String> getMaterialDefinitionsInClasspath() {
        return materialDefinitionsInClasspath;
    }

    /**
     * @return the list of resources in the classpath.
     */
    @NotNull
    private Array<String> getResourcesInClasspath() {
        return resourcesInClasspath;
    }

    /**
     * Prepare classpath resources.
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
     * @return the list of available material definitions in the classpath.
     */
    @NotNull
    private Array<String> getMaterialDefinitions() {
        return materialDefinitions;
    }

    /**
     * @return the list of an additional classpath.
     */
    @NotNull
    public Array<URLClassLoader> getClassLoaders() {
        return classLoaders;
    }

    /**
     * @return the list of all available material definitions.
     */
    @NotNull
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
     * Reload available resources.
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
            watchKeys.add(currentAsset.register(WATCH_SERVICE, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY));
            Files.walkFileTree(currentAsset, (SimpleFolderVisitor) (file, attrs) -> registerFiles(watchKeys, file));
        } catch (final IOException e) {
            LOGGER.warning(e);
        }
    }

    private static void registerFiles(@NotNull final Array<WatchKey> watchKeys, @NotNull final Path file) {
        watchKeys.add(get(file, toRegister -> toRegister.register(WATCH_SERVICE, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY)));
    }

    /**
     * Handle a file event in an asset folder.
     */
    private void handleFile(@NotNull final Path file) {
        if (Files.isDirectory(file)) return;

        final String extension = FileUtils.getExtension(file);

        if (extension.endsWith(FileExtensions.JME_MATERIAL_DEFINITION)) {
            final Path assetFile = Objects.requireNonNull(getAssetFile(file), "Not found asset file for " + file);
            final Array<String> materialDefinitions = getMaterialDefinitions();
            materialDefinitions.add(toAssetPath(assetFile));
        } else if (extension.endsWith(FileExtensions.JAVA_LIBRARY)) {

            final Editor editor = Editor.getInstance();
            final AssetManager assetManager = editor.getAssetManager();

            final URL url = get(file, FileUtils::toUrl);

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

                    final boolean directory = Files.isDirectory(realFile);

                    final CreatedFileEvent event = new CreatedFileEvent();
                    event.setFile(realFile);
                    event.setNeedSelect(false);
                    event.setDirectory(directory);

                    if (directory) {
                        registerWatchKey(realFile);
                    }

                    FX_EVENT_MANAGER.notify(event);

                } else if (watchEvent.kind() == ENTRY_DELETE) {

                    final boolean directory = Files.isDirectory(realFile);

                    final DeletedFileEvent event = new DeletedFileEvent();
                    event.setFile(realFile);
                    event.setDirectory(directory);

                    removeWatchKeyFor(realFile);

                    FX_EVENT_MANAGER.notify(event);

                } else if (watchEvent.kind() == ENTRY_MODIFY) {

                    final FileChangedEvent event = new FileChangedEvent();
                    event.setFile(realFile);

                    FX_EVENT_MANAGER.notify(event);
                }
            }
        }
    }

    private synchronized WatchKey findWatchKey(@NotNull final Path path) {
        final Array<WatchKey> watchKeys = getWatchKeys();
        return watchKeys.search(path, (watchKey, toCheck) -> watchKey.watchable().equals(toCheck));
    }

    private synchronized void removeWatchKeyFor(@NotNull final Path path) {

        final WatchKey watchKey = findWatchKey(path);

        if (watchKey != null) {
            getWatchKeys().fastRemove(watchKey);
            watchKey.cancel();
        }
    }

    private synchronized void registerWatchKey(@NotNull final Path dir) {
        Util.run(() -> getWatchKeys().add(dir.register(WATCH_SERVICE, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY)));
    }

    /**
     * Handle refreshing asset folder.
     */
    private void processRefreshAsset() {
        EXECUTOR_MANAGER.addBackgroundTask(this::reload);
    }

    /**
     * Handle changing asset folder.
     */
    private void processChangeAsset() {
        EXECUTOR_MANAGER.addBackgroundTask(this::reload);
    }

    /**
     * @return the list of keys for watching to folders.
     */
    @NotNull
    private Array<WatchKey> getWatchKeys() {
        return watchKeys;
    }
}
