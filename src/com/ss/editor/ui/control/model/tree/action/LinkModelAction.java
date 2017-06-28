package com.ss.editor.ui.control.model.tree.action;

import static com.ss.editor.control.transform.SceneEditorControl.LOADED_MODEL_KEY;
import static com.ss.editor.util.EditorUtil.getAssetFile;
import static com.ss.editor.util.EditorUtil.toAssetPath;
import static java.util.Objects.requireNonNull;
import com.jme3.asset.AssetManager;
import com.jme3.asset.ModelKey;
import com.jme3.scene.AssetLinkNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.asset.tree.context.menu.action.DeleteFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.NewFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.RenameFileAction;
import com.ss.editor.ui.control.model.tree.action.operation.AddChildOperation;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import com.ss.editor.ui.util.UIUtils;
import com.ss.editor.util.EditorUtil;
import com.ss.editor.extension.scene.SceneLayer;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;

import java.nio.file.Path;
import java.util.function.Predicate;

/**
 * The implementation of the {@link AbstractNodeAction} for loading the {@link Spatial} to the editor.
 *
 * @author vp -byte
 */
public class LinkModelAction extends AbstractNodeAction<ModelChangeConsumer> {

    private static final Predicate<Class<?>> ACTION_TESTER = type -> type == NewFileAction.class ||
            type == DeleteFileAction.class ||
            type == RenameFileAction.class;

    private static final Array<String> MODEL_EXTENSIONS = ArrayFactory.newArray(String.class);

    static {
        MODEL_EXTENSIONS.add(FileExtensions.JME_OBJECT);
    }

    /**
     * Instantiates a new Link model action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public LinkModelAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.LINK_FILE_16;
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_LINK_MODEL;
    }

    @FXThread
    @Override
    protected void process() {
        super.process();
        UIUtils.openAssetDialog(this::processOpen, MODEL_EXTENSIONS, ACTION_TESTER);
    }

    /**
     * The process of opening file.
     *
     * @param file the file
     */
    protected void processOpen(@NotNull final Path file) {

        final AbstractNodeTree<?> nodeTree = getNodeTree();
        final ChangeConsumer consumer = requireNonNull(nodeTree.getChangeConsumer());
        final SceneLayer defaultLayer = EditorUtil.getDefaultLayer(consumer);

        final Path assetFile = requireNonNull(getAssetFile(file), "Not found asset file for " + file);
        final String assetPath = toAssetPath(assetFile);

        final ModelKey modelKey = new ModelKey(assetPath);

        final AssetManager assetManager = EDITOR.getAssetManager();
        final Spatial loadedModel = assetManager.loadModel(modelKey);

        final AssetLinkNode assetLinkNode = new AssetLinkNode(modelKey);
        assetLinkNode.attachLinkedChild(loadedModel, modelKey);
        assetLinkNode.setUserData(LOADED_MODEL_KEY, true);

        if (defaultLayer != null) {
            SceneLayer.setLayer(defaultLayer, assetLinkNode);
        }

        final ModelNode<?> modelNode = getNode();
        final Node parent = (Node) modelNode.getElement();
        consumer.execute(new AddChildOperation(assetLinkNode, parent));
    }
}
