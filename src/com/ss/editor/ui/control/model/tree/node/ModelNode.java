package com.ss.editor.ui.control.model.tree.node;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.Editor;
import com.ss.editor.model.UObject;
import com.ss.editor.state.editor.impl.model.ModelEditorState;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.RemoveNodeAction;

import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import rlib.util.array.Array;

/**
 * Базовая реализация узла модели в дереве.
 *
 * @author Ronn
 */
public abstract class ModelNode<T> implements UObject {

    protected static final Editor EDITOR = Editor.getInstance();

    /**
     * Уникальный ид объекта.
     */
    private final long objectId;

    /**
     * Элемент модели.
     */
    private T element;

    public ModelNode(final T element, final long objectId) {
        this.element = element;
        this.objectId = objectId;
    }

    /**
     * @return элемент модели.
     */
    public T getElement() {
        return element;
    }

    /**
     * @return есть ли дочерние элементы.
     */
    public boolean hasChildren() {
        return false;
    }

    /**
     * @return список дочерних элементов.
     */
    public Array<ModelNode<?>> getChildren() {
        return null;
    }

    /**
     * @return мокнка этого элемента.
     */
    public Image getIcon() {
        return null;
    }

    /**
     * Наполнение контекстного меню возможными действиями.
     */
    public void fillContextMenu(final ModelNodeTree nodeTree, final ObservableList<MenuItem> items) {

        final T element = getElement();

        Node parent = null;

        if (element instanceof Spatial) {
            parent = ((Spatial) element).getParent();
        }

        if (parent != null && parent.getUserData(ModelEditorState.class.getName()) != Boolean.TRUE) {
            items.add(new RemoveNodeAction(nodeTree, this));
        }
    }

    /**
     * Удаление дочернего элемента.
     */
    public void remove(final ModelNode<?> child) {

    }

    /**
     * Добавление дочернего элемента.
     */
    public void add(final ModelNode<?> child) {

    }

    /**
     * Может ли добавить к себе новый элемент.
     */
    public boolean canAccept(final ModelNode<?> node) {
        return false;
    }

    /**
     * @return можно ли перемещать элемент в вструктуре.
     */
    public boolean canMove() {
        return true;
    }

    /**
     * @return можно ли копировать элемент.
     */
    public boolean canCopy() {
        return false;
    }

    /**
     * @return копия этого узла.
     */
    public ModelNode<?> copy() {
        return null;
    }

    @Override
    public long getObjectId() {
        return objectId;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "objectId=" + objectId +
                ", element=" + element +
                '}';
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ModelNode<?> modelNode = (ModelNode<?>) o;

        return !(element != null ? !element.equals(modelNode.element) : modelNode.element != null);
    }

    @Override
    public int hashCode() {
        return element != null ? element.hashCode() : 0;
    }
}
