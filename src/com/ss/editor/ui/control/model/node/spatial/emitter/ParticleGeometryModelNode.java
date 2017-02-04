package com.ss.editor.ui.control.model.node.spatial.emitter;

import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.node.spatial.GeometryModelNode;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.emitter.mesh.CreateImpostorParticleMeshAction;
import com.ss.editor.ui.control.model.tree.action.emitter.mesh.CreatePointParticleMeshAction;
import com.ss.editor.ui.control.model.tree.action.emitter.mesh.CreateQuadParticleMeshAction;
import com.ss.editor.ui.control.model.tree.action.emitter.mesh.LoadModelParticlesMeshAction;
import com.ss.editor.ui.control.tree.AbstractNodeTree;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import tonegod.emitter.geometry.ParticleGeometry;

/**
 * The implementation of the {@link GeometryModelNode} for representing the {@link ParticleGeometry} in the editor.
 *
 * @author JavaSaBr
 */
public class ParticleGeometryModelNode extends GeometryModelNode<ParticleGeometry> {

    public ParticleGeometryModelNode(@NotNull final ParticleGeometry element, final long objectId) {
        super(element, objectId);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.PARTICLE_GEOMETRY_16;
    }

    @Override
    public void fillContextMenu(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ObservableList<MenuItem> items) {
        if (!(nodeTree instanceof ModelNodeTree)) return;

        final Menu changeMeshMenu = new Menu(Messages.MODEL_NODE_TREE_ACTION_EMITTER_CHANGE_PARTICLES_MESH, new ImageView(Icons.MESH_16));
        final ObservableList<MenuItem> subItems = changeMeshMenu.getItems();
        subItems.add(new CreateQuadParticleMeshAction(nodeTree, this));
        subItems.add(new CreatePointParticleMeshAction(nodeTree, this));
        subItems.add(new CreateImpostorParticleMeshAction(nodeTree, this));
        subItems.add(new LoadModelParticlesMeshAction(nodeTree, this));

        items.add(changeMeshMenu);

        super.fillContextMenu(nodeTree, items);
    }

    @Override
    public boolean canRemove() {
        return false;
    }

    @Override
    public boolean canMove() {
        return false;
    }

    @Override
    public boolean canEditName() {
        return false;
    }
}
