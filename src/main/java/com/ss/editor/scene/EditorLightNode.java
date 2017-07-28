package com.ss.editor.scene;

import static com.ss.editor.util.GeomUtils.getDirection;

import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
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
    }

    /**
     * Set a light.
     *
     * @param light the light.
     */
    public void setLight(@Nullable final Light light) {
        this.light = light;
    }

    /**
     * Get a light.
     *
     * @return the light.
     */
    @Nullable
    public Light getLight() {
        return light;
    }

    /**
     * Get a model.
     *
     * @return the model.
     */
    @Nullable
    public Node getModel() {
        return model;
    }

    /**
     * Set a model.
     *
     * @param model the model.
     */
    public void setModel(@Nullable final Node model) {
        this.model = model;
    }

    @NotNull
    @Override
    public Object getWrappedObject() {
        return light;
    }

    @Override
    public void updateGeometricState() {

        final Light light = getLight();

        if (light instanceof PointLight) {
            final PointLight pointLight = (PointLight) light;
            pointLight.setPosition(getLocalTranslation());
        } else if (light instanceof DirectionalLight) {
            final DirectionalLight directionalLight = (DirectionalLight) light;
            final Quaternion rotation = getLocalRotation();
            directionalLight.setDirection(getDirection(rotation, directionalLight.getDirection()));
        } else if (light instanceof SpotLight) {
            final SpotLight spotLight = (SpotLight) light;
            final Quaternion rotation = getLocalRotation();
            final Vector3f direction = getDirection(rotation, spotLight.getDirection());
            spotLight.setDirection(direction);
            spotLight.setPosition(getLocalTranslation());
        }

        super.updateGeometricState();
    }

    /**
     * Synchronize this node with presented object.
     */
    public void sync() {

        final Light light = getLight();
        final LocalObjects local = LocalObjects.get();

        if (light instanceof SpotLight) {

            final SpotLight spotLight = (SpotLight) light;

            final Quaternion rotation = local.nextRotation();
            rotation.lookAt(spotLight.getDirection(), camera.getUp(local.nextVector()));

            setLocalTranslation(spotLight.getPosition());
            setLocalRotation(rotation);

        } else if (light instanceof PointLight) {
            setLocalTranslation(((PointLight) light).getPosition());
        } else if (light instanceof DirectionalLight) {

            final DirectionalLight directionalLight = (DirectionalLight) light;
            final Quaternion rotation = local.nextRotation();
            rotation.lookAt(directionalLight.getDirection(), camera.getUp(local.nextVector()));

            setLocalRotation(rotation);
        }
    }

    /**
     * Update position and rotation of a model.
     */
    public void updateModel() {

        final Node model = getModel();
        if (model == null) return;

        final LocalObjects local = LocalObjects.get();
        final Vector3f positionOnCamera = local.nextVector();
        positionOnCamera.set(getLocalTranslation()).subtractLocal(camera.getLocation());
        positionOnCamera.normalizeLocal();
        positionOnCamera.multLocal(camera.getFrustumNear() + 0.4f);
        positionOnCamera.addLocal(camera.getLocation());

        model.setLocalTranslation(positionOnCamera);
        model.setLocalRotation(getLocalRotation());
    }
}
