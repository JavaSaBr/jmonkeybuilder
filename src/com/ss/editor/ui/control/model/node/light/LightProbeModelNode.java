package com.ss.editor.ui.control.model.node.light;

import com.jme3.light.LightProbe;
import com.ss.editor.Messages;
import com.ss.editor.ui.control.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;

/**
 * Реализация узла с цветопробой для PBR.
 *
 * @author Ronn
 */
public class LightProbeModelNode extends ModelNode<LightProbe> {

    /**
     * Instantiates a new Light probe model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public LightProbeModelNode(final LightProbe element, final long objectId) {
        super(element, objectId);
    }

    @NotNull
    @Override
    public String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_LIGHT_PROBE;
    }
}
