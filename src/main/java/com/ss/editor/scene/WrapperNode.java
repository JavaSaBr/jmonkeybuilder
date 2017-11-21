package com.ss.editor.scene;

import org.jetbrains.annotations.NotNull;

/**
 * The interface to mark some object about that the object has another object to update.
 *
 * @author JavaSaBr
 */
public interface WrapperNode {

    @NotNull Object getWrappedObject();
}
