package com.ss.builder.ui.control.tree.action.impl.scene;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.node.layer.LayersRoot;
import com.ss.builder.model.undo.editor.SceneChangeConsumer;
import com.ss.builder.model.undo.impl.scene.RemoveSceneLayerOperation;
import com.ss.builder.ui.Icons;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.extension.scene.SceneLayer;
import com.ss.editor.extension.scene.SceneNode;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.model.node.layer.LayersRoot;
import com.ss.editor.ui.control.tree.node.impl.layer.SceneLayerTreeNode;
import com.ss.editor.model.undo.impl.scene.RemoveSceneLayerOperation;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.tree.node.TreeNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to remove a scene layer.
 *
 * @author JavaSaBr
 */
public class RemoveSceneLayerAction extends AbstractNodeAction<SceneChangeConsumer> {

    public RemoveSceneLayerAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.REMOVE_12;
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_REMOVE;
    }

    @Override
    @FxThread
    protected void process() {
        super.process();

        final NodeTree<SceneChangeConsumer> nodeTree = getNodeTree();
        final SceneChangeConsumer changeConsumer = notNull(nodeTree.getChangeConsumer());
        final SceneNode sceneNode = changeConsumer.getCurrentModel();

        final SceneLayerTreeNode modelNode = (SceneLayerTreeNode) getNode();
        final SceneLayer layer = modelNode.getElement();

        changeConsumer.execute(new RemoveSceneLayerOperation(new LayersRoot(changeConsumer), layer, sceneNode));
    }
}
