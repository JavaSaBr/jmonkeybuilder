package com.ss.editor.state.editor.impl.model;

import static com.ss.editor.util.NodeUtils.findParent;

import com.jme3.scene.Spatial;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.geometry.ParticleGeometry;

/**
 * The class with utilities methods for {@link ModelEditorAppState}.
 *
 * @author JavaSaBr.
 */
public class ModelEditorUtils {

    @NotNull
    public static Object findToSelect(@NotNull final Object object) {

        if (object instanceof ParticleGeometry) {
            final Spatial parent = findParent((Spatial) object, spatial -> spatial instanceof ParticleEmitterNode);
            if (parent != null) return parent;
        }

        return object;
    }
}
