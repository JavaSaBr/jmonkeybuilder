package com.ss.editor.ui.control.model.tree.action.scene;

import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.extension.scene.SceneLayer;
import com.ss.editor.extension.scene.SceneNode;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.layer.LayersRoot;
import com.ss.editor.ui.control.layer.node.LayersRootTreeNode;
import com.ss.editor.ui.control.model.tree.action.operation.scene.AddSceneLayerOperation;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.tree.node.TreeNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to create a scene layer.
 *
 * @author JavaSaBr
 */
public class CreateSceneLayerAction extends AbstractNodeAction<ModelChangeConsumer> {

    public CreateSceneLayerAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.LAYERS_16;
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_CREATE_LAYER;
    }

    @Override
    @FxThread
    protected void process() {
        super.process();

        final SceneLayer layer = new SceneLayer("New Layer", false);
        final LayersRootTreeNode modelNode = (LayersRootTreeNode) getNode();
        final LayersRoot element = modelNode.getElement();
        final SceneChangeConsumer changeConsumer = element.getChangeConsumer();
        final SceneNode sceneNode = changeConsumer.getCurrentModel();

        changeConsumer.execute(new AddSceneLayerOperation(element, layer, sceneNode));
    }
}
