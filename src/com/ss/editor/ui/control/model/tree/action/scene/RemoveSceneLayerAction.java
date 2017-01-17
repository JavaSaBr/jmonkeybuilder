package com.ss.editor.ui.control.model.tree.action.scene;

import static java.util.Objects.requireNonNull;

import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.model.tree.action.operation.scene.RemoveSceneLayerOperation;
import com.ss.editor.ui.control.model.tree.node.ModelNode;
import com.ss.editor.ui.control.model.tree.node.spatial.scene.SceneLayerModelNode;
import com.ss.extension.scene.SceneLayer;
import com.ss.extension.scene.SceneNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import javafx.scene.image.Image;

/**
 * The action to remove a scene layer.
 *
 * @author JavaSaBr
 */
public class RemoveSceneLayerAction extends AbstractNodeAction {

    public RemoveSceneLayerAction(@NotNull final ModelNodeTree nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.REMOVE_18;
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_REMOVE;
    }

    @Override
    protected void process() {

        final ModelNodeTree nodeTree = getNodeTree();
        final ModelChangeConsumer consumer = requireNonNull(nodeTree.getModelChangeConsumer());

        final SceneLayerModelNode modelNode = (SceneLayerModelNode) getNode();
        final SceneLayer layer = modelNode.getElement();

        final ModelNode<?> parent = Objects.requireNonNull(modelNode.getParent(), "The layer doesn't have a parent.");
        final SceneNode sceneNode = (SceneNode) parent.getElement();

        consumer.execute(new RemoveSceneLayerOperation(layer, sceneNode));
    }
}
