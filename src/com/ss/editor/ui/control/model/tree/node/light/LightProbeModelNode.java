package com.ss.editor.ui.control.model.tree.node.light;

import com.jme3.light.LightProbe;
import com.ss.editor.Messages;
import com.ss.editor.ui.control.model.tree.node.ModelNode;

/**
 * Реализация узла с цветопробой для PBR.
 *
 * @author Ronn
 */
public class LightProbeModelNode extends ModelNode<LightProbe> {

    public LightProbeModelNode(final LightProbe element, final long objectId) {
        super(element, objectId);
    }

    @Override
    public String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_LIGHT_PROBE;
    }
}
