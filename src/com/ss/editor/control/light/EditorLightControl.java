package com.ss.editor.control.light;

import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.LightProbe;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.ss.editor.Editor;
import com.ss.editor.util.LocalObjects;

/**
 * Реализация контролера источника света для редактора.
 *
 * @author Ronn
 */
public class EditorLightControl extends AbstractControl {

    private static final Editor EDITOR = Editor.getInstance();

    private static final int DIRECTION_LIGHT_POSITION = 20;

    /**
     * Редактируемый источник света.
     */
    private final Light light;

    public EditorLightControl(final Light light) {
        this.light = light;
    }

    /**
     * @return редактируемый источник света.
     */
    public Light getLight() {
        return light;
    }

    @Override
    protected void controlUpdate(final float tpf) {

        final Spatial spatial = getSpatial();
        final Light light = getLight();

        final LocalObjects local = LocalObjects.get();
        final Vector3f position = local.getNextVector();
        position.set(Vector3f.ZERO);

        if (light instanceof PointLight) {
            position.addLocal(((PointLight) light).getPosition());
        } else if (light instanceof SpotLight) {

            final SpotLight spotLight = (SpotLight) light;
            final Vector3f direction = local.getNextVector();
            direction.set(spotLight.getDirection());
            direction.negateLocal();

            final Quaternion lightRotation = local.getNextRotation();
            lightRotation.lookAt(direction, Vector3f.UNIT_Z);

            spatial.setLocalRotation(lightRotation);
            position.addLocal(spotLight.getPosition());

        } else if (light instanceof LightProbe) {
            position.addLocal(((LightProbe) light).getPosition());
        } else if (light instanceof DirectionalLight) {

            final DirectionalLight directionLight = (DirectionalLight) light;
            final Vector3f lightPosition = local.getNextVector();
            final Vector3f direction = local.getNextVector();
            direction.set(directionLight.getDirection());
            direction.negateLocal();

            final Quaternion lightRotation = local.getNextRotation();
            lightRotation.lookAt(direction, Vector3f.UNIT_Z);
            lightRotation.getRotationColumn(2, lightPosition);

            lightPosition.multLocal(DIRECTION_LIGHT_POSITION);

            spatial.setLocalRotation(lightRotation);
            position.addLocal(lightPosition);
        }

        spatial.setLocalTranslation(getPositionOnCamera(position));
    }

    private Vector3f getPositionOnCamera(final Vector3f location) {

        final Camera camera = EDITOR.getCamera();
        final LocalObjects local = LocalObjects.get();
        final Vector3f positionOnCamera = local.getNextVector();
        positionOnCamera.set(location).subtractLocal(camera.getLocation());
        positionOnCamera.normalizeLocal();
        positionOnCamera.multLocal(camera.getFrustumNear() + 0.4f);
        positionOnCamera.addLocal(camera.getLocation());

        return positionOnCamera;
    }

    @Override
    protected void controlRender(final RenderManager rm, final ViewPort vp) {
    }
}
