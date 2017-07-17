package com.ss.editor.ui.control.model.node.spatial;

import static com.ss.editor.control.transform.SceneEditorControl.LOADED_MODEL_KEY;
import static com.ss.editor.ui.control.tree.node.ModelNodeFactory.createFor;
import static com.ss.editor.util.EditorUtil.*;
import static java.util.Objects.requireNonNull;
import com.jme3.asset.AssetManager;
import com.jme3.asset.ModelKey;
import com.jme3.scene.AssetLinkNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.*;
import com.ss.editor.ui.control.model.tree.action.audio.CreateAudioNodeAction;
import com.ss.editor.ui.control.model.tree.action.particle.emitter.CreateParticleEmitterAction;
import com.ss.editor.ui.control.model.tree.action.particle.emitter.toneg0d.CreateToneg0dParticleEmitterAction;
import com.ss.editor.ui.control.model.tree.action.particle.emitter.toneg0d.CreateToneg0dSoftParticleEmitterAction;
import com.ss.editor.ui.control.model.tree.action.particle.emitter.ResetParticleEmittersAction;
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
import com.ss.editor.util.NodeUtils;
import com.ss.editor.extension.scene.SceneLayer;
import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import tonegod.emitter.ParticleEmitterNode;

import java.nio.file.Path;
import java.util.List;

/**
 * The implementation of the {@link SpatialModelNode} for representing the {@link Node} in the editor.
 *
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public class NodeModelNode<T extends Node> extends SpatialModelNode<T> {

    /**
     * Instantiates a new Node model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public NodeModelNode(@NotNull final T element, final long objectId) {
        super(element, objectId);
    }

    @Nullable
    @Override
    protected Menu createToolMenu(final @NotNull AbstractNodeTree<?> nodeTree) {
        final Menu toolMenu = new Menu(Messages.MODEL_NODE_TREE_ACTION_TOOLS, new ImageView(Icons.INFLUENCER_16));
        toolMenu.getItems().addAll(new OptimizeGeometryAction(nodeTree, this));
        return toolMenu;
    }

    @Nullable
    @Override
    protected Menu createCreationMenu(@NotNull final AbstractNodeTree<?> nodeTree) {

        final Menu menu = super.createCreationMenu(nodeTree);
        if (menu == null) return null;

        final Menu createPrimitiveMenu = new Menu(Messages.MODEL_NODE_TREE_ACTION_CREATE_PRIMITIVE,
                new ImageView(Icons.ADD_12));
        createPrimitiveMenu.getItems()
                .addAll(new CreateBoxAction(nodeTree, this), new CreateSphereAction(nodeTree, this),
                        new CreateQuadAction(nodeTree, this));

        final Menu addLightMenu = new Menu(Messages.MODEL_NODE_TREE_ACTION_LIGHT, new ImageView(Icons.ADD_12));
        addLightMenu.getItems()
                .addAll(new CreateSpotLightAction(nodeTree, this), new CreatePointLightAction(nodeTree, this),
                        new CreateAmbientLightAction(nodeTree, this), new CreateDirectionLightAction(nodeTree, this));

        menu.getItems().addAll(new CreateNodeAction(nodeTree, this), new LoadModelAction(nodeTree, this),
                new LinkModelAction(nodeTree, this), new CreateSkyAction(nodeTree, this),
                new CreateToneg0dParticleEmitterAction(nodeTree, this), new CreateToneg0dSoftParticleEmitterAction(nodeTree, this),
                new CreateParticleEmitterAction(nodeTree, this),
                new CreateAudioNodeAction(nodeTree, this), new CreateTerrainAction(nodeTree, this),
                createPrimitiveMenu, addLightMenu);

        return menu;
    }

    @Override
    public void fillContextMenu(@NotNull final AbstractNodeTree<?> nodeTree,
                                @NotNull final ObservableList<MenuItem> items) {
        if (!(nodeTree instanceof ModelNodeTree)) return;

        final T element = getElement();
        final Spatial emitter = NodeUtils.findSpatial(element, ParticleEmitterNode.class::isInstance);

        if (emitter != null) {
            items.add(new ResetParticleEmittersAction(nodeTree, this));
        }

        super.fillContextMenu(nodeTree, items);
    }

    @Override
    public boolean hasChildren(@NotNull final AbstractNodeTree<?> nodeTree) {
        return nodeTree instanceof ModelNodeTree;
    }

    @NotNull
    @Override
    public Array<ModelNode<?>> getChildren(@NotNull final AbstractNodeTree<?> nodeTree) {

        final Array<ModelNode<?>> result = ArrayFactory.newArray(ModelNode.class);
        final List<Spatial> children = getSpatials();
        children.forEach(spatial -> result.add(createFor(spatial)));
        result.addAll(super.getChildren(nodeTree));

        return result;
    }

    /**
     * Gets spatials.
     *
     * @return the spatials
     */
    @NotNull
    protected List<Spatial> getSpatials() {
        final Node element = getElement();
        return element.getChildren();
    }

    @Override
    public boolean canAccept(@NotNull final ModelNode<?> child) {
        if (child == this) return false;
        final Object element = child.getElement();
        return element instanceof Spatial && GeomUtils.canAttach(getElement(), (Spatial) element);
    }

    @Override
    public void add(@NotNull final ModelNode<?> child) {
        super.add(child);

        final Node node = getElement();

        if (child instanceof SpatialModelNode) {
            final Spatial element = (Spatial) child.getElement();
            node.attachChildAt(element, 0);
        }
    }

    @Override
    public void remove(@NotNull final ModelNode<?> child) {
        super.remove(child);

        final Node node = getElement();

        if (child instanceof SpatialModelNode) {
            final Spatial element = (Spatial) child.getElement();
            node.detachChild(element);
        }
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.NODE_16;
    }

    @Override
    public boolean canAcceptExternal(@NotNull final Dragboard dragboard) {
        return UIUtils.isHasFile(dragboard, FileExtensions.JME_OBJECT);
    }

    @Override
    public void acceptExternal(@NotNull final Dragboard dragboard, @NotNull final ChangeConsumer consumer) {
        UIUtils.handleDroppedFile(dragboard, FileExtensions.JME_OBJECT, getElement(), consumer, (node, cons, path) -> {

            final SceneLayer defaultLayer = getDefaultLayer(cons);
            final Path assetFile = requireNonNull(getAssetFile(path), "Not found asset file for " + path);
            final String assetPath = toAssetPath(assetFile);
            final ModelKey modelKey = new ModelKey(assetPath);

            final AssetManager assetManager = EDITOR.getAssetManager();
            final Spatial loadedModel = assetManager.loadModel(assetPath);
            final AssetLinkNode assetLinkNode = new AssetLinkNode(modelKey);
            assetLinkNode.attachLinkedChild(loadedModel, modelKey);
            assetLinkNode.setUserData(LOADED_MODEL_KEY, true);

            if (defaultLayer != null) {
                SceneLayer.setLayer(defaultLayer, loadedModel);
            }

            cons.execute(new AddChildOperation(assetLinkNode, node));
        });
    }
}
