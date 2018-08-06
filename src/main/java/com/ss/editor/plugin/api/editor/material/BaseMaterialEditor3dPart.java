package com.ss.editor.plugin.api.editor.material;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RendererException;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.ss.editor.EditorThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.plugin.api.editor.part3d.AdvancedPbrWithStudioSky3dEditorPart;
import com.ss.editor.ui.component.editor.impl.material.MaterialFileEditor;
import com.ss.editor.util.EditorUtils;
import com.ss.editor.util.TangentGenerator;
import com.ss.rlib.common.function.BooleanFloatConsumer;
import com.ss.rlib.common.geom.util.AngleUtils;
import com.ss.rlib.common.util.dictionary.ObjectDictionary;
import javafx.scene.input.KeyCode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    @Nullable
    private ModelType currentModelType;

    /**
     * The flag of enabling light.
     */
    private boolean lightEnabled;

    public BaseMaterialEditor3dPart(@NotNull T fileEditor) {
        super(fileEditor);
        this.testBox = new Geometry("Box", new Box(2, 2, 2));
        this.testSphere = new Geometry("Sphere", new Sphere(30, 30, 2));
        this.testQuad = new Geometry("Quad", new Quad(4, 4));
        this.testQuad.setLocalTranslation(QUAD_OFFSET);
        this.lightEnabled = MaterialFileEditor.DEFAULT_LIGHT_ENABLED;

        TangentGenerator.useMikktspaceGenerator(testBox);
        TangentGenerator.useMikktspaceGenerator(testSphere);
        TangentGenerator.useMikktspaceGenerator(testQuad);

        var light = notNull(getLightForCamera());
        light.setDirection(LIGHT_DIRECTION);

        var editorCamera = notNull(getEditorCamera());
        editorCamera.setDefaultHorizontalRotation(H_ROTATION);
        editorCamera.setDefaultVerticalRotation(V_ROTATION);

        modelNode.attachChild(getNodeForCamera());

        controls.add(new BaseMaterialEditor3dPartControl(this));
    }

    @Override
    @JmeThread
    protected void registerActionHandlers(@NotNull ObjectDictionary<String, BooleanFloatConsumer> actionHandlers) {
        super.registerActionHandlers(actionHandlers);

    }

    @Override
    @JmeThread
    protected void registerActionListener(@NotNull InputManager inputManager) {
        super.registerActionListener(inputManager);
        inputManager.addListener(actionListener, KEY_S, KEY_C, KEY_P, KEY_L);
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

        testBox.setMaterial(material);
        testQuad.setMaterial(material);
        testSphere.setMaterial(material);

        var renderManager = EditorUtils.getRenderManager();
        try {
            renderManager.preloadScene(testBox);
        } catch (RendererException | AssetNotFoundException | UnsupportedOperationException e) {
            handleMaterialException(e);
            testBox.setMaterial(EditorUtils.getDefaultMaterial());
            testQuad.setMaterial(EditorUtils.getDefaultMaterial());
            testSphere.setMaterial(EditorUtils.getDefaultMaterial());
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
    public void changeMode(@NotNull ModelType modelType) {
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

        setCurrentModelType(modelType);
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
        testQuad.setQueueBucket(bucket);
        testSphere.setQueueBucket(bucket);
        testBox.setQueueBucket(bucket);
    }

    @Override
    @JmeThread
    public void initialize(@NotNull AppStateManager stateManager, @NotNull Application application) {
        super.initialize(stateManager, application);
        changeModeInJme(getCurrentModelType());
    }

    @Override
    @JmeThread
    protected boolean needMovableCamera() {
        return false;
    }

    @Override
    @JmeThread
    protected boolean needEditorCamera() {
        return true;
    }

    @Override
    @JmeThread
    protected boolean needLightForCamera() {
        return true;
    }

    /**
     * Get the current model mode.
     *
     * @return the current model mode.
     */
    @JmeThread
    protected @NotNull ModelType getCurrentModelType() {
        return notNull(currentModelType);
    }

    /**
     * Set the current model mode.
     *
     * @param currentModelType the current model mode.
     */
    @JmeThread
    protected void setCurrentModelType(@NotNull ModelType currentModelType) {
        this.currentModelType = currentModelType;
    }

    /**
     * Return true if the light is enabled.
     *
     * @return true if the light is enabled.
     */
    @JmeThread
    protected boolean isLightEnabled() {
        return lightEnabled;
    }

    /**
     * Set true if the light is enabled.
     *
     * @param lightEnabled true if the light is enabled.
     */
    @JmeThread
    protected void setLightEnabled(boolean lightEnabled) {
        this.lightEnabled = lightEnabled;
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

        if (enabled == isLightEnabled()) {
            return;
        }

        var light = getLightForCamera();

        if (enabled) {
            stateNode.addLight(light);
        } else {
            stateNode.removeLight(light);
        }

        setLightEnabled(enabled);
    }

    @Override
    @JmeThread
    protected boolean needUpdateCameraLight() {
        return false;
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
        public static ModelType valueOf(final int index) {
            return VALUES[index];
        }
    }
}
