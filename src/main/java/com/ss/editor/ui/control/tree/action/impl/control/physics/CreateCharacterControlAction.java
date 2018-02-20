package com.ss.editor.ui.control.tree.action.impl.control.physics;

import static com.ss.editor.extension.property.EditablePropertyType.FLOAT;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.scene.Spatial;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AddControlOperation;
import com.ss.editor.plugin.api.dialog.GenericFactoryDialog;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.action.impl.control.AbstractCreateControlAction;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to create a {@link CharacterControl}.
 *
 * @author JavaSaBr
 */
public class CreateCharacterControlAction extends AbstractCreateControlAction {

    private static final String PROPERTY_RADIUS = "radius";
    private static final String PROPERTY_HEIGHT = "height";
    private static final String PROPERTY_MASS = "mass";

    public CreateCharacterControlAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.CHARACTER_16;
    }


    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_ADD_CONTROL_CHARACTER;
    }

    @Override
    protected void process() {

        final Array<PropertyDefinition> definitions = ArrayFactory.newArray(PropertyDefinition.class);
        definitions.add(new PropertyDefinition(FLOAT, Messages.MODEL_PROPERTY_RADIUS, PROPERTY_RADIUS, 1F));
        definitions.add(new PropertyDefinition(FLOAT, Messages.MODEL_PROPERTY_HEIGHT, PROPERTY_HEIGHT, 1F));
        definitions.add(new PropertyDefinition(FLOAT, Messages.MODEL_PROPERTY_MASS, PROPERTY_MASS, 1F));

        final GenericFactoryDialog dialog = new GenericFactoryDialog(definitions, vars -> {

            final float radius = vars.getFloat(PROPERTY_RADIUS);
            final float height = vars.getFloat(PROPERTY_HEIGHT);
            final float mass = vars.getFloat(PROPERTY_MASS);

            final TreeNode<?> treeNode = getNode();
            final Spatial parent = (Spatial) treeNode.getElement();

            final NodeTree<ModelChangeConsumer> nodeTree = getNodeTree();
            final ModelChangeConsumer consumer = notNull(nodeTree.getChangeConsumer());
            consumer.execute(new AddControlOperation(new BetterCharacterControl(radius, height, mass), parent));
        });
        dialog.show();
    }
}
