package com.ss.editor.ui.control.model.tree.action;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.node.ModelNode;
import com.ss.editor.ui.control.model.tree.node.ModelNodeFactory;
import com.ss.editor.ui.dialog.asset.AssetEditorDialog;
import com.ss.editor.ui.scene.EditorFXScene;
import com.ss.editor.util.EditorUtil;

import java.nio.file.Path;

import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * Реализация действия по загрузки другой модели в эту.
 *
 * @author Ronn
 */
public class LoadModelAction extends AbstractNodeAction {

    private static final Array<String> MODEL_EXTENSIONS = ArrayFactory.newArray(String.class);

    static {
        MODEL_EXTENSIONS.add(FileExtensions.JME_OBJECT);
    }

    public LoadModelAction(final ModelNodeTree nodeTree, final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_LOAD_MODEL;
    }

    @Override
    protected void process() {

        final EditorFXScene scene = EDITOR.getScene();

        final AssetEditorDialog dialog = new AssetEditorDialog(this::processOpen);
        dialog.setExtensionFilter(MODEL_EXTENSIONS);
        dialog.show(scene.getWindow());
    }

    /**
     * Процесс открытия файла.
     */
    protected void processOpen(final Path file) {

        final Path assetFile = EditorUtil.getAssetFile(file);
        final String assetPath = EditorUtil.toAssetPath(assetFile);

        final AssetManager assetManager = EDITOR.getAssetManager();
        assetManager.clearCache();

        final Spatial loadedModel = assetManager.loadModel(assetPath);

        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final ModelNode<Spatial> newNode = ModelNodeFactory.createFor(loadedModel);
            final ModelNode<?> modelNode = getNode();
            modelNode.add(newNode);

            EXECUTOR_MANAGER.addFXTask(() -> {
                final ModelNodeTree nodeTree = getNodeTree();
                nodeTree.notifyAdded(modelNode, newNode);
            });
        });
    }
}
