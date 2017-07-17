package com.ss.editor.ui.control.model.tree.action.animation;

import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.model.tree.dialog.animation.ExtractSubAnimationDialog;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import com.ss.editor.ui.control.model.node.control.anim.AnimationModelNode;
import com.ss.editor.ui.scene.EditorFXScene;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;

/**
 * The action to manual extract a sub-animation from an animation.
 *
 * @author JavaSaBr
 */
public class ManualExtractSubAnimationAction extends AbstractNodeAction<ModelChangeConsumer> {

    /**
     * Instantiates a new Manual extract sub animation action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public ManualExtractSubAnimationAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_ANIMATION_MANUAL_EXTRAXT_SUB_ANIMATION;
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.EXTRACT_16;
    }

    @FXThread
    @Override
    protected void process() {
        super.process();
        
        final EditorFXScene scene = JFX_APPLICATION.getScene();
        final AbstractNodeTree<ModelChangeConsumer> nodeTree = getNodeTree();

        final ExtractSubAnimationDialog dialog = new ExtractSubAnimationDialog(nodeTree, (AnimationModelNode) getNode());
        dialog.show(scene.getWindow());
    }
}
