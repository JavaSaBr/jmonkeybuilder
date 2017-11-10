package com.ss.editor.ui.control.model.tree.action.particle.emitter.toneg0d.shape;

import static com.ss.editor.extension.property.EditablePropertyType.FLOAT;
import com.jme3.scene.Mesh;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.rlib.util.VarTable;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.shapes.TriangleEmitterShape;

/**
 * The action to switch an emitter shape of the {@link ParticleEmitterNode} to a {@link TriangleEmitterShape}.
 *
 * @author JavaSaBr
 */
public class CreateTriangleShapeEmitterAction extends AbstractCreateShapeEmitterAction {

    @NotNull
    private static final String PROPERTY_SIZE = "size";

    public CreateTriangleShapeEmitterAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FXThread
    protected @Nullable Image getIcon() {
        return Icons.TRIANGLE_16;
    }

    @Override
    @FXThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_TRIANGLE_SHAPE;
    }

    @Override
    @FXThread
    protected @NotNull Array<PropertyDefinition> getPropertyDefinitions() {

        final Array<PropertyDefinition> definitions = ArrayFactory.newArray(PropertyDefinition.class);
        definitions.add(new PropertyDefinition(FLOAT, Messages.MODEL_PROPERTY_SIZE, PROPERTY_SIZE, 1F));

        return definitions;
    }

    @Override
    @FXThread
    protected @NotNull String getDialogTitle() {
        return Messages.CREATE_PARTICLE_EMITTER_TRIANGLE_SHAPE_DIALOG_TITLE;
    }

    @Override
    @FXThread
    protected @NotNull Mesh createMesh(@NotNull final VarTable vars) {
        final float size = vars.getFloat(PROPERTY_SIZE);
        return new TriangleEmitterShape(size);
    }
}
