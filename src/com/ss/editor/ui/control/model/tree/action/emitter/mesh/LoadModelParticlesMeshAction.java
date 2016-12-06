package com.ss.editor.ui.control.model.tree.action.emitter.mesh;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.model.tree.action.operation.ChangeParticleMeshOperation;
import com.ss.editor.ui.control.model.tree.node.ModelNode;
import com.ss.editor.ui.dialog.asset.AssetEditorDialog;
import com.ss.editor.ui.dialog.asset.FileAssetEditorDialog;
import com.ss.editor.ui.scene.EditorFXScene;
import com.ss.editor.util.EditorUtil;
import com.ss.editor.util.GeomUtils;
import com.ss.editor.util.NodeUtils;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.particle.ParticleDataMeshInfo;
import tonegod.emitter.particle.ParticleDataTemplateMesh;

/**
 * The action for switching the emitter shape of the {@link ParticleEmitterNode} to {@link
 * ParticleDataTemplateMesh}.
 *
 * @author JavaSaBr
 */
public class LoadModelParticlesMeshAction extends AbstractNodeAction {

    private static final Array<String> MODEL_EXTENSIONS = ArrayFactory.newArray(String.class);

    static {
        MODEL_EXTENSIONS.add(FileExtensions.JME_OBJECT);
    }

    public LoadModelParticlesMeshAction(@NotNull final ModelNodeTree nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_EMITTER_CHANGE_PARTICLES_MESH_MODEL;
    }

    @Override
    protected void process() {
        final EditorFXScene scene = JFX_APPLICATION.getScene();
        final AssetEditorDialog dialog = new FileAssetEditorDialog(this::processOpen);
        dialog.setExtensionFilter(MODEL_EXTENSIONS);
        dialog.show(scene.getWindow());
    }

    /**
     * The process of opening file.
     */
    protected void processOpen(final Path file) {

        final ModelNodeTree nodeTree = getNodeTree();
        final ModelChangeConsumer modelChangeConsumer = nodeTree.getModelChangeConsumer();
        final AssetManager assetManager = EDITOR.getAssetManager();
        assetManager.clearCache();

        final Path assetFile = EditorUtil.getAssetFile(file);
        final String assetPath = EditorUtil.toAssetPath(assetFile);

        final Spatial loadedModel = assetManager.loadModel(assetPath);
        final Geometry geometry = NodeUtils.findGeometry(loadedModel);

        if (geometry == null) {
            LOGGER.warning(this, "not found a geometry in the model " + assetPath);
            return;
        }

        final ModelNode<?> modelNode = getNode();
        final Geometry element = (Geometry) modelNode.getElement();

        final int index = GeomUtils.getIndex(modelChangeConsumer.getCurrentModel(), element);
        final ParticleDataMeshInfo meshInfo = new ParticleDataMeshInfo(ParticleDataTemplateMesh.class, geometry.getMesh());

        modelChangeConsumer.execute(new ChangeParticleMeshOperation(meshInfo, index));
    }
}
