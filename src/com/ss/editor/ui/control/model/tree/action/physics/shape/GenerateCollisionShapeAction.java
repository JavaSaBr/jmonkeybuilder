package com.ss.editor.ui.control.model.tree.action.physics.shape;

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
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.operation.ChangeCollisionShapeOperation;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import com.ss.editor.ui.dialog.factory.PropertyDefinition;
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

    /**
     * Instantiates a new Generate collision shape action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public GenerateCollisionShapeAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.INFLUENCER_16;
    }

    @FXThread
    @Override
    protected void process() {
        super.process();

        final ModelNode<?> modelNode = getNode();
        final ModelNode<?> parentNode = notNull(modelNode.getParent());

        final PhysicsCollisionObject object = (PhysicsCollisionObject) modelNode.getElement();
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

        final AbstractNodeTree<?> nodeTree = getNodeTree();
        final ChangeConsumer changeConsumer = notNull(nodeTree.getChangeConsumer());
        changeConsumer.execute(new ChangeCollisionShapeOperation(shape, object.getCollisionShape(), object));
    }

    @NotNull
    @Override
    protected String getDialogTitle() {
        throw new RuntimeException();
    }

    @NotNull
    @Override
    protected Array<PropertyDefinition> getPropertyDefinitions() {
        return EMPTY_DEFINITIONS;
    }

    @NotNull
    @Override
    protected CollisionShape createShape(@NotNull final VarTable vars) {
        throw new RuntimeException();
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_GENERATE_COLLISION_SHAPE;
    }
}
