package com.ss.editor.ui.control.model.tree.action.operation;

import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;
import com.ss.editor.util.GeomUtils;

/**
 * Реализация операции по переименованию узла модели.
 *
 * @author Ronn
 */
public class RenameNodeOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    /**
     * Старое название узла.
     */
    private final String oldName;

    /**
     * Новое название узла.
     */
    private final String newName;

    /**
     * Индекс переименуемого узла.
     */
    private final int index;

    public RenameNodeOperation(final String oldName, final String newName, final int index) {
        this.oldName = oldName;
        this.newName = newName;
        this.index = index;
    }

    @Override
    protected void redoImpl(final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Spatial currentModel = editor.getCurrentModel();
            final Object object = GeomUtils.getObjectByIndex(currentModel, index);
            if (!(object instanceof Spatial)) return;

            final Spatial spatial = (Spatial) object;
            spatial.setName(newName);

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyChangeProperty(object, "name"));
        });
    }

    @Override
    protected void undoImpl(final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Spatial currentModel = editor.getCurrentModel();
            final Object object = GeomUtils.getObjectByIndex(currentModel, index);
            if (!(object instanceof Spatial)) return;

            final Spatial spatial = (Spatial) object;
            spatial.setName(oldName);

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyChangeProperty(object, "name"));
        });
    }
}
