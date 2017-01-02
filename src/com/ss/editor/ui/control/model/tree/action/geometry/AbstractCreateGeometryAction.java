package com.ss.editor.ui.control.model.tree.action.geometry;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.model.tree.action.operation.AddChildOperation;
import com.ss.editor.ui.control.model.tree.node.ModelNode;
import com.ss.editor.util.GeomUtils;

import org.jetbrains.annotations.NotNull;

/**
 * The action for creating the {@link Geometry}.
 *
 * @author JavaSaBr
 */
public abstract class AbstractCreateGeometryAction extends AbstractNodeAction {

    public AbstractCreateGeometryAction(@NotNull final ModelNodeTree nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    protected void process() {

        final ModelNodeTree nodeTree = getNodeTree();
        final ModelChangeConsumer modelChangeConsumer = nodeTree.getModelChangeConsumer();
        final AssetManager assetManager = EDITOR.getAssetManager();

        final Geometry geometry = createGeometry();
        geometry.setMaterial(new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md"));

        final ModelNode<?> modelNode = getNode();
        final Node element = (Node) modelNode.getElement();

        final int index = GeomUtils.getIndex(modelChangeConsumer.getCurrentModel(), element);

        modelChangeConsumer.execute(new AddChildOperation(geometry, index));
    }

    @NotNull
    protected abstract Geometry createGeometry();
}
