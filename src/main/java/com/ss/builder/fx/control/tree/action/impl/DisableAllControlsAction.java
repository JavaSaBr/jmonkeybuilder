package com.ss.builder.ui.control.tree.action.impl;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import static com.ss.rlib.common.util.array.ArrayCollectors.toArray;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.model.undo.impl.DisableControlsOperation;
import com.ss.builder.ui.Icons;
import com.ss.builder.util.ControlUtils;
import com.ss.builder.util.NodeUtils;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.DisableControlsOperation;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.editor.util.ControlUtils;
import com.ss.editor.util.NodeUtils;
import com.ss.rlib.common.util.array.Array;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to disable all controls in a selected node.
 *
 * @author JavaSaBr
 */
public class DisableAllControlsAction extends AbstractNodeAction<ModelChangeConsumer> {

    public DisableAllControlsAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.STOP_16;
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_DISABLE_ALL_CONTROLS;
    }

    @Override
    @FxThread
    protected void process() {

        final Array<Control> controls = NodeUtils.children((Spatial) getNode().getElement())
                .flatMap(ControlUtils::controls)
                .filter(ControlUtils::isEnabled)
                .collect(toArray(Control.class));

        final ModelChangeConsumer changeConsumer = notNull(getNodeTree().getChangeConsumer());
        changeConsumer.execute(new DisableControlsOperation(controls));
    }
}
