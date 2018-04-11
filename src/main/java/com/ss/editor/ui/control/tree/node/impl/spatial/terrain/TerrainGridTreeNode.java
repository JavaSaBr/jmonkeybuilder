package com.ss.editor.ui.control.tree.node.impl.spatial.terrain;

import com.jme3.terrain.geomipmap.TerrainGrid;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.node.impl.spatial.NodeTreeNode;
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

    public TerrainGridTreeNode(@NotNull final TerrainGrid element, final long objectId) {
        super(element, objectId);
    }

    @Override
    @FxThread
    public @Nullable Image getIcon() {
        return Icons.TERRAIN_16;
    }

    @Override
    @FxThread
    protected @Nullable Menu createCreationMenu(@NotNull final NodeTree<?> nodeTree) {
        return null;
    }

    @Override
    @FxThread
    protected @Nullable Menu createToolMenu(@NotNull final NodeTree<?> nodeTree) {
        return null;
    }
}
