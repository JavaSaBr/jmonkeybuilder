package com.ss.editor.ui.control.model.tree.action.particle.emitter.shape;

import static com.ss.editor.util.EditorUtil.getAssetFile;
import static com.ss.editor.util.EditorUtil.toAssetPath;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.asset.AssetManager;
import com.jme3.asset.ModelKey;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.shapes.EmitterMeshVertexShape;
import com.jme3.effect.shapes.EmitterShape;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.asset.tree.context.menu.action.DeleteFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.RenameFileAction;
import com.ss.editor.ui.control.model.tree.action.operation.particle.emitter.ChangeEmitterShapeOperation;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import com.ss.editor.ui.util.UIUtils;
import com.ss.editor.util.NodeUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * The action to create a {@link EmitterMeshVertexShape} to the {@link ParticleEmitter}.
 *
 * @author JavaSaBr
 */
public class CreateMeshVertexShapeEmitterAction extends AbstractCreateShapeEmitterAction {

    private static final Predicate<Class<?>> ACTION_TESTER = type -> type == DeleteFileAction.class ||
            type == RenameFileAction.class;

    private static final Array<String> MODEL_EXTENSIONS = ArrayFactory.newArray(String.class);

    static {
        MODEL_EXTENSIONS.add(FileExtensions.JME_OBJECT);
    }

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

    @Override
    protected void process(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ParticleEmitter emitter) {
        UIUtils.openAssetDialog(this::processOpen, MODEL_EXTENSIONS, ACTION_TESTER);
    }

    private void processOpen(@NotNull final Path file) {

        final ModelNode<?> node = getNode();
        final ParticleEmitter emitter = (ParticleEmitter) node.getElement();

        final Path assetFile = notNull(getAssetFile(file), "Not found asset file for " + file);
        final String assetPath = toAssetPath(assetFile);

        final ModelKey modelKey = new ModelKey(assetPath);

        final AssetManager assetManager = EDITOR.getAssetManager();
        final Spatial loadedModel = assetManager.loadModel(modelKey);
        final List<Mesh> meshes = new ArrayList<>();

        NodeUtils.visitGeometry(loadedModel, geometry -> meshes.add(geometry.getMesh()));

        if (meshes.isEmpty()) {
            LOGGER.warning(this, "not found any mesh in the model " + assetPath);
            return;
        }

        final EmitterShape emitterShape = createEmitterShape(meshes);

        final ModelChangeConsumer changeConsumer = notNull(getNodeTree().getChangeConsumer());
        changeConsumer.execute(new ChangeEmitterShapeOperation(emitterShape, emitter));
    }

    /**
     * Create emitter shape emitter mesh vertex shape.
     *
     * @param meshes the meshes
     * @return the emitter mesh vertex shape
     */
    @NotNull
    protected EmitterMeshVertexShape createEmitterShape(@NotNull final List<Mesh> meshes) {
        return new EmitterMeshVertexShape(meshes);
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
}
