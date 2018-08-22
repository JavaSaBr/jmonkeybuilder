package com.ss.builder.jme.editor.part3d.impl.model;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.environment.generation.JobProgressAdapter;
import com.jme3.light.LightProbe;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.jme.editor.part3d.control.impl.CameraEditor3dPartControl;
import com.ss.builder.jme.editor.part3d.impl.scene.AbstractSceneEditor3dPart;
import com.ss.builder.manager.ExecutorManager;
import com.ss.builder.plugin.api.RenderFilterRegistry;
import com.ss.builder.util.EditorUtils;
import com.ss.builder.editor.impl.model.ModelFileEditor;
import com.ss.rlib.common.util.array.Array;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link AbstractSceneEditor3dPart} for the {@link ModelFileEditor}.
 *
 * @author JavaSaBr
 */
public class ModelEditor3dPart extends AbstractSceneEditor3dPart<ModelFileEditor, Spatial> {

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
     * The frame rate.
     */
    private int frame;

    public ModelEditor3dPart(@NotNull ModelFileEditor fileEditor) {
        super(fileEditor);
        this.customSkyNode = new Node("Custom Sky");
        this.customSky = Array.ofType(Spatial.class);
        stateNode.attachChild(customSkyNode);
    }


    /**
     * Activate the node with models.
     */
    @JmeThread
    private void notifyProbeComplete() {

        stateNode.attachChild(modelNode);
        stateNode.attachChild(toolNode);

        customSkyNode.detachAllChildren();

        RenderFilterRegistry.getInstance()
                .refreshFilters();
    }

    @Override
    @JmeThread
    public void initialize(@NotNull AppStateManager stateManager, @NotNull Application application) {
        super.initialize(stateManager, application);
        frame = 0;
    }

    @Override
    @JmeThread
    public void cleanup() {
        super.cleanup();
        stateNode.detachChild(modelNode);
        stateNode.detachChild(toolNode);
    }

    @Override
    @JmeThread
    public void update(float tpf) {
        super.update(tpf);

        if (frame == 2) {
            customSky.forEach(spatial -> customSkyNode.attachChild(spatial.clone(false)));
            EditorUtils.updateGlobalLightProbe(probeHandler);
        }

        frame++;
    }

    /**
     * Update light.
     *
     * @param enabled the enabled.
     */
    @FromAnyThread
    public void updateLightEnabled(boolean enabled) {
        ExecutorManager.getInstance()
                .addJmeTask(() -> updateLightEnabledInJme(enabled));
    }

    /**
     * The process of updating the light.
     */
    @JmeThread
    private void updateLightEnabledInJme(boolean enabled) {

        var cameraControl = requireControl(CameraEditor3dPartControl.class);

        if (enabled) {
            cameraControl.enableLight();
        } else {
            cameraControl.disableLight();
        }
    }

    /**
     * Change the fast sky.
     *
     * @param fastSky the fast sky
     */
    @FromAnyThread
    public void changeFastSky(@Nullable Spatial fastSky) {
        ExecutorManager.getInstance()
                .addJmeTask(() -> changeFastSkyInJme(fastSky));
    }

    /**
     * The process of changing the fast sky.
     */
    @JmeThread
    private void changeFastSkyInJme(@Nullable Spatial fastSky) {

        if (currentFastSky != null) {
            stateNode.detachChild(currentFastSky);
        }

        if (fastSky != null) {
            stateNode.attachChild(fastSky);
        }

        stateNode.detachChild(modelNode);
        stateNode.detachChild(toolNode);

        currentFastSky = fastSky;

        frame = 0;
    }

    /**
     * Add the custom sky.
     *
     * @param sky the sky.
     */
    @FromAnyThread
    public void addCustomSky(@NotNull Spatial sky) {
        ExecutorManager.getInstance()
                .addJmeTask(() -> addCustomSkyInJme(sky));
    }

    /**
     * The process of adding the custom sky.
     */
    @JmeThread
    private void addCustomSkyInJme(@NotNull Spatial sky) {
        customSky.add(sky);
    }

    /**
     * Remove the custom sky.
     *
     * @param sky the sky
     */
    @FromAnyThread
    public void removeCustomSky(@NotNull Spatial sky) {
        ExecutorManager.getInstance()
                .addJmeTask(() -> removeCustomSkyInJme(sky));
    }

    /**
     * The process of removing the custom sky.
     */
    @JmeThread
    private void removeCustomSkyInJme(@NotNull Spatial sky) {
        customSky.slowRemove(sky);
    }

    /**
     * Update the light probe.
     */
    @FromAnyThread
    public void updateLightProbe() {
        ExecutorManager.getInstance()
                .addJmeTask(this::updateLightProbeInJme);
    }

    @JmeThread
    private void updateLightProbeInJme() {
        stateNode.detachChild(modelNode);
        stateNode.detachChild(toolNode);
        frame = 0;
    }
}
