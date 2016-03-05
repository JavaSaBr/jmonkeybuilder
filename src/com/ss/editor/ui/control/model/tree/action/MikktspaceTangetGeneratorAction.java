package com.ss.editor.ui.control.model.tree.action;

import com.jme3.scene.Geometry;
import com.ss.editor.Messages;
import com.ss.editor.model.tool.TangentGenerator;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.node.ModelNode;

/**
 * Реализация действия по генерации тангетов используя алгоритм Mikktspace.
 *
 * @author Ronn
 */
public class MikktspaceTangetGeneratorAction extends AbstractNodeAction {

    public MikktspaceTangetGeneratorAction(final ModelNodeTree nodeTree, final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_MIKKTSPACE_TANGENT_GENERATOR;
    }

    @Override
    protected void process() {

        final ModelNode<?> node = getNode();
        final Geometry element = (Geometry) node.getElement();

        TangentGenerator.useMikktspaceGenerator(element);

        final ModelNodeTree nodeTree = getNodeTree();
        nodeTree.notifyChanged(node);
    }
}
