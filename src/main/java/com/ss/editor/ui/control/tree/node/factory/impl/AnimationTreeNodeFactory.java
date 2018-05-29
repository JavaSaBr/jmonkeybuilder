package com.ss.editor.ui.control.tree.node.factory.impl;

import com.jme3.anim.AnimClip;
import com.jme3.anim.AnimComposer;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.editor.ui.control.tree.node.factory.TreeNodeFactory;
import com.ss.editor.ui.control.tree.node.impl.control.anim.AnimationClipTreeNode;
import com.ss.editor.ui.control.tree.node.impl.control.anim.AnimationControlTreeNode;
import org.jetbrains.annotations.Nullable;

import static com.ss.rlib.common.util.ClassUtils.unsafeCast;

/**
 * The implementation of a tree node factory to make animation nodes.
 *
 * @author JavaSaBr
 */
public class AnimationTreeNodeFactory implements TreeNodeFactory {

    public static final int PRIORITY = 1;

    @Override
    @FxThread
    public <T, V extends TreeNode<T>> @Nullable V createFor(@Nullable T element, long objectId) {

        if (element instanceof AnimComposer) {
            return unsafeCast(new AnimationControlTreeNode((AnimComposer) element, objectId));
        } else if (element instanceof AnimClip) {
            return unsafeCast(new AnimationClipTreeNode((AnimClip) element, objectId));
        }

        return null;
    }

    @Override
    @FxThread
    public int getPriority() {
        return PRIORITY;
    }
}
