package com.ss.editor.ui.control.tree.action.impl.physics.shape;

import static com.ss.editor.extension.property.EditablePropertyType.ENUM;
import static com.ss.editor.extension.property.EditablePropertyType.FLOAT;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.ConeCollisionShape;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.rlib.common.util.VarTable;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to create a cone collision shape.
 *
 * @author JavaSaBr
 */
public class CreateConeCollisionShapeAction extends AbstractCreateShapeAction {

    protected enum Axis {
        X,
        Y,
        Z
    }

    @NotNull
    private static final String PROPERTY_RADIUS = "radius";

    @NotNull
    private static final String PROPERTY_HEIGHT = "height";

    @NotNull
    private static final String PROPERTY_AXIS = "axis";

    public CreateConeCollisionShapeAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.CONE_16;
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_CONE_COLLISION_SHAPE;
    }

    @Override
    @FxThread
    protected @NotNull String getDialogTitle() {
        return Messages.CREATE_CONE_COLLISION_SHAPE_DIALOG_TITLE;
    }

    @Override
    @FxThread
    protected @NotNull Array<PropertyDefinition> getPropertyDefinitions() {
        final Array<PropertyDefinition> definitions = ArrayFactory.newArray(PropertyDefinition.class);
        definitions.add(new PropertyDefinition(FLOAT, Messages.MODEL_PROPERTY_RADIUS, PROPERTY_RADIUS, 1F));
        definitions.add(new PropertyDefinition(FLOAT, Messages.MODEL_PROPERTY_HEIGHT, PROPERTY_HEIGHT, 1F));
        definitions.add(new PropertyDefinition(ENUM, Messages.MODEL_PROPERTY_AXIS, PROPERTY_AXIS, Axis.X));
        return definitions;
    }

    @Override
    @FxThread
    protected @NotNull CollisionShape createShape(@NotNull final VarTable vars) {

        final float height = vars.getFloat(PROPERTY_HEIGHT);
        final float radius = vars.getFloat(PROPERTY_RADIUS);
        final Axis axis = vars.get(PROPERTY_AXIS);

        return new ConeCollisionShape(radius, height, axis.ordinal());
    }
}
