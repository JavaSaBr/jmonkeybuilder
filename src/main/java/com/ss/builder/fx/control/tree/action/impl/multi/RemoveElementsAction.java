package com.ss.builder.ui.control.tree.action.impl.multi;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.model.undo.impl.RemoveElementsOperation;
import com.ss.builder.ui.Icons;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.action.AbstractNodeAction;
import com.ss.editor.model.undo.impl.RemoveElementsOperation;
import com.ss.editor.model.undo.impl.RemoveElementsOperation.Element;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.editor.ui.control.tree.node.impl.control.ControlTreeNode;
import com.ss.editor.ui.control.tree.node.impl.control.legacyanim.AnimationTreeNode;
import com.ss.editor.ui.control.tree.node.impl.light.LightTreeNode;
import com.ss.editor.ui.control.tree.node.impl.spatial.SpatialTreeNode;
import com.ss.rlib.common.function.TripleConsumer;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayCollectors;
import com.ss.rlib.common.util.array.ArrayFactory;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * The action to remove some elements from a scene/model.
 *
 * @author JavaSaBr
 */
public class RemoveElementsAction extends AbstractNodeAction<ModelChangeConsumer> {

    private static final Array<Class<?>> AVAILABLE_TYPES = ArrayFactory.newArray(Class.class);

    static {
        AVAILABLE_TYPES.add(SpatialTreeNode.class);
        AVAILABLE_TYPES.add(ControlTreeNode.class);
        AVAILABLE_TYPES.add(LightTreeNode.class);
        AVAILABLE_TYPES.add(AnimationTreeNode.class);
    }

    public static final NodeTree.MultiItemActionFiller ACTION_FILLER = (nodeTree, menuItems, treeNodes) -> {

        var unexpectedItem = treeNodes.findAny(treeNode ->
                AVAILABLE_TYPES.findAny(treeNode, Class::isInstance) == null || !treeNode.canRemove());

        if (unexpectedItem == null) {
            menuItems.add(new RemoveElementsAction(nodeTree, treeNodes));
        }
    };

    public RemoveElementsAction(@NotNull NodeTree<?> nodeTree, @NotNull Array<TreeNode<?>> nodes) {
        super(nodeTree, nodes);
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_REMOVE;
    }

    @Override
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.REMOVE_12;
    }

    @Override
    @FxThread
    public void process() {
        super.process();

        var nodes = getNodes();
        var toRemove = nodes.stream()
                .filter(treeNode -> treeNode.getParent() != null)
                .map(this::convert)
                .collect(ArrayCollectors.toArray(RemoveElementsOperation.Element.class));

        getNodeTree().requireChangeConsumer()
                .execute(new RemoveElementsOperation(toRemove));
    }

    @FxThread
    private @NotNull RemoveElementsOperation.Element convert(@NotNull TreeNode<?> treeNode) {
        var value = treeNode.getElement();
        var parentNode = notNull(treeNode.getParent());
        var parent = parentNode.getElement();
        return new RemoveElementsOperation.Element(value, parent);
    }
}
