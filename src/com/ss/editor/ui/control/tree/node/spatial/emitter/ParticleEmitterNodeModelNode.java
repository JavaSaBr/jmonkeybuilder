package com.ss.editor.ui.control.tree.node.spatial.emitter;

import static com.ss.editor.ui.control.tree.node.ModelNodeFactory.createFor;

import com.jme3.scene.Spatial;
import com.ss.editor.Messages;
import com.ss.editor.model.node.ParticleInfluencers;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.emitter.shape.CreateBoxShapeEmitterAction;
import com.ss.editor.ui.control.model.tree.action.emitter.shape.CreateCylinderShapeEmitterAction;
import com.ss.editor.ui.control.model.tree.action.emitter.shape.CreateDomeShapeEmitterAction;
import com.ss.editor.ui.control.model.tree.action.emitter.shape.CreateQuadShapeEmitterAction;
import com.ss.editor.ui.control.model.tree.action.emitter.shape.CreateSphereShapeEmitterAction;
import com.ss.editor.ui.control.model.tree.action.emitter.shape.CreateTorusShapeEmitterAction;
import com.ss.editor.ui.control.model.tree.action.emitter.shape.CreateTriangleShapeEmitterAction;
import com.ss.editor.ui.control.model.tree.action.emitter.shape.LoadModelShapeEmitterAction;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import com.ss.editor.ui.control.tree.node.spatial.NodeModelNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;
import tonegod.emitter.EmitterMesh;
import tonegod.emitter.ParticleEmitterNode;

/**
 * The implementation of the {@link NodeModelNode} to represent the {@link ParticleEmitterNode} in the editor.
 *
 * @author JavaSaBr
 */
public class ParticleEmitterNodeModelNode extends NodeModelNode<ParticleEmitterNode> {

    public ParticleEmitterNodeModelNode(@NotNull final ParticleEmitterNode element, final long objectId) {
        super(element, objectId);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.PARTICLES_16;
    }

    @Override
    public void fillContextMenu(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ObservableList<MenuItem> items) {

        final Menu jmePrimitivesMenu = new Menu(Messages.MODEL_NODE_TREE_ACTION_CREATE_PRIMITIVE, new ImageView(Icons.ADD_18));
        final ObservableList<MenuItem> primitivesItems = jmePrimitivesMenu.getItems();
        primitivesItems.add(new CreateBoxShapeEmitterAction(nodeTree, this));
        primitivesItems.add(new CreateCylinderShapeEmitterAction(nodeTree, this));
        primitivesItems.add(new CreateDomeShapeEmitterAction(nodeTree, this));
        primitivesItems.add(new CreateQuadShapeEmitterAction(nodeTree, this));
        primitivesItems.add(new CreateSphereShapeEmitterAction(nodeTree, this));
        primitivesItems.add(new CreateTorusShapeEmitterAction(nodeTree, this));

        final Menu changeShapeMenu = new Menu(Messages.MODEL_NODE_TREE_ACTION_EMITTER_CHANGE_SHAPE, new ImageView(Icons.GEOMETRY_16));
        changeShapeMenu.getItems().addAll(new CreateTriangleShapeEmitterAction(nodeTree, this), jmePrimitivesMenu, new LoadModelShapeEmitterAction(nodeTree, this));

        items.add(changeShapeMenu);

        super.fillContextMenu(nodeTree, items);
    }

    @NotNull
    @Override
    protected List<Spatial> getSpatials() {
        final ParticleEmitterNode element = getElement();
        final List<Spatial> spatials = new ArrayList<>(super.getSpatials());
        spatials.remove(element.getEmitterTestNode());
        return spatials;
    }

    @NotNull
    @Override
    public Array<ModelNode<?>> getChildren() {
        final ParticleEmitterNode element = getElement();
        final EmitterMesh emitterShape = element.getEmitterShape();
        final Array<ModelNode<?>> children = ArrayFactory.newArray(ModelNode.class);
        children.add(createFor(new ParticleInfluencers(element)));
        children.add(createFor(emitterShape));
        children.addAll(super.getChildren());
        return children;
    }
}
