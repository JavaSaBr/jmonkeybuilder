package com.ss.editor.ui.control.model.tree.action.terrain;

import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.model.tree.dialog.terrain.CreateTerrainDialog;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import com.ss.editor.ui.scene.EditorFXScene;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to create terrain.
 *
 * @author JavaSaBr
 */
public class CreateTerrainAction extends AbstractNodeAction<ModelChangeConsumer> {

    /**
     * Instantiates a new Create terrain action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public CreateTerrainAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.TERRAIN_16;
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_ADD_TERRAIN;
    }

    @FXThread
    @Override
    protected void process() {
        super.process();

        final EditorFXScene scene = JFX_APPLICATION.getScene();

        final CreateTerrainDialog dialog = new CreateTerrainDialog(getNode(), getNodeTree());
        dialog.show(scene.getWindow());
    }
}
