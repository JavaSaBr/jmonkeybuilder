package com.ss.editor.model.undo.impl;

import static com.ss.editor.model.undo.impl.RenameNodeOperation.PROPERTY_NAME;
import com.jme3.light.Light;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import org.jetbrains.annotations.NotNull;

/**
 * The operation to rename light.
 */
public class RenameLightOperation extends AbstractEditorOperation<ModelChangeConsumer> {

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

    public RenameLightOperation(@NotNull String oldName, @NotNull String newName, @NotNull Light light) {
        this.oldName = oldName;
        this.newName = newName;
        this.light = light;
    }

    @Override
    protected void redoInFx(@NotNull ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addJmeTask(() -> {
            light.setName(newName);
            EXECUTOR_MANAGER.addFxTask(() -> editor.notifyFxChangeProperty(light, PROPERTY_NAME));
        });
    }

    @Override
    protected void undoImpl(@NotNull ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addJmeTask(() -> {
            light.setName(oldName);
            EXECUTOR_MANAGER.addFxTask(() -> editor.notifyFxChangeProperty(light, PROPERTY_NAME));
        });
    }
}
