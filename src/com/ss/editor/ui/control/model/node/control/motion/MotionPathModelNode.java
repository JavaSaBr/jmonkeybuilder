package com.ss.editor.ui.control.model.node.control.motion;

import static com.ss.editor.ui.control.tree.node.ModelNodeFactory.createFor;
import static java.util.Objects.requireNonNull;
import com.jme3.cinematic.MotionPath;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.node.PositionModelNode;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;

/**
 * The implementation of the {@link ModelNode} to show a {@link MotionPath} in the tree.
 *
 * @author JavaSaBr
 */
public class MotionPathModelNode extends ModelNode<MotionPath> {

    /**
     * Instantiates a new Motion path model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public MotionPathModelNode(@NotNull final MotionPath element, final long objectId) {
        super(element, objectId);
    }

    @NotNull
    @Override
    public String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_MOTION_PATH;
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.PATH_16;
    }

    @NotNull
    @Override
    public Array<ModelNode<?>> getChildren(@NotNull final AbstractNodeTree<?> nodeTree) {

        final MotionPath element = getElement();
        final int wayPoints = element.getNbWayPoints();

        final Array<ModelNode<?>> result = ArrayFactory.newArray(ModelNode.class);

        for (int i = 0; i < wayPoints; i++) {
            final PositionModelNode modelNode = requireNonNull(createFor(element.getWayPoint(i)));
            modelNode.setName(Messages.MODEL_FILE_EDITOR_NODE_WAY_POINT + " #" + (i + 1));
            result.add(modelNode);
        }

        return result;
    }

    @Override
    public boolean hasChildren(@NotNull final AbstractNodeTree<?> nodeTree) {
        return nodeTree instanceof ModelNodeTree;
    }
}
