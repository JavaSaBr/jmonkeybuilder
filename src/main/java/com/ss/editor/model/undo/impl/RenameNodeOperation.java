package com.ss.editor.model.undo.impl;

import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

/**
 * The operation to rename a node.
 *
 * @author JavaSaBr
 */
public class RenameNodeOperation extends RenameObjectOperation<Spatial> {

    public RenameNodeOperation(
            @NotNull String oldName,
            @NotNull String newName,
            @NotNull Spatial object
    ) {
        super(oldName, newName, object, Spatial::setName);
    }
}
