package com.ss.editor.ui.control.model.tree.node.spatial;

import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.emitter.JMEBoxShapeEmitterAction;
import com.ss.editor.ui.control.model.tree.action.emitter.JMECylinderShapeEmitterAction;
import com.ss.editor.ui.control.model.tree.action.emitter.JMEDomeShapeEmitterAction;
import com.ss.editor.ui.control.model.tree.action.emitter.JMEQuadShapeEmitterAction;
import com.ss.editor.ui.control.model.tree.action.emitter.JMESphereShapeEmitterAction;
import com.ss.editor.ui.control.model.tree.action.emitter.JMETorusShapeEmitterAction;
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

        final Menu jmePrimitivesMenu = new Menu("jME Primitives");
        final ObservableList<MenuItem> primitiviesItems = jmePrimitivesMenu.getItems();
        primitiviesItems.add(new JMEBoxShapeEmitterAction(nodeTree, this));
        primitiviesItems.add(new JMECylinderShapeEmitterAction(nodeTree, this));
        primitiviesItems.add(new JMEDomeShapeEmitterAction(nodeTree, this));
        primitiviesItems.add(new JMEQuadShapeEmitterAction(nodeTree, this));
        primitiviesItems.add(new JMESphereShapeEmitterAction(nodeTree, this));
        primitiviesItems.add(new JMETorusShapeEmitterAction(nodeTree, this));

        final Menu changeShapeMenu = new Menu("Change Shape");
        changeShapeMenu.getItems().addAll(new TriangleShapeEmitterAction(nodeTree, this), jmePrimitivesMenu);

        items.add(changeShapeMenu);

        super.fillContextMenu(nodeTree, items);
    }
}
