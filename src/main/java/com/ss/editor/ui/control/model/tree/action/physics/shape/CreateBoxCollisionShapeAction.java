package com.ss.editor.ui.control.model.tree.action.physics.shape;

import static com.ss.editor.extension.property.EditablePropertyType.VECTOR_3F;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.math.Vector3f;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.rlib.util.VarTable;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;

/**
 * The action to create a box collision shape.
 *
 * @author JavaSaBr
 */
public class CreateBoxCollisionShapeAction extends AbstractCreateShapeAction {

    @NotNull
    private static final String PROPERTY_HALF_EXTENTS = "halfExtents";

    public CreateBoxCollisionShapeAction(@NotNull final NodeTree<?> nodeTree,
                                         @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FXThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_BOX_COLLISION_SHAPE;
    }

    @Override
    @FXThread
    protected @NotNull String getDialogTitle() {
        return Messages.CREATE_BOX_COLLISION_SHAPE_DIALOG_TITLE;
    }


    @Override
    @FXThread
    protected @NotNull Array<PropertyDefinition> getPropertyDefinitions() {
        final Array<PropertyDefinition> definitions = ArrayFactory.newArray(PropertyDefinition.class);
        definitions.add(new PropertyDefinition(VECTOR_3F, Messages.MODEL_PROPERTY_HALF_EXTENTS, PROPERTY_HALF_EXTENTS, new Vector3f(1, 1, 1)));
        return definitions;
    }


    @Override
    @FXThread
    protected @NotNull CollisionShape createShape(@NotNull final VarTable vars) {
        final Vector3f halfExtents = vars.get(PROPERTY_HALF_EXTENTS);
        return new BoxCollisionShape(halfExtents);
    }
}
