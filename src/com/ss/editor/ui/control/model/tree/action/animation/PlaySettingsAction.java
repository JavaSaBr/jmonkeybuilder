package com.ss.editor.ui.control.model.tree.action.animation;

import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.model.tree.dialog.animation.PlayParametersDialog;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.model.node.control.anim.AnimationControlModelNode;
import com.ss.editor.ui.scene.EditorFXScene;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;

/**
 * The action to open play settings of an animation.
 *
 * @author JavaSaBr
 */
public class PlaySettingsAction extends AbstractNodeAction<ModelChangeConsumer> {

    public PlaySettingsAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final AnimationControlModelNode node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_ANIMATION_PLAY_SETTINGS;
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.SETTINGS_16;
    }

    @FXThread
    @Override
    protected void process() {

        final EditorFXScene scene = JFX_APPLICATION.getScene();

        final PlayParametersDialog dialog = new PlayParametersDialog((AnimationControlModelNode) getNode());
        dialog.show(scene.getWindow());
    }
}
