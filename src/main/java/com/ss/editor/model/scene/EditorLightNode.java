package com.ss.editor.model.scene;

import static com.ss.editor.util.GeomUtils.getDirection;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.util.LocalObjects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The node to present and edit lights.
 *
 * @author JavaSaBr
 */
public class EditorLightNode extends Node implements NoSelection, WrapperNode {

    /**
     * The camera.
     */
    @NotNull
    private final Camera camera;

    /**
     * The last light position.
     */
    @NotNull
    private final Vector3f lastLightPosition;

    /**
     * The last light rotation.
     */
    @NotNull
    private final Quaternion lastLightRotation;

    /**
     * The light.
     */
    @Nullable
    private Light light;

    /**
     * The model.
     */
    @Nullable
    private Node model;

    public EditorLightNode(@NotNull final Camera camera) {
        this.camera = camera;
        this.lastLightPosition = new Vector3f();
        this.lastLightRotation = new Quaternion();
    }

    /**
     * Set the light.
     *
     * @param light the light.
     */
    @JmeThread
    public void setLight(@Nullable final Light light) {
        this.light = light;
        this.lastLightRotation.set(Quaternion.IDENTITY);
        this.lastLightPosition.set(Vector3f.ZERO);
    }

    /**
     * Get the light.
     *
     * @return the light.
     */
    @JmeThread
    public @Nullable Light getLight() {
        return light;
    }

    /**
     * Get the model.
     *
     * @return the model.
     */
    @JmeThread
    public @Nullable Node getModel() {
        return model;
    }

    /**
     * Set the model.
     *
     * @param model the model.
     */
    @JmeThread
    public void setModel(@Nullable final Node model) {
        this.model = model;
    }

    @Override
    @FromAnyThread
    public @NotNull Object getWrappedObject() {
        return notNull(light);
    }

    /**
     * Get the last light rotation.
     *
     * @return the last light rotation.
     */
    @FromAnyThread
    private @NotNull Quaternion getLastLightRotation() {
        return lastLightRotation;
    }

    /**
     * Get the last light position.
     *
     * @return the last light position.
     */
    @FromAnyThread
    private @NotNull Vector3f getLastLightPosition() {
        return lastLightPosition;
    }

    @Override
    @JmeThread
    public void updateGeometricState() {

        final Light light = getLight();
        final Vector3f lastLightPosition = getLastLightPosition();
        final Quaternion lastLightRotation = getLastLightRotation();

        if (light instanceof PointLight) {

            final PointLight pointLight = (PointLight) light;

            if (lastLightPosition.equals(pointLight.getPosition())) {
                pointLight.setPosition(getLocalTranslation());
                lastLightPosition.set(pointLight.getPosition());
            } else {
                sync();
            }

        } else if (light instanceof DirectionalLight) {

            final DirectionalLight directionalLight = (DirectionalLight) light;
            final LocalObjects local = LocalObjects.get();
            final Quaternion lightRotation = local.nextRotation();
            lightRotation.lookAt(directionalLight.getDirection(), Vector3f.UNIT_Y);

            final Quaternion rotation = getLocalRotation();

            if (lastLightRotation.equals(lightRotation)) {
                directionalLight.setDirection(getDirection(rotation, local.nextVector()));
                lastLightRotation.set(rotation);
            } else {
                sync();
            }

        } else if (light instanceof SpotLight) {

            final SpotLight spotLight = (SpotLight) light;
            final LocalObjects local = LocalObjects.get();
            final Quaternion lightRotation = local.nextRotation();
            lightRotation.lookAt(spotLight.getDirection(), Vector3f.UNIT_Y);

            final Quaternion rotation = getLocalRotation();

            if (lastLightPosition.equals(spotLight.getPosition()) && lastLightRotation.equals(lightRotation)) {
                final Vector3f direction = getDirection(rotation, local.nextVector());
                spotLight.setDirection(direction);
                spotLight.setPosition(getLocalTranslation());
            } else {
                sync();
            }
        }

        super.updateGeometricState();
    }

    /**
     * Synchronize this node with presented object.
     */
    @JmeThread
    public void sync() {

        final Light light = getLight();
        final LocalObjects local = LocalObjects.get();
        final Vector3f lastLightPosition = getLastLightPosition();
        final Quaternion lastLightRotation = getLastLightRotation();

        if (light instanceof SpotLight) {

            final SpotLight spotLight = (SpotLight) light;

            final Quaternion rotation = local.nextRotation();
            rotation.lookAt(spotLight.getDirection(), Vector3f.UNIT_Y);

            setLocalTranslation(spotLight.getPosition());
            setLocalRotation(rotation);

            lastLightPosition.set(getLocalTranslation());
            lastLightRotation.set(getLocalRotation());

        } else if (light instanceof PointLight) {
            setLocalTranslation(((PointLight) light).getPosition());
            lastLightPosition.set(getLocalTranslation());
        } else if (light instanceof DirectionalLight) {

            final DirectionalLight directionalLight = (DirectionalLight) light;
            final Quaternion rotation = local.nextRotation();
            rotation.lookAt(directionalLight.getDirection(), Vector3f.UNIT_Y);

            setLocalRotation(rotation);

            lastLightRotation.set(getLocalRotation());
        }
    }

    /**
     * Update position and rotation of a model.
     */
    @JmeThread
    public void updateModel() {

        final Node model = getModel();
        if (model == null) return;

        final LocalObjects local = LocalObjects.get();
        final Vector3f positionOnCamera = local.nextVector();
        positionOnCamera.set(getLocalTranslation())
                .subtractLocal(camera.getLocation())
                .normalizeLocal()
                .multLocal(camera.getFrustumNear() + 0.4f)
                .addLocal(camera.getLocation());

        model.setLocalTranslation(positionOnCamera);
        model.setLocalRotation(getLocalRotation());
    }
}
