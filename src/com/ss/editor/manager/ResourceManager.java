package com.ss.editor.manager;

import com.ss.editor.Editor;
import com.ss.editor.FileExtensions;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.ChangedCurrentAssetFolderEvent;
import com.ss.editor.ui.event.impl.CreatedFileEvent;
import com.ss.editor.ui.event.impl.DeletedFileEvent;
import com.ss.editor.ui.event.impl.RequestedRefreshAssetEvent;
import com.ss.editor.util.EditorUtil;
import com.ss.editor.util.SimpleFileVisitor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import rlib.classpath.ClassPathScanner;
import rlib.classpath.ClassPathScannerFactory;
import rlib.logging.Logger;
import rlib.logging.LoggerManager;
import rlib.manager.InitializeManager;
import rlib.util.StringUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayComparator;
import rlib.util.array.ArrayFactory;

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

        this.resourcesInClasspath = ArrayFactory.newArray(String.class);
        this.materialDefinitionsInClasspath = ArrayFactory.newArray(String.class);
        this.materialDefinitions = ArrayFactory.newConcurrentAtomicArray(String.class);

        final ClassPathScanner scanner = ClassPathScannerFactory.newManifestScanner(Editor.class, "Class-Path");
        scanner.scanning(path -> {

            if (!path.contains("jME")) {
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
    }

    /**
     * Обработка удаления файла из Asset.
     */
    private void processEvent(final DeletedFileEvent event) {

        final Path file = event.getFile();
        final String filename = file.getFileName().toString();

        if (filename.endsWith(FileExtensions.JME_MATERIAL_DEFINITION)) {

            final Path assetFile = EditorUtil.getAssetFile(file);

            final Array<String> materialDefinitions = getMaterialDefinitions();
            materialDefinitions.writeLock();
            try {
                materialDefinitions.fastRemove(assetFile.toString());
            } finally {
                materialDefinitions.writeUnlock();
            }
        }
    }

    /**
     * Обработка созадния файла в Asset.
     */
    private void processEvent(final CreatedFileEvent event) {

        final Path file = event.getFile();
        final String filename = file.getFileName().toString();

        if (filename.endsWith(FileExtensions.JME_MATERIAL_DEFINITION)) {

            final Path assetFile = EditorUtil.getAssetFile(file);

            final Array<String> materialDefinitions = getMaterialDefinitions();
            materialDefinitions.writeLock();
            try {

                if (!materialDefinitions.contains(assetFile)) {
                    materialDefinitions.add(filename);
                }

            } finally {
                materialDefinitions.writeUnlock();
            }
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
     * @return список доступных типов материалов.
     */
    public Array<String> getAvailableMaterialDefinitions() {

        final Array<String> result = ArrayFactory.newArray(String.class);

        final Array<String> materialDefinitions = getMaterialDefinitions();
        materialDefinitions.readLock();
        try {
            result.addAll(materialDefinitions);
        } finally {
            materialDefinitions.readUnlock();
        }

        final Array<String> materialDefinitionsInClasspath = getMaterialDefinitionsInClasspath();
        materialDefinitionsInClasspath.forEach(resource -> {
            if (!result.contains(resource)) {
                result.add(resource);
            }
        });

        result.sort(STRING_ARRAY_COMPARATOR);

        return result;
    }

    /**
     * Перезагрузка доступных ресурсов.
     */
    private void reload() {

        final Array<String> materialDefinitions = getMaterialDefinitions();
        materialDefinitions.writeLock();
        try {

            materialDefinitions.clear();

            final EditorConfig editorConfig = EditorConfig.getInstance();
            final Path currentAsset = editorConfig.getCurrentAsset();

            if (currentAsset == null) {
                return;
            }

            final SimpleFileVisitor fileVisitor = (file, attrs) -> {

                if (Files.isDirectory(file)) {
                    return;
                }

                final String filename = file.getFileName().toString();

                if (filename.endsWith(FileExtensions.JME_MATERIAL_DEFINITION)) {
                    final Path assetFile = EditorUtil.getAssetFile(file);
                    materialDefinitions.add(assetFile.toString());
                }
            };

            try {
                Files.walkFileTree(currentAsset, fileVisitor);
            } catch (IOException e) {
                LOGGER.warning(e);
            }

        } finally {
            materialDefinitions.writeUnlock();
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
