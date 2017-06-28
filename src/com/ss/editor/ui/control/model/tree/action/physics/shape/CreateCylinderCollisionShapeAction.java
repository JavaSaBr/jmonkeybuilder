package com.ss.editor.ui.control.model.tree.action.physics.shape;

import static com.ss.editor.extension.property.EditablePropertyType.ENUM;
import static com.ss.editor.extension.property.EditablePropertyType.VECTOR_3F;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.math.Vector3f;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.physics.shape.CreateConeCollisionShapeAction.Axis;
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
 * The action to create a cylinder collision shape.
 *
 * @author JavaSaBr
 */
public class CreateCylinderCollisionShapeAction extends AbstractCreateShapeAction {

    @NotNull
    private static final String PROPERTY_HALF_EXTENTS = "halfExtents";

    @NotNull
    private static final String PROPERTY_AXIS = "axis";

    /**
     * Instantiates a new Create cylinder collision shape action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public CreateCylinderCollisionShapeAction(@NotNull final AbstractNodeTree<?> nodeTree,
                                              @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.CYLINDER_16;
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_CYLINDER_COLLISION_SHAPE;
    }

    @NotNull
    @Override
    protected String getDialogTitle() {
        return Messages.CREATE_CYLINDER_COLLISION_SHAPE_DIALOG_TITLE;
    }

    @NotNull
    @Override
    protected Array<PropertyDefinition> getPropertyDefinitions() {
        final Array<PropertyDefinition> definitions = ArrayFactory.newArray(PropertyDefinition.class);
        definitions.add(new PropertyDefinition(VECTOR_3F, Messages.MODEL_PROPERTY_HALF_EXTENTS, PROPERTY_HALF_EXTENTS, new Vector3f(1, 1, 1)));
        definitions.add(new PropertyDefinition(ENUM, Messages.MODEL_PROPERTY_AXIS, PROPERTY_AXIS, Axis.X));
        return definitions;
    }

    @NotNull
    @Override
    protected CollisionShape createShape(@NotNull final VarTable vars) {
        final Vector3f halfExtents = vars.get(PROPERTY_HALF_EXTENTS);
        final Axis axis = vars.get(PROPERTY_AXIS);
        return new CylinderCollisionShape(halfExtents, axis.ordinal());
    }
}
