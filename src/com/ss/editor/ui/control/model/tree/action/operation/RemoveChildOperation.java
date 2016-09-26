package com.ss.editor.ui.control.model.tree.action.operation;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;
import com.ss.editor.util.GeomUtils;

import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link AbstractEditorOperation} for removing a {@link Spatial} from the
 * {@link Node}.
 *
 * @author JavaSaBr.
 */
public class RemoveChildOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    /**
     * The child to remove.
     */
    private final Spatial child;

    /**
     * The index of the parent element.
     */
    private final int index;

    /**
     * The index of position in the parent.
     */
    private final int childIndex;

    public RemoveChildOperation(final Spatial child, final int index) {
        final Node parent = child.getParent();
        this.child = child;
        this.index = index;
        this.childIndex = parent.getChildIndex(child);
    }

    @Override
    protected void redoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Spatial currentModel = editor.getCurrentModel();
            final Object parent = GeomUtils.getObjectByIndex(currentModel, index);
            if (!(parent instanceof Node)) return;

            final Node node = (Node) parent;
            node.detachChild(child);

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyRemovedChild(node, child));
        });
    }

    @Override
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Spatial currentModel = editor.getCurrentModel();
            final Object parent = GeomUtils.getObjectByIndex(currentModel, index);
            if (!(parent instanceof Node)) return;

            final Node node = (Node) parent;
            node.attachChildAt(child, childIndex);

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyAddedChild(node, child, childIndex));
        });
    }
}
