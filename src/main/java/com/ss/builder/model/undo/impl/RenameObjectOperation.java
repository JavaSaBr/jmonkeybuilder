package com.ss.builder.model.undo.impl;

import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

/**
 * The operation to rename light.
 */
public abstract class RenameObjectOperation<T> extends AbstractEditorOperation<ModelChangeConsumer> {

    public static final String PROPERTY_NAME = "name";

    @NotNull
    private final BiConsumer<@NotNull T, @NotNull String> nameSetter;

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
     * The object.
     */
    @NotNull
    private final T object;

    public RenameObjectOperation(
            @NotNull String oldName,
            @NotNull String newName,
            @NotNull T object,
            @NotNull BiConsumer<@NotNull T, @NotNull String> nameSetter
    ) {
        this.oldName = oldName;
        this.newName = newName;
        this.object = object;
        this.nameSetter = nameSetter;
    }

    @Override
    @JmeThread
    protected void redoInJme(@NotNull ModelChangeConsumer editor) {
        super.redoInJme(editor);
        nameSetter.accept(object, newName);
    }

    @Override
    @JmeThread
    protected void undoInJme(@NotNull ModelChangeConsumer editor) {
        super.undoInJme(editor);
        nameSetter.accept(object, oldName);
    }

    @Override
    @FxThread
    protected void endInFx(@NotNull ModelChangeConsumer editor) {
        super.endInFx(editor);
        editor.notifyFxChangeProperty(object, PROPERTY_NAME);
    }
}
