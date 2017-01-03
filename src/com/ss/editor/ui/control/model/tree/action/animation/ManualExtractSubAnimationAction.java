package com.ss.editor.ui.control.model.tree.action.animation;

import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.model.tree.dialog.animation.ExtractSubAnimationDialog;
import com.ss.editor.ui.control.model.tree.node.ModelNode;
import com.ss.editor.ui.control.model.tree.node.control.anim.AnimationModelNode;
import com.ss.editor.ui.scene.EditorFXScene;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;

/**
 * The action to manual extract a sub-animation from an animation.
 *
 * @author JavaSaBr
 */
public class ManualExtractSubAnimationAction extends AbstractNodeAction {

    public ManualExtractSubAnimationAction(final ModelNodeTree nodeTree, final ModelNode<?> node) {
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

    @Override
    protected void process() {

        final EditorFXScene scene = JFX_APPLICATION.getScene();

        final ExtractSubAnimationDialog dialog = new ExtractSubAnimationDialog(getNodeTree(), (AnimationModelNode) getNode());
        dialog.show(scene.getWindow());
    }
}
