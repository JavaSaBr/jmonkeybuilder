package com.ss.editor.ui.control.model.tree.action.particle.emitter.shape;

import static com.ss.editor.extension.property.EditablePropertyType.VECTOR_3F;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.shapes.EmitterBoxShape;
import com.jme3.effect.shapes.EmitterShape;
import com.jme3.math.Vector3f;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.rlib.util.VarTable;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to create a {@link EmitterBoxShape} to the {@link ParticleEmitter}.
 *
 * @author JavaSaBr
 */
public class CreateBoxShapeEmitterAction extends AbstractCreateShapeEmitterAction {

    @NotNull
    private static final String PROPERTY_MIN = "min";

    @NotNull
    private static final String PROPERTY_MAX = "max";

    public CreateBoxShapeEmitterAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FXThread
    protected @Nullable Image getIcon() {
        return Icons.CUBE_16;
    }

    @Override
    @FXThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_BOX_SHAPE;
    }

    @Override
    @FXThread
    protected @NotNull Array<PropertyDefinition> getPropertyDefinitions() {
        final Array<PropertyDefinition> definitions = ArrayFactory.newArray(PropertyDefinition.class);
        definitions.add(new PropertyDefinition(VECTOR_3F, Messages.MODEL_PROPERTY_MIN, PROPERTY_MIN, new Vector3f(1F, 1F, 1F)));
        definitions.add(new PropertyDefinition(VECTOR_3F, Messages.MODEL_PROPERTY_MAX, PROPERTY_MAX, new Vector3f(1F, 1F, 1F)));
        return definitions;
    }

    @Override
    @FXThread
    protected @NotNull String getDialogTitle() {
        return Messages.CREATE_PARTICLE_EMITTER_BOX_SHAPE_DIALOG_TITLE;
    }

    @Override
    @FXThread
    protected @NotNull EmitterShape createEmitterShape(@NotNull final VarTable vars) {
        final Vector3f min = vars.get(PROPERTY_MIN);
        final Vector3f max = vars.get(PROPERTY_MAX);
        return new EmitterBoxShape(min, max);
    }
}
