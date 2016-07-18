package com.ss.editor.ui.control.model.tree.node;

import com.ss.editor.Editor;
import com.ss.editor.model.UObject;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;

import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * Базовая реализация узла модели в дереве.
 *
 * @author Ronn
 */
public abstract class ModelNode<T> implements UObject {

    private static final Array<ModelNode<?>> EMPTY_ARRAY = ArrayFactory.newArray(ModelNode.class);

    protected static final Editor EDITOR = Editor.getInstance();

    /**
     * Уникальный ид объекта.
     */
    private final long objectId;

    /**
     * Элемент модели.
     */
    private final T element;

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
     * @return название узла.
     */
    public String getName() {
        return "null";
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
        return EMPTY_ARRAY;
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
     * Обработка изменения имени узла.
     */
    public void changeName(final ModelNodeTree nodeTree, final String newName) {
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
     * @return можно ли редактировать имя.
     */
    public boolean canEditName() {
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
