package com.ss.editor.ui.control.model.tree.action.particle.emitter.toneg0d.shape;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.scene.Mesh;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.plugin.api.dialog.GenericFactoryDialog;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.operation.particle.emitter.toneg0d.ChangeEmitterMeshOperation;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.rlib.util.VarTable;
import com.ss.rlib.util.array.Array;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tonegod.emitter.ParticleEmitterNode;

/**
 * The action to switch an emitter shape of the {@link ParticleEmitterNode}.
 *
 * @author JavaSaBr
 */
public abstract class AbstractCreateShapeEmitterAction extends AbstractNodeAction<ModelChangeConsumer> {

    public AbstractCreateShapeEmitterAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FXThread
    protected @Nullable Image getIcon() {
        return Icons.GEOMETRY_16;
    }

    @Override
    @FXThread
    protected void process() {
        super.process();
        final GenericFactoryDialog dialog = new GenericFactoryDialog(getPropertyDefinitions(), this::handleResult);
        dialog.setTitle(getDialogTitle());
        dialog.show();
    }

    /**
     * Gets a dialog title.
     *
     * @return the dialog title.
     */
    @FXThread
    protected abstract @NotNull String getDialogTitle();

    /**
     * Handle the result from the dialog.
     *
     * @param vars the table with variables.
     */
    @FXThread
    private void handleResult(@NotNull final VarTable vars) {

        final TreeNode<?> treeNode = getNode();
        final ParticleEmitterNode element = (ParticleEmitterNode) treeNode.getElement();
        final Mesh shape = createMesh(vars);

        final NodeTree<?> nodeTree = getNodeTree();
        final ChangeConsumer changeConsumer = notNull(nodeTree.getChangeConsumer());
        changeConsumer.execute(new ChangeEmitterMeshOperation(shape, element));
    }

    /**
     * Gets a list of property definitions to create a mesh.
     *
     * @return the list of definitions.
     */
    @FXThread
    protected abstract @NotNull Array<PropertyDefinition> getPropertyDefinitions();

    /**
     * Create a mesh.
     *
     * @param vars the table with variables.
     * @return the mesh
     */
    @FXThread
    protected abstract @NotNull Mesh createMesh(@NotNull final VarTable vars);
}
