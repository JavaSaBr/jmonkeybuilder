package com.ss.editor.ui.control.model.tree.action;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.asset.AssetKey;
import com.jme3.material.Material;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.property.operation.PropertyOperation;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.tree.node.TreeNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link AbstractNodeAction} to make a material as embedded.
 *
 * @author JavaSaBr
 */
public class MakeAsEmbeddedMaterialAction extends AbstractNodeAction<ChangeConsumer> {

    public MakeAsEmbeddedMaterialAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    protected @Nullable Image getIcon() {
        return Icons.INFLUENCER_16;
    }

    @Override
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_MAKE_EMBEDDED;
    }

    @Override
    @FXThread
    protected void process() {
        super.process();

        final TreeNode<?> node = getNode();
        final Material material = (Material) node.getElement();

        final PropertyOperation<ChangeConsumer, Material, AssetKey> operation =
                new PropertyOperation<>(material, "AssetKey", null, material.getKey());
        operation.setApplyHandler(Material::setKey);

        final ChangeConsumer changeConsumer = notNull(getNodeTree().getChangeConsumer());
        changeConsumer.execute(operation);
    }
}
