package com.ss.editor.ui.control.model.node.spatial;

import static com.ss.editor.ui.control.tree.node.ModelNodeFactory.createFor;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.CreateNodeAction;
import com.ss.editor.ui.control.model.tree.action.CreateSkyAction;
import com.ss.editor.ui.control.model.tree.action.LoadModelAction;
import com.ss.editor.ui.control.model.tree.action.OptimizeGeometryAction;
import com.ss.editor.ui.control.model.tree.action.audio.CreateAudioNodeAction;
import com.ss.editor.ui.control.model.tree.action.emitter.CreateTonegodEmitterAction;
import com.ss.editor.ui.control.model.tree.action.emitter.CreateTonegodSoftEmitterAction;
import com.ss.editor.ui.control.model.tree.action.geometry.CreateBoxAction;
import com.ss.editor.ui.control.model.tree.action.geometry.CreateQuadAction;
import com.ss.editor.ui.control.model.tree.action.geometry.CreateSphereAction;
import com.ss.editor.ui.control.model.tree.action.light.CreateAmbientLightAction;
import com.ss.editor.ui.control.model.tree.action.light.CreateDirectionLightAction;
import com.ss.editor.ui.control.model.tree.action.light.CreatePointLightAction;
import com.ss.editor.ui.control.model.tree.action.light.CreateSpotLightAction;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import com.ss.editor.util.GeomUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The implementation of the {@link SpatialModelNode} for representing the {@link Node} in the editor.
 *
 * @author JavaSaBr
 */
public class NodeModelNode<T extends Node> extends SpatialModelNode<T> {

    public NodeModelNode(@NotNull final T element, final long objectId) {
        super(element, objectId);
    }

    @Override
    public void fillContextMenu(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ObservableList<MenuItem> items) {

        final Menu toolMenu = createToolMenu(nodeTree);
        final Menu createMenu = createCreationMenu(nodeTree);

        if (toolMenu != null) {
            items.add(toolMenu);
        }

        if (createMenu != null) {
            items.add(createMenu);
        }

        items.add(new LoadModelAction(nodeTree, this));

        super.fillContextMenu(nodeTree, items);
    }

    @Nullable
    protected Menu createToolMenu(final @NotNull AbstractNodeTree<?> nodeTree) {
        final Menu toolMenu = new Menu(Messages.MODEL_NODE_TREE_ACTION_TOOLS, new ImageView(Icons.INFLUENCER_16));
        toolMenu.getItems().addAll(new OptimizeGeometryAction(nodeTree, this));
        return toolMenu;
    }

    @Nullable
    @Override
    protected Menu createCreationMenu(@NotNull final AbstractNodeTree<?> nodeTree) {

        final Menu menu = super.createCreationMenu(nodeTree);
        if (menu == null) return null;

        final Menu createPrimitiveMenu = new Menu(Messages.MODEL_NODE_TREE_ACTION_CREATE_PRIMITIVE, new ImageView(Icons.ADD_18));
        createPrimitiveMenu.getItems().addAll(new CreateBoxAction(nodeTree, this),
                new CreateSphereAction(nodeTree, this),
                new CreateQuadAction(nodeTree, this));

        final Menu addLightMenu = new Menu(Messages.MODEL_NODE_TREE_ACTION_LIGHT, new ImageView(Icons.ADD_18));
        addLightMenu.getItems().addAll(new CreateSpotLightAction(nodeTree, this),
                new CreatePointLightAction(nodeTree, this),
                new CreateAmbientLightAction(nodeTree, this),
                new CreateDirectionLightAction(nodeTree, this));

        menu.getItems().addAll(new CreateNodeAction(nodeTree, this),
                new CreateSkyAction(nodeTree, this),
                new CreateTonegodEmitterAction(nodeTree, this),
                new CreateTonegodSoftEmitterAction(nodeTree, this),
                new CreateAudioNodeAction(nodeTree, this),
                createPrimitiveMenu, addLightMenu);

        return menu;
    }

    @Override
    public boolean hasChildren() {
        return true;
    }

    @NotNull
    @Override
    public Array<ModelNode<?>> getChildren() {

        final Array<ModelNode<?>> result = ArrayFactory.newArray(ModelNode.class);
        final List<Spatial> children = getSpatials();
        children.forEach(spatial -> result.add(createFor(spatial)));
        result.addAll(super.getChildren());

        return result;
    }

    @NotNull
    protected List<Spatial> getSpatials() {
        final Node element = getElement();
        return element.getChildren();
    }

    @Override
    public boolean canAccept(@NotNull final ModelNode<?> child) {
        if (child == this) return false;
        final Object element = child.getElement();
        return element instanceof Spatial && GeomUtils.canAttach(getElement(), (Spatial) element);
    }

    @Override
    public void add(@NotNull final ModelNode<?> child) {
        super.add(child);

        final Node node = getElement();

        if (child instanceof SpatialModelNode) {
            final Spatial element = (Spatial) child.getElement();
            node.attachChildAt(element, 0);
        }
    }

    @Override
    public void remove(@NotNull final ModelNode<?> child) {
        super.remove(child);

        final Node node = getElement();

        if (child instanceof SpatialModelNode) {
            final Spatial element = (Spatial) child.getElement();
            node.detachChild(element);
        }
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.NODE_16;
    }
}
