package com.ss.editor.ui.control.model.tree.action.operation;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;
import com.ss.editor.util.GeomUtils;

import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link AbstractEditorOperation} for adding a control to a node.
 *
 * @author JavaSaBr
 */
public class AddControlOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    /**
     * The new control.
     */
    private final Control newControl;

    /**
     * The index of parent node.
     */
    private final int index;

    public AddControlOperation(@NotNull final Control newControl, final int index) {
        this.newControl = newControl;
        this.index = index;
    }

    @Override
    protected void redoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Spatial currentModel = editor.getCurrentModel();
            final Object parent = GeomUtils.getObjectByIndex(currentModel, index);
            if (!(parent instanceof Node)) return;

            final Node node = (Node) parent;
            node.addControl(newControl);

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyAddedControl(node, newControl, -1));
        });
    }

    @Override
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Spatial currentModel = editor.getCurrentModel();
            final Object parent = GeomUtils.getObjectByIndex(currentModel, index);
            if (!(parent instanceof Node)) return;

            final Node node = (Node) parent;
            node.removeControl(newControl);

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyRemovedControl(node, newControl));
        });
    }
}
