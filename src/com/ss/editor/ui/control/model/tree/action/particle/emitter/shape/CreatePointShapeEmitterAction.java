package com.ss.editor.ui.control.model.tree.action.particle.emitter.shape;

import com.jme3.effect.ParticleEmitter;
import com.ss.editor.Messages;
import com.ss.editor.ui.control.model.tree.dialog.particle.emitter.shape.CreatePointShapeDialog;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import com.ss.editor.ui.scene.EditorFXScene;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to create a point shape to the {@link ParticleEmitter}.
 *
 * @author JavaSaBr
 */
public class CreatePointShapeEmitterAction extends AbstractCreateShapeEmitterAction {

    /**
     * Instantiates a new Create point shape emitter action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public CreatePointShapeEmitterAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    protected void process(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ParticleEmitter emitter) {
        super.process(nodeTree, emitter);

        final EditorFXScene scene = JFX_APPLICATION.getScene();
        final CreatePointShapeDialog dialog = new CreatePointShapeDialog(nodeTree, emitter);
        dialog.show(scene.getWindow());
    }

    @Nullable
    @Override
    protected Image getIcon() {
        //FIXME need to find an icon for this.
        return null;
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_EMITTER_CHANGE_POINT_SHAPE;
    }
}
