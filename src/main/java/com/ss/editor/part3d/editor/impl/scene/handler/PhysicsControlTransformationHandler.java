package com.ss.editor.part3d.editor.impl.scene.handler;

import com.jme3.scene.Spatial;
import com.ss.editor.extension.util.JmbExtUtils;
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
        JmbExtUtils.resetPhysicsControlPositions(spatial);
    }
}
