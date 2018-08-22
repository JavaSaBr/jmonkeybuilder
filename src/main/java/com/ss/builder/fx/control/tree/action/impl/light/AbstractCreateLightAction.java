package com.ss.builder.fx.control.tree.action.impl.light;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.light.Light;
import com.jme3.scene.Node;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.undo.editor.ChangeConsumer;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.model.undo.impl.AddLightOperation;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.undo.editor.ChangeConsumer;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.model.undo.impl.AddLightOperation;
import com.ss.builder.fx.control.tree.NodeTree;
import com.ss.builder.fx.control.tree.action.AbstractNodeAction;
import com.ss.builder.fx.control.tree.node.TreeNode;
import org.jetbrains.annotations.NotNull;

/**
 * The action to create a {@link Light}.
 *
 * @author JavaSaBr
 */
public abstract class AbstractCreateLightAction extends AbstractNodeAction<ModelChangeConsumer> {

    public AbstractCreateLightAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FxThread
    protected void process() {
        super.process();

        final NodeTree<?> nodeTree = getNodeTree();

        final Light light = createLight();
        if (light.getName() == null) {
            light.setName("");
        }

        final TreeNode<?> treeNode = getNode();
        final Node element = (Node) treeNode.getElement();

        final ChangeConsumer changeConsumer = notNull(nodeTree.getChangeConsumer());
        changeConsumer.execute(new AddLightOperation(light, element));
    }

    /**
     * Create light light.
     *
     * @return the light
     */
    @FxThread
    protected abstract @NotNull Light createLight();
}
