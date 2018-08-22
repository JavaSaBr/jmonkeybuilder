package com.ss.builder.jme.editor.part3d.impl.scene.handler;

import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.jme.editor.part3d.impl.scene.AbstractSceneEditor3dPart;
import com.ss.builder.util.ControlUtils;
import com.ss.builder.util.NodeUtils;
import com.ss.rlib.common.util.dictionary.DictionaryFactory;
import com.ss.rlib.common.util.dictionary.ObjectDictionary;
import org.jetbrains.annotations.NotNull;

/**
 * The handler to disable all controls during transforming spatial.
 *
 * @author JavaSaBr
 */
public class ApplyScaleToPhysicsControlsHandler {

    /**
     * The condition to find a model root.
     *
     * @param spatial the model.
     * @return true if the node is model root.
     */
    @FromAnyThread
    protected static boolean isModelRoot(@NotNull final Spatial spatial) {
        final Node parent = spatial.getParent();
        return parent == null || parent.getUserData(AbstractSceneEditor3dPart.KEY_MODEL_NODE) != null;
    }

    /**
     * The saved previous scales.
     */
    @NotNull
    private final ObjectDictionary<Spatial, Vector3f> previousScales;

    public ApplyScaleToPhysicsControlsHandler() {
        this.previousScales = DictionaryFactory.newObjectDictionary();
    }

    /**
     * Disable all controls before transform.
     *
     * @param spatial the spatial.
     */
    @JmeThread
    public void onPreTransform(@NotNull final Spatial spatial) {
        NodeUtils.children(NodeUtils.findParent(spatial, ApplyScaleToPhysicsControlsHandler::isModelRoot))
                .filter(sp -> ControlUtils.has(sp, PhysicsCollisionObject.class))
                .peek(sp -> previousScales.put(sp, sp.getWorldScale().clone()))
                .flatMap(NodeUtils::children)
                .filter(ch -> !previousScales.containsKey(ch))
                .forEach(ch -> previousScales.put(ch, ch.getWorldScale().clone()));
    }


    /**
     * Enable disabled controls before transform.
     *
     * @param spatial the spatial.
     */
    @JmeThread
    public void onPostTransform(@NotNull final Spatial spatial) {

        NodeUtils.children(NodeUtils.findParent(spatial, ApplyScaleToPhysicsControlsHandler::isModelRoot))
                .filter(sp -> ControlUtils.has(sp, PhysicsCollisionObject.class))
                .filter(previousScales::containsKey)
                .filter(sp -> isChanged(sp) || NodeUtils.children(sp).anyMatch(this::isChanged))
                .forEach(sp -> ControlUtils.controls(sp)
                        .filter(PhysicsCollisionObject.class::isInstance)
                        .map(PhysicsCollisionObject.class::cast)
                        .forEach(object -> ControlUtils.applyScale(sp, sp.getWorldScale(), object)));

        previousScales.clear();
    }

    @JmeThread
    protected boolean isChanged(@NotNull final Spatial sp) {
        final Vector3f prevScale = previousScales.remove(sp);
        return prevScale != null && !prevScale.equals(sp.getWorldScale());
    }
}
