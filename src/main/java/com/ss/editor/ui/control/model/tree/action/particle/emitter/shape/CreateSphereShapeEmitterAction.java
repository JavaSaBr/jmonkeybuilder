package com.ss.editor.ui.control.model.tree.action.particle.emitter.shape;

import static com.ss.editor.extension.property.EditablePropertyType.FLOAT;
import static com.ss.editor.extension.property.EditablePropertyType.VECTOR_3F;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.shapes.EmitterShape;
import com.jme3.effect.shapes.EmitterSphereShape;
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
 * The action to create a {@link}to the {@link ParticleEmitter}.
 *
 * @author JavaSaBr
 */
public class CreateSphereShapeEmitterAction extends AbstractCreateShapeEmitterAction {

    @NotNull
    private static final String PROPERTY_CENTER = "center";

    @NotNull
    private static final String PROPERTY_RADIUS = "radius";

    /**
     * Instantiates a new Create sphere shape emitter action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public CreateSphereShapeEmitterAction(@NotNull final AbstractNodeTree<?> nodeTree,
                                          @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.SPHERE_16;
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_SPHERE_SHAPE;
    }

    @NotNull
    @Override
    protected Array<PropertyDefinition> getPropertyDefinitions() {
        final Array<PropertyDefinition> definitions = ArrayFactory.newArray(PropertyDefinition.class);
        definitions.add(new PropertyDefinition(VECTOR_3F, Messages.MODEL_PROPERTY_CENTER, PROPERTY_CENTER, new Vector3f(1F, 1F, 1F)));
        definitions.add(new PropertyDefinition(FLOAT, Messages.MODEL_PROPERTY_RADIUS, PROPERTY_RADIUS, 1F));
        return definitions;
    }

    @NotNull
    @Override
    protected String getDialogTitle() {
        return Messages.CREATE_PARTICLE_EMITTER_SPHERE_SHAPE_DIALOG_TITLE;
    }

    @NotNull
    @Override
    protected EmitterShape createEmitterShape(@NotNull final VarTable vars) {
        final Vector3f center = vars.get(PROPERTY_CENTER);
        final float radius = vars.getFloat(PROPERTY_RADIUS);
        return new EmitterSphereShape(center, radius);
    }
}
