package com.ss.editor.state.editor.impl.post.filter;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.asset.MaterialKey;
import com.jme3.material.Material;
import com.jme3.post.FilterPostProcessor;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.util.SkyFactory;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.state.editor.impl.AbstractEditorState;
import com.ss.editor.ui.component.editor.impl.post.filter.PostFilterEditor;

import org.jetbrains.annotations.NotNull;

import rlib.util.dictionary.ConcurrentObjectDictionary;
import rlib.util.dictionary.DictionaryFactory;

/**
 * Реализация 3D части для редактора пост эффектов.
 *
 * @author Ronn
 */
public class PostFilterEditorState extends AbstractEditorState<PostFilterEditor> {

    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    /**
     * Таблица активных фильтров.
     */
    private final ConcurrentObjectDictionary<MaterialKey, GenericFilter> filters;

    public PostFilterEditorState(final PostFilterEditor fileEditor) {
        super(fileEditor);
        this.filters = DictionaryFactory.newConcurrentAtomicObjectDictionary();

        final AssetManager assetManager = EDITOR.getAssetManager();
        final Spatial sky = SkyFactory.createSky(assetManager, "graphics/textures/sky/path.hdr", SkyFactory.EnvMapType.EquirectMap);

        final Node stateNode = getStateNode();
        stateNode.attachChild(sky);
    }

    @Override
    protected boolean needEditorCamera() {
        return true;
    }

    /**
     * @return таблица активных фильтров.
     */
    private ConcurrentObjectDictionary<MaterialKey, GenericFilter> getFilters() {
        return filters;
    }

    @Override
    public void initialize(@NotNull AppStateManager stateManager, @NotNull Application application) {
        super.initialize(stateManager, application);

        final FilterPostProcessor postProcessor = EDITOR.getPostProcessor();

        final ConcurrentObjectDictionary<MaterialKey, GenericFilter> filters = getFilters();
        final long stamp = filters.readLock();
        try {
            filters.forEach(postProcessor::addFilter);
        } finally {
            filters.readUnlock(stamp);
        }
    }

    @Override
    public void cleanup() {
        super.cleanup();

        final FilterPostProcessor postProcessor = EDITOR.getPostProcessor();

        final ConcurrentObjectDictionary<MaterialKey, GenericFilter> filters = getFilters();
        final long stamp = filters.readLock();
        try {
            filters.forEach(postProcessor::removeFilter);
        } finally {
            filters.readUnlock(stamp);
        }
    }

    /**
     * Есть ли уже здесь фильтр по этому материалу.
     *
     * @param materialKey ключ для загрузки материала.
     * @return есть ли уже такой фильтр.
     */
    public boolean hasFilter(final MaterialKey materialKey) {
        final ConcurrentObjectDictionary<MaterialKey, GenericFilter> filters = getFilters();
        final long stamp = filters.readLock();
        try {
            return filters.containsKey(materialKey);
        } finally {
            filters.readUnlock(stamp);
        }
    }

    /**
     * Добавление нового фильтра на сцену.
     *
     * @param material материал нового фильтра.
     */
    public void addFilter(final Material material) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> addFilterImpl(material));
    }

    /**
     * Процесс добавление нового материала.
     */
    private void addFilterImpl(final Material material) {

        final GenericFilter genericFilter = new GenericFilter(material, false, true);
        final FilterPostProcessor postProcessor = EDITOR.getPostProcessor();

        final ConcurrentObjectDictionary<MaterialKey, GenericFilter> filters = getFilters();
        final long stamp = filters.writeLock();
        try {

            final MaterialKey key = (MaterialKey) material.getKey();
            if (filters.containsKey(key)) return;

            filters.put(key, genericFilter);
            if (isInitialized()) postProcessor.addFilter(genericFilter);

        } finally {
            filters.writeUnlock(stamp);
        }
    }

    /**
     * Удаление фильтра из сцены.
     */
    public void removeFilter(final Material material) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> removeFilterImpl(material));
    }

    /**
     * Процесс удаления материала.
     */
    private void removeFilterImpl(final Material material) {

        final FilterPostProcessor postProcessor = EDITOR.getPostProcessor();

        final ConcurrentObjectDictionary<MaterialKey, GenericFilter> filters = getFilters();
        final long stamp = filters.writeLock();
        try {

            final MaterialKey key = (MaterialKey) material.getKey();
            final GenericFilter genericFilter = filters.remove(key);

            if (genericFilter == null) return;
            if (isInitialized()) postProcessor.removeFilter(genericFilter);

        } finally {
            filters.writeUnlock(stamp);
        }
    }
}
