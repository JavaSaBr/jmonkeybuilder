package com.ss.editor.ui.control.layer;

import static java.util.Objects.requireNonNull;
import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.control.layer.node.SceneLayerModelNode;
import com.ss.editor.ui.control.model.property.operation.ModelPropertyOperation;
import com.ss.editor.ui.control.tree.AbstractNodeTreeCell;
import com.ss.editor.ui.control.tree.node.ModelNode;
import com.ss.editor.extension.scene.SceneLayer;
import javafx.scene.control.TreeItem;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of {@link AbstractNodeTreeCell} to show layer nodes.
 *
 * @author JavaSaBr
 */
public class LayerNodeTreeCell extends AbstractNodeTreeCell<SceneChangeConsumer, LayerNodeTree> {

    /**
     * Instantiates a new Layer node tree cell.
     *
     * @param nodeTree the node tree
     */
    LayerNodeTreeCell(@NotNull final LayerNodeTree nodeTree) {
        super(nodeTree);
    }

    @Override
    protected boolean processDragDropped(@NotNull final TreeItem<ModelNode<?>> dragTreeItem,
                                         @NotNull final ModelNode<?> dragItem, @NotNull final ModelNode<?> item,
                                         final boolean isCopy, @NotNull final TreeItem<ModelNode<?>> newParentItem,
                                         @NotNull final Object element) {

        final ModelNode<?> newParent = newParentItem.getValue();

        if (element instanceof Spatial && newParent instanceof SceneLayerModelNode) {

            final LayerNodeTree nodeTree = getNodeTree();
            final ModelChangeConsumer changeConsumer = requireNonNull(nodeTree.getChangeConsumer());

            final Spatial spatial = (Spatial) element;
            final SceneLayer targetLayer = ((SceneLayerModelNode) newParent).getElement();
            final SceneLayer currentLayer = SceneLayer.getLayer(spatial);

            final ModelPropertyOperation<Spatial, SceneLayer> operation =
                    new ModelPropertyOperation<>(spatial, SceneLayer.KEY, targetLayer, currentLayer);
            operation.setApplyHandler((sp, layer) -> SceneLayer.setLayer(layer, sp));

            changeConsumer.execute(operation);
            return false;
        }

        return super.processDragDropped(dragTreeItem, dragItem, item, isCopy, newParentItem, element);
    }
}
