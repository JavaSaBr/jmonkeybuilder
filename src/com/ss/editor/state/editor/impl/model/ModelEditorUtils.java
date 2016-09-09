package com.ss.editor.state.editor.impl.model;

import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.Emitter;
import tonegod.emitter.node.EmitterNode;

/**
 * The class with utilities methods for {@link ModelEditorState}.
 *
 * @author JavaSaBr.
 */
public class ModelEditorUtils {

    @NotNull
    public static Object findToSelect(@NotNull final Object object) {

        if (object instanceof Geometry) {
            final Node parent = ((Geometry) object).getParent();
            if (parent instanceof EmitterNode) {
                final Emitter emitter = ((EmitterNode) parent).getEmitter();
                return emitter.getSpatial();
            }
        }

        return object;
    }
}
