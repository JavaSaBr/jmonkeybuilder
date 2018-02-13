package com.ss.editor.ui.control.tree.action.impl.multi;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.tree.action.impl.operation.RemoveElementsOperation;
import com.ss.editor.ui.control.tree.action.impl.operation.RemoveElementsOperation.Element;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.editor.ui.control.tree.node.impl.control.ControlTreeNode;
import com.ss.editor.ui.control.tree.node.impl.control.anim.AnimationTreeNode;
import com.ss.editor.ui.control.tree.node.impl.light.LightTreeNode;
import com.ss.editor.ui.control.tree.node.impl.spatial.SpatialTreeNode;
import com.ss.rlib.function.TripleConsumer;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayCollectors;
import com.ss.rlib.util.array.ArrayFactory;
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

    public static final TripleConsumer<NodeTree<?>, List<MenuItem>, Array<TreeNode<?>>> ACTION_FILLER = (nodeTree, menuItems, treeNodes) -> {

        final TreeNode<?> unexpectedItem = treeNodes.search(treeNode ->
                AVAILABLE_TYPES.search(treeNode, Class::isInstance) == null || !treeNode.canRemove());

        if (unexpectedItem == null) {
            menuItems.add(new RemoveElementsAction(nodeTree, treeNodes));
        }
    };

    public RemoveElementsAction(@NotNull final NodeTree<?> nodeTree, @NotNull final Array<TreeNode<?>> nodes) {
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
    protected void process() {
        super.process();

        final Array<TreeNode<?>> nodes = getNodes();
        final Array<Element> toRemove = nodes.stream()
                .filter(treeNode -> treeNode.getParent() != null)
                .map(this::convert)
                .collect(ArrayCollectors.toArray(Element.class));


        final NodeTree<ModelChangeConsumer> nodeTree = getNodeTree();
        final ChangeConsumer changeConsumer = notNull(nodeTree.getChangeConsumer());
        changeConsumer.execute(new RemoveElementsOperation(toRemove));
    }

    @FxThread
    private @NotNull Element convert(@NotNull final TreeNode<?> treeNode) {
        final Object value = treeNode.getElement();
        final TreeNode<?> parentNode = notNull(treeNode.getParent());
        final Object parent = parentNode.getElement();
        return new Element(value, parent);
    }
}
