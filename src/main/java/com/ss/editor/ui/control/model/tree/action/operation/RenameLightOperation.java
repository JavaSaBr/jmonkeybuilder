package com.ss.editor.ui.control.model.tree.action.operation;

import com.jme3.light.Light;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;
import org.jetbrains.annotations.NotNull;

/**
 * The operation to rename light.
 */
public class RenameLightOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    /**
     * The constant PROPERTY_NAME.
     */
    public static final String PROPERTY_NAME = "name";

    /**
     * The old name.
     */
    @NotNull
    private final String oldName;

    /**
     * The new name.
     */
    @NotNull
    private final String newName;

    /**
     * The node.
     */
    @NotNull
    private final Light light;

    public RenameLightOperation(@NotNull final String oldName, @NotNull final String newName, @NotNull final Light light) {
        this.oldName = oldName;
        this.newName = newName;
        this.light = light;
    }

    @Override
    protected void redoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addJmeTask(() -> {
            light.setName(newName);
            EXECUTOR_MANAGER.addFxTask(() -> editor.notifyFxChangeProperty(light, PROPERTY_NAME));
        });
    }

    @Override
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addJmeTask(() -> {
            light.setName(oldName);
            EXECUTOR_MANAGER.addFxTask(() -> editor.notifyFxChangeProperty(light, PROPERTY_NAME));
        });
    }
}
