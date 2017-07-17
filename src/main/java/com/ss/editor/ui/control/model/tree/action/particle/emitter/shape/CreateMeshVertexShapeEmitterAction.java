package com.ss.editor.ui.control.model.tree.action.particle.emitter.shape;

import static com.ss.editor.extension.property.EditablePropertyType.GEOMETRY_FROM_ASSET_FOLDER;
import static java.util.Collections.singletonList;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.shapes.EmitterMeshVertexShape;
import com.jme3.effect.shapes.EmitterShape;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
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

import java.util.List;

/**
 * The action to create a {@link EmitterMeshVertexShape} to the {@link ParticleEmitter}.
 *
 * @author JavaSaBr
 */
public class CreateMeshVertexShapeEmitterAction extends AbstractCreateShapeEmitterAction {

    @NotNull
    private static final String PROPERTY_GEOMETRY = "geometry";

    /**
     * Instantiates a new Create mesh vertex shape emitter action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public CreateMeshVertexShapeEmitterAction(@NotNull final AbstractNodeTree<?> nodeTree,
                                              @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.MESH_16;
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_MESH_VERTEX_SHAPE;
    }

    @NotNull
    @Override
    protected Array<PropertyDefinition> getPropertyDefinitions() {
        final Array<PropertyDefinition> definitions = ArrayFactory.newArray(PropertyDefinition.class);
        definitions.add(new PropertyDefinition(GEOMETRY_FROM_ASSET_FOLDER, Messages.MODEL_PROPERTY_GEOMETRY, PROPERTY_GEOMETRY, null));
        return definitions;
    }

    @NotNull
    @Override
    protected String getDialogTitle() {
        return Messages.ASSET_EDITOR_DIALOG_TITLE;
    }

    @NotNull
    @Override
    protected EmitterShape createEmitterShape(@NotNull final VarTable vars) {
        final Geometry geometry = vars.get(PROPERTY_GEOMETRY);
        final List<Mesh> meshes = singletonList(geometry.getMesh());
        return createEmitterShape(meshes);
    }

    @NotNull
    protected EmitterMeshVertexShape createEmitterShape(final List<Mesh> meshes) {
        return new EmitterMeshVertexShape(meshes);
    }
}
