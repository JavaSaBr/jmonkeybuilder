package com.ss.editor.ui.control.model.tree.action;

import com.ss.editor.Messages;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.node.ModelNode;
import com.ss.editor.ui.dialog.model.GenerateTangentsDialog;
import com.ss.editor.ui.scene.EditorFXScene;

/**
 * Реализация действия по генерации тангетов используя старый алгоритм.
 *
 * @author Ronn
 */
public class TangentGeneratorAction extends AbstractNodeAction {

    public TangentGeneratorAction(final ModelNodeTree nodeTree, final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_TANGENT_GENERATOR;
    }

    @Override
    protected void process() {

        final EditorFXScene scene = EDITOR.getScene();

        final GenerateTangentsDialog dialog = new GenerateTangentsDialog(getNodeTree(), getNode());
        dialog.show(scene.getWindow());
    }
}
