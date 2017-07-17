package com.ss.editor.state.editor.impl.model;

import static com.ss.editor.control.transform.SceneEditorControl.LOADED_MODEL_KEY;
import static com.ss.editor.util.NodeUtils.findParent;

import com.jme3.scene.AssetLinkNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.scene.EditorAudioNode;
import com.ss.editor.scene.EditorLightNode;
import com.ss.editor.state.editor.impl.scene.AbstractSceneEditorAppState;
import com.ss.editor.ui.component.editor.impl.scene.AbstractSceneFileEditor;
import com.ss.editor.util.NodeUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.geometry.ParticleGeometry;

/**
 * The class with utilities methods for {@link ModelEditorAppState}.
 *
 * @author JavaSaBr
 */
public class ModelEditorUtils {

    /**
     * Find to select object.
     *
     * @param <T>    the type parameter
     * @param <M>    the type parameter
     * @param state  the state
     * @param object the object
     * @return the object
     */
    @Nullable
    public static <T extends AbstractSceneFileEditor & ModelChangeConsumer, M extends Spatial> Object findToSelect(
            @NotNull final AbstractSceneEditorAppState<T, M> state, @NotNull final Object object) {

        if (object instanceof ParticleGeometry) {
            final Spatial parent = findParent((Spatial) object, spatial -> spatial instanceof ParticleEmitterNode);
            if (parent != null && parent.isVisible()) return parent;
        }

        if (object instanceof Geometry) {

            final Spatial spatial = (Spatial) object;

            Spatial parent = NodeUtils.findParent(spatial, 2);

            final EditorLightNode lightNode = parent == null ? null : state.getLightNode(parent);
            if (lightNode != null) return lightNode;

            final EditorAudioNode audioNode = parent == null ? null : state.getAudioNode(parent);
            if (audioNode != null) return audioNode;

            parent = NodeUtils.findParent(spatial, p -> p instanceof AssetLinkNode);
            if (parent != null) return parent;

            parent = NodeUtils.findParent(spatial, p -> p.getUserData(LOADED_MODEL_KEY) == Boolean.TRUE);
            if (parent != null) return parent;
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
