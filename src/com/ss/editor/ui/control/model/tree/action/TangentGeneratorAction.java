package com.ss.editor.ui.control.model.tree.action;

import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.dialog.GenerateTangentsDialog;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import com.ss.editor.ui.scene.EditorFXScene;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;

/**
 * The action for generating tangents.
 *
 * @author JavaSaBr
 */
public class TangentGeneratorAction extends AbstractNodeAction<ModelChangeConsumer> {

    /**
     * Instantiates a new Tangent generator action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public TangentGeneratorAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_TANGENT_GENERATOR;
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.MESH_16;
    }

    @FXThread
    @Override
    protected void process() {
        super.process();

        final EditorFXScene scene = JFX_APPLICATION.getScene();
        final GenerateTangentsDialog dialog = new GenerateTangentsDialog(getNodeTree(), getNode());
        dialog.show(scene.getWindow());
    }
}
