package com.ss.builder.ui.control.tree.action.impl.physics.shape;

import static com.ss.editor.extension.property.EditablePropertyType.VECTOR_3F;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.math.Vector3f;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.plugin.api.property.PropertyDefinition;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.rlib.common.util.VarTable;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
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
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_BOX_COLLISION_SHAPE;
    }

    @Override
    @FxThread
    protected @NotNull String getDialogTitle() {
        return Messages.CREATE_BOX_COLLISION_SHAPE_DIALOG_TITLE;
    }


    @Override
    @FxThread
    protected @NotNull Array<PropertyDefinition> getPropertyDefinitions() {
        final Array<PropertyDefinition> definitions = ArrayFactory.newArray(PropertyDefinition.class);
        definitions.add(new PropertyDefinition(VECTOR_3F, Messages.MODEL_PROPERTY_HALF_EXTENTS, PROPERTY_HALF_EXTENTS, new Vector3f(1, 1, 1)));
        return definitions;
    }


    @Override
    @FxThread
    protected @NotNull CollisionShape createShape(@NotNull final VarTable vars) {
        final Vector3f halfExtents = vars.get(PROPERTY_HALF_EXTENTS);
        return new BoxCollisionShape(halfExtents);
    }
}
