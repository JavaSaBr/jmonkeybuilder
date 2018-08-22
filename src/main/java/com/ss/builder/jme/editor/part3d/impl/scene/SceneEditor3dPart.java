package com.ss.builder.editor.part3d.impl.scene;

import static com.ss.editor.config.DefaultSettingsProvider.Defaults.PREF_DEFAULT_FXAA_FILTER;
import static com.ss.editor.config.DefaultSettingsProvider.Defaults.PREF_DEFAULT_TONEMAP_FILTER;
import static com.ss.editor.config.DefaultSettingsProvider.Preferences.PREF_FILTER_FXAA;
import static com.ss.editor.config.DefaultSettingsProvider.Preferences.PREF_FILTER_TONEMAP;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Node;
import com.ss.builder.JmeApplication;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.manager.ExecutorManager;
import com.ss.builder.util.EditorUtils;
import com.ss.editor.JmeApplication;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.extension.scene.SceneNode;
import com.ss.editor.extension.scene.app.state.SceneAppState;
import com.ss.editor.extension.scene.filter.SceneFilter;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.ui.component.editor.impl.scene.SceneFileEditor;
import com.ss.editor.util.EditorUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link AbstractSceneEditor3dPart} for the {@link SceneFileEditor}.
 *
 * @author JavaSaBr
 */
public class SceneEditor3dPart extends AbstractSceneEditor3dPart<SceneFileEditor, SceneNode> {

    /**
     * The flag of showing light models.
     */
    private boolean lightShowed;

    /**
     * The flag of showing audio models.
     */
    private boolean audioShowed;

    public SceneEditor3dPart(@NotNull SceneFileEditor fileEditor) {
        super(fileEditor);

        this.lightShowed = true;
        this.audioShowed = true;

        stateNode.attachChild(modelNode);
        stateNode.attachChild(toolNode);
    }

    @Override
    @JmeThread
    protected int getGridSize() {
        return 1000;
    }

    @Override
    @JmeThread
    protected void attachModel(@NotNull SceneNode model, @NotNull Node modelNode) {
    }

    @Override
    @JmeThread
    protected void detachPrevModel(@NotNull Node modelNode, @Nullable SceneNode currentModel) {
    }

    @Override
    @JmeThread
    public void initialize(@NotNull AppStateManager stateManager, @NotNull Application application) {
        super.initialize(stateManager, application);

        var currentModel = getCurrentModel();

        if (currentModel != null) {
            modelNode.attachChild(currentModel);
        }

        var jmeApplication = JmeApplication.getInstance();
        var fxaaFilter = jmeApplication.getFXAAFilter();
        var toneMapFilter = jmeApplication.getToneMapFilter();

        fxaaFilter.setEnabled(false);
        toneMapFilter.setEnabled(false);
    }

    @Override
    @JmeThread
    public void cleanup() {
        super.cleanup();

        var currentModel = getCurrentModel();

        if (currentModel != null) {
            modelNode.detachChild(currentModel);
        }

        var editorConfig = EditorConfig.getInstance();
        var jmeApplication = JmeApplication.getInstance();

        var fxaaFilter = jmeApplication.getFXAAFilter();
        fxaaFilter.setEnabled(editorConfig.getBoolean(PREF_FILTER_FXAA, PREF_DEFAULT_FXAA_FILTER));

        var toneMapFilter = jmeApplication.getToneMapFilter();
        toneMapFilter.setEnabled(editorConfig.getBoolean(PREF_FILTER_TONEMAP, PREF_DEFAULT_TONEMAP_FILTER));
    }

    /**
     * Add the scene app state to this editor.
     *
     * @param appState the scene app state.
     */
    @FromAnyThread
    public void addAppState(@NotNull SceneAppState appState) {
        ExecutorManager.getInstance()
                .addJmeTask(() -> addAppStateInJme(appState));
    }

    /**
     * Add a scene app state to this editor in jME thread.
     *
     * @param appState the scene app state.
     */
    @JmeThread
    private void addAppStateInJme(@NotNull SceneAppState appState) {
        var stateManager = EditorUtils.getStateManager();
        stateManager.attach(appState);
    }

    /**
     * Remove the scene app state from this editor.
     *
     * @param appState the scene app state.
     */
    @FromAnyThread
    public void removeAppState(@NotNull SceneAppState appState) {
        ExecutorManager.getInstance()
                .addJmeTask(() -> removeAppStateInJme(appState));
    }

    /**
     * Remove the scene app state from this editor in jME thread.
     *
     * @param appState the scene app state.
     */
    @JmeThread
    private void removeAppStateInJme(@NotNull SceneAppState appState) {
        var stateManager = EditorUtils.getStateManager();
        stateManager.detach(appState);
    }

    /**
     * Add the scene filter to this editor.
     *
     * @param sceneFilter the scene filter.
     */
    @FromAnyThread
    public void addFilter(@NotNull SceneFilter sceneFilter) {
        ExecutorManager.getInstance()
                .addJmeTask(() -> addFilterImpl(sceneFilter));
    }

    /**
     * Add the scene filter to this editor in jME thread.
     *
     * @param sceneFilter the scene filter.
     */
    @JmeThread
    private void addFilterImpl(@NotNull SceneFilter sceneFilter) {
        var postProcessor = EditorUtils.getGlobalFilterPostProcessor();
        postProcessor.addFilter(sceneFilter.get());
    }

    /**
     * Remove the scene filter from this editor.
     *
     * @param sceneFilter the scene filter.
     */
    @FromAnyThread
    public void removeFilter(@NotNull SceneFilter sceneFilter) {
        ExecutorManager.getInstance()
                .addJmeTask(() -> removeFilterInJme(sceneFilter));
    }

    /**
     * Remove the scene filter from this editor in jME thread.
     *
     * @param sceneFilter the scene filter.
     */
    @JmeThread
    private void removeFilterInJme(@NotNull SceneFilter sceneFilter) {
        var postProcessor = EditorUtils.getGlobalFilterPostProcessor();
        postProcessor.removeFilter(sceneFilter.get());
    }

    /**
     * Return true if need to show light models.
     *
     * @return true if need to show light models.
     */
    @JmeThread
    private boolean isLightShowed() {
        return lightShowed;
    }

    /**
     * Set true if need to show light models.
     *
     * @param lightShowed true if need to show light models.
     */
    @JmeThread
    private void setLightShowed(boolean lightShowed) {
        this.lightShowed = lightShowed;
    }

    /**
     * Return true if need to show audio models.
     *
     * @return true if need to show audio models.
     */
    @JmeThread
    private boolean isAudioShowed() {
        return audioShowed;
    }

    /**
     * Set true if need to show audio models.
     *
     * @param audioShowed true if need to show audio models.
     */
    @JmeThread
    private void setAudioShowed(boolean audioShowed) {
        this.audioShowed = audioShowed;
    }

    /**
     * Change light showing.
     *
     * @param showed the showed
     */
    @FromAnyThread
    public void updateLightShowed(boolean showed) {
        ExecutorManager.getInstance()
                .addJmeTask(() -> updateLightShowedInJme(showed));
    }

    /**
     * Change light showing in jME thread.
     *
     * @param showed the showed.
     */
    @JmeThread
    private void updateLightShowedInJme(boolean showed) {

        if (showed == isLightShowed()) {
            return;
        }

        if (showed) {
            modelNode.attachChild(lightNode);
        } else {
            modelNode.detachChild(lightNode);
        }

        setLightShowed(showed);
    }

    /**
     * Change audio showing.
     *
     * @param showed the showed.
     */
    @FromAnyThread
    public void updateAudioShowed(boolean showed) {
        ExecutorManager.getInstance()
                .addJmeTask(() -> updateAudioShowedInJme(showed));
    }

    /**
     * The process to change audio showing.
     */
    @JmeThread
    private void updateAudioShowedInJme(boolean showed) {

        if (showed == isAudioShowed()) {
            return;
        }

        if (showed) {
            modelNode.attachChild(audioNode);
        } else {
            modelNode.detachChild(audioNode);
        }

        setAudioShowed(showed);
    }
}
