package com.ss.editor.ui.control.model.tree.action.operation;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;
import com.ss.editor.util.GeomUtils;

/**
 * Реализация операции по перемещению узла.
 *
 * @author Ronn
 */
public class MoveChildOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    /**
     * Новый дочерний элемент.
     */
    private final Spatial moved;

    /**
     * Порядок размещения в предыдущем родителе.
     */
    private final int childIndex;

    /**
     * Индекс старого родительского узла.
     */
    private int oldParentIndex;

    /**
     * Индекс нового родительского узла.
     */
    private int newParentIndex;

    public MoveChildOperation(final Spatial moved, final int oldParentIndex, final int newParentIndex, final int childIndex) {
        this.moved = moved;
        this.oldParentIndex = oldParentIndex;
        this.newParentIndex = newParentIndex;
        this.childIndex = childIndex;
    }

    @Override
    protected void redoImpl(final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Spatial currentModel = editor.getCurrentModel();
            final Object oldParent = GeomUtils.getObjectByIndex(currentModel, oldParentIndex);
            final Object newParent = GeomUtils.getObjectByIndex(currentModel, newParentIndex);

            if (!(oldParent instanceof Node && newParent instanceof Node)) {
                return;
            }

            final Node node = (Node) newParent;
            node.attachChildAt(moved, 0);

            oldParentIndex = GeomUtils.getIndex(currentModel, oldParent);
            newParentIndex = GeomUtils.getIndex(currentModel, newParent);

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyMoved((Node) oldParent, node, moved, 0));
        });
    }

    @Override
    protected void undoImpl(final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Spatial currentModel = editor.getCurrentModel();
            final Object oldParent = GeomUtils.getObjectByIndex(currentModel, oldParentIndex);
            final Object newParent = GeomUtils.getObjectByIndex(currentModel, newParentIndex);

            if (!(oldParent instanceof Node && newParent instanceof Node)) {
                return;
            }

            final Node node = (Node) oldParent;
            node.attachChildAt(moved, childIndex);

            oldParentIndex = GeomUtils.getIndex(currentModel, oldParent);
            newParentIndex = GeomUtils.getIndex(currentModel, newParent);

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyMoved((Node) newParent, node, moved, childIndex));
        });
    }
}
