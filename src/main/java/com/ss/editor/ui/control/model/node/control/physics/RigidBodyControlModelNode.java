package com.ss.editor.ui.control.model.node.control.physics;

import com.jme3.bullet.control.RigidBodyControl;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.control.physics.ReactivatePhysicsControl;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of node to show {@link RigidBodyControl}.
 *
 * @author JavaSaBr
 */
public class RigidBodyControlModelNode extends PhysicsObjectModelNode<RigidBodyControl> {

    /**
     * Instantiates a new Rigid body control model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public RigidBodyControlModelNode(@NotNull final RigidBodyControl element, final long objectId) {
        super(element, objectId);
    }

    @Nullable
    @Override
    public Image getIcon() {

        final RigidBodyControl element = getElement();

        if (element.getMass() == 0F) {
            return Icons.STATIC_RIGID_BODY_16;
        }

        return Icons.RIGID_BODY_16;
    }

    @NotNull
    @Override
    public String getName() {

        final RigidBodyControl element = getElement();

        if (element.getMass() == 0F) {
            return Messages.MODEL_FILE_EDITOR_NODE_STATIC_RIGID_BODY_CONTROL;
        }

        return Messages.MODEL_FILE_EDITOR_NODE_RIGID_BODY_CONTROL;
    }

    @Override
    public void fillContextMenu(@NotNull final AbstractNodeTree<?> nodeTree,
                                @NotNull final ObservableList<MenuItem> items) {

        final RigidBodyControl element = getElement();

        if (!element.isActive()) {
            items.add(new ReactivatePhysicsControl(nodeTree, this));
        }

        super.fillContextMenu(nodeTree, items);
    }
}
