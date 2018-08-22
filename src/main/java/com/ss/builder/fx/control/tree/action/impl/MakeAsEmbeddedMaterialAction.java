package com.ss.builder.fx.control.tree.action.impl;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.asset.AssetKey;
import com.jme3.material.Material;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.undo.editor.ChangeConsumer;
import com.ss.builder.fx.Icons;
import com.ss.builder.fx.control.property.operation.PropertyOperation;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.undo.editor.ChangeConsumer;
import com.ss.builder.fx.Icons;
import com.ss.builder.fx.control.property.operation.PropertyOperation;
import com.ss.builder.fx.control.tree.NodeTree;
import com.ss.builder.fx.control.tree.action.AbstractNodeAction;
import com.ss.builder.fx.control.tree.node.TreeNode;
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
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.INFLUENCER_16;
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_MAKE_EMBEDDED;
    }

    @Override
    @FxThread
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
