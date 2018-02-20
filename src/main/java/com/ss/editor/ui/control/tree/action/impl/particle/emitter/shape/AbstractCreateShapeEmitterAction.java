package com.ss.editor.ui.control.tree.action.impl.particle.emitter.shape;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.shapes.EmitterShape;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.plugin.api.dialog.GenericFactoryDialog;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.Icons;
import com.ss.editor.model.undo.impl.emitter.ChangeEmitterShapeOperation;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.rlib.util.VarTable;
import com.ss.rlib.util.array.Array;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * The action to switch an {@link EmitterShape} of the {@link ParticleEmitter}.
 *
 * @author JavaSaBr
 */
public abstract class AbstractCreateShapeEmitterAction extends AbstractNodeAction<ModelChangeConsumer> {

    public AbstractCreateShapeEmitterAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.GEOMETRY_16;
    }

    @FxThread
    @Override
    protected void process() {
        super.process();

        final Point dialogSize = getDialogSize();

        final GenericFactoryDialog dialog = new GenericFactoryDialog(getPropertyDefinitions(), this::handleResult);
        dialog.setTitle(getDialogTitle());

        if (dialogSize != null) {
            dialog.updateSize(dialogSize);
        }

        dialog.show();
    }

    /**
     * Gets another dialog size.
     *
     * @return the dialog size or null.
     */
    @FxThread
    protected @Nullable Point getDialogSize() {
        return null;
    }

    /**
     * Gets a dialog title.
     *
     * @return the dialog title.
     */
    @FxThread
    protected abstract @NotNull String getDialogTitle();

    /**
     * Handle the result from the dialog.
     *
     * @param vars the table with variables.
     */
    @FxThread
    private void handleResult(@NotNull final VarTable vars) {

        final TreeNode<?> treeNode = getNode();
        final ParticleEmitter element = (ParticleEmitter) treeNode.getElement();
        final EmitterShape emitterShape = createEmitterShape(vars);

        final NodeTree<?> nodeTree = getNodeTree();
        final ChangeConsumer changeConsumer = notNull(nodeTree.getChangeConsumer());
        changeConsumer.execute(new ChangeEmitterShapeOperation(emitterShape, element));
    }

    /**
     * Gets a list of property definitions to create a shape.
     *
     * @return the list of definitions.
     */
    @FxThread
    protected abstract @NotNull Array<PropertyDefinition> getPropertyDefinitions();

    /**
     * Create emitter shape.
     *
     * @param vars the table with variables.
     * @return the emitter shape.
     */
    @FxThread
    protected abstract @NotNull EmitterShape createEmitterShape(@NotNull final VarTable vars);
}
