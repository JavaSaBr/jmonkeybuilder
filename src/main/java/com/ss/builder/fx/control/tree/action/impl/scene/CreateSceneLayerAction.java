package com.ss.builder.fx.control.tree.action.impl.scene;

import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.node.layer.LayersRoot;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.model.undo.editor.SceneChangeConsumer;
import com.ss.builder.model.undo.impl.scene.AddSceneLayerOperation;
import com.ss.builder.fx.Icons;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.editor.extension.scene.SceneLayer;
import com.ss.editor.extension.scene.SceneNode;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.model.undo.editor.SceneChangeConsumer;
import com.ss.builder.fx.Icons;
import com.ss.builder.model.node.layer.LayersRoot;
import com.ss.builder.fx.control.tree.node.impl.layer.LayersRootTreeNode;
import com.ss.builder.model.undo.impl.scene.AddSceneLayerOperation;
import com.ss.builder.fx.control.tree.NodeTree;
import com.ss.builder.fx.control.tree.action.AbstractNodeAction;
import com.ss.builder.fx.control.tree.node.TreeNode;
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
