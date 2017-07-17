package com.ss.editor.manager;

import static com.ss.editor.FileExtensions.*;
import static com.ss.editor.util.EditorUtil.*;
import static com.ss.rlib.util.ArrayUtils.contains;
import static com.ss.rlib.util.ArrayUtils.move;
import static com.ss.rlib.util.FileUtils.getFiles;
import static com.ss.rlib.util.FileUtils.toUrl;
import static com.ss.rlib.util.ObjectUtils.notNull;
import static com.ss.rlib.util.Utils.get;
import static com.ss.rlib.util.array.ArrayFactory.toArray;
import static com.ss.rlib.util.ref.ReferenceFactory.newRef;
import static java.lang.System.currentTimeMillis;
import static java.nio.file.StandardWatchEventKinds.*;
import com.jme3.asset.AssetEventListener;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.ss.editor.Editor;
import com.ss.editor.EditorThread;
import com.ss.editor.FileExtensions;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.*;
import com.ss.editor.util.SimpleFileVisitor;
import com.ss.editor.util.SimpleFolderVisitor;
import com.ss.rlib.classpath.ClassPathScanner;
import com.ss.rlib.classpath.ClassPathScannerFactory;
import com.ss.rlib.concurrent.util.ThreadUtils;
import com.ss.rlib.logging.Logger;
import com.ss.rlib.logging.LoggerManager;
import com.ss.rlib.manager.InitializeManager;
import com.ss.rlib.util.FileUtils;
import com.ss.rlib.util.StringUtils;
import com.ss.rlib.util.Utils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayComparator;
import com.ss.rlib.util.array.ArrayFactory;
import com.ss.rlib.util.dictionary.DictionaryFactory;
import com.ss.rlib.util.dictionary.ObjectDictionary;
import com.ss.rlib.util.ref.Reference;
import com.ss.rlib.util.ref.ReferenceType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * The class to manage working with resources of an editor.
 *
 * @author JavaSaBr
 */
public class ResourceManager extends EditorThread implements AssetEventListener {

    @NotNull
    private static final Logger LOGGER = LoggerManager.getLogger(ResourceManager.class);

    @NotNull
    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    @NotNull
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

    @Nullable
    private static ResourceManager instance;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    @NotNull
    public static ResourceManager getInstance() {
        if (instance == null) instance = new ResourceManager();
        return instance;
    }

    /**
     * The table with last modify dates.
     */
    @NotNull
    private final ObjectDictionary<String, Reference> assetCacheTable;

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

    /**
     * The classpath scanner.
     */
    @NotNull
    private final ClassPathScanner classPathScanner;

    /**
     * Instantiates a new Resource manager.
     */
    public ResourceManager() {
        InitializeManager.valid(getClass());

        this.assetCacheTable = DictionaryFactory.newObjectDictionary();
        this.additionalEnvs = ArrayFactory.newArray(Path.class);
        this.watchKeys = ArrayFactory.newArray(WatchKey.class);
        this.classLoaders = ArrayFactory.newArray(URLClassLoader.class);
        this.resourcesInClasspath = ArrayFactory.newArray(String.class);
        this.materialDefinitionsInClasspath = ArrayFactory.newArray(String.class);
        this.materialDefinitions = ArrayFactory.newArray(String.class);

        classPathScanner = ClassPathScannerFactory.newManifestScanner(Editor.class, "Class-Path");
        classPathScanner.scanning(path -> {

            if (!(path.contains("jme3-core") || path.contains("jme3-effects") || path.contains("tonegod"))) {
                return false;
            } else if (path.contains("natives")) {
                return false;
            } else if (path.contains("sources") || path.contains("javadoc")) {
                return false;
            }

            return true;
        });

        classPathScanner.getAllResources(resourcesInClasspath);
        prepareClasspathResources();

        final ExecutorManager executorManager = ExecutorManager.getInstance();
        executorManager.addFXTask(() -> {
            final FXEventManager fxEventManager = FXEventManager.getInstance();
            fxEventManager.addEventHandler(ChangedCurrentAssetFolderEvent.EVENT_TYPE, event -> processChangeAsset());
            fxEventManager.addEventHandler(RequestedRefreshAssetEvent.EVENT_TYPE, event -> processRefreshAsset());
            fxEventManager.addEventHandler(CreatedFileEvent.EVENT_TYPE, event -> processEvent((CreatedFileEvent) event));
            fxEventManager.addEventHandler(DeletedFileEvent.EVENT_TYPE, event -> processEvent((DeletedFileEvent) event));
        });

        final Editor editor = Editor.getInstance();
        final AssetManager assetManager = editor.getAssetManager();
        assetManager.addAssetEventListener(this);

        updateAdditionalEnvs();
        reload();
        start();
    }

    /**
     * Get a classpath scanner.
     *
     * @return the classpath scanner.
     */
    @NotNull
    @FromAnyThread
    public ClassPathScanner getClassPathScanner() {
        return classPathScanner;
    }

    @Override
    @FromAnyThread
    public synchronized void assetLoaded(@NotNull final AssetKey key) {

        final String extension = key.getExtension();
        if (StringUtils.isEmpty(extension)) return;

        final ObjectDictionary<String, Reference> table = getAssetCacheTable();
        final Reference reference = notNull(table.get(key.getName(), () -> newRef(ReferenceType.LONG)));
        reference.setLong(currentTimeMillis());
    }

    @Override
    @FromAnyThread
    public synchronized void assetRequested(@NotNull final AssetKey key) {

        final String extension = key.getExtension();
        if (StringUtils.isEmpty(extension)) return;

        final ObjectDictionary<String, Reference> table = getAssetCacheTable();
        final Reference reference = table.get(key.getName());
        if (reference == null) return;

        final Path assetFile = getRealFile(Paths.get(key.getName()));
        if (assetFile == null || !Files.exists(assetFile)) return;

        try {

            final long timestamp = reference.getLong();

            final FileTime lastModifiedTime = Files.getLastModifiedTime(assetFile);
            if (lastModifiedTime.to(TimeUnit.MILLISECONDS) <= timestamp) return;

            final Editor editor = Editor.getInstance();
            final AssetManager assetManager = editor.getAssetManager();
            assetManager.deleteFromCache(key);

        } catch (final IOException e) {
            LOGGER.warning(e);
        }
    }

    @Override
    @FromAnyThread
    public void assetDependencyNotFound(@NotNull final AssetKey parentKey, @NotNull final AssetKey dependentAssetKey) {

    }

    /**
     * Update the list with additional ENVs.
     */
    @FromAnyThread
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
     * @return the table with last modify dates.
     */
    @NotNull
    private ObjectDictionary<String, Reference> getAssetCacheTable() {
        return assetCacheTable;
    }

    /**
     * Handle a removed file.
     */
    @FromAnyThread
    private synchronized void processEvent(@NotNull final DeletedFileEvent event) {
        if (event.isDirectory()) return;

        final Path file = event.getFile();
        final String extension = FileUtils.getExtension(file);

        final Path assetFile = notNull(getAssetFile(file), "Not found asset file for " + file);
        final String assetPath = toAssetPath(assetFile);

        if (extension.endsWith(FileExtensions.JME_MATERIAL_DEFINITION)) {
            final Array<String> materialDefinitions = getMaterialDefinitions();
            materialDefinitions.fastRemove(assetPath);
        } else if (extension.endsWith(FileExtensions.JAVA_LIBRARY)) {

            final Editor editor = Editor.getInstance();
            final AssetManager assetManager = editor.getAssetManager();

            final URL url = toUrl(file);
            final Array<URLClassLoader> classLoaders = getClassLoaders();

            final URLClassLoader oldLoader = classLoaders.search(url, (loader, toCheck) -> contains(loader.getURLs(), toCheck));

            if (oldLoader != null) {
                classLoaders.fastRemove(oldLoader);
                assetManager.removeClassLoader(oldLoader);
            }
        }
    }

    /**
     * Handle a created file.
     */
    @FromAnyThread
    private synchronized void processEvent(@NotNull final CreatedFileEvent event) {
        if (event.isDirectory()) return;
        handleFile(event.getFile());
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
     * Gets class loaders.
     *
     * @return the list of an additional classpath.
     */
    @NotNull
    public Array<URLClassLoader> getClassLoaders() {
        return classLoaders;
    }

    /**
     * Gets available material definitions.
     *
     * @return the list of all available material definitions.
     */
    @NotNull
    @FromAnyThread
    public synchronized Array<String> getAvailableMaterialDefinitions() {
        final Array<String> result = ArrayFactory.newArray(String.class);
        addAvailableMaterialDefinitionsTo(result);
        return result;
    }

    /**
     * Add available material definitions to the result array.
     *
     * @param result the result
     */
    @FromAnyThread
    public synchronized void addAvailableMaterialDefinitionsTo(@NotNull final Array<String> result) {

        final Array<String> materialDefinitions = getMaterialDefinitions();

        move(materialDefinitions, result, false);

        final Array<String> materialDefinitionsInClasspath = getMaterialDefinitionsInClasspath();
        materialDefinitionsInClasspath.forEach(result, (resource, container) -> {
            if (!container.contains(resource)) container.add(resource);
        });

        result.sort(STRING_ARRAY_COMPARATOR);
    }

    /**
     * Reload available resources.
     */
    @FromAnyThread
    private synchronized void reload() {

        final ObjectDictionary<String, Reference> lastModifyTable = getAssetCacheTable();
        lastModifyTable.clear();

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
    @FromAnyThread
    private synchronized void handleFile(@NotNull final Path file) {
        if (Files.isDirectory(file)) return;

        final String extension = FileUtils.getExtension(file);

        if (extension.endsWith(FileExtensions.JME_MATERIAL_DEFINITION)) {
            final Path assetFile = notNull(getAssetFile(file), "Not found asset file for " + file);
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

    /**
     * Find a watch key for the file.
     *
     * @param path the file.
     * @return the watch key or null.
     */
    @Nullable
    @FromAnyThread
    private synchronized WatchKey findWatchKey(@NotNull final Path path) {
        final Array<WatchKey> watchKeys = getWatchKeys();
        return watchKeys.search(path, (watchKey, toCheck) -> watchKey.watchable().equals(toCheck));
    }

    /**
     * Remove a watch key for the file.
     *
     * @param path the file.
     */
    @FromAnyThread
    private synchronized void removeWatchKeyFor(@NotNull final Path path) {

        final WatchKey watchKey = findWatchKey(path);
        if (watchKey == null) return;

        final Array<WatchKey> watchKeys = getWatchKeys();
        watchKeys.fastRemove(watchKey);

        watchKey.cancel();
    }

    /**
     * Register a watch key for the file.
     *
     * @param path the file.
     */
    @FromAnyThread
    private synchronized void registerWatchKey(@NotNull final Path path) {
        Utils.run(() -> getWatchKeys().add(path.register(WATCH_SERVICE, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY)));
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
