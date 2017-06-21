package com.ss.editor.ui.control.model.node.spatial.emitter;

import static com.ss.editor.ui.control.tree.node.ModelNodeFactory.createFor;
import com.ss.editor.Messages;
import com.ss.editor.model.node.ParticleInfluencers;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.node.spatial.NodeModelNode;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.emitter.ResetToneg0dParticleEmittersAction;
import com.ss.editor.ui.control.model.tree.action.emitter.mesh.CreateImpostorParticleMeshAction;
import com.ss.editor.ui.control.model.tree.action.emitter.mesh.CreatePointParticleMeshAction;
import com.ss.editor.ui.control.model.tree.action.emitter.mesh.CreateQuadParticleMeshAction;
import com.ss.editor.ui.control.model.tree.action.emitter.mesh.LoadModelParticlesMeshAction;
import com.ss.editor.ui.control.model.tree.action.emitter.shape.*;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import tonegod.emitter.ParticleEmitterNode;

/**
 * The implementation of the {@link NodeModelNode} to represent the {@link ParticleEmitterNode} in the editor.
 *
 * @author JavaSaBr
 */
public class ParticleEmitterNodeModelNode extends NodeModelNode<ParticleEmitterNode> {

    /**
     * Instantiates a new Particle emitter node model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public ParticleEmitterNodeModelNode(@NotNull final ParticleEmitterNode element, final long objectId) {
        super(element, objectId);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.PARTICLES_16;
    }

    @Nullable
    @Override
    protected Menu createToolMenu(@NotNull final AbstractNodeTree<?> nodeTree) {
        return null;
    }

    @Nullable
    @Override
    protected Menu createCreationMenu(@NotNull final AbstractNodeTree<?> nodeTree) {
        return null;
    }

    @Override
    public void fillContextMenu(@NotNull final AbstractNodeTree<?> nodeTree,
                                @NotNull final ObservableList<MenuItem> items) {
        if (!(nodeTree instanceof ModelNodeTree)) return;

        final Menu changeMeshMenu = new Menu(Messages.MODEL_NODE_TREE_ACTION_EMITTER_CHANGE_PARTICLES_MESH, new ImageView(Icons.MESH_16));
        final ObservableList<MenuItem> subItems = changeMeshMenu.getItems();
        subItems.add(new CreateQuadParticleMeshAction(nodeTree, this));
        subItems.add(new CreatePointParticleMeshAction(nodeTree, this));
        subItems.add(new CreateImpostorParticleMeshAction(nodeTree, this));
        subItems.add(new LoadModelParticlesMeshAction(nodeTree, this));

        final Menu jmePrimitivesMenu = new Menu(Messages.MODEL_NODE_TREE_ACTION_CREATE_PRIMITIVE, new ImageView(Icons.GEOMETRY_16));
        final ObservableList<MenuItem> primitivesItems = jmePrimitivesMenu.getItems();
        primitivesItems.add(new CreateBoxShapeEmitterAction(nodeTree, this));
        primitivesItems.add(new CreateCylinderShapeEmitterAction(nodeTree, this));
        primitivesItems.add(new CreateDomeShapeEmitterAction(nodeTree, this));
        primitivesItems.add(new CreateQuadShapeEmitterAction(nodeTree, this));
        primitivesItems.add(new CreateSphereShapeEmitterAction(nodeTree, this));
        primitivesItems.add(new CreateTorusShapeEmitterAction(nodeTree, this));

        final Menu changeShapeMenu = new Menu(Messages.MODEL_NODE_TREE_ACTION_EMITTER_CHANGE_SHAPE, new ImageView(Icons.GEOMETRY_16));
        changeShapeMenu.getItems().addAll(new CreateTriangleShapeEmitterAction(nodeTree, this),
                jmePrimitivesMenu,
                new LoadModelShapeEmitterAction(nodeTree, this));

        items.add(new ResetToneg0dParticleEmittersAction(nodeTree, this));
        items.add(changeShapeMenu);
        items.add(changeMeshMenu);

        super.fillContextMenu(nodeTree, items);
    }

    @NotNull
    @Override
    public Array<ModelNode<?>> getChildren(@NotNull final AbstractNodeTree<?> nodeTree) {
        final ParticleEmitterNode element = getElement();
        final Array<ModelNode<?>> children = ArrayFactory.newArray(ModelNode.class);
        children.add(createFor(new ParticleInfluencers(element)));
        return children;
    }
}
