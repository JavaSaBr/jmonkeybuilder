package com.ss.builder.model.editor;

import com.jme3.scene.Node;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.annotation.JmeThread;
import org.jetbrains.annotations.NotNull;

/**
 * The interface to implement a provider.
 *
 * @author JavaSaBr
 */
public interface Editor3DProvider {

    /**
     * Get a cursor node in 3D editor.
     *
     * @return the cursor node.
     */
    @JmeThread
    @NotNull Node getCursorNode();

    /**
     * Get a node to place some markers in 3D editor.
     *
     * @return the markers node.
     */
    @JmeThread
    @NotNull Node getMarkersNode();
}
