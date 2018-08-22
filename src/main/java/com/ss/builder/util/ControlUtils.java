package com.ss.editor.util;

import static com.jme3.bullet.util.CollisionShapeFactory.createDynamicMeshShape;
import static com.jme3.bullet.util.CollisionShapeFactory.createMeshShape;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.PhysicsControl;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.JmeThread;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * The utility class to work with controls.
 *
 * @author JavaSaBr
 */
public class ControlUtils {

    /**
     * Create control's stream by the spatial.
     *
     * @param spatial the spatial.
     * @return the control's stream.
     */
    @FromAnyThread
    public static boolean has(@NotNull Spatial spatial, @NotNull Class<?> type) {

        var numControls = spatial.getNumControls();
        if (numControls < 1) {
            return false;
        }

        for (var i = 0; i < numControls; i++) {
            if (type.isInstance(spatial.getControl(i))) {
                return true;
            }
        }

        return false;
    }

    /**
     * Create control's stream by the spatial.
     *
     * @param spatial the spatial.
     * @return the control's stream.
     */
    @FromAnyThread
    public static @NotNull Stream<Control> controls(@NotNull Spatial spatial) {

        var numControls = spatial.getNumControls();
        if (numControls < 1) {
            return Stream.empty();
        }

        var controls = new Control[numControls];
        for (var i = 0; i < numControls; i++) {
            controls[i] = spatial.getControl(i);
        }

        return Arrays.stream(controls);
    }

    /**
     * Check enabled status of the control.
     *
     * @param control the control.
     * @return true if this control is enabled.
     */
    @FromAnyThread
    public static boolean isEnabled(@NotNull Control control) {
        if (control instanceof AbstractControl) {
            return ((AbstractControl) control).isEnabled();
        } else if (control instanceof PhysicsControl) {
            return ((PhysicsControl) control).isEnabled();
        } else {
            return true;
        }
    }

    /**
     * Check enabled status of the control.
     *
     * @param control the control.
     * @return true if this control isn't enabled.
     */
    @FromAnyThread
    public static boolean isNotEnabled(@NotNull Control control) {
        return !isEnabled(control);
    }

    /**
     * Change the enabled status of the control.
     *
     * @param control the control.
     * @param enabled true if the control should be enabled.
     */
    @FromAnyThread
    public static void setEnabled(@NotNull Control control, boolean enabled) {
        if (control instanceof AbstractControl) {
            ((AbstractControl) control).setEnabled(enabled);
        } else if (control instanceof PhysicsControl) {
            ((PhysicsControl) control).setEnabled(enabled);
        }
    }

    /**
     * Apply the new scale.
     *
     * @param spatial      the spatial.
     * @param currentScale the current scale.
     * @param object       the collision object.
     */
    @JmeThread
    public static void applyScale(
            @NotNull Spatial spatial,
            @NotNull Vector3f currentScale,
            @NotNull PhysicsCollisionObject object
    ) {

        var mass = object instanceof PhysicsRigidBody ? ((PhysicsRigidBody) object).getMass() : 1F;

        CollisionShape shape = null;

        if (spatial instanceof Geometry) {

            var geom = (Geometry) spatial;
            var mesh = geom.getMesh();

            if (mesh instanceof Sphere) {

                var x = currentScale.getX();
                if (Float.compare(x, currentScale.getY()) == 0 && Float.compare(x, currentScale.getZ()) == 0) {
                    shape = new SphereCollisionShape(((Sphere) mesh).getRadius() * x);
                }

            } else if (mesh instanceof Box) {

                var box = (Box) mesh;
                var halfExtents = new Vector3f(box.getXExtent(), box.getYExtent(), box.getZExtent());
                halfExtents.multLocal(currentScale);

                shape = new BoxCollisionShape(halfExtents);
            }
        }

        if (shape == null) {
            if (mass > 0) {
                shape = createDynamicMeshShape(spatial);
            } else {
                shape = createMeshShape(spatial);
            }
        }

        object.setCollisionShape(shape);
    }
}
