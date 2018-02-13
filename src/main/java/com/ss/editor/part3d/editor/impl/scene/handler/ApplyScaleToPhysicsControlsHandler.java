package com.ss.editor.part3d.editor.impl.scene.handler;

import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.PhysicsControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.util.ControlUtils;
import com.ss.editor.util.NodeUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.dictionary.DictionaryFactory;
import com.ss.rlib.util.dictionary.ObjectDictionary;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.jme3.bullet.util.CollisionShapeFactory.createDynamicMeshShape;
import static com.jme3.bullet.util.CollisionShapeFactory.createMeshShape;
import static com.ss.rlib.util.array.ArrayCollectors.toArray;

/**
 * The handler to disable all controls during transforming spatial.
 *
 * @author JavaSaBr
 */
public class ApplyScaleToPhysicsControlsHandler {

    /**
     * The saved previous scales.
     */
    @NotNull
    private final ObjectDictionary<Spatial, Vector3f> enabledControls;

    public ApplyScaleToPhysicsControlsHandler() {
        this.enabledControls = DictionaryFactory.newObjectDictionary();
    }

    /**
     * Disable all controls before transform.
     *
     * @param spatial the spatial.
     */
    @JmeThread
    public void onPreTransform(@NotNull final Spatial spatial) {
        NodeUtils.children(spatial).forEach(sp -> {
            if (ControlUtils.controls(sp).anyMatch(PhysicsCollisionObject.class::isInstance)) {
                enabledControls.put(sp, sp.getWorldScale().clone());
            }
        });
    }

    /**
     * Enable disabled controls before transform.
     *
     * @param spatial the spatial.
     */
    @JmeThread
    public void onPostTransform(@NotNull final Spatial spatial) {
        NodeUtils.children(spatial).forEach(sp -> {

            final Vector3f prevScale = enabledControls.remove(sp);
            if (prevScale == null) {
                return;
            }

            final Vector3f currentScale = sp.getWorldScale();
            if (prevScale.equals(currentScale)) {
                return;
            }

            ControlUtils.controls(sp)
                .filter(PhysicsCollisionObject.class::isInstance)
                .map(PhysicsCollisionObject.class::cast)
                .forEach(object -> ControlUtils.applyScale(sp, currentScale, object));
        });
    }

}
