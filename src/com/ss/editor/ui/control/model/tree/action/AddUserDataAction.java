package com.ss.editor.ui.control.model.tree.action;

import com.jme3.scene.Spatial;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.dialog.AddUserDataDialog;
import com.ss.editor.ui.control.model.tree.node.ModelNode;
import com.ss.editor.ui.scene.EditorFXScene;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import javafx.scene.image.Image;

/**
 * The action to add a new user data.
 *
 * @author JavaSaBr
 */
public class AddUserDataAction extends AbstractNodeAction {

    public AddUserDataAction(@NotNull final ModelNodeTree nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_ADD_USER_DATA;
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.ADD_18;
    }

    @Override
    protected void process() {

        final ModelNode<?> node = getNode();
        final ModelNodeTree nodeTree = getNodeTree();
        final ModelChangeConsumer changeConsumer = Objects.requireNonNull(nodeTree.getModelChangeConsumer());
        final Object element = node.getElement();

        final EditorFXScene scene = JFX_APPLICATION.getScene();
        final AddUserDataDialog dialog = new AddUserDataDialog(changeConsumer, (Spatial) element);
        dialog.show(scene.getWindow());
    }
}
