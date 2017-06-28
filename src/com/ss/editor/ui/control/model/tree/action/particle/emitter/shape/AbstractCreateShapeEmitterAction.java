package com.ss.editor.ui.control.model.tree.action.particle.emitter.shape;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.shapes.EmitterShape;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.model.tree.action.operation.particle.emitter.ChangeEmitterShapeOperation;
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

import java.awt.*;

/**
 * The action to switch an {@link EmitterShape} of the {@link ParticleEmitter}.
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
        final Point dialogSize = getDialogSize();

        final ObjectFactoryDialog dialog = new ObjectFactoryDialog(getPropertyDefinitions(), this::handleResult);
        dialog.setTitle(getDialogTitle());

        if (dialogSize != null) {
            dialog.updateSize(dialogSize);
        }

        dialog.show(scene.getWindow());
    }

    /**
     * Gets another dialog size.
     *
     * @return the dialog size or null.
     */
    @Nullable
    protected Point getDialogSize() {
        return null;
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
        final ParticleEmitter element = (ParticleEmitter) modelNode.getElement();
        final EmitterShape emitterShape = createEmitterShape(vars);

        final AbstractNodeTree<?> nodeTree = getNodeTree();
        final ChangeConsumer changeConsumer = notNull(nodeTree.getChangeConsumer());
        changeConsumer.execute(new ChangeEmitterShapeOperation(emitterShape, element));
    }

    /**
     * Gets a list of property definitions to create a shape.
     *
     * @return the list of definitions.
     */
    @NotNull
    protected abstract Array<PropertyDefinition> getPropertyDefinitions();

    /**
     * Create emitter shape.
     *
     * @param vars the table with variables.
     * @return the emitter shape.
     */
    @NotNull
    protected abstract EmitterShape createEmitterShape(@NotNull final VarTable vars);
}
