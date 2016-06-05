package com.ss.editor.ui.control.model.tree.action.operation;

import com.jme3.light.Light;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;
import com.ss.editor.util.GeomUtils;

/**
 * Реализация операции по удалению источника света.
 *
 * @author Ronn
 */
public class RemoveLightOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    /**
     * Удаляемый источник света.
     */
    private final Light light;

    /**
     * Индекс родительского элемента.
     */
    private final int index;

    public RemoveLightOperation(final Light light, final int index) {
        this.light = light;
        this.index = index;
    }

    @Override
    protected void redoImpl(final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Spatial currentModel = editor.getCurrentModel();
            final Object parent = GeomUtils.getObjectByIndex(currentModel, index);
            if (!(parent instanceof Node)) return;

            final Node node = (Node) parent;
            node.removeLight(light);

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyRemovedLight(node, light));
        });
    }

    @Override
    protected void undoImpl(final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Spatial currentModel = editor.getCurrentModel();
            final Object parent = GeomUtils.getObjectByIndex(currentModel, index);
            if (!(parent instanceof Node)) return;

            final Node node = (Node) parent;
            node.addLight(light);

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyAddedLight(node, light));
        });
    }
}
