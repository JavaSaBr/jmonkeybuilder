package com.ss.editor.ui.control.tree.action.impl.physics.shape;

import static com.jme3.bullet.util.CollisionShapeFactory.createDynamicMeshShape;
import static com.jme3.bullet.util.CollisionShapeFactory.createMeshShape;
import static com.ss.rlib.util.ObjectUtils.notNull;
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
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.action.impl.operation.ChangeCollisionShapeOperation;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.rlib.util.VarTable;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to generate  a collision shape.
 *
 * @author JavaSaBr
 */
public class GenerateCollisionShapeAction extends AbstractCreateShapeAction {

    @NotNull
    private static final Array<PropertyDefinition> EMPTY_DEFINITIONS = ArrayFactory.asArray();

    public GenerateCollisionShapeAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.INFLUENCER_16;
    }

    @Override
    @FxThread
    protected void process() {
        super.process();

        final TreeNode<?> treeNode = getNode();
        final TreeNode<?> parentNode = notNull(treeNode.getParent());

        final PhysicsCollisionObject object = (PhysicsCollisionObject) treeNode.getElement();
        final Spatial parentElement = (Spatial) notNull(parentNode.getElement());

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

        final NodeTree<?> nodeTree = getNodeTree();
        final ChangeConsumer changeConsumer = notNull(nodeTree.getChangeConsumer());
        changeConsumer.execute(new ChangeCollisionShapeOperation(shape, object.getCollisionShape(), object));
    }

    @Override
    @FxThread
    protected @NotNull String getDialogTitle() {
        throw new RuntimeException();
    }

    @Override
    @FxThread
    protected @NotNull Array<PropertyDefinition> getPropertyDefinitions() {
        return EMPTY_DEFINITIONS;
    }

    @Override
    @FxThread
    protected @NotNull CollisionShape createShape(@NotNull final VarTable vars) {
        throw new RuntimeException();
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_GENERATE_COLLISION_SHAPE;
    }
}
