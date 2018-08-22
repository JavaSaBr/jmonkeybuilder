package com.ss.builder.jme.editor.part3d.impl.scene.handler;

import com.jme3.scene.Spatial;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.editor.extension.util.JmbExtUtils;
import com.ss.builder.jme.editor.part3d.impl.scene.AbstractSceneEditor3dPart;
import org.jetbrains.annotations.NotNull;

/**
 * The handler to updated positions for physics controls on spatial transformations.
 *
 * @author JavaSaBr
 */
public class PhysicsControlTransformationHandler implements AbstractSceneEditor3dPart.TransformationHandler {

    @Override
    @JmeThread
    public void handle(@NotNull Spatial object) {
        JmbExtUtils.resetPhysicsControlPositions(object);
    }
}
