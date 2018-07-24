package com.ss.editor.model.undo.impl;

import com.jme3.light.Light;
import org.jetbrains.annotations.NotNull;

/**
 * The operation to rename light.
 *
 * @author JavaSaBr
 */
public class RenameLightOperation extends RenameObjectOperation<Light> {

    public RenameLightOperation(@NotNull String oldName, @NotNull String newName, @NotNull Light object) {
        super(oldName, newName, object, Light::setName);
    }
}
