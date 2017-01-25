package com.ss.editor.state.editor.impl.model;

import static com.ss.editor.util.NodeUtils.findParent;

import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.scene.EditorAudioNode;
import com.ss.editor.scene.EditorLightNode;
import com.ss.editor.state.editor.impl.scene.AbstractSceneEditorAppState;
import com.ss.editor.ui.component.editor.FileEditor;
import com.ss.editor.util.NodeUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.geometry.ParticleGeometry;

/**
 * The class with utilities methods for {@link ModelEditorAppState}.
 *
 * @author JavaSaBr.
 */
public class ModelEditorUtils {

    @Nullable
    public static <T extends FileEditor & ModelChangeConsumer, M extends Spatial> Object findToSelect(
            @NotNull final AbstractSceneEditorAppState<T, M> state, @NotNull final Object object) {

        if (object instanceof ParticleGeometry) {
            final Spatial parent = findParent((Spatial) object, spatial -> spatial instanceof ParticleEmitterNode);
            if (parent != null && parent.isVisible()) return parent;
        }

        if (object instanceof Geometry) {
            final Spatial parent = NodeUtils.findParent((Spatial) object, 2);
            final EditorLightNode lightNode = parent == null ? null : state.getLightNode(parent);
            if (lightNode != null) return lightNode;
            final EditorAudioNode audioNode = parent == null ? null : state.getAudioNode(parent);
            if (audioNode != null) return audioNode;
        }

        if (object instanceof Spatial && !((Spatial) object).isVisible()) {
            return null;
        }

        if (object instanceof Spatial && findParent((Spatial) object, spatial -> !spatial.isVisible()) != null) {
            return null;
        }

        return object;
    }
}
