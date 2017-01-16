package com.ss.editor.ui.control.model.tree.node.spatial.scene;

import com.ss.editor.ui.control.model.tree.node.spatial.NodeModelNode;
import com.ss.extension.scene.SceneLayer;

import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link NodeModelNode} for representing the {@link SceneLayer} in the editor.
 *
 * @author JavaSaBr
 */
public class SceneLayerModelNode extends NodeModelNode<SceneLayer> {

    public SceneLayerModelNode(@NotNull final SceneLayer element, final long objectId) {
        super(element, objectId);
    }
}
