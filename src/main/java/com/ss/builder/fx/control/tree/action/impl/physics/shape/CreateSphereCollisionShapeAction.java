package com.ss.builder.fx.control.tree.action.impl.physics.shape;

import static com.ss.editor.extension.property.EditablePropertyType.FLOAT;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.plugin.api.property.PropertyDefinition;
import com.ss.builder.fx.Icons;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.plugin.api.property.PropertyDefinition;
import com.ss.builder.fx.Icons;
import com.ss.builder.fx.control.tree.NodeTree;
import com.ss.builder.fx.control.tree.node.TreeNode;
import com.ss.rlib.common.util.VarTable;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to create a sphere collision shape.
 *
 * @author JavaSaBr
 */
public class CreateSphereCollisionShapeAction extends AbstractCreateShapeAction {

    @NotNull
    private static final String PROPERTY_RADIUS = "radius";

    public CreateSphereCollisionShapeAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.SPHERE_16;
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_SPHERE_COLLISION_SHAPE;
    }

    @Override
    @FxThread
    protected @NotNull String getDialogTitle() {
        return Messages.CREATE_SPHERE_COLLISION_SHAPE_DIALOG_TITLE;
    }

    @Override
    @FxThread
    protected @NotNull Array<PropertyDefinition> getPropertyDefinitions() {
        final Array<PropertyDefinition> definitions = ArrayFactory.newArray(PropertyDefinition.class);
        definitions.add(new PropertyDefinition(FLOAT, Messages.MODEL_PROPERTY_RADIUS, PROPERTY_RADIUS, 1F));
        return definitions;
    }

    @Override
    @FxThread
    protected @NotNull CollisionShape createShape(@NotNull final VarTable vars) {
        final float radius = vars.get(PROPERTY_RADIUS);
        return new SphereCollisionShape(radius);
    }
}
