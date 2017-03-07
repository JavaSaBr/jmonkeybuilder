package com.ss.editor.ui.control.model.node.spatial;

import com.jme3.asset.AssetManager;
import com.jme3.scene.AssetLinkNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.CreateNodeAction;
import com.ss.editor.ui.control.model.tree.action.CreateSkyAction;
import com.ss.editor.ui.control.model.tree.action.LoadModelAction;
import com.ss.editor.ui.control.model.tree.action.OptimizeGeometryAction;
import com.ss.editor.ui.control.model.tree.action.audio.CreateAudioNodeAction;
import com.ss.editor.ui.control.model.tree.action.emitter.CreateTonegodEmitterAction;
import com.ss.editor.ui.control.model.tree.action.emitter.CreateTonegodSoftEmitterAction;
import com.ss.editor.ui.control.model.tree.action.geometry.CreateBoxAction;
import com.ss.editor.ui.control.model.tree.action.geometry.CreateQuadAction;
import com.ss.editor.ui.control.model.tree.action.geometry.CreateSphereAction;
import com.ss.editor.ui.control.model.tree.action.light.CreateAmbientLightAction;
import com.ss.editor.ui.control.model.tree.action.light.CreateDirectionLightAction;
import com.ss.editor.ui.control.model.tree.action.light.CreatePointLightAction;
import com.ss.editor.ui.control.model.tree.action.light.CreateSpotLightAction;
import com.ss.editor.ui.control.model.tree.action.operation.AddChildOperation;
import com.ss.editor.ui.control.model.tree.action.terrain.CreateTerrainAction;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import com.ss.editor.ui.util.UIUtils;
import com.ss.editor.util.GeomUtils;
import com.ss.extension.scene.SceneLayer;
import javafx.scene.control.Menu;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

import java.nio.file.Path;
import java.util.List;

import static com.ss.editor.control.transform.SceneEditorControl.LOADED_MODEL_KEY;
import static com.ss.editor.ui.control.tree.node.ModelNodeFactory.createFor;
import static com.ss.editor.util.EditorUtil.*;
import static java.util.Objects.requireNonNull;

/**
 * The implementation of the {@link SpatialModelNode} for representing the {@link Node} in the editor.
 *
 * @author vp-byte
 */
public class AssetLinkNodeModelNode extends NodeModelNode<AssetLinkNode> {

    public AssetLinkNodeModelNode(@NotNull AssetLinkNode element, long objectId) {
        super(element, objectId);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.LINKNODE_16;
    }

}
