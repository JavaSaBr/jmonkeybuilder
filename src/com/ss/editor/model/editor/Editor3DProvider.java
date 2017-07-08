package com.ss.editor.model.editor;

import com.jme3.scene.Node;
import com.ss.editor.annotation.JMEThread;
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
    @NotNull
    @JMEThread
    Node getCursorNode();

    /**
     * Get a node to place some markers in 3D editor.
     *
     * @return the markers node.
     */
    @NotNull
    @JMEThread
    Node getMarkersNode();
}
