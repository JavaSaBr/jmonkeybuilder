package com.ss.editor.ui.control.model.tree.node.spatial.scene;

import static com.ss.editor.ui.control.model.tree.node.ModelNodeFactory.createFor;

import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.RenameNodeAction;
import com.ss.editor.ui.control.model.tree.node.ModelNode;
import com.ss.editor.ui.control.model.tree.node.spatial.NodeModelNode;
import com.ss.extension.scene.SceneLayer;
import com.ss.extension.scene.SceneNode;

import org.jetbrains.annotations.NotNull;

import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The implementation of the {@link NodeModelNode} for representing the {@link SceneNode} in the editor.
 *
 * @author JavaSaBr
 */
public class SceneNodeModelNode extends NodeModelNode<SceneNode> {

    public SceneNodeModelNode(@NotNull final SceneNode element, final long objectId) {
        super(element, objectId);
    }

    @Override
    public void fillContextMenu(@NotNull final ModelNodeTree nodeTree, @NotNull final ObservableList<MenuItem> items) {
        items.add(new RenameNodeAction(nodeTree, this));
    }

    @NotNull
    @Override
    public Array<ModelNode<?>> getChildren() {

        final SceneNode sceneNode = getElement();
        final Array<SceneLayer> layers = sceneNode.getLayers();

        final Array<ModelNode<?>> result = ArrayFactory.newArray(ModelNode.class);
        layers.forEach(layer -> result.add(createFor(layer)));

        return result;
    }
}
