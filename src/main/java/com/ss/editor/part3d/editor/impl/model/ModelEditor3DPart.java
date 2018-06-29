package com.ss.editor.part3d.editor.impl.model;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.environment.generation.JobProgressAdapter;
import com.jme3.light.DirectionalLight;
import com.jme3.light.LightProbe;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.part3d.editor.impl.scene.AbstractSceneEditor3DPart;
import com.ss.editor.plugin.api.RenderFilterRegistry;
import com.ss.editor.ui.component.editor.impl.model.ModelFileEditor;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link AbstractSceneEditor3DPart} for the {@link ModelFileEditor}.
 *
 * @author JavaSaBr
 */
public class ModelEditor3DPart extends AbstractSceneEditor3DPart<ModelFileEditor, Spatial> {

    @NotNull
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

    public ModelEditor3DPart(@NotNull final ModelFileEditor fileEditor) {
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
    @JmeThread
    private @NotNull Node getCustomSkyNode() {
        return customSkyNode;
    }

    /**
     * @return the array of custom skies.
     */
    @JmeThread
    private @NotNull Array<Spatial> getCustomSky() {
        return customSky;
    }

    /**
     * Activate the node with models.
     */
    @JmeThread
    private void notifyProbeComplete() {

        final Node stateNode = getStateNode();
        stateNode.attachChild(getModelNode());
        stateNode.attachChild(getToolNode());

        final Node customSkyNode = getCustomSkyNode();
        customSkyNode.detachAllChildren();

        final RenderFilterRegistry filterExtension = RenderFilterRegistry.getInstance();
        filterExtension.refreshFilters();
    }

    /**
     * @param currentFastSky the current fast sky.
     */
    @JmeThread
    private void setCurrentFastSky(@Nullable final Spatial currentFastSky) {
        this.currentFastSky = currentFastSky;
    }

    /**
     * @return the current fast sky.
     */
    @JmeThread
    private @Nullable Spatial getCurrentFastSky() {
        return currentFastSky;
    }

    /**
     * @return true if the light of the camera is enabled.
     */
    @JmeThread
    private boolean isLightEnabled() {
        return lightEnabled;
    }

    /**
     * @param lightEnabled the flag of activity light of the camera.
     */
    @JmeThread
    private void setLightEnabled(final boolean lightEnabled) {
        this.lightEnabled = lightEnabled;
    }

    @Override
    @JmeThread
    public void initialize(@NotNull final AppStateManager stateManager, @NotNull final Application application) {
        super.initialize(stateManager, application);
        frame = 0;
    }

    @Override
    @JmeThread
    public void cleanup() {
        super.cleanup();

        final Node stateNode = getStateNode();
        stateNode.detachChild(getModelNode());
        stateNode.detachChild(getToolNode());
    }

    @Override
    @JmeThread
    public void update(final float tpf) {
        super.update(tpf);

        if (frame == 2) {
            final Node customSkyNode = getCustomSkyNode();
            final Array<Spatial> customSky = getCustomSky();
            customSky.forEach(spatial -> customSkyNode.attachChild(spatial.clone(false)));
            EditorUtil.updateGlobalLightProbe(probeHandler);
        }

        frame++;
    }

    @Override
    @JmeThread
    protected boolean needUpdateCameraLight() {
        return true;
    }

    @Override
    @JmeThread
    protected boolean needLightForCamera() {
        return true;
    }

    /**
     * Update light.
     *
     * @param enabled the enabled
     */
    @FromAnyThread
    public void updateLightEnabled(final boolean enabled) {
        EXECUTOR_MANAGER.addJmeTask(() -> updateLightEnabledImpl(enabled));
    }

    /**
     * The process of updating the light.
     */
    @JmeThread
    private void updateLightEnabledImpl(boolean enabled) {

        if (enabled == isLightEnabled()) {
            return;
        }

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
    @FromAnyThread
    public void changeFastSky(@Nullable final Spatial fastSky) {
        EXECUTOR_MANAGER.addJmeTask(() -> changeFastSkyImpl(fastSky));
    }

    /**
     * The process of changing the fast sky.
     */
    @JmeThread
    private void changeFastSkyImpl(@Nullable final Spatial fastSky) {

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
    @FromAnyThread
    public void addCustomSky(@NotNull final Spatial sky) {
        EXECUTOR_MANAGER.addJmeTask(() -> addCustomSkyImpl(sky));
    }

    /**
     * The process of adding the custom sky.
     */
    @JmeThread
    private void addCustomSkyImpl(@NotNull final Spatial sky) {
        final Array<Spatial> customSky = getCustomSky();
        customSky.add(sky);
    }

    /**
     * Remove the custom sky.
     *
     * @param sky the sky
     */
    @FromAnyThread
    public void removeCustomSky(@NotNull final Spatial sky) {
        EXECUTOR_MANAGER.addJmeTask(() -> removeCustomSkyImpl(sky));
    }

    /**
     * The process of removing the custom sky.
     */
    @JmeThread
    private void removeCustomSkyImpl(@NotNull final Spatial sky) {
        final Array<Spatial> customSky = getCustomSky();
        customSky.slowRemove(sky);
    }

    /**
     * Update the light probe.
     */
    @FromAnyThread
    public void updateLightProbe() {
        EXECUTOR_MANAGER.addJmeTask(() -> {

            final Node stateNode = getStateNode();
            stateNode.detachChild(getModelNode());
            stateNode.detachChild(getToolNode());

            frame = 0;
        });
    }

    @Override
    public String toString() {
        return "ModelEditor3DState{" +
                ", lightEnabled=" + lightEnabled +
                "} " + super.toString();
    }
}
