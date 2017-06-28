package com.ss.editor.ui.control.model.tree.action.scene;

import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.layer.LayersRoot;
import com.ss.editor.ui.control.layer.node.LayersRootModelNode;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.model.tree.action.operation.scene.AddSceneLayerOperation;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import com.ss.editor.extension.scene.SceneLayer;
import com.ss.editor.extension.scene.SceneNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;

/**
 * The action to create a scene layer.
 *
 * @author JavaSaBr
 */
public class CreateSceneLayerAction extends AbstractNodeAction<ModelChangeConsumer> {

    /**
     * Instantiates a new Create scene layer action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public CreateSceneLayerAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
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

    @FXThread
    @Override
    protected void process() {
        super.process();

        final SceneLayer layer = new SceneLayer("New Layer", false);
        final LayersRootModelNode modelNode = (LayersRootModelNode) getNode();
        final LayersRoot element = modelNode.getElement();
        final SceneChangeConsumer changeConsumer = element.getChangeConsumer();
        final SceneNode sceneNode = changeConsumer.getCurrentModel();

        changeConsumer.execute(new AddSceneLayerOperation(element, layer, sceneNode));
    }
}
