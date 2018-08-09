package com.ss.editor.part3d.editor;

import com.jme3.scene.Node;
import com.ss.editor.annotation.JmeThread;
import org.jetbrains.annotations.NotNull;

/**
 * The interface to mark an editor 3d part that it supports editing a 3d scene.
 *
 * @author JavaSaBr
 */
public interface EditableSceneEditor3dPart extends Editor3dPart {

    String PROP_IS_EDITING = "EditableSceneEditor3dPart.isEditing";

    /**
     * Get a tool node.
     *
     * @return the tool node.
     */
    @JmeThread
    @NotNull Node getToolNode();
}
