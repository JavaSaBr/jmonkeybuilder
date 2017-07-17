package com.ss.editor.state.editor.impl.model;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.environment.generation.JobProgressAdapter;
import com.jme3.light.DirectionalLight;
import com.jme3.light.LightProbe;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.state.editor.impl.scene.AbstractSceneEditorAppState;
import com.ss.editor.ui.component.editor.impl.model.ModelFileEditor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import tonegod.emitter.filter.TonegodTranslucentBucketFilter;

/**
 * The implementation of the {@link AbstractSceneEditorAppState} for the {@link ModelFileEditor}.
 *
 * @author JavaSaBr
 */
public class ModelEditorAppState extends AbstractSceneEditorAppState<ModelFileEditor, Spatial> {

    private final JobProgressAdapter<LightProbe> probeHandler = new JobProgressAdapter<LightProbe>() {

        @Override
        public void done(final LightProbe result) {
            if (!isInitialized()) return;
            notifyProbeComplete();
        }
    };

    /**
     * The array of custom skies.
     */
    @NotNull
    private final Array<Spatial> customSky;

    /**
     * The node for the placement of custom sky.
     */
    @NotNull
    private final Node customSkyNode;

    /**
     * The current fast sky.
     */
    @Nullable
    private Spatial currentFastSky;

    /**
     * The flag of activity light of the camera.
     */
    private boolean lightEnabled;

    /**
     * The frame rate.
     */
    private int frame;

    /**
     * Instantiates a new Model editor app state.
     *
     * @param fileEditor the file editor
     */
    public ModelEditorAppState(final ModelFileEditor fileEditor) {
        super(fileEditor);
        this.customSkyNode = new Node("Custom Sky");
        this.customSky = ArrayFactory.newArray(Spatial.class);

        final Node stateNode = getStateNode();
        stateNode.attachChild(getCustomSkyNode());

        setLightEnabled(true);
    }

    /**
     * @return the node for the placement of custom sky.
     */
    @NotNull
    private Node getCustomSkyNode() {
        return customSkyNode;
    }

    /**
     * @return the array of custom skies.
     */
    @NotNull
    private Array<Spatial> getCustomSky() {
        return customSky;
    }

    /**
     * Activate the node with models.
     */
    private void notifyProbeComplete() {

        final Node stateNode = getStateNode();
        stateNode.attachChild(getModelNode());
        stateNode.attachChild(getToolNode());

        final Node customSkyNode = getCustomSkyNode();
        customSkyNode.detachAllChildren();

        final TonegodTranslucentBucketFilter translucentBucketFilter = EDITOR.getTranslucentBucketFilter();
        translucentBucketFilter.refresh();
    }

    /**
     * @param currentFastSky the current fast sky.
     */
    private void setCurrentFastSky(@Nullable final Spatial currentFastSky) {
        this.currentFastSky = currentFastSky;
    }

    /**
     * @return the current fast sky.
     */
    @Nullable
    private Spatial getCurrentFastSky() {
        return currentFastSky;
    }

    /**
     * @return true if the light of the camera is enabled.
     */
    private boolean isLightEnabled() {
        return lightEnabled;
    }

    /**
     * @param lightEnabled the flag of activity light of the camera.
     */
    private void setLightEnabled(final boolean lightEnabled) {
        this.lightEnabled = lightEnabled;
    }

    @Override
    public void initialize(@NotNull final AppStateManager stateManager, @NotNull final Application application) {
        super.initialize(stateManager, application);
        frame = 0;
    }

    @Override
    public void cleanup() {
        super.cleanup();

        final Node stateNode = getStateNode();
        stateNode.detachChild(getModelNode());
        stateNode.detachChild(getToolNode());
    }

    @Override
    public void update(final float tpf) {
        super.update(tpf);

        if (frame == 2) {

            final Node customSkyNode = getCustomSkyNode();

            final Array<Spatial> customSky = getCustomSky();
            customSky.forEach(spatial -> customSkyNode.attachChild(spatial.clone(false)));

            EDITOR.updateProbe(probeHandler);
        }

        frame++;
    }

    @Override
    protected boolean needUpdateCameraLight() {
        return true;
    }

    @Override
    protected boolean needLightForCamera() {
        return true;
    }

    /**
     * Update light.
     *
     * @param enabled the enabled
     */
    public void updateLightEnabled(final boolean enabled) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> updateLightEnabledImpl(enabled));
    }

    /**
     * The process of updating the light.
     */
    private void updateLightEnabledImpl(boolean enabled) {
        if (enabled == isLightEnabled()) return;

        final DirectionalLight light = getLightForCamera();
        final Node stateNode = getStateNode();

        if (enabled) {
            stateNode.addLight(light);
        } else {
            stateNode.removeLight(light);
        }

        setLightEnabled(enabled);
    }

    /**
     * Change the fast sky.
     *
     * @param fastSky the fast sky
     */
    public void changeFastSky(final Spatial fastSky) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> changeFastSkyImpl(fastSky));
    }

    /**
     * The process of changing the fast sky.
     */
    private void changeFastSkyImpl(final Spatial fastSky) {

        final Node stateNode = getStateNode();
        final Spatial currentFastSky = getCurrentFastSky();

        if (currentFastSky != null) {
            stateNode.detachChild(currentFastSky);
        }

        if (fastSky != null) {
            stateNode.attachChild(fastSky);
        }

        stateNode.detachChild(getModelNode());
        stateNode.detachChild(getToolNode());

        setCurrentFastSky(fastSky);

        frame = 0;
    }

    /**
     * Add the custom sky.
     *
     * @param sky the sky
     */
    public void addCustomSky(@NotNull final Spatial sky) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> addCustomSkyImpl(sky));
    }

    /**
     * The process of adding the custom sky.
     */
    private void addCustomSkyImpl(@NotNull final Spatial sky) {
        final Array<Spatial> customSky = getCustomSky();
        customSky.add(sky);
    }

    /**
     * Remove the custom sky.
     *
     * @param sky the sky
     */
    public void removeCustomSky(@NotNull final Spatial sky) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> removeCustomSkyImpl(sky));
    }

    /**
     * The process of removing the custom sky.
     */
    private void removeCustomSkyImpl(@NotNull final Spatial sky) {
        final Array<Spatial> customSky = getCustomSky();
        customSky.slowRemove(sky);
    }

    /**
     * Update the light probe.
     */
    public void updateLightProbe() {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Node stateNode = getStateNode();
            stateNode.detachChild(getModelNode());
            stateNode.detachChild(getToolNode());

            frame = 0;
        });
    }

    @Override
    public String toString() {
        return "ModelEditorAppState{" +
                ", lightEnabled=" + lightEnabled +
                "} " + super.toString();
    }
}
