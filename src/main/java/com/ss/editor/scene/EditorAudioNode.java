package com.ss.editor.scene;

import static com.ss.editor.util.GeomUtils.getDirection;

import com.jme3.audio.AudioNode;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.ss.editor.Editor;
import com.ss.editor.util.LocalObjects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The node to present and edit audio nodes.
 *
 * @author JavaSaBr
 */
public class EditorAudioNode extends Node {

    @NotNull
    private static final Editor EDITOR = Editor.getInstance();

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

    /**
     * Instantiates a new Editor audio node.
     */
    public EditorAudioNode() {
        this.editedNode = new Node("EditedNode");
        attachChild(editedNode);
    }

    /**
     * Set a audio node.
     *
     * @param audioNode the audio node.
     */
    public void setAudioNode(@Nullable final AudioNode audioNode) {
        this.audioNode = audioNode;
    }

    /**
     * Get a audio node.
     *
     * @return the audio node.
     */
    @Nullable
    public AudioNode getAudioNode() {
        return audioNode;
    }

    /**
     * Get a model.
     *
     * @return the model.
     */
    @Nullable
    public Node getModel() {
        return model;
    }

    /**
     * Set a model.
     *
     * @param model the model.
     */
    public void setModel(@Nullable final Node model) {
        this.model = model;
    }

    /**
     * Gets edited node.
     *
     * @return the edited node.
     */
    @NotNull
    public Node getEditedNode() {
        return editedNode;
    }

    @Override
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
     * Update position and rotation of a model.
     */
    public void updateModel() {

        final AudioNode audioNode = getAudioNode();
        final Node model = getModel();
        if (model == null || audioNode == null) return;

        final Node parent = audioNode.getParent();
        if (parent != null) {
            setLocalTranslation(parent.getWorldTranslation());
        }

        final Node editedNode = getEditedNode();
        final Camera camera = EDITOR.getCamera();
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
