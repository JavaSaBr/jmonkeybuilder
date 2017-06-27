package com.ss.editor.ui.control.model.tree.action.particle.emitter.shape;

import static com.ss.editor.extension.property.EditablePropertyType.VECTOR_3F;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.shapes.EmitterBoxShape;
import com.jme3.effect.shapes.EmitterShape;
import com.jme3.math.Vector3f;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import com.ss.editor.ui.dialog.factory.PropertyDefinition;
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

    /**
     * Instantiates a new Create box shape emitter action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public CreateBoxShapeEmitterAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.CUBE_16;
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_BOX_SHAPE;
    }

    @NotNull
    @Override
    protected Array<PropertyDefinition> getPropertyDefinitions() {
        final Array<PropertyDefinition> definitions = ArrayFactory.newArray(PropertyDefinition.class);
        definitions.add(new PropertyDefinition(VECTOR_3F, Messages.MODEL_PROPERTY_MIN, PROPERTY_MIN, new Vector3f(1F, 1F, 1F)));
        definitions.add(new PropertyDefinition(VECTOR_3F, Messages.MODEL_PROPERTY_MAX, PROPERTY_MAX, new Vector3f(1F, 1F, 1F)));
        return definitions;
    }

    @NotNull
    @Override
    protected String getDialogTitle() {
        return Messages.CREATE_PARTICLE_EMITTER_BOX_SHAPE_DIALOG_TITLE;
    }

    @NotNull
    @Override
    protected EmitterShape createEmitterShape(@NotNull final VarTable vars) {
        final Vector3f min = vars.get(PROPERTY_MIN);
        final Vector3f max = vars.get(PROPERTY_MAX);
        return new EmitterBoxShape(min, max);
    }
}
