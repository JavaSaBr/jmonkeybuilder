package com.ss.builder.fx.control.tree.node.impl.spatial.terrain;

import com.jme3.terrain.geomipmap.TerrainQuad;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.Icons;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.Icons;
import com.ss.builder.fx.control.tree.node.impl.spatial.NodeTreeNode;
import com.ss.builder.fx.control.tree.NodeTree;
import com.ss.builder.fx.control.tree.node.TreeNode;
import javafx.scene.control.Menu;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * The implementation of the {@link TreeNode} to represent a {@link TerrainQuad} in an editor.
 *
 * @author JavaSaBr
 */
public class TerrainQuadTreeNode extends NodeTreeNode<TerrainQuad> {

    public TerrainQuadTreeNode(@NotNull TerrainQuad element, long objectId) {
        super(element, objectId);
    }

    @Override
    @FxThread
    public @Nullable Image getIcon() {
        return Icons.TERRAIN_16;
    }

    @Override
    @FxThread
    protected @NotNull Optional<Menu> createToolMenu(@NotNull NodeTree<?> nodeTree) {
        return Optional.empty();
    }
}
