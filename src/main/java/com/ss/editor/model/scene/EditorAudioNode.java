package com.ss.editor.model.scene;

import static com.ss.editor.util.GeomUtils.getDirection;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.audio.AudioNode;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.util.LocalObjects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The node to present and edit audio nodes.
 *
 * @author JavaSaBr
 */
public class EditorAudioNode extends Node {

    private class EditedNode extends Node implements NoSelection, WrapperNode {

        private EditedNode(@NotNull final String name) {
            super(name);
        }

        @Override
        @FromAnyThread
        public @NotNull Object getWrappedObject() {
            return notNull(getAudioNode());
        }
    }

    /**
     * The camera.
     */
    @NotNull
    private final Camera camera;

    /**
     * The node to edit.
     */
    @NotNull
    private final Node editedNode;

    /**
     * The audio node.
     */
    @Nullable
    private AudioNode audioNode;

    /**
     * The model.
     */
    @Nullable
    private Node model;

    public EditorAudioNode(@NotNull final Camera camera) {
        this.camera = camera;
        this.editedNode = new EditedNode("EditedNode");
        attachChild(editedNode);
    }

    /**
     * Set the audio node.
     *
     * @param audioNode the audio node.
     */
    @JmeThread
    public void setAudioNode(@Nullable final AudioNode audioNode) {
        this.audioNode = audioNode;
    }

    /**
     * Get the audio node.
     *
     * @return the audio node.
     */
    @JmeThread
    public @Nullable AudioNode getAudioNode() {
        return audioNode;
    }

    /**
     * Get the model.
     *
     * @return the model.
     */
    @JmeThread
    public @Nullable Node getModel() {
        return model;
    }

    /**
     * Set the model.
     *
     * @param model the model.
     */
    @JmeThread
    public void setModel(@Nullable final Node model) {
        this.model = model;
    }

    /**
     * Get the edited node.
     *
     * @return the edited node.
     */
    @JmeThread
    public @NotNull Node getEditedNode() {
        return editedNode;
    }

    @Override
    @JmeThread
    public void updateGeometricState() {

        final AudioNode audioNode = getAudioNode();

        if (audioNode != null) {
            final Node editedNode = getEditedNode();
            final Quaternion rotation = editedNode.getLocalRotation();
            audioNode.setDirection(getDirection(rotation, audioNode.getDirection()));
            audioNode.setLocalTranslation(editedNode.getLocalTranslation());
        }

        super.updateGeometricState();
    }

    /**
     * Synchronize this node with audio node.
     */
    @JmeThread
    public void sync() {

        final AudioNode audioNode = getAudioNode();
        if (audioNode == null) return;

        final LocalObjects local = LocalObjects.get();
        final Quaternion rotation = local.nextRotation();
        rotation.lookAt(audioNode.getDirection(), camera.getUp(local.nextVector()));

        final Node editedNode = getEditedNode();
        editedNode.setLocalRotation(rotation);
        editedNode.setLocalTranslation(audioNode.getLocalTranslation());
    }

    /**
     * Update position and rotation of a model.
     */
    @JmeThread
    public void updateModel() {

        final AudioNode audioNode = getAudioNode();
        final Node model = getModel();
        if (model == null || audioNode == null) return;

        final Node parent = audioNode.getParent();

        if (parent != null) {
            setLocalTranslation(parent.getWorldTranslation());
            setLocalRotation(parent.getWorldRotation());
            setLocalScale(parent.getWorldScale());
        }

        final Node editedNode = getEditedNode();
        final LocalObjects local = LocalObjects.get();
        final Vector3f positionOnCamera = local.nextVector();
        positionOnCamera.set(editedNode.getWorldTranslation()).subtractLocal(camera.getLocation());
        positionOnCamera.normalizeLocal();
        positionOnCamera.multLocal(camera.getFrustumNear() + 0.4f);
        positionOnCamera.addLocal(camera.getLocation());

        model.setLocalTranslation(positionOnCamera);
        model.setLocalRotation(editedNode.getLocalRotation());
    }
}
