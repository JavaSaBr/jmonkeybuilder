package com.ss.editor.ui.control.model.tree.action.scene;

import static java.util.Objects.requireNonNull;

import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.model.tree.action.operation.scene.AddSceneLayerOperation;
import com.ss.editor.ui.control.model.tree.node.ModelNode;
import com.ss.editor.ui.control.model.tree.node.spatial.scene.SceneNodeModelNode;
import com.ss.extension.scene.SceneLayer;
import com.ss.extension.scene.SceneNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;

/**
 * The action to create a scene layer.
 *
 * @author JavaSaBr
 */
public class CreateSceneLayerAction extends AbstractNodeAction {

    public CreateSceneLayerAction(@NotNull final ModelNodeTree nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.LAYERS_16;
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_CREATE_LAYER;
    }

    @Override
    protected void process() {

        final ModelNodeTree nodeTree = getNodeTree();
        final ModelChangeConsumer consumer = requireNonNull(nodeTree.getModelChangeConsumer());

        final SceneLayer layer = new SceneLayer("New Layer", false);
        final SceneNodeModelNode modelNode = (SceneNodeModelNode) getNode();
        final SceneNode sceneNode = modelNode.getElement();

        consumer.execute(new AddSceneLayerOperation(layer, sceneNode));
    }
}
