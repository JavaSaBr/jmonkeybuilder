package com.ss.editor.ui.control.model.tree.action.particle.emitter.shape;

import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.shapes.EmitterBoxShape;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.dialog.particle.emitter.shape.CreateBoxShapeDialog;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import com.ss.editor.ui.scene.EditorFXScene;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to create a {@link EmitterBoxShape} to the {@link ParticleEmitter}.
 *
 * @author JavaSaBr
 */
public class CreateBoxShapeEmitterAction extends AbstractCreateShapeEmitterAction {

    /**
     * Instantiates a new Create box shape emitter action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public CreateBoxShapeEmitterAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    protected void process(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ParticleEmitter emitter) {
        final EditorFXScene scene = JFX_APPLICATION.getScene();
        final CreateBoxShapeDialog dialog = new CreateBoxShapeDialog(nodeTree, emitter);
        dialog.show(scene.getWindow());
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.CUBE_16;
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_BOX_SHAPE;
    }
}
