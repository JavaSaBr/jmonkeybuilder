package com.ss.editor.ui.control.model.tree.action.geometry;

import static com.ss.editor.util.EditorUtil.getDefaultLayer;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.extension.scene.SceneLayer;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.model.tree.action.operation.AddChildOperation;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.editor.util.EditorUtil;
import org.jetbrains.annotations.NotNull;

/**
 * The action to create the {@link Geometry}.
 *
 * @author JavaSaBr
 */
public abstract class AbstractCreateGeometryAction extends AbstractNodeAction<ModelChangeConsumer> {

    public AbstractCreateGeometryAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FxThread
    protected void process() {
        super.process();

        final NodeTree<ModelChangeConsumer> nodeTree = getNodeTree();
        final ModelChangeConsumer consumer = notNull(nodeTree.getChangeConsumer());
        final SceneLayer defaultLayer = getDefaultLayer(consumer);

        final AssetManager assetManager = EditorUtil.getAssetManager();
        final Geometry geometry = createGeometry();
        geometry.setMaterial(new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md"));

        final TreeNode<?> treeNode = getNode();
        final Node parent = (Node) treeNode.getElement();

        if (defaultLayer != null) {
            SceneLayer.setLayer(defaultLayer, geometry);
        }

        consumer.execute(new AddChildOperation(geometry, parent));
    }

    /**
     * Create geometry geometry.
     *
     * @return the geometry
     */
    @FxThread
    protected abstract @NotNull Geometry createGeometry();
}
