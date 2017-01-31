package com.ss.editor.ui.control.model.tree.action.physics.shape;

import static com.jme3.bullet.util.CollisionShapeFactory.createDynamicMeshShape;
import static com.jme3.bullet.util.CollisionShapeFactory.createMeshShape;
import static java.util.Objects.requireNonNull;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.operation.ChangeCollisionShapeOperation;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to generate  a collision shape.
 *
 * @author JavaSaBr
 */
public class GenerateCollisionShapeAction extends AbstractCreateShapeAction<PhysicsCollisionObject> {

    public GenerateCollisionShapeAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.INFLUENCER_16;
    }

    @Override
    protected void createShape(@NotNull final PhysicsCollisionObject object, @NotNull final Spatial parentElement,
                               @NotNull final AbstractNodeTree<?> nodeTree) {

        CollisionShape shape = null;

        final float mass = object instanceof PhysicsRigidBody ? ((PhysicsRigidBody) object).getMass() : 1F;

        if (parentElement instanceof Geometry) {

            final Geometry geom = (Geometry) parentElement;
            final Mesh mesh = geom.getMesh();

            if (mesh instanceof Sphere) {
                shape = new SphereCollisionShape(((Sphere) mesh).getRadius());
            } else if (mesh instanceof Box) {
                final Box box = (Box) mesh;
                shape = new BoxCollisionShape(new Vector3f(box.getXExtent(), box.getYExtent(), box.getZExtent()));
            }

        }

        if (shape == null) {
            if (mass > 0) {
                shape = createDynamicMeshShape(parentElement);
            } else {
                shape = createMeshShape(parentElement);
            }
        }

        final ChangeConsumer changeConsumer = requireNonNull(nodeTree.getChangeConsumer());
        changeConsumer.execute(new ChangeCollisionShapeOperation(shape, object.getCollisionShape(), object));
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_GENERATE_COLLISION_SHAPE;
    }
}
