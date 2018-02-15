package com.ss.editor.ui.control.tree.action.impl;

import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.EnableControlsOperation;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.editor.util.ControlUtils;
import com.ss.editor.util.NodeUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayCollectors;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.ss.rlib.util.ObjectUtils.notNull;

/**
 * The action to re-activate a physics control.
 *
 * @author JavaSaBr
 */
public class EnableAllControlsAction extends AbstractNodeAction<ModelChangeConsumer> {

    public EnableAllControlsAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.REPLAY_16;
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_ENABLE_ALL_CONTROLS;
    }

    @Override
    @FxThread
    protected void process() {

        final Array<Control> controls = NodeUtils.children((Spatial) getNode().getElement())
            .flatMap(ControlUtils::controls)
            .collect(ArrayCollectors.toArray(Control.class));

        final ModelChangeConsumer changeConsumer = notNull(getNodeTree().getChangeConsumer());
        changeConsumer.execute(new EnableControlsOperation(controls));
    }
}
