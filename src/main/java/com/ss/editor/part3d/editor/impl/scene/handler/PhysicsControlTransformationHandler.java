package com.ss.editor.part3d.editor.impl.scene.handler;

import com.jme3.bullet.control.PhysicsControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Spatial;
import com.ss.editor.util.ControlUtils;
import com.ss.editor.util.NodeUtils;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * The handler to updated positions for physics controls on spatial transformations.
 *
 * @author JavaSaBr
 */
public class PhysicsControlTransformationHandler implements Consumer<Spatial> {

    @Override
    public void accept(@NotNull final Spatial spatial) {
        NodeUtils.children(spatial)
            .flatMap(ControlUtils::controls)
            .filter(PhysicsControl.class::isInstance)
            .filter(ControlUtils::isEnabled)
            .forEach(control -> {
                if (control instanceof RigidBodyControl) {
                    final RigidBodyControl bodyControl = (RigidBodyControl) control;
                    final boolean kinematic = bodyControl.isKinematic();
                    final boolean kinematicSpatial = bodyControl.isKinematicSpatial();
                    bodyControl.setKinematic(true);
                    bodyControl.setKinematicSpatial(true);
                    bodyControl.clearForces();
                    bodyControl.update(0);
                    bodyControl.setKinematic(kinematic);
                    bodyControl.setKinematicSpatial(kinematicSpatial);
                }
            });
    }
}
