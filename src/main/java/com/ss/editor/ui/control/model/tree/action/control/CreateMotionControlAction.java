package com.ss.editor.ui.control.model.tree.action.control;

import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to create a {@link MotionEvent}.
 *
 * @author JavaSaBr
 */
public class CreateMotionControlAction extends AbstractCreateControlAction {

    /**
     * Instantiates a new Create motion control action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public CreateMotionControlAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.GEAR_16;
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_ADD_CONTROL_NOTION;
    }

    @NotNull
    @Override
    protected Control createControl(@NotNull final Spatial parent) {

        final MotionPath motionPath = new MotionPath();
        motionPath.addWayPoint(Vector3f.ZERO.clone());
        motionPath.addWayPoint(new Vector3f(0f, 1f, 0f));
        motionPath.addWayPoint(new Vector3f(1f, 0f, 1f));

        final MotionEvent control = new MotionEvent();
        control.setLookAt(Vector3f.UNIT_Z, Vector3f.UNIT_Y);
        control.setRotation(Quaternion.IDENTITY);
        control.setPath(motionPath);

        return control;
    }
}
