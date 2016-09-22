package com.ss.editor.ui.control.model.tree.node.spatial;

import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.emitter.ImpostorParticleMeshAction;
import com.ss.editor.ui.control.model.tree.action.emitter.PointParticleMeshAction;
import com.ss.editor.ui.control.model.tree.action.emitter.QuadParticleMeshAction;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import tonegod.emitter.geometry.ParticleGeometry;

/**
 * The implementation of the {@link GeometryModelNode} for representing the {@link ParticleGeometry}
 * in the editor.
 *
 * @author JavaSaBr
 */
public class ParticleGeometryModelNode extends GeometryModelNode<ParticleGeometry> {

    public ParticleGeometryModelNode(final ParticleGeometry element, final long objectId) {
        super(element, objectId);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.PARTICLE_GEOMETRY_16;
    }

    @Override
    public void fillContextMenu(@NotNull final ModelNodeTree nodeTree, @NotNull final ObservableList<MenuItem> items) {

        final Menu changeMeshMenu = new Menu("Change Mesh");
        final ObservableList<MenuItem> subItems = changeMeshMenu.getItems();
        subItems.add(new QuadParticleMeshAction(nodeTree, this));
        subItems.add(new PointParticleMeshAction(nodeTree, this));
        subItems.add(new ImpostorParticleMeshAction(nodeTree, this));

        items.add(changeMeshMenu);

        super.fillContextMenu(nodeTree, items);
    }

    @Override
    protected boolean canRemove() {
        return false;
    }
}
