package com.ss.editor.ui.control.model.tree.action;

import static com.ss.editor.control.transform.SceneEditorControl.LOADED_MODEL_KEY;
import static com.ss.editor.util.EditorUtil.getAssetFile;
import static com.ss.editor.util.EditorUtil.toAssetPath;
import static java.util.Objects.requireNonNull;

import com.jme3.asset.AssetManager;
import com.jme3.asset.ModelKey;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.asset.tree.context.menu.action.DeleteFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.NewFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.RenameFileAction;
import com.ss.editor.ui.control.model.tree.action.operation.AddChildOperation;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import com.ss.editor.ui.dialog.asset.AssetEditorDialog;
import com.ss.editor.ui.dialog.asset.FileAssetEditorDialog;
import com.ss.editor.ui.scene.EditorFXScene;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.function.Predicate;

import javafx.scene.image.Image;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The implementation of the {@link AbstractNodeAction} for loading the {@link Spatial} to the editor.
 *
 * @author JavaSaBr
 */
public class LoadModelAction extends AbstractNodeAction<ModelChangeConsumer> {

    private static final Predicate<Class<?>> ACTION_TESTER = type -> type == NewFileAction.class ||
            type == DeleteFileAction.class ||
            type == RenameFileAction.class;

    private static final Array<String> MODEL_EXTENSIONS = ArrayFactory.newArray(String.class);

    static {
        MODEL_EXTENSIONS.add(FileExtensions.JME_OBJECT);
    }

    public LoadModelAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.OPEN_FILE_16;
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_LOAD_MODEL;
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

        final AbstractNodeTree<?> nodeTree = getNodeTree();

        final Path assetFile = requireNonNull(getAssetFile(file), "Not found asset file for " + file);
        final String assetPath = toAssetPath(assetFile);

        final ModelKey modelKey = new ModelKey(assetPath);

        final AssetManager assetManager = EDITOR.getAssetManager();
        final Spatial loadedModel = assetManager.loadModel(modelKey);
        loadedModel.setUserData(LOADED_MODEL_KEY, true);

        final ModelNode<?> modelNode = getNode();
        final Node parent = (Node) modelNode.getElement();

        final ChangeConsumer consumer = requireNonNull(nodeTree.getChangeConsumer());
        consumer.execute(new AddChildOperation(loadedModel, parent));
    }
}
