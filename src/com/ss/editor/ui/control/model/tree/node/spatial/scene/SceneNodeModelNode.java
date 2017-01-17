package com.ss.editor.ui.control.model.tree.node.spatial.scene;

import static com.ss.editor.ui.control.model.tree.node.ModelNodeFactory.createFor;

import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.RenameNodeAction;
import com.ss.editor.ui.control.model.tree.action.scene.CreateSceneLayerAction;
import com.ss.editor.ui.control.model.tree.node.ModelNode;
import com.ss.editor.ui.control.model.tree.node.spatial.NodeModelNode;
import com.ss.extension.scene.SceneLayer;
import com.ss.extension.scene.SceneNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

        final Menu createMenu = createCreationMenu(nodeTree);

        items.add(createMenu);
        items.add(new RenameNodeAction(nodeTree, this));
    }

    @Nullable
    @Override
    protected Menu createCreationMenu(@NotNull final ModelNodeTree nodeTree) {

        final Menu menu = new Menu(Messages.MODEL_NODE_TREE_ACTION_CREATE, new ImageView(Icons.ADD_18));
        menu.getItems().add(new CreateSceneLayerAction(nodeTree, this));

        return menu;
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.SCENE_16;
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
