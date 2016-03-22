package com.ss.editor.ui.control.model.tree.action.operation;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;
import com.ss.editor.util.GeomUtils;

/**
 * Реализация операции по удалению дочернего узла.
 *
 * @author Ronn
 */
public class RemoveChildOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    /**
     * Удаляемый дочерний элемент.
     */
    private final Spatial removeChild;

    /**
     * Индекс родительского элемента.
     */
    private final int index;

    /**
     * Прядок в родительском элементе.
     */
    private final int childIndex;

    public RemoveChildOperation(final Spatial removeChild, final int index) {
        final Node parent = removeChild.getParent();
        this.removeChild = removeChild;
        this.index = index;
        this.childIndex = parent.getChildIndex(removeChild);
    }

    @Override
    protected void redoImpl(final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Spatial currentModel = editor.getCurrentModel();
            final Object parent = GeomUtils.getObjectByIndex(currentModel, index);

            if (!(parent instanceof Node)) {
                return;
            }

            final Node node = (Node) parent;
            node.detachChild(removeChild);

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyRemovedChild(node, removeChild));
        });
    }

    @Override
    protected void undoImpl(final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Spatial currentModel = editor.getCurrentModel();
            final Object parent = GeomUtils.getObjectByIndex(currentModel, index);

            if (!(parent instanceof Node)) {
                return;
            }

            final Node node = (Node) parent;
            node.attachChildAt(removeChild, childIndex);

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyAddedChild(node, removeChild));
        });
    }
}
