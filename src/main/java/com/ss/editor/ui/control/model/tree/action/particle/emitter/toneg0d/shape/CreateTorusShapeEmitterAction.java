package com.ss.editor.ui.control.model.tree.action.particle.emitter.toneg0d.shape;

import static com.ss.editor.extension.property.EditablePropertyType.FLOAT;
import static com.ss.editor.extension.property.EditablePropertyType.INTEGER;
import com.jme3.scene.Mesh;
import com.jme3.scene.shape.Torus;
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
import tonegod.emitter.ParticleEmitterNode;

/**
 * The action to switch an emitter shape of the {@link ParticleEmitterNode} to a {@link Torus}.
 *
 * @author JavaSaBr
 */
public class CreateTorusShapeEmitterAction extends AbstractCreateShapeEmitterAction {

    @NotNull
    private static final String PROPERTY_CIRCLE_SAMPLES = "circleSamples";

    @NotNull
    private static final String PROPERTY_RADIAL_SAMPLES = "radialSamples";

    @NotNull
    private static final String PROPERTY_INNER_RADIUS = "innerRadius";

    @NotNull
    private static final String PROPERTY_OUTER_RADIUS = "outerRadius";

    /**
     * Instantiates a new Create torus shape emitter action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public CreateTorusShapeEmitterAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.TORUS_16;
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_TORUS_SHAPE;
    }

    @NotNull
    @Override
    protected Array<PropertyDefinition> getPropertyDefinitions() {

        final Array<PropertyDefinition> definitions = ArrayFactory.newArray(PropertyDefinition.class);
        definitions.add(new PropertyDefinition(INTEGER, Messages.MODEL_PROPERTY_CIRCLE_SAMPLES, PROPERTY_CIRCLE_SAMPLES, 10));
        definitions.add(new PropertyDefinition(INTEGER, Messages.MODEL_PROPERTY_RADIAL_SAMPLES, PROPERTY_RADIAL_SAMPLES, 10));
        definitions.add(new PropertyDefinition(FLOAT, Messages.MODEL_PROPERTY_INNER_RADIUS, PROPERTY_INNER_RADIUS, 0.1F));
        definitions.add(new PropertyDefinition(FLOAT, Messages.MODEL_PROPERTY_OUTER_RADIUS, PROPERTY_OUTER_RADIUS, 1F));

        return definitions;
    }

    @NotNull
    @Override
    protected String getDialogTitle() {
        return Messages.CREATE_PARTICLE_EMITTER_TORUS_SHAPE_DIALOG_TITLE;
    }

    @NotNull
    @Override
    protected Mesh createMesh(@NotNull final VarTable vars) {
        final int circleSamples = vars.getInteger(PROPERTY_CIRCLE_SAMPLES);
        final int radialSamples = vars.getInteger(PROPERTY_RADIAL_SAMPLES);
        final float innerRadius = vars.getFloat(PROPERTY_INNER_RADIUS);
        final float outerRadius = vars.getFloat(PROPERTY_OUTER_RADIUS);
        return new Torus(circleSamples, radialSamples, innerRadius, outerRadius);
    }
}
