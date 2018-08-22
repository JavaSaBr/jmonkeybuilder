package com.ss.builder.editor.part3d.event;

import com.ss.editor.annotation.FromAnyThread;
import org.jetbrains.annotations.NotNull;

/**
 * The interface to implement an event in editor 3d parts.
 *
 * @author Alex Brui
 */
public interface Editor3dPartEvent {

    @FromAnyThread
    @NotNull Object getSource();
}
