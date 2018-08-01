package com.ss.editor.ui.control.tree.action.impl;

import static com.ss.editor.part3d.editor.impl.scene.AbstractSceneEditor3dPart.KEY_LOADED_MODEL;
import static com.ss.editor.util.EditorUtils.getAssetFile;
import static com.ss.editor.util.EditorUtils.toAssetPath;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.asset.AssetManager;
import com.jme3.asset.ModelKey;
import com.jme3.scene.AssetLinkNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.extension.scene.SceneLayer;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.asset.tree.context.menu.action.DeleteFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.NewFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.RenameFileAction;
import com.ss.editor.model.undo.impl.AddChildOperation;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.editor.ui.util.UiUtils;
import com.ss.editor.util.EditorUtils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.function.Predicate;

/**
 * The implementation of the {@link AbstractNodeAction} for loading the {@link Spatial} to the editor.
 *
 * @author vp -byte
 */
public class LinkModelAction extends AbstractNodeAction<ModelChangeConsumer> {

    @NotNull
    private static final Predicate<Class<?>> ACTION_TESTER = type -> type == NewFileAction.class ||
            type == DeleteFileAction.class ||
            type == RenameFileAction.class;

    @NotNull
    private static final Array<String> MODEL_EXTENSIONS = ArrayFactory.newArray(String.class);

    static {
        MODEL_EXTENSIONS.add(FileExtensions.JME_OBJECT);
    }

    public LinkModelAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.LINK_FILE_16;
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_LINK_MODEL;
    }

    @FxThread
    @Override
    protected void process() {
        super.process();
        UiUtils.openFileAssetDialog(this::processOpen, MODEL_EXTENSIONS, ACTION_TESTER);
    }

    /**
     * The process of opening file.
     *
     * @param file the file
     */
    @FxThread
    protected void processOpen(@NotNull final Path file) {

        final NodeTree<?> nodeTree = getNodeTree();
        final ChangeConsumer consumer = notNull(nodeTree.getChangeConsumer());
        final SceneLayer defaultLayer = EditorUtils.getDefaultLayer(consumer);

        final Path assetFile = notNull(getAssetFile(file), "Not found asset file for " + file);
        final String assetPath = toAssetPath(assetFile);

        final ModelKey modelKey = new ModelKey(assetPath);

        final AssetManager assetManager = EditorUtils.getAssetManager();
        final Spatial loadedModel = assetManager.loadModel(modelKey);

        final AssetLinkNode assetLinkNode = new AssetLinkNode(modelKey);
        assetLinkNode.attachLinkedChild(loadedModel, modelKey);
        assetLinkNode.setUserData(KEY_LOADED_MODEL, true);

        if (defaultLayer != null) {
            SceneLayer.setLayer(defaultLayer, assetLinkNode);
        }

        final TreeNode<?> treeNode = getNode();
        final Node parent = (Node) treeNode.getElement();
        consumer.execute(new AddChildOperation(assetLinkNode, parent));
    }
}
