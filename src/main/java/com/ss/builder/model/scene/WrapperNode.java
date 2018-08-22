package com.ss.builder.model.scene;

import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FromAnyThread;
import org.jetbrains.annotations.NotNull;

/**
 * The interface to mark some object about that the object has another object to update.
 *
 * @author JavaSaBr
 */
public interface WrapperNode {

    @FromAnyThread
    @NotNull Object getWrappedObject();
}
