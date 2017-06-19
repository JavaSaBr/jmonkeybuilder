package com.ss.editor.ui.control.model.tree.action.emitter.shape;

import static java.util.Objects.requireNonNull;

import com.jme3.scene.Mesh;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.model.tree.action.operation.ChangeEmitterShapeOperation;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;
import tonegod.emitter.ParticleEmitterNode;

/**
 * The action to switch an emitter shape of the {@link ParticleEmitterNode}.
 *
 * @author JavaSaBr
 */
public abstract class AbstractCreateShapeEmitterAction extends AbstractNodeAction<ModelChangeConsumer> {

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

        final AbstractNodeTree<?> nodeTree = getNodeTree();

        final ModelNode<?> modelNode = getNode();
        final ParticleEmitterNode element = (ParticleEmitterNode) modelNode.getElement();
        final Mesh shape = createMesh();

        final ChangeConsumer changeConsumer = requireNonNull(nodeTree.getChangeConsumer());
        changeConsumer.execute(new ChangeEmitterShapeOperation(shape, element));
    }

    @NotNull
    protected abstract Mesh createMesh();
}
