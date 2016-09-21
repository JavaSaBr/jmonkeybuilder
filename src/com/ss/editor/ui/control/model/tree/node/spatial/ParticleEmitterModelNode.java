package com.ss.editor.ui.control.model.tree.node.spatial;

import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.emitter.BoxShapeEmitterAction;
import com.ss.editor.ui.control.model.tree.action.emitter.CylinderShapeEmitterAction;
import com.ss.editor.ui.control.model.tree.action.emitter.DomeShapeEmitterAction;
import com.ss.editor.ui.control.model.tree.action.emitter.ModelShapeEmitterAction;
import com.ss.editor.ui.control.model.tree.action.emitter.QuadShapeEmitterAction;
import com.ss.editor.ui.control.model.tree.action.emitter.SphereShapeEmitterAction;
import com.ss.editor.ui.control.model.tree.action.emitter.TorusShapeEmitterAction;
import com.ss.editor.ui.control.model.tree.action.emitter.TriangleShapeEmitterAction;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import tonegod.emitter.ParticleEmitterNode;

/**
 * The implementation of the {@link NodeModelNode} for representing the {@link ParticleEmitterNode}
 * in the editor.
 *
 * @author JavaSaBr
 */
public class ParticleEmitterModelNode extends NodeModelNode<ParticleEmitterNode> {

    public ParticleEmitterModelNode(final ParticleEmitterNode element, final long objectId) {
        super(element, objectId);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.PARTICLES_16;
    }

    @Override
    public void fillContextMenu(@NotNull final ModelNodeTree nodeTree, @NotNull final ObservableList<MenuItem> items) {

        final Menu jmePrimitivesMenu = new Menu(Messages.MODEL_NODE_TREE_ACTION_CREATE_PRIMITIVE);
        final ObservableList<MenuItem> primitivesItems = jmePrimitivesMenu.getItems();
        primitivesItems.add(new BoxShapeEmitterAction(nodeTree, this));
        primitivesItems.add(new CylinderShapeEmitterAction(nodeTree, this));
        primitivesItems.add(new DomeShapeEmitterAction(nodeTree, this));
        primitivesItems.add(new QuadShapeEmitterAction(nodeTree, this));
        primitivesItems.add(new SphereShapeEmitterAction(nodeTree, this));
        primitivesItems.add(new TorusShapeEmitterAction(nodeTree, this));

        final Menu changeShapeMenu = new Menu(Messages.MODEL_NODE_TREE_ACTION_EMITTER_CHANGE_SHAPE);
        changeShapeMenu.getItems().addAll(new TriangleShapeEmitterAction(nodeTree, this), jmePrimitivesMenu, new ModelShapeEmitterAction(nodeTree, this));

        items.add(changeShapeMenu);

        super.fillContextMenu(nodeTree, items);
    }
}
