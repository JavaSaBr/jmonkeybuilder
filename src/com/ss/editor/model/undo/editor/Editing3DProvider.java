package com.ss.editor.model.undo.editor;

import com.jme3.scene.Node;
import com.ss.editor.annotation.EditorThread;
import org.jetbrains.annotations.NotNull;

/**
 * The interface to implement a provider of things to editing 3D.
 *
 * @author JavaSaBr
 */
public interface Editing3DProvider {

    /**
     * Get a cursor node in 3D editor.
     *
     * @return the cursor node.
     */
    @NotNull
    @EditorThread
    Node getCursorNode();

    /**
     * Get a node to place some markers in 3D editor.
     *
     * @return the markers node.
     */
    @NotNull
    @EditorThread
    Node getMarkersNode();
}
