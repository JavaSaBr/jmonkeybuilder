package com.ss.editor.ui.control.model.node.spatial.terrain;

import com.jme3.terrain.geomipmap.TerrainGrid;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.node.spatial.NodeTreeNode;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import javafx.scene.control.Menu;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link TreeNode} to represent a {@link TerrainGrid} in an editor.
 *
 * @author JavaSaBr
 */
public class TerrainGridTreeNode extends NodeTreeNode<TerrainGrid> {

    /**
     * Instantiates a new Terrain grid model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public TerrainGridTreeNode(@NotNull final TerrainGrid element, final long objectId) {
        super(element, objectId);
    }

    @FxThread
    @Nullable
    @Override
    public Image getIcon() {
        return Icons.TERRAIN_16;
    }

    @FxThread
    @Nullable
    @Override
    protected Menu createCreationMenu(@NotNull final NodeTree<?> nodeTree) {
        return null;
    }

    @FxThread
    @Nullable
    @Override
    protected Menu createToolMenu(@NotNull final NodeTree<?> nodeTree) {
        return null;
    }
}
