package com.ss.editor.manager;

import static com.ss.editor.FileExtensions.*;
import static com.ss.editor.config.DefaultSettingsProvider.Preferences.PREF_FAST_SKY_FOLDER;
import static com.ss.rlib.common.util.ArrayUtils.contains;
import static com.ss.rlib.common.util.FileUtils.getFiles;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import static com.ss.rlib.common.util.array.ArrayFactory.toArray;
import static java.lang.System.currentTimeMillis;
import static java.nio.file.StandardWatchEventKinds.*;
import com.jme3.asset.AssetEventListener;
import com.jme3.asset.AssetKey;
import com.ss.editor.EditorThread;
import com.ss.editor.FileExtensions;
import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.manager.AsyncEventManager.CombinedAsyncEventHandlerBuilder;
import com.ss.editor.ui.event.FxEventManager;
import com.ss.editor.ui.event.impl.*;
import com.ss.editor.util.EditorUtil;
import com.ss.editor.util.SimpleFileVisitor;
import com.ss.editor.util.SimpleFolderVisitor;
import com.ss.rlib.common.concurrent.util.ThreadUtils;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.common.manager.InitializeManager;
import com.ss.rlib.common.util.FileUtils;
import com.ss.rlib.common.util.StringUtils;
import com.ss.rlib.common.util.Utils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayComparator;
import com.ss.rlib.common.util.array.ArrayFactory;
import com.ss.rlib.common.util.array.ConcurrentArray;
import com.ss.rlib.common.util.dictionary.ConcurrentObjectDictionary;
import com.ss.rlib.common.util.dictionary.Dictionary;
import com.ss.rlib.common.util.dictionary.ObjectDictionary;
import com.ss.rlib.common.util.ref.Reference;
import com.ss.rlib.common.util.ref.ReferenceFactory;
import com.ss.rlib.common.util.ref.ReferenceType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * The class to manage working with resources of an editor.
 *
 * @author JavaSaBr
 */
public class ResourceManager extends EditorThread implements AssetEventListener {

    private static final Logger LOGGER = LoggerManager.getLogger(ResourceManager.class);

    private static final FxEventManager FX_EVENT_MANAGER = FxEventManager.getInstance();
    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    private static final ArrayComparator<String> STRING_ARRAY_COMPARATOR = StringUtils::compareIgnoreCase;

    private static final WatchService WATCH_SERVICE = FileUtils.newDefaultWatchService();

    @Nullable
    private static ResourceManager instance;

    @FromAnyThread
    public static @NotNull ResourceManager getInstance() {
        if (instance == null) instance = new ResourceManager();
        return instance;
    }

    /**
     * The table with last modify dates.
     */
    @NotNull
    private final ConcurrentObjectDictionary<String, Reference> assetCacheTable;

    /**
     * The table with interested resources.
     */
    @NotNull
    private final ConcurrentObjectDictionary<String, ConcurrentArray<String>> interestedResources;

    /**
     * The table with interested resources in the classpath.
     */
    @NotNull
    private final ConcurrentObjectDictionary<String, ConcurrentArray<String>> interestedResourcesInClasspath;

    /**
     * The list of additional ENVs.
     */
    @NotNull
    private final ConcurrentArray<Path> additionalEnvs;

    /**
     * The list of an additional classpath.
     */
    @NotNull
    private final ConcurrentArray<URLClassLoader> classLoaders;

    /**
     * The list of resources in the classpath.
     */
    @NotNull
    private final ConcurrentArray<String> resourcesInClasspath;

    /**
     * The list of keys for watching to folders.
     */
    @NotNull
    private final ConcurrentArray<WatchKey> watchKeys;

    private ResourceManager() {
        InitializeManager.valid(getClass());

        this.assetCacheTable = ConcurrentObjectDictionary.ofType(String.class, Reference.class);
        this.additionalEnvs = ConcurrentArray.ofType(Path.class);
        this.watchKeys = ConcurrentArray.ofType(WatchKey.class);
        this.classLoaders = ConcurrentArray.ofType(URLClassLoader.class);
        this.resourcesInClasspath = ConcurrentArray.ofType(String.class);
        this.interestedResources = ConcurrentObjectDictionary.ofType(String.class, ConcurrentArray.class);
        this.interestedResourcesInClasspath = ConcurrentObjectDictionary.ofType(String.class, ConcurrentArray.class);

        registerInterestedFileType(FileExtensions.JME_MATERIAL_DEFINITION);
        updateAdditionalEnvs();
        start();

        CombinedAsyncEventHandlerBuilder.of(this::registerFxListeners)
                .add(EditorFinishedLoadingEvent.EVENT_TYPE)
                .buildAndRegister();

        CombinedAsyncEventHandlerBuilder.of(this::registerAssetListenerAndReload)
                .add(ManagersInitializedEvent.EVENT_TYPE)
                .add(JmeContextCreatedEvent.EVENT_TYPE)
                .buildAndRegister();

        CombinedAsyncEventHandlerBuilder.of(this::prepareClasspathResources)
                .add(CoreClassesScannedEvent.EVENT_TYPE)
                .add(PluginsRegisteredResourcesEvent.EVENT_TYPE)
                .buildAndRegister();

        LOGGER.info("initialized.");
    }

    /**
     * Register all FX listeners.
     */
    @BackgroundThread
    private void registerFxListeners() {
        var executorManager = ExecutorManager.getInstance();
        executorManager.addFxTask(() -> {
            FX_EVENT_MANAGER.addEventHandler(ChangedCurrentAssetFolderEvent.EVENT_TYPE, this::processChangeAsset);
            FX_EVENT_MANAGER.addEventHandler(RequestedRefreshAssetEvent.EVENT_TYPE, this::processRefreshAsset);
            FX_EVENT_MANAGER.addEventHandler(CreatedFileEvent.EVENT_TYPE, this::processEvent);
            FX_EVENT_MANAGER.addEventHandler(DeletedFileEvent.EVENT_TYPE, this::processEvent);
            LOGGER.info("registered FX listeners.");
        });
    }

    /**
     * Register an asset listener and reload this manager.
     */
    @BackgroundThread
    private void registerAssetListenerAndReload() {
        var executorManager = ExecutorManager.getInstance();
        executorManager.addJmeTask(() -> {
            var assetManager = EditorUtil.getAssetManager();
            assetManager.addAssetEventListener(this);
            reload();
            LOGGER.info("registered an asset listener and reloaded.");
        });
    }

    /**
     * Try to find a resource by the resource path.
     *
     * @param resourcePath the resource path.
     * @return the URL or null.
     */
    @FromAnyThread
    public @Nullable URL tryToFindResource(@NotNull String resourcePath) {

        var classLoaders = ArrayFactory.<ClassLoader>newArray(ClassLoader.class);
        classLoaders.add(getClass().getClassLoader());

        var classpathManager = ClasspathManager.getInstance();
        var classesLoader = classpathManager.getClassesLoader();
        var librariesLoader = classpathManager.getLibrariesLoader();

        if (classesLoader != null) {
            classLoaders.add(classesLoader);
        }

        if (librariesLoader != null) {
            classLoaders.add(librariesLoader);
        }

        var pluginManager = PluginManager.getInstance();
        pluginManager.handlePluginsNow(plugin -> classLoaders.add(plugin.getClassLoader()));

        var altResourcePath = "/" + resourcePath;

        URL url = null;

        for (var classLoader : classLoaders) {
            url = classLoader.getResource(resourcePath);
            if (url != null) break;
            url = classLoader.getResource(altResourcePath);
            if (url != null) break;
        }

        if (url == null) {
            url = getClass().getResource("/" + resourcePath);
        }

        return url;
    }

    /**
     * Get the table with interested resources.
     *
     * @return the table with interested resources.
     */
    @FromAnyThread
    private @NotNull ConcurrentObjectDictionary<String, ConcurrentArray<String>> getInterestedResources() {
        return interestedResources;
    }

    /**
     * Get the table with interested resources in the classpath.
     *
     * @return the table with interested resources in the classpath.
     */
    @FromAnyThread
    private @NotNull ConcurrentObjectDictionary<String, ConcurrentArray<String>> getInterestedResourcesInClasspath() {
        return interestedResourcesInClasspath;
    }

    /**
     * Register the file type of interested resources.
     *
     * @param fileExtension the file type.
     */
    @FromAnyThread
    public void registerInterestedFileType(@NotNull String fileExtension) {

        getInterestedResources().runInWriteLock(fileExtension, (dictionary, extension) -> {
            if (!dictionary.containsKey(extension)) {
                dictionary.put(extension, ArrayFactory.newConcurrentStampedLockArray(String.class));
            }
        });

        getInterestedResourcesInClasspath().runInWriteLock(fileExtension, (dictionary, extension) -> {
            if (!dictionary.containsKey(extension)) {
                dictionary.put(extension, ArrayFactory.newConcurrentStampedLockArray(String.class));
            }
        });
    }

    @Override
    @JmeThread
    public void assetLoaded(@NotNull AssetKey key) {

        if (key.getCacheType() == null) {
            return;
        }

        var extension = key.getExtension();
        if (StringUtils.isEmpty(extension)) {
            return;
        }

        var reference = getAssetCacheTable()
                .getInReadLock(key.getName(), (references, name) ->
                        references.get(name, () -> ReferenceFactory.newRef(ReferenceType.LONG)));

        reference.setLong(currentTimeMillis());
    }

    @Override
    @JmeThread
    public void assetRequested(@NotNull AssetKey key) {

        if (key.getCacheType() == null) {
            return;
        }

        var extension = key.getExtension();
        if (StringUtils.isEmpty(extension)){
            return;
        }

        var reference = getAssetCacheTable()
                .getInReadLock(key.getName(), ObjectDictionary::get);

        if (reference == null) {
            return;
        }

        var realFile = EditorUtil.getRealFile(Paths.get(key.getName()));
        if (realFile == null || !Files.exists(realFile)) {
            return;
        }

        var timestamp = reference.getLong();

        var lastModifiedTime = FileUtils.getLastModifiedTime(realFile);
        if (lastModifiedTime.to(TimeUnit.MILLISECONDS) <= timestamp) {
            return;
        }

        EditorUtil.getAssetManager()
                .deleteFromCache(key);
    }

    @Override
    @FromAnyThread
    public void assetDependencyNotFound(@NotNull AssetKey parentKey, @NotNull AssetKey dependentAssetKey) {
    }

    /**
     * Update the list with additional ENVs.
     */
    @FromAnyThread
    public void updateAdditionalEnvs() {
        EXECUTOR_MANAGER.addBackgroundTask(this::updateAdditionalEnvsInBackground);
    }

    /**
     * Update the list of additional ENVs textures.
     */
    @BackgroundThread
    private void updateAdditionalEnvsInBackground() {

        var editorConfig = EditorConfig.getInstance();
        var additionalEnvs = getAdditionalEnvs();
        additionalEnvs.runInWriteLock(Collection::clear);

        var folder = editorConfig.getFile(PREF_FAST_SKY_FOLDER);
        if (folder == null) {
            return;
        }

        additionalEnvs.runInWriteLock(paths ->
                paths.addAll(getFiles(folder, IMAGE_HDR, IMAGE_TGA, IMAGE_PNG)));
    }

    /**
     * Get a list with additional ENVs.
     *
     * @return the list.
     */
    @FromAnyThread
    public @NotNull ConcurrentArray<Path> getAdditionalEnvs() {
        return additionalEnvs;
    }

    /**
     * Get the table with last modify dates.
     *
     * @return the table with last modify dates.
     */
    @FromAnyThread
    private @NotNull ConcurrentObjectDictionary<String, Reference> getAssetCacheTable() {
        return assetCacheTable;
    }

    /**
     * Handle a removed file.
     */
    @FxThread
    private void processEvent(@NotNull DeletedFileEvent event) {

        if (event.isDirectory()) {
            return;
        }

        var file = event.getFile();
        var extension = FileUtils.getExtension(file);

        var resources = getInterestedResources()
                .getInReadLock(extension, ObjectDictionary::get);

        var assetFile = notNull(EditorUtil.getAssetFile(file));
        var assetPath = EditorUtil.toAssetPath(assetFile);

        if (resources != null) {
            resources.runInWriteLock(assetPath, Array::fastRemove);
        }

        if (extension.endsWith(FileExtensions.JAVA_LIBRARY)) {

            var assetManager = EditorUtil.getAssetManager();
            var url = FileUtils.getUrl(file);

            var classLoaders = getClassLoaders();
            classLoaders.runInWriteLock(urlClassLoaders -> {

                var oldLoader = urlClassLoaders.findAny(url,
                        (loader, toCheck) -> contains(loader.getURLs(), toCheck));

                if (oldLoader != null) {
                    urlClassLoaders.fastRemove(oldLoader);
                    EXECUTOR_MANAGER.addJmeTask(() ->
                            assetManager.removeClassLoader(oldLoader));
                }
            });
        }
    }

    /**
     * Handle a created file.
     */
    @FxThread
    private void processEvent(@NotNull CreatedFileEvent event) {
        if (!event.isDirectory()) {
            handleFile(event.getFile());
        }
    }

    /**
     * Get the list of resources in the classpath.
     *
     * @return the list of resources in the classpath.
     */
    @FromAnyThread
    private @NotNull ConcurrentArray<String> getResourcesInClasspath() {
        return resourcesInClasspath;
    }

    /**
     * Prepare classpath resources.
     */
    @BackgroundThread
    private void prepareClasspathResources() {

        var classpathManager = ClasspathManager.getInstance();

        var resourcesInClasspath = getResourcesInClasspath();
        resourcesInClasspath.runInWriteLock(array ->
                array.addAll(classpathManager.getAllResources()));

        getInterestedResourcesInClasspath().runInWriteLock(dictionary -> {
            resourcesInClasspath.runInReadLock(array -> {
                for (var resource : array) {
                    var resources = dictionary.get(FileUtils.getExtension(resource));
                    if (resources != null) {
                        resources.runInWriteLock(resource, Collection::add);
                    }
                }
            });
        });

        LOGGER.info("prepared classpath resources.");
    }

    /**
     * Get the list of class loaders.
     *
     * @return the list of class loaders.
     */
    @FromAnyThread
    public @NotNull ConcurrentArray<URLClassLoader> getClassLoaders() {
        return classLoaders;
    }

    /**
     * Get available resources by the file extension.
     *
     * @param extension the interested extension.
     * @return the list of all available material definitions.
     */
    @FromAnyThread
    public @NotNull Array<String> getAvailableResources(@NotNull String extension) {
        var result = ArrayFactory.<String>newArraySet(String.class);
        addAvailableResources(result, extension);
        return result;
    }

    /**
     * Add available interested resources to the result array by the file extension.
     *
     * @param result    the array to store result.
     * @param extension the interested extension.
     */
    @FromAnyThread
    public void addAvailableResources(@NotNull Array<String> result, @NotNull String extension) {

        var resourcesInClasspath = getInterestedResourcesInClasspath();
        var resources = getInterestedResources();

        var inAsset = resources.getInReadLock(extension, ObjectDictionary::get);
        var inClassPath = resourcesInClasspath.getInReadLock(extension, ObjectDictionary::get);

        if (inAsset != null) {
            inAsset.runInReadLock(result,
                    (res, toStore) -> toStore.addAll(res));
        }

        if (inClassPath != null) {
            inClassPath.runInReadLock(result,
                (res, toStore) -> toStore.addAll(res));
        }

        result.sort(STRING_ARRAY_COMPARATOR);
    }

    /**
     * Reload available resources.
     */
    @BackgroundThread
    private void reloadInBackground() {

        var lastModifyTable = getAssetCacheTable();
        lastModifyTable.runInWriteLock(Dictionary::clear);

        var watchKeys = getWatchKeys();
        watchKeys.runInWriteLock(keys -> {
            keys.forEach(WatchKey::cancel);
            keys.clear();
        });

        var classLoadersCopy = ArrayFactory.<ClassLoader>newArray(ClassLoader.class);
        var classLoaders = getClassLoaders();
        classLoaders.runInWriteLock(classLoadersCopy, (urlClassLoaders, toStore) -> {
            toStore.addAll(urlClassLoaders);
            urlClassLoaders.clear();
        });

        EXECUTOR_MANAGER.addJmeTask(() -> {
            var assetManager = EditorUtil.getAssetManager();
            classLoadersCopy.forEach(assetManager::removeClassLoader);
            assetManager.clearCache();
        });

        var interestedResources = getInterestedResources();
        interestedResources.runInWriteLock(resources -> resources.forEach(Collection::clear));

        var editorConfig = EditorConfig.getInstance();
        var currentAsset = editorConfig.getCurrentAsset();
        if (currentAsset == null) {
            return;
        }

        FileUtils.walkFileTree(currentAsset, (SimpleFileVisitor)
                (file, attrs) -> handleFile(file));

        watchKeys.add(Utils.get(currentAsset, toRegister ->
                toRegister.register(WATCH_SERVICE, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY)));

        FileUtils.walkFileTree(currentAsset, (SimpleFolderVisitor)
                (file, attrs) -> registerFiles(watchKeys, file));
    }

    /**
     * Reload available resources.
     */
    @FromAnyThread
    public void reload() {
        EXECUTOR_MANAGER.addBackgroundTask(this::reloadInBackground);
    }

    @FromAnyThread
    private static void registerFiles(@NotNull Array<WatchKey> watchKeys, @NotNull Path file) {
        watchKeys.add(Utils.get(file, toRegister ->
                toRegister.register(WATCH_SERVICE, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY)));
    }

    /**
     * Handle a file event in an asset folder.
     */
    @FromAnyThread
    private void handleFile(@NotNull Path file) {

        if (Files.isDirectory(file)) {
            return;
        }

        var extension = FileUtils.getExtension(file);
        var toStore = getInterestedResources()
                .getInReadLock(extension, ObjectDictionary::get);

        if (toStore != null) {
            var assetFile = notNull(EditorUtil.getAssetFile(file));
            var assetPath = EditorUtil.toAssetPath(assetFile);
            toStore.runInWriteLock(assetPath, Collection::add);
        }

        if (extension.endsWith(FileExtensions.JAVA_LIBRARY)) {

            var assetManager = EditorUtil.getAssetManager();
            var url = Utils.get(file, FileUtils::getUrl);

            var classLoaders = getClassLoaders();
            long stamp = classLoaders.writeLock();
            try {

                var oldLoader = classLoaders.findAny(url,
                        (loader, toCheck) -> contains(loader.getURLs(), toCheck));

                if (oldLoader != null) {
                    return;
                }

                var newLoader = new URLClassLoader(toArray(url), getClass().getClassLoader());
                classLoaders.add(newLoader);

                EXECUTOR_MANAGER.addJmeTask(() ->
                        assetManager.addClassLoader(newLoader));

            } finally {
                classLoaders.writeUnlock(stamp);
            }
        }
    }

    @Override
    public void run() {
        super.run();

        while (true) {

            ThreadUtils.sleep(200);

            var watchKeys = getWatchKeys();

            List<WatchEvent<?>> watchEvents = null;
            WatchKey watchKey = null;

            long stamp = watchKeys.readLock();
            try {

                for (var key : watchKeys) {

                    watchKey = key;
                    watchEvents = key.pollEvents();

                    if (!watchEvents.isEmpty()) {
                        break;
                    }
                }

            } finally {
                watchKeys.readUnlock(stamp);
            }

            if (watchEvents == null || watchEvents.isEmpty()) {
                continue;
            }

            for (var watchEvent : watchEvents) {

                var file = (Path) watchEvent.context();
                var folder = (Path) watchKey.watchable();
                var realFile = folder.resolve(file);

                if (watchEvent.kind() == ENTRY_CREATE) {

                    var directory = Files.isDirectory(realFile);

                    var event = new CreatedFileEvent();
                    event.setFile(realFile);
                    event.setNeedSelect(false);
                    event.setDirectory(directory);

                    if (directory) {
                        registerWatchKey(realFile);
                    }

                    FX_EVENT_MANAGER.notify(event);

                } else if (watchEvent.kind() == ENTRY_DELETE) {

                    var directory = Files.isDirectory(realFile);

                    var event = new DeletedFileEvent();
                    event.setFile(realFile);
                    event.setDirectory(directory);

                    removeWatchKeyFor(realFile);

                    FX_EVENT_MANAGER.notify(event);

                } else if (watchEvent.kind() == ENTRY_MODIFY) {

                    var event = new FileChangedEvent();
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
    @FromAnyThread
    private @Nullable WatchKey findWatchKey(@NotNull Path path) {

        var watchKeys = getWatchKeys();
        var stamp = watchKeys.readLock();
        try {

            return watchKeys.findAny(path,
                    (watchKey, toCheck) -> watchKey.watchable().equals(toCheck));

        } finally {
            watchKeys.readUnlock(stamp);
        }
    }

    /**
     * Remove a watch key for the file.
     *
     * @param path the file.
     */
    @FromAnyThread
    private void removeWatchKeyFor(@NotNull Path path) {

        var watchKey = findWatchKey(path);
        if (watchKey == null) {
            return;
        }

        getWatchKeys().runInWriteLock(watchKey, Array::fastRemove);

        watchKey.cancel();
    }

    /**
     * Register a watch key for the file.
     *
     * @param path the file.
     */
    @FromAnyThread
    private void registerWatchKey(@NotNull Path path) {

        var watchKeys = getWatchKeys();
        var stamp = watchKeys.writeLock();
        try {

            watchKeys.add(Utils.get(path, f ->
                    f.register(WATCH_SERVICE, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY)));

        } finally {
            watchKeys.writeUnlock(stamp);
        }
    }

    /**
     * Handle refreshing asset folder.
     */
    @FromAnyThread
    private void processRefreshAsset(@NotNull RequestedRefreshAssetEvent event) {
        EXECUTOR_MANAGER.addBackgroundTask(this::reload);
    }

    /**
     * Handle changing asset folder.
     */
    @FromAnyThread
    private void processChangeAsset(@NotNull ChangedCurrentAssetFolderEvent event) {
        EXECUTOR_MANAGER.addBackgroundTask(this::reload);
    }

    /**
     * Get the list of keys for watching to folders.
     *
     * @return the list of keys for watching to folders.
     */
    @FromAnyThread
    private @NotNull ConcurrentArray<WatchKey> getWatchKeys() {
        return watchKeys;
    }
}
