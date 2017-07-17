package com.ss.editor.ui.control.model.tree.action.particle.emitter.toneg0d.shape;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.scene.Mesh;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.model.tree.action.operation.particle.emitter.toneg0d.ChangeEmitterMeshOperation;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import com.ss.editor.ui.dialog.factory.ObjectFactoryDialog;
import com.ss.editor.ui.dialog.factory.PropertyDefinition;
import com.ss.editor.ui.scene.EditorFXScene;
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

    /**
     * Instantiates a new Abstract create shape emitter action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public AbstractCreateShapeEmitterAction(@NotNull final AbstractNodeTree<?> nodeTree,
                                            @NotNull final ModelNode<?> node) {
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
        super.process();

        final EditorFXScene scene = JFX_APPLICATION.getScene();
        final ObjectFactoryDialog dialog = new ObjectFactoryDialog(getPropertyDefinitions(), this::handleResult);
        dialog.setTitle(getDialogTitle());
        dialog.show(scene.getWindow());
    }

    /**
     * Gets a dialog title.
     *
     * @return the dialog title.
     */
    @NotNull
    protected abstract String getDialogTitle();

    /**
     * Handle the result from the dialog.
     *
     * @param vars the table with variables.
     */
    private void handleResult(@NotNull final VarTable vars) {

        final ModelNode<?> modelNode = getNode();
        final ParticleEmitterNode element = (ParticleEmitterNode) modelNode.getElement();
        final Mesh shape = createMesh(vars);

        final AbstractNodeTree<?> nodeTree = getNodeTree();
        final ChangeConsumer changeConsumer = notNull(nodeTree.getChangeConsumer());
        changeConsumer.execute(new ChangeEmitterMeshOperation(shape, element));
    }

    /**
     * Gets a list of property definitions to create a mesh.
     *
     * @return the list of definitions.
     */
    @NotNull
    protected abstract Array<PropertyDefinition> getPropertyDefinitions();

    /**
     * Create a mesh.
     *
     * @param vars the table with variables.
     * @return the mesh
     */
    @NotNull
    protected abstract Mesh createMesh(@NotNull final VarTable vars);
}
