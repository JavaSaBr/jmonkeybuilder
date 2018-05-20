package com.ss.editor.ui.control.tree.node.factory.impl;

import static com.ss.rlib.common.util.ClassUtils.unsafeCast;
import com.jme3.audio.AudioNode;
import com.jme3.material.Material;
import com.jme3.post.Filter;
import com.jme3.scene.AssetLinkNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.terrain.geomipmap.TerrainGrid;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.extension.scene.SceneLayer;
import com.ss.editor.extension.scene.SceneNode;
import com.ss.editor.extension.scene.app.state.SceneAppState;
import com.ss.editor.model.node.layer.LayersRoot;
import com.ss.editor.model.scene.SceneAppStatesNode;
import com.ss.editor.model.scene.SceneFiltersNode;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.editor.ui.control.tree.node.factory.TreeNodeFactory;
import com.ss.editor.ui.control.tree.node.impl.layer.LayersRootTreeNode;
import com.ss.editor.ui.control.tree.node.impl.layer.SceneLayerTreeNode;
import com.ss.editor.ui.control.tree.node.impl.scene.*;
import com.ss.editor.ui.control.tree.node.impl.spatial.*;
import com.ss.editor.ui.control.tree.node.impl.spatial.terrain.TerrainGridTreeNode;
import com.ss.editor.ui.control.tree.node.impl.spatial.terrain.TerrainQuadTreeNode;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of a tree node factory to make default nodes.
 *
 * @author JavaSaBr
 */
public class DefaultTreeNodeFactory implements TreeNodeFactory {

    public static final int PRIORITY = 0;

    @Override
    @FxThread
    public <T, V extends TreeNode<T>> @Nullable V createFor(@Nullable final T element, final long objectId) {

        if (element instanceof LayersRoot) {
            return unsafeCast(new LayersRootTreeNode((LayersRoot) element, objectId));
        } else if (element instanceof TerrainGrid) {
            return unsafeCast(new TerrainGridTreeNode((TerrainGrid) element, objectId));
        } else if (element instanceof TerrainQuad) {
            return unsafeCast(new TerrainQuadTreeNode((TerrainQuad) element, objectId));
        } else if (element instanceof SceneNode) {
            return unsafeCast(new SceneNodeTreeNode((SceneNode) element, objectId));
        } else if (element instanceof SceneLayer) {
            return unsafeCast(new SceneLayerTreeNode((SceneLayer) element, objectId));
        } else if (element instanceof Mesh) {
            return unsafeCast(new MeshTreeNode((Mesh) element, objectId));
        } else if (element instanceof Geometry) {
            return unsafeCast(new GeometryTreeNode<>((Geometry) element, objectId));
        } else if (element instanceof AudioNode) {
            return unsafeCast(new AudioTreeNode((AudioNode) element, objectId));
        } else if (element instanceof AssetLinkNode) {
            return unsafeCast(new AssetLinkNodeTreeNode((AssetLinkNode) element, objectId));
        } else if (element instanceof Node) {
            return unsafeCast(new NodeTreeNode<>((Node) element, objectId));
        } else if (element instanceof Material) {
            return unsafeCast(new MaterialTreeNode((Material) element, objectId));
        } else if (element instanceof SceneFiltersNode) {
            return unsafeCast(new SceneFiltersTreeNode((SceneFiltersNode) element, objectId));
        } else if (element instanceof SceneAppStatesNode) {
            return unsafeCast(new SceneAppStatesTreeNode((SceneAppStatesNode) element, objectId));
        } else if (element instanceof Filter) {
            return unsafeCast(new SceneFilterTreeNode((Filter) element, objectId));
        } else if (element instanceof SceneAppState) {
            return unsafeCast(new SceneAppStateTreeNode((SceneAppState) element, objectId));
        }

        return null;
    }

    @Override
    public int getPriority() {
        return PRIORITY;
    }
}
