package com.ss.editor.ui.control.model.tree.node.control;

import com.jme3.scene.control.Control;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.node.ModelNode;

import javafx.scene.image.Image;

/**
 * Базовая реализация узла в дереве для контрола модели.
 *
 * @author Ronn
 */
public class ControlModelNode<T extends Control> extends ModelNode<T> {

    public ControlModelNode(final T element, final long objectId) {
        super(element, objectId);
    }

    @Override
    public Image getIcon() {
        return Icons.GEAR_16;
    }

    @Override
    public String getName() {
        return getElement().getClass().getSimpleName();
    }
}
