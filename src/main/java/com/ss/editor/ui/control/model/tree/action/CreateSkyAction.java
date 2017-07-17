package com.ss.editor.ui.control.model.tree.action;

import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.dialog.sky.CreateSkyDialog;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import com.ss.editor.ui.scene.EditorFXScene;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;

/**
 * The action to create sky.
 *
 * @author JavaSaBr
 */
public class CreateSkyAction extends AbstractNodeAction<ModelChangeConsumer> {

    /**
     * Instantiates a new Create sky action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public CreateSkyAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.SKY_16;
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_CREATE_SKY;
    }

    @FXThread
    @Override
    protected void process() {
        super.process();

        final EditorFXScene scene = JFX_APPLICATION.getScene();

        final CreateSkyDialog dialog = new CreateSkyDialog(getNode(), getNodeTree());
        dialog.show(scene.getWindow());
    }
}
