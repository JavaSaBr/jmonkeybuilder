package com.ss.editor.ui.control.model.tree.action;

import com.ss.editor.Messages;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.dialog.sky.CreateSkyDialog;
import com.ss.editor.ui.control.model.tree.node.ModelNode;
import com.ss.editor.ui.scene.EditorFXScene;

import org.jetbrains.annotations.NotNull;

/**
 * Действие по созданию нового фона.
 *
 * @author Ronn
 */
public class CreateSkyAction extends AbstractNodeAction {

    public CreateSkyAction(final ModelNodeTree nodeTree, final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_CREATE_SKY;
    }

    @Override
    protected void process() {

        final EditorFXScene scene = EDITOR.getScene();

        final CreateSkyDialog dialog = new CreateSkyDialog(getNode(), getNodeTree());
        dialog.show(scene.getWindow());
    }
}
