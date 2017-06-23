package com.ss.editor.ui.control.model.tree.dialog.particle.emitter.shape;

import static java.util.Objects.requireNonNull;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.shapes.EmitterShape;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.model.tree.action.operation.ChangeParticleEmitterShapeOperation;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.dialog.AbstractSimpleEditorDialog;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of a dialog to create an emitter shape.
 *
 * @author JavaSaBr
 */
public abstract class AbstractCreateShapeDialog extends AbstractSimpleEditorDialog {

    /**
     * The node tree component.
     */
    @NotNull
    private final AbstractNodeTree<?> nodeTree;

    /**
     * The collision object.
     */
    @NotNull
    private final ParticleEmitter emitter;

    /**
     * Instantiates a new Abstract create shape dialog.
     *
     * @param nodeTree the node tree
     * @param emitter  the emitter
     */
    AbstractCreateShapeDialog(@NotNull final AbstractNodeTree<?> nodeTree,
                              @NotNull final ParticleEmitter emitter) {
        this.nodeTree = nodeTree;
        this.emitter = emitter;
    }

    /**
     * Gets node tree.
     *
     * @return the node tree component.
     */
    @NotNull
    protected AbstractNodeTree<?> getNodeTree() {
        return nodeTree;
    }

    /**
     * @return the emitter.
     */
    @NotNull
    private ParticleEmitter getEmitter() {
        return emitter;
    }

    @Override
    protected boolean isGridStructure() {
        return true;
    }

    @Override
    protected void processOk() {
        super.processOk();

        final AbstractNodeTree<?> nodeTree = getNodeTree();

        final EmitterShape emitterShape = createEmitterShape();
        final ParticleEmitter element = getEmitter();

        final ChangeConsumer changeConsumer = requireNonNull(nodeTree.getChangeConsumer());
        changeConsumer.execute(new ChangeParticleEmitterShapeOperation(emitterShape, element));
    }

    /**
     * Create emitter shape emitter shape.
     *
     * @return the emitter shape
     */
    @NotNull
    protected abstract EmitterShape createEmitterShape();
}
