package com.ss.editor.ui.control.model.tree.action.particle.emitter.toneg0d.shape;

import static com.ss.editor.util.EditorUtil.getAssetFile;
import static com.ss.editor.util.EditorUtil.toAssetPath;
import static java.util.Objects.requireNonNull;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.asset.tree.context.menu.action.DeleteFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.NewFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.RenameFileAction;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.model.tree.action.operation.particle.emitter.toneg0d.ChangeEmitterMeshOperation;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import com.ss.editor.ui.util.UIUtils;
import com.ss.editor.util.NodeUtils;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import tonegod.emitter.ParticleEmitterNode;

import java.nio.file.Path;
import java.util.function.Predicate;

/**
 * The action for switching the emitter shape of the {@link ParticleEmitterNode} to {@link Spatial}.
 *
 * @author JavaSaBr
 */
public class LoadModelShapeEmitterAction extends AbstractNodeAction<ModelChangeConsumer> {

    private static final Predicate<Class<?>> ACTION_TESTER = type -> type == NewFileAction.class ||
            type == DeleteFileAction.class ||
            type == RenameFileAction.class;

    private static final Array<String> MODEL_EXTENSIONS = ArrayFactory.newArray(String.class);

    static {
        MODEL_EXTENSIONS.add(FileExtensions.JME_OBJECT);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.OPEN_FILE_16;
    }

    /**
     * Instantiates a new Load model shape emitter action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public LoadModelShapeEmitterAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_MODEL_SHAPE;
    }

    @FXThread
    @Override
    protected void process() {
        UIUtils.openAssetDialog(this::processOpen, MODEL_EXTENSIONS, ACTION_TESTER);
    }

    /**
     * The process of opening file.
     *
     * @param file the file
     */
    protected void processOpen(@NotNull final Path file) {

        final AbstractNodeTree<ModelChangeConsumer> nodeTree = getNodeTree();
        final ModelChangeConsumer changeConsumer = requireNonNull(nodeTree.getChangeConsumer());

        final Path assetFile = requireNonNull(getAssetFile(file), "Not found asset file for " + file);
        final String assetPath = toAssetPath(assetFile);

        final AssetManager assetManager = EDITOR.getAssetManager();
        final Spatial loadedModel = assetManager.loadModel(assetPath);
        final Geometry geometry = NodeUtils.findGeometry(loadedModel);

        if (geometry == null) {
            LOGGER.warning(this, "not found a geometry in the model " + assetPath);
            return;
        }

        final ModelNode<?> modelNode = getNode();
        final ParticleEmitterNode element = (ParticleEmitterNode) modelNode.getElement();

        changeConsumer.execute(new ChangeEmitterMeshOperation(geometry.getMesh(), element));
    }
}
