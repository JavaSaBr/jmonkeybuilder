package com.ss.builder.fx.control.tree.node.impl.control.anim;

import com.jme3.anim.AnimComposer;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.Icons;
import com.ss.builder.fx.control.model.ModelNodeTree;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.Icons;
import com.ss.builder.fx.control.model.ModelNodeTree;
import com.ss.builder.fx.control.tree.NodeTree;
import com.ss.builder.fx.control.tree.node.TreeNode;
import com.ss.builder.fx.control.tree.node.impl.control.ControlTreeNode;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of node to show {@link AnimComposer}.
 *
 * @author JavaSaBr
 */
public class AnimationControlTreeNode extends ControlTreeNode<AnimComposer> {

    public AnimationControlTreeNode(@NotNull AnimComposer animComposer, long objectId) {
        super(animComposer, objectId);
    }

    @Override
    @FxThread
    public void fillContextMenu(@NotNull NodeTree<?> nodeTree, @NotNull ObservableList<MenuItem> items) {
        super.fillContextMenu(nodeTree, items);
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_ANIM_COMPOSER;
    }

    @Override
    public @Nullable Image getIcon() {
        return Icons.ANIMATION_16;
    }

    @Override
    @FxThread
    public boolean hasChildren(@NotNull NodeTree<?> nodeTree) {
        return nodeTree instanceof ModelNodeTree;
    }

    @Override
    @FxThread
    public @NotNull Array<TreeNode<?>> getChildren(@NotNull NodeTree<?> nodeTree) {

        var animComposer = getElement();
        var animClips = animComposer.getAnimClips();

        var result = ArrayFactory.<TreeNode<?>>newArray(TreeNode.class, animClips.size());

        animClips.forEach(animClip -> result.add(FACTORY_REGISTRY.createFor(animClip)));

        result.addAll(super.getChildren(nodeTree));

        return result;
    }
}
