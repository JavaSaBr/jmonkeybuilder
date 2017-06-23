package com.ss.editor.ui.control.model.tree.action.particle.emitter.shape;

import com.jme3.effect.ParticleEmitter;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to switch an emitter shape of the {@link ParticleEmitter}.
 *
 * @author JavaSaBr
 */
public abstract class AbstractCreateShapeEmitterAction extends AbstractNodeAction<ModelChangeConsumer> {

    /**
     * Instantiates a new Abstract create shape emitter action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public AbstractCreateShapeEmitterAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.GEOMETRY_16;
    }

    @FXThread
    @Override
    protected void process() {

        final ModelNode<?> node = getNode();
        final ParticleEmitter particleEmitter = (ParticleEmitter) node.getElement();

        process(getNodeTree(), particleEmitter);
    }

    @FXThread
    protected void process(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ParticleEmitter emitter) {
    }
}
