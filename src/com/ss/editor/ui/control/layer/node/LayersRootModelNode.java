package com.ss.editor.ui.control.layer.node;

import static com.ss.editor.ui.control.tree.node.ModelNodeFactory.createFor;

import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.control.layer.LayersRoot;
import com.ss.editor.ui.control.tree.node.ModelNode;
import com.ss.extension.scene.SceneLayer;
import com.ss.extension.scene.SceneNode;

import org.jetbrains.annotations.NotNull;

import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The implementation of {@link ModelNode} to present {@link LayersRoot}.
 *
 * @author JavaSaBr
 */
public class LayersRootModelNode extends ModelNode<LayersRoot> {

    public LayersRootModelNode(@NotNull final LayersRoot element, final long objectId) {
        super(element, objectId);
    }

    @NotNull
    @Override
    public Array<ModelNode<?>> getChildren() {

        final LayersRoot element = getElement();
        final SceneChangeConsumer changeConsumer = element.getChangeConsumer();
        final SceneNode sceneNode = changeConsumer.getCurrentModel();
        final Array<SceneLayer> layers = sceneNode.getLayers();

        final Array<ModelNode<?>> result = ArrayFactory.newArray(ModelNode.class);
        layers.forEach(layer -> result.add(createFor(layer)));

        return result;
    }
}
