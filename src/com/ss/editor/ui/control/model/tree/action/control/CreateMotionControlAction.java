package com.ss.editor.ui.control.model.tree.action.control;

import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.control.Control;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;

/**
 * The action to create a {@link MotionEvent}.
 *
 * @author JavaSaBr
 */
public class CreateMotionControlAction extends AbstractCreateControlAction {

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
        return "Motion";
    }

    @NotNull
    @Override
    protected Control createControl() {

        final MotionPath motionPath = new MotionPath();
        motionPath.addWayPoint(Vector3f.ZERO.clone());
        motionPath.addWayPoint(new Vector3f(0f, 1f, 0f));
        motionPath.addWayPoint(new Vector3f(1f, 0f, 1f));

        final MotionEvent control = new MotionEvent();
        control.setLookAt(Vector3f.UNIT_Z, Vector3f.UNIT_Y); // TODO: DELETE WHEN ALPHA-4 IS OUT!!
        control.setRotation(Quaternion.IDENTITY); // TODO: DELETE WHEN ALPHA-4 IS OUT!!
        control.setPath(motionPath);

        return control;
    }
}
