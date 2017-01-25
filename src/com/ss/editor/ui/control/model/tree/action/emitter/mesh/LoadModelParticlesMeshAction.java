package com.ss.editor.ui.control.model.tree.action.emitter.mesh;

import static com.ss.editor.util.EditorUtil.getAssetFile;
import static java.util.Objects.requireNonNull;

import com.jme3.asset.AssetManager;
import com.jme3.asset.ModelKey;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.component.asset.tree.context.menu.action.DeleteFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.NewFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.RenameFileAction;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.model.tree.action.operation.ChangeParticleMeshOperation;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import com.ss.editor.ui.dialog.asset.AssetEditorDialog;
import com.ss.editor.ui.dialog.asset.FileAssetEditorDialog;
import com.ss.editor.ui.scene.EditorFXScene;
import com.ss.editor.util.EditorUtil;
import com.ss.editor.util.NodeUtils;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.function.Predicate;

import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.geometry.ParticleGeometry;
import tonegod.emitter.particle.ParticleDataMeshInfo;
import tonegod.emitter.particle.ParticleDataTemplateMesh;

/**
 * The action for switching the emitter shape of the {@link ParticleEmitterNode} to {@link ParticleDataTemplateMesh}.
 *
 * @author JavaSaBr
 */
public class LoadModelParticlesMeshAction extends AbstractNodeAction<ModelChangeConsumer> {

    private static final Predicate<Class<?>> ACTION_TESTER = type -> type == NewFileAction.class ||
            type == DeleteFileAction.class ||
            type == RenameFileAction.class;

    private static final Array<String> MODEL_EXTENSIONS = ArrayFactory.newArray(String.class);

    static {
        MODEL_EXTENSIONS.add(FileExtensions.JME_OBJECT);
    }

    public LoadModelParticlesMeshAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
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
        dialog.setActionTester(ACTION_TESTER);
        dialog.show(scene.getWindow());
    }

    /**
     * The process of opening file.
     */
    protected void processOpen(@NotNull final Path file) {

        final AbstractNodeTree<ModelChangeConsumer> nodeTree = getNodeTree();
        final ModelChangeConsumer changeConsumer = requireNonNull(nodeTree.getChangeConsumer());

        final Path assetFile = requireNonNull(getAssetFile(file), "Not found asset file for " + file);
        final String assetPath = EditorUtil.toAssetPath(assetFile);

        final ModelKey modelKey = new ModelKey(assetPath);

        final AssetManager assetManager = EDITOR.getAssetManager();
        assetManager.deleteFromCache(modelKey);

        final Spatial loadedModel = assetManager.loadModel(modelKey);
        final Geometry geometry = NodeUtils.findGeometry(loadedModel);

        if (geometry == null) {
            LOGGER.warning(this, "not found a geometry in the model " + assetPath);
            return;
        }

        final ModelNode<?> modelNode = getNode();
        final ParticleGeometry element = (ParticleGeometry) modelNode.getElement();
        final ParticleDataMeshInfo meshInfo = new ParticleDataMeshInfo(ParticleDataTemplateMesh.class, geometry.getMesh());

        changeConsumer.execute(new ChangeParticleMeshOperation(meshInfo, element));
    }
}
