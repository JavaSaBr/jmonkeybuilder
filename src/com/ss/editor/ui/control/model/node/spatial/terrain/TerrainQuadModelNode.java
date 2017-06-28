package com.ss.editor.ui.control.model.node.spatial.terrain;

import com.jme3.terrain.geomipmap.TerrainQuad;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.node.spatial.NodeModelNode;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import javafx.scene.control.Menu;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link ModelNode} to represent a {@link TerrainQuad} in an editor.
 *
 * @author JavaSaBr
 */
public class TerrainQuadModelNode extends NodeModelNode<TerrainQuad> {

    /**
     * Instantiates a new Terrain quad model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public TerrainQuadModelNode(@NotNull final TerrainQuad element, final long objectId) {
        super(element, objectId);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.TERRAIN_16;
    }

    @Nullable
    @Override
    protected Menu createCreationMenu(@NotNull final AbstractNodeTree<?> nodeTree) {
        return null;
    }

    @Nullable
    @Override
    protected Menu createToolMenu(@NotNull final AbstractNodeTree<?> nodeTree) {
        return null;
    }
}
