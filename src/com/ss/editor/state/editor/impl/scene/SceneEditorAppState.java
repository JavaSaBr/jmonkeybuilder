package com.ss.editor.state.editor.impl.scene;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.ColorRGBA;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.post.filters.ToneMapFilter;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Grid;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.ui.component.editor.impl.scene.SceneFileEditor;
import com.ss.extension.scene.SceneNode;
import com.ss.extension.scene.app.state.SceneAppState;
import com.ss.extension.scene.filter.SceneFilter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import rlib.util.array.Array;

/**
 * The implementation of the {@link AbstractSceneEditorAppState} for the {@link SceneFileEditor}.
 *
 * @author JavaSaBr
 */
public class SceneEditorAppState extends AbstractSceneEditorAppState<SceneFileEditor, SceneNode> {

    public SceneEditorAppState(@NotNull final SceneFileEditor fileEditor) {
        super(fileEditor);

        final Node stateNode = getStateNode();
        stateNode.attachChild(getModelNode());
        stateNode.attachChild(getToolNode());
    }

    @Override
    public void notifyTransformed(@NotNull final Spatial spatial) {
        getFileEditor().notifyTransformed(spatial);
    }

    @NotNull
    @Override
    protected Geometry createGrid() {
        final Geometry grid = new Geometry("grid", new Grid(2000, 2000, 1.0f));
        grid.setMaterial(createColorMaterial(ColorRGBA.Gray));
        grid.setLocalTranslation(-1000, 0, -1000);
        return grid;
    }

    @Override
    protected void notifySelected(@Nullable final Object object) {
        getFileEditor().notifySelected(object);
    }

    @Override
    protected void undo() {
        final SceneFileEditor fileEditor = getFileEditor();
        fileEditor.undo();
    }

    @Override
    protected void redo() {
        final SceneFileEditor fileEditor = getFileEditor();
        fileEditor.redo();
    }

    @Override
    public void initialize(@NotNull final AppStateManager stateManager, @NotNull final Application application) {
        super.initialize(stateManager, application);

        final FilterPostProcessor postProcessor = EDITOR.getPostProcessor();
        final SceneNode currentModel = getCurrentModel();

        if (currentModel != null) {
            final Array<SceneAppState> appStates = currentModel.getAppStates();
            appStates.forEach(stateManager, (state, manager) -> manager.attach(state));

            final Array<SceneFilter<?>> filters = currentModel.getFilters();
            filters.forEach(sceneFilter -> postProcessor.addFilter(sceneFilter.get()));
        }

        final FXAAFilter fxaaFilter = EDITOR.getFXAAFilter();
        fxaaFilter.setEnabled(false);

        final ToneMapFilter toneMapFilter = EDITOR.getToneMapFilter();
        toneMapFilter.setEnabled(false);
    }

    @Override
    public void cleanup() {
        super.cleanup();

        final FilterPostProcessor postProcessor = EDITOR.getPostProcessor();
        final SceneNode currentModel = getCurrentModel();

        if (currentModel != null) {

            final Array<SceneAppState> appStates = currentModel.getAppStates();
            appStates.forEach(stateManager, (state, manager) -> manager.detach(state));

            final Array<SceneFilter<?>> filters = currentModel.getFilters();
            filters.forEach(sceneFilter -> postProcessor.removeFilter(sceneFilter.get()));
        }

        final EditorConfig editorConfig = EditorConfig.getInstance();

        final FXAAFilter fxaaFilter = EDITOR.getFXAAFilter();
        fxaaFilter.setEnabled(editorConfig.isFXAA());

        final ToneMapFilter toneMapFilter = EDITOR.getToneMapFilter();
        toneMapFilter.setEnabled(editorConfig.isToneMapFilter());
    }

    /**
     * Add a scene app state.
     *
     * @param appState the scene app state.
     */
    @FromAnyThread
    public void addAppState(@NotNull final SceneAppState appState) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> addAppStateImpl(appState));
    }

    private void addAppStateImpl(@NotNull final SceneAppState appState) {
        final AppStateManager stateManager = EDITOR.getStateManager();
        stateManager.attach(appState);
    }

    /**
     * Remove a scene app state.
     *
     * @param appState the scene app state.
     */
    @FromAnyThread
    public void removeAppState(@NotNull final SceneAppState appState) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> removeAppStateImpl(appState));
    }

    private void removeAppStateImpl(@NotNull final SceneAppState appState) {
        final AppStateManager stateManager = EDITOR.getStateManager();
        stateManager.detach(appState);
    }

    /**
     * Add a scene filter.
     *
     * @param sceneFilter the scene filter.
     */
    @FromAnyThread
    public void addFilter(@NotNull final SceneFilter<?> sceneFilter) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> addFilterImpl(sceneFilter));
    }

    private void addFilterImpl(@NotNull final SceneFilter<?> sceneFilter) {
        final FilterPostProcessor postProcessor = EDITOR.getPostProcessor();
        postProcessor.addFilter(sceneFilter.get());
    }

    /**
     * Remove a scene filter.
     *
     * @param sceneFilter the scene filter.
     */
    @FromAnyThread
    public void removeFilter(@NotNull final SceneFilter<?> sceneFilter) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> removeFilterImpl(sceneFilter));
    }

    private void removeFilterImpl(@NotNull final SceneFilter<?> sceneFilter) {
        final FilterPostProcessor postProcessor = EDITOR.getPostProcessor();
        postProcessor.removeFilter(sceneFilter.get());
    }
}
