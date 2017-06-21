package com.ss.editor.ui.control.model.tree.action.geometry;

import com.jme3.scene.Geometry;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.model.tree.dialog.geometry.lod.GenerateLodLevelsDialog;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import com.ss.editor.ui.control.model.node.spatial.GeometryModelNode;
import com.ss.editor.ui.scene.EditorFXScene;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;
import com.ss.rlib.util.ClassUtils;

/**
 * The action to generate levels of details for the geometry.
 *
 * @author JavaSaBr
 */
public class GenerateLoDAction extends AbstractNodeAction<ModelChangeConsumer> {

    /**
     * Instantiates a new Generate lo d action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public GenerateLoDAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.MESH_16;
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_LOD_GENERATOR;
    }

    @FXThread
    @Override
    protected void process() {
        final EditorFXScene scene = JFX_APPLICATION.getScene();
        final GeometryModelNode<Geometry> modelNode = ClassUtils.unsafeCast(getNode());
        final Geometry geometry = modelNode.getElement();
        final GenerateLodLevelsDialog dialog = new GenerateLodLevelsDialog(getNodeTree(), geometry);
        dialog.show(scene.getWindow());
    }
}
