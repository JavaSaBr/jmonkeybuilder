package com.ss.editor.ui.control.model.tree.action.scene;

import static java.util.Objects.requireNonNull;

import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.model.tree.action.operation.scene.RemoveSceneLayerOperation;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import com.ss.editor.ui.control.layer.node.SceneLayerModelNode;
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
public class RemoveSceneLayerAction extends AbstractNodeAction<ModelChangeConsumer> {

    public RemoveSceneLayerAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
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

        final AbstractNodeTree<?> nodeTree = getNodeTree();

        final SceneLayerModelNode modelNode = (SceneLayerModelNode) getNode();
        final SceneLayer layer = modelNode.getElement();

        final ModelNode<?> parent = Objects.requireNonNull(modelNode.getParent(), "The layer doesn't have a parent.");
        final SceneNode sceneNode = (SceneNode) parent.getElement();

        final ChangeConsumer changeConsumer = requireNonNull(nodeTree.getChangeConsumer());
        changeConsumer.execute(new RemoveSceneLayerOperation(layer, sceneNode));
    }
}
