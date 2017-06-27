package com.ss.editor.ui.control.model.tree.action.particle.emitter.toneg0d.shape;

import static com.ss.editor.extension.property.EditablePropertyType.BOOLEAN;
import static com.ss.editor.extension.property.EditablePropertyType.FLOAT;
import com.jme3.scene.Mesh;
import com.jme3.scene.shape.Quad;
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
 * The action to switch an emitter shape of the {@link ParticleEmitterNode} to a {@link Quad}.
 *
 * @author JavaSaBr
 */
public class CreateQuadShapeEmitterAction extends AbstractCreateShapeEmitterAction {

    @NotNull
    private static final String PROPERTY_WIDTH = "width";

    @NotNull
    private static final String PROPERTY_HEIGHT = "height";

    @NotNull
    private static final String PROPERTY_FLIP_COORDS = "flipCoords";

    /**
     * Instantiates a new Create quad shape emitter action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public CreateQuadShapeEmitterAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.QUAD_16;
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_QUAD_SHAPE;
    }

    @NotNull
    @Override
    protected Array<PropertyDefinition> getPropertyDefinitions() {

        final Array<PropertyDefinition> definitions = ArrayFactory.newArray(PropertyDefinition.class);
        definitions.add(new PropertyDefinition(FLOAT, Messages.MODEL_PROPERTY_WIDTH, PROPERTY_WIDTH, 1F));
        definitions.add(new PropertyDefinition(FLOAT, Messages.MODEL_PROPERTY_HEIGHT, PROPERTY_HEIGHT, 1F));
        definitions.add(new PropertyDefinition(BOOLEAN, Messages.MODEL_PROPERTY_FLIP_COORDS, PROPERTY_FLIP_COORDS, true));

        return definitions;
    }

    @NotNull
    @Override
    protected String getDialogTitle() {
        return Messages.CREATE_PARTICLE_EMITTER_QUAD_SHAPE_DIALOG_TITLE;
    }

    @NotNull
    @Override
    protected Mesh createMesh(@NotNull final VarTable vars) {
        final float width = vars.getFloat(PROPERTY_WIDTH);
        final float height = vars.getFloat(PROPERTY_HEIGHT);
        final boolean flipCoords = vars.getBoolean(PROPERTY_FLIP_COORDS);
        return new Quad(width, height, flipCoords);
    }
}
