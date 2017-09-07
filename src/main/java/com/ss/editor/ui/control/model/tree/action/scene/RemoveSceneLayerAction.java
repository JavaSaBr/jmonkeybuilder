package com.ss.editor.ui.control.model.tree.action.scene;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.extension.scene.SceneLayer;
import com.ss.editor.extension.scene.SceneNode;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.layer.LayersRoot;
import com.ss.editor.ui.control.layer.node.SceneLayerTreeNode;
import com.ss.editor.ui.control.model.tree.action.operation.scene.RemoveSceneLayerOperation;
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
    @FXThread
    protected @Nullable Image getIcon() {
        return Icons.REMOVE_12;
    }

    @Override
    @FXThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_REMOVE;
    }

    @Override
    @FXThread
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
