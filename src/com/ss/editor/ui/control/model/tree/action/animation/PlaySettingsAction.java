package com.ss.editor.ui.control.model.tree.action.animation;

import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.model.tree.node.control.anim.AnimationControlModelNode;
import com.ss.editor.ui.dialog.model.animation.PlayParametersDialog;
import com.ss.editor.ui.scene.EditorFXScene;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;

/**
 * The action to open play settings of an animation.
 *
 * @author JavaSaBr
 */
public class PlaySettingsAction extends AbstractNodeAction {

    public PlaySettingsAction(@NotNull final ModelNodeTree nodeTree, @NotNull final AnimationControlModelNode node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected String getName() {
        return "Play settings";
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.SETTINGS_16;
    }

    @Override
    protected void process() {

        final EditorFXScene scene = JFX_APPLICATION.getScene();

        final PlayParametersDialog dialog = new PlayParametersDialog(getNodeTree(), (AnimationControlModelNode) getNode());
        dialog.show(scene.getWindow());
    }
}
