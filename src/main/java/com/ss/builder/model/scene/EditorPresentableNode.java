package com.ss.builder.model.scene;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.util.EditorUtils;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.editor.extension.scene.ScenePresentable;
import com.ss.editor.extension.scene.ScenePresentable.PresentationType;
import com.ss.builder.util.EditorUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The node to present and edit objects on a scene which aren't models.
 *
 * @author JavaSaBr
 */
public class EditorPresentableNode extends Node {

    private class EditedNode extends Node implements VisibleOnlyWhenSelected, NoSelection, WrapperNode {

        private EditedNode(@NotNull String name) {
            super(name);
        }

        @Override
        @FromAnyThread
        public @NotNull Object getWrappedObject() {
            return notNull(getObject());
        }

        @Override
        @JmeThread
        public void setCullHint(CullHint hint) {
            super.setCullHint(hint);
            notNull(getModel()).setCullHint(hint);
        }
    }

    /**
     * The node to edit.
     */
    @NotNull
    private final EditedNode editedNode;

    /**
     * The presented object.
     */
    @Nullable
    private ScenePresentable object;

    /**
     * Previous presentation type.
     */
    @Nullable
    private PresentationType prevPresentationType;

    /**
     * The view model.
     */
    @Nullable
    private Geometry model;

    public EditorPresentableNode() {
        this.editedNode = new EditedNode("EditedNode");
        attachChild(editedNode);
    }

    /**
     * Set the object.
     *
     * @param object the object.
     */
    @JmeThread
    public void setObject(@Nullable ScenePresentable object) {
        this.object = object;
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

    /**
     * Get the object.
     *
     * @return the object.
     */
    @JmeThread
    public @Nullable ScenePresentable getObject() {
        return object;
    }

    /**
     * Get the model.
     *
     * @return the model.
     */
    @JmeThread
    public @Nullable Geometry getModel() {
        return model;
    }

    /**
     * Set the model.
     *
     * @param model the model.
     */
    @JmeThread
    public void setModel(@Nullable Geometry model) {
        this.model = model;
    }

    @Override
    @JmeThread
    public void updateGeometricState() {

        var object = getObject();

        if (object != null) {
            var editedNode = getEditedNode();
            object.setRotation(editedNode.getLocalRotation());
            object.setLocation(editedNode.getLocalTranslation());
            object.setScale(editedNode.getLocalScale());
        }

        super.updateGeometricState();
    }

    /**
     * Synchronize this node with presented object.
     */
    @JmeThread
    public void sync() {

        var object = notNull(getObject());

        var editedNode = getEditedNode();
        editedNode.setLocalRotation(object.getRotation());
        editedNode.setLocalTranslation(object.getLocation());
        editedNode.setLocalScale(object.getScale());
    }

    /**
     * Update position and rotation of a model.
     */
    @JmeThread
    public void updateModel() {

        var object = getObject();
        var model = getModel();

        if (model == null || object == null) {
            return;
        }

        // TODO implement getting parent
        /*final Node parent = object.getParent();

        if (parent != null) {
            setLocalTranslation(parent.getWorldTranslation());
            setLocalRotation(parent.getWorldRotation());
            setLocalScale(parent.getWorldScale());
        }*/

        var editedNode = getEditedNode();

        model.setLocalTranslation(editedNode.getWorldTranslation());
        model.setLocalRotation(editedNode.getWorldRotation());
        model.setLocalScale(editedNode.getWorldScale());
    }

    /**
     * Update a geometry of presentation.
     */
    @JmeThread
    public void updateGeometry() {

        var presentationType = notNull(getObject()).getPresentationType();
        if (presentationType == prevPresentationType) {
            return;
        }

        var prevModel = getModel();
        var newModel = createGeometry(presentationType);

        setModel(newModel);

        if (prevModel != null) {
            Node parent = prevModel.getParent();
            prevModel.removeFromParent();
            if (parent != null) {
                parent.attachChild(newModel);
            }
        }

        this.prevPresentationType = presentationType;
    }

    @JmeThread
    private @NotNull Geometry createGeometry(@NotNull PresentationType presentationType) {

        var material = new Material(EditorUtils.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        material.setColor("Color", ColorRGBA.Yellow);
        material.getAdditionalRenderState().setWireframe(true);

        Geometry geometry;

        switch (presentationType) {
            case SPHERE: {
                geometry = new Geometry("Sphere", new Sphere(8, 8, 1));
                break;
            }
            default: {
                geometry = new Geometry("Box", new Box(1, 1, 1));
            }
        }

        geometry.setMaterial(material);

        return geometry;
    }
}
