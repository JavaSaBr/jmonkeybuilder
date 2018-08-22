package com.ss.builder.plugin.api.editor.material;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RendererException;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.ss.builder.EditorThread;
import com.ss.builder.annotation.BackgroundThread;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.jme.editor.part3d.control.impl.CameraEditor3dPartControl;
import com.ss.builder.manager.ExecutorManager;
import com.ss.builder.util.EditorUtils;
import com.ss.builder.plugin.api.editor.part3d.AdvancedPbrWithStudioSky3dEditorPart;
import com.ss.builder.editor.impl.material.MaterialFileEditor;
import com.ss.builder.util.TangentGenerator;
import com.ss.rlib.common.geom.util.AngleUtils;
import com.ss.rlib.common.util.array.Array;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * The implementation the 3D part of the {@link MaterialFileEditor}.
 *
 * @author JavaSaBr
 */
public class BaseMaterialEditor3dPart<T extends BaseMaterialFileEditor> extends
        AdvancedPbrWithStudioSky3dEditorPart<T> {

    private static final Vector3f QUAD_OFFSET =
            new Vector3f(0, -2, 2);

    private static final Vector3f LIGHT_DIRECTION =
            new Vector3f(0.007654993F, 0.39636374F, 0.9180617F).negate();

    private static final float H_ROTATION = AngleUtils.degreeToRadians(75);
    private static final float V_ROTATION = AngleUtils.degreeToRadians(25);

    /**
     * The array of all used geometries.
     */
    @NotNull
    private final Array<Geometry> geometries;

    /**
     * The test box.
     */
    @NotNull
    private final Geometry testBox;

    /**
     * The test sphere.
     */
    @NotNull
    private final Geometry testSphere;

    /**
     * The test quad.
     */
    @NotNull
    private final Geometry testQuad;

    /**
     * The current model mode.
     */
    @NotNull
    private ModelType currentModelType;

    public BaseMaterialEditor3dPart(@NotNull T fileEditor) {
        super(fileEditor);
        this.testBox = new Geometry("Box", new Box(2, 2, 2));
        this.testSphere = new Geometry("Sphere", new Sphere(30, 30, 2));
        this.testQuad = new Geometry("Quad", new Quad(4, 4));
        this.testQuad.setLocalTranslation(QUAD_OFFSET);
        this.geometries = Array.of(testBox, testQuad, testSphere);
        this.currentModelType = ModelType.BOX;

        geometries.forEach(TangentGenerator::useMikktspaceGenerator);

        var cameraControl = requireControl(CameraEditor3dPartControl.class);
        cameraControl.setLightDirection(LIGHT_DIRECTION);
        cameraControl.setDefaultHorizontalRotation(H_ROTATION);
        cameraControl.setDefaultVerticalRotation(V_ROTATION);

        controls.add(new BaseMaterialEditorHotKeys3dPartControl(this));
    }

    @Override
    @BackgroundThread
    protected @NotNull Optional<CameraEditor3dPartControl> createCameraControl() {
        return Optional.of(new CameraEditor3dPartControl(this, false, false, false));
    }

    /**
     * Update the {@link Material}.
     *
     * @param material the material
     */
    @FromAnyThread
    public void updateMaterial(@NotNull Material material) {
        ExecutorManager.getInstance()
                .addJmeTask(() -> updateMaterialInJme(material));
    }

    /**
     * Update the {@link Material} in the jME thread.
     *
     * @param material the new material.
     */
    @JmeThread
    protected void updateMaterialInJme(@NotNull Material material) {

        geometries.forEach(material, Geometry::setMaterial);

        var renderManager = EditorUtils.getRenderManager();
        try {
            renderManager.preloadScene(testBox);
        } catch (RendererException | AssetNotFoundException | UnsupportedOperationException e) {
            handleMaterialException(e);
            geometries.forEach(EditorUtils.getDefaultMaterial(), Geometry::setMaterial);
        }
    }

    /**
     * Handle the material exception.
     *
     * @param exception the exception.
     */
    @JmeThread
    protected void handleMaterialException(@NotNull RuntimeException exception) {
        EditorUtils.handleException(LOGGER, this, exception);
    }

    /**
     * Change the {@link ModelType}.
     *
     * @param modelType the model type
     */
    @FromAnyThread
    public void changeModelType(@NotNull ModelType modelType) {
        ExecutorManager.getInstance()
                .addJmeTask(() -> changeModeInJme(modelType));
    }

    /**
     * Change the {@link ModelType} in the jMe thread.
     *
     * @param modelType the new model type.
     */
    @JmeThread
    protected void changeModeInJme(@NotNull ModelType modelType) {

        modelNode.detachAllChildren();

        switch (modelType) {
            case BOX: {
                modelNode.attachChild(testBox);
                break;
            }
            case QUAD: {
                modelNode.attachChild(testQuad);
                break;
            }
            case SPHERE: {
                modelNode.attachChild(testSphere);
                break;
            }
        }

        this.currentModelType = modelType;
    }

    /**
     * Change the {@link Bucket}.
     *
     * @param bucket the bucket
     */
    @FromAnyThread
    public void changeBucketType(@NotNull Bucket bucket) {
        ExecutorManager.getInstance()
                .addJmeTask(() -> changeBucketTypeInJme(bucket));
    }

    /**
     * Change the {@link Bucket} in the jMe thread.
     *
     * @param bucket the new bucket.
     */
    @JmeThread
    protected void changeBucketTypeInJme(@NotNull Bucket bucket) {
        geometries.forEach(bucket, Geometry::setQueueBucket);
    }

    @Override
    @JmeThread
    public void initialize(@NotNull AppStateManager stateManager, @NotNull Application application) {
        super.initialize(stateManager, application);
        changeModeInJme(currentModelType);
    }

    /**
     * Update the light in the scene.
     *
     * @param enabled the enabled
     */
    @FromAnyThread
    public void updateLightEnabled(boolean enabled) {
        ExecutorManager.getInstance()
                .addJmeTask(() -> updateLightEnabledInJme(enabled));
    }

    /**
     * Update the light in the scene in the {@link EditorThread}.
     *
     * @param enabled true if light should be enabled.
     */
    @JmeThread
    protected void updateLightEnabledInJme(boolean enabled) {

        var cameraControl = requireControl(CameraEditor3dPartControl.class);

        if (enabled) {
            cameraControl.enableLight();
        } else {
            cameraControl.disableLight();
        }
    }

    /**
     * The enum Model type.
     */
    public enum ModelType {
        /**
         * Sphere model type.
         */
        SPHERE,
        /**
         * Box model type.
         */
        BOX,
        /**
         * Quad model type.
         */
        QUAD;

        private static final ModelType[] VALUES = values();

        /**
         * Value of model type.
         *
         * @param index the index
         * @return the model type
         */
        public static ModelType valueOf(int index) {
            return VALUES[index];
        }
    }
}
