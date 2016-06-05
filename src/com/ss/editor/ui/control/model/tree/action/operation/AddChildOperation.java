package com.ss.editor.ui.control.model.tree.action.operation;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;
import com.ss.editor.util.GeomUtils;

/**
 * Реализация операции по добавлению нового дочернего узла.
 *
 * @author Ronn
 */
public class AddChildOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    /**
     * Новый дочерний элемент.
     */
    private final Spatial newChild;

    /**
     * Индекс родительского элемента.
     */
    private final int index;

    public AddChildOperation(final Spatial newChild, final int index) {
        this.newChild = newChild;
        this.index = index;
    }

    @Override
    protected void redoImpl(final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Spatial currentModel = editor.getCurrentModel();
            final Object parent = GeomUtils.getObjectByIndex(currentModel, index);
            if (!(parent instanceof Node)) return;

            final Node node = (Node) parent;
            node.attachChildAt(newChild, 0);

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyAddedChild(node, newChild));
        });
    }

    @Override
    protected void undoImpl(final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Spatial currentModel = editor.getCurrentModel();
            final Object parent = GeomUtils.getObjectByIndex(currentModel, index);
            if (!(parent instanceof Node)) return;

            final Node node = (Node) parent;
            node.detachChild(newChild);

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyRemovedChild(node, newChild));
        });
    }
}
