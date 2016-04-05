package com.ss.editor.ui.control.model.tree.node.control.anim;

import com.jme3.animation.AnimControl;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.node.ModelNode;
import com.ss.editor.ui.control.model.tree.node.ModelNodeFactory;
import com.ss.editor.ui.control.model.tree.node.control.ControlModelNode;

import java.util.Collection;

import javafx.scene.image.Image;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * Реализация узла для отображения контрола анимации модели.
 *
 * @author Ronn
 */
public class AnimationControlModelNode extends ControlModelNode<AnimControl> {

    public AnimationControlModelNode(final AnimControl element, final long objectId) {
        super(element, objectId);
    }

    @Override
    public String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_ANIM_CONTROL;
    }

    @Override
    public Image getIcon() {
        return Icons.ANIMATION_16;
    }

    @Override
    public boolean hasChildren() {
        return true;
    }

    @Override
    public Array<ModelNode<?>> getChildren() {

        final Array<ModelNode<?>> result = ArrayFactory.newArray(ModelNode.class);

        final AnimControl element = getElement();
        final Collection<String> animationNames = element.getAnimationNames();
        animationNames.forEach(name -> {

            final AnimationModelNode modelNode = ModelNodeFactory.createFor(element.getAnim(name));
            modelNode.setControl(getElement());

            result.add(modelNode);
        });

        result.addAll(super.getChildren());

        return result;
    }
}
