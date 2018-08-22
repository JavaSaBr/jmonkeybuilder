package com.ss.builder.ui.control.tree.action.impl.control;

import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.ui.Icons;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to create a {@link MotionEvent}.
 *
 * @author JavaSaBr
 */
public class CreateMotionControlAction extends AbstractCreateControlAction {

    public CreateMotionControlAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.GEAR_16;
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_ADD_CONTROL_MOTION;
    }

    @Override
    @FxThread
    protected @NotNull Control createControl(@NotNull final Spatial parent) {

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
