package com.ss.editor.ui.control.model.tree.action.operation;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;
import com.ss.editor.util.GeomUtils;

/**
 * Реализация операции по оптимизации геометрии.
 *
 * @author Ronn
 */
public class OptimizeGeometryOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    /**
     * Новый элемент.
     */
    private final Spatial newSpatial;

    /**
     * Старый элемент.
     */
    private final Spatial oldSpatial;

    /**
     * Индекс родительского элемента.
     */
    private final int index;

    public OptimizeGeometryOperation(final Spatial newSpatial, final Spatial oldSpatial, final int index) {
        this.newSpatial = newSpatial;
        this.oldSpatial = oldSpatial;
        this.index = index;
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
            final int index = node.getChildIndex(oldSpatial);
            node.detachChildAt(index);
            node.attachChildAt(newSpatial, index);

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyReplaced(node, oldSpatial, newSpatial));
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
            final int index = node.getChildIndex(newSpatial);
            node.detachChildAt(index);
            node.attachChildAt(oldSpatial, index);

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyReplaced(node, newSpatial, oldSpatial));
        });
    }
}
