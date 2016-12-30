package com.ss.editor.ui.control.model.tree.action;

import com.ss.editor.Messages;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.node.ModelNode;
import com.ss.editor.ui.dialog.model.GenerateTangentsDialog;
import com.ss.editor.ui.scene.EditorFXScene;

import org.jetbrains.annotations.NotNull;

/**
 * The action for generating tangents.
 *
 * @author JavaSaBr
 */
public class TangentGeneratorAction extends AbstractNodeAction {

    public TangentGeneratorAction(final ModelNodeTree nodeTree, final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_TANGENT_GENERATOR;
    }

    @Override
    protected void process() {

        final EditorFXScene scene = JFX_APPLICATION.getScene();

        final GenerateTangentsDialog dialog = new GenerateTangentsDialog(getNodeTree(), getNode());
        dialog.show(scene.getWindow());
    }
}
