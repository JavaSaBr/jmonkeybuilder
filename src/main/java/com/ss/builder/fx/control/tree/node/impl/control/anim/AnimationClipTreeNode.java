package com.ss.builder.ui.control.tree.node.impl.control.anim;

import com.jme3.anim.AnimClip;
import com.jme3.anim.AnimComposer;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.ui.Icons;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.rlib.common.util.ObjectUtils;
import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of node to show {@link AnimClip}.
 *
 * @author JavaSaBr
 */
public class AnimationClipTreeNode extends TreeNode<AnimClip> {

    public AnimationClipTreeNode(@NotNull AnimClip animClip, long objectId) {
        super(animClip, objectId);
    }

    @Override
    @FxThread
    public void fillContextMenu(@NotNull NodeTree<?> nodeTree, @NotNull ObservableList<MenuItem> items) {

        var animClip = getElement();
        var animationControlTreeNode = ObjectUtils.notNull((AnimationControlTreeNode) getParent());
        var animComposer = animationControlTreeNode.getElement();

        super.fillContextMenu(nodeTree, items);
    }

    @Override
    @FxThread
    public @NotNull String getName() {
        return getElement().getName();
    }

    @Override
    @FxThread
    public @Nullable Image getIcon() {
        return Icons.ANIMATION_16;
    }
}
