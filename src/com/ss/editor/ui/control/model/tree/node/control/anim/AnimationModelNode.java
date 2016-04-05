package com.ss.editor.ui.control.model.tree.node.control.anim;

import com.jme3.animation.AnimControl;
import com.jme3.animation.Animation;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.PlayAnimationAction;
import com.ss.editor.ui.control.model.tree.node.ModelNode;

import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;

/**
 * Реализация структурного узла с анимаций модели.
 *
 * @author Ronn
 */
public class AnimationModelNode extends ModelNode<Animation> {

    /**
     * Контроллер анимации.
     */
    private AnimControl control;

    /**
     * Индекс канала воспроизведения анимации.
     */
    private int channel;

    public AnimationModelNode(final Animation element, final long objectId) {
        super(element, objectId);
        this.channel = -1;
    }

    @Override
    public void fillContextMenu(final ModelNodeTree nodeTree, final ObservableList<MenuItem> items) {

        if (getChannel() < 0) {
            items.add(new PlayAnimationAction(nodeTree, this));
        }

        super.fillContextMenu(nodeTree, items);
    }

    /**
     * @param control контроллер анимации.
     */
    public void setControl(final AnimControl control) {
        this.control = control;
    }

    /**
     * @return контроллер анимации.
     */
    public AnimControl getControl() {
        return control;
    }

    @Override
    public String getName() {
        return getElement().getName();
    }

    /**
     * @return индекс канала воспроизведения анимации.
     */
    public int getChannel() {
        return channel;
    }

    /**
     * @param channel индекс канала воспроизведения анимации.
     */
    public void setChannel(int channel) {
        this.channel = channel;
    }

    @Override
    public Image getIcon() {
        return getChannel() < 0 ? Icons.PLAY_16 : Icons.STOP_16;
    }
}
