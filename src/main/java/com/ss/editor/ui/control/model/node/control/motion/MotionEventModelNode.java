package com.ss.editor.ui.control.model.node.control.motion;

import static com.ss.editor.ui.control.tree.node.ModelNodeFactory.createFor;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionEvent;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.node.control.ControlModelNode;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;

/**
 * The implementation of the {@link ControlModelNode} to show a {@link MotionEvent} in the tree.
 *
 * @author JavaSaBr
 */
public class MotionEventModelNode extends ControlModelNode<MotionEvent> {

    /**
     * Instantiates a new Motion event model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public MotionEventModelNode(@NotNull final MotionEvent element, final long objectId) {
        super(element, objectId);
    }

    @NotNull
    @Override
    public String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_MOTION_CONTROL;
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.MOTION_16;
    }

    @NotNull
    @Override
    public Array<ModelNode<?>> getChildren(@NotNull final AbstractNodeTree<?> nodeTree) {

        final MotionPath path = getElement().getPath();
        final Array<ModelNode<?>> result = ArrayFactory.newArray(ModelNode.class);
        result.add(createFor(path));

        return result;
    }

    @Override
    public boolean hasChildren(@NotNull final AbstractNodeTree<?> nodeTree) {
        return true;
    }
}
