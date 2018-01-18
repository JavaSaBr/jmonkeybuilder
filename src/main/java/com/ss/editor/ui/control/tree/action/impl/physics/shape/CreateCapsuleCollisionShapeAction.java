package com.ss.editor.ui.control.tree.action.impl.physics.shape;

import static com.ss.editor.extension.property.EditablePropertyType.FLOAT;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.rlib.util.VarTable;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to create a capsule collision shape.
 *
 * @author JavaSaBr
 */
public class CreateCapsuleCollisionShapeAction extends AbstractCreateShapeAction {

    @NotNull
    private static final String PROPERTY_RADIUS = "radius";

    @NotNull
    private static final String PROPERTY_HEIGHT = "height";

    public CreateCapsuleCollisionShapeAction(@NotNull final NodeTree<?> nodeTree,
                                             @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.CAPSULE_16;
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_CAPSULE_COLLISION_SHAPE;
    }

    @Override
    @FxThread
    protected @NotNull String getDialogTitle() {
        return Messages.CREATE_CAPSULE_COLLISION_SHAPE_DIALOG_TITLE;
    }

    @Override
    @FxThread
    protected @NotNull Array<PropertyDefinition> getPropertyDefinitions() {
        final Array<PropertyDefinition> definitions = ArrayFactory.newArray(PropertyDefinition.class);
        definitions.add(new PropertyDefinition(FLOAT, Messages.MODEL_PROPERTY_RADIUS, PROPERTY_RADIUS, 1F));
        definitions.add(new PropertyDefinition(FLOAT, Messages.MODEL_PROPERTY_HEIGHT, PROPERTY_HEIGHT, 1F));
        return definitions;
    }

    @Override
    @FxThread
    protected @NotNull CollisionShape createShape(@NotNull final VarTable vars) {
        final float height = vars.getFloat(PROPERTY_HEIGHT);
        final float radius = vars.getFloat(PROPERTY_RADIUS);
        return new CapsuleCollisionShape(radius, height);
    }
}
