package com.ss.builder.fx.control.tree.action.impl.physics.shape;

import static com.ss.editor.extension.property.EditablePropertyType.ENUM;
import static com.ss.editor.extension.property.EditablePropertyType.VECTOR_3F;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.math.Vector3f;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.plugin.api.property.PropertyDefinition;
import com.ss.builder.fx.Icons;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.plugin.api.property.PropertyDefinition;
import com.ss.builder.fx.Icons;
import com.ss.builder.fx.control.tree.action.impl.physics.shape.CreateConeCollisionShapeAction.Axis;
import com.ss.builder.fx.control.tree.NodeTree;
import com.ss.builder.fx.control.tree.node.TreeNode;
import com.ss.rlib.common.util.VarTable;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
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

    public CreateCylinderCollisionShapeAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.CYLINDER_16;
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_CYLINDER_COLLISION_SHAPE;
    }

    @Override
    @FxThread
    protected @NotNull String getDialogTitle() {
        return Messages.CREATE_CYLINDER_COLLISION_SHAPE_DIALOG_TITLE;
    }

    @Override
    @FxThread
    protected @NotNull Array<PropertyDefinition> getPropertyDefinitions() {
        final Array<PropertyDefinition> definitions = ArrayFactory.newArray(PropertyDefinition.class);
        definitions.add(new PropertyDefinition(VECTOR_3F, Messages.MODEL_PROPERTY_HALF_EXTENTS, PROPERTY_HALF_EXTENTS, new Vector3f(1, 1, 1)));
        definitions.add(new PropertyDefinition(ENUM, Messages.MODEL_PROPERTY_AXIS, PROPERTY_AXIS, Axis.X));
        return definitions;
    }

    @Override
    @FxThread
    protected @NotNull CollisionShape createShape(@NotNull final VarTable vars) {
        final Vector3f halfExtents = vars.get(PROPERTY_HALF_EXTENTS);
        final Axis axis = vars.get(PROPERTY_AXIS);
        return new CylinderCollisionShape(halfExtents, axis.ordinal());
    }
}
