package com.ss.editor.ui.control.model.tree.action.particle.emitter.shape;

import com.jme3.effect.ParticleEmitter;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.dialog.particle.emitter.shape.CreateSphereShapeDialog;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import com.ss.editor.ui.scene.EditorFXScene;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to create a {@link}to the {@link ParticleEmitter}.
 *
 * @author JavaSaBr
 */
public class CreateSphereShapeEmitterAction extends AbstractCreateShapeEmitterAction {

    /**
     * Instantiates a new Create sphere shape emitter action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public CreateSphereShapeEmitterAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    protected void process(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ParticleEmitter emitter) {
        final EditorFXScene scene = JFX_APPLICATION.getScene();
        final CreateSphereShapeDialog dialog = new CreateSphereShapeDialog(nodeTree, emitter);
        dialog.show(scene.getWindow());
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.SPHERE_16;
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_EMITTER_CHANGE_SPHERE_SHAPE;
    }
}
