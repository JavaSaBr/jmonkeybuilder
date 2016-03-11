package com.ss.editor.ui.control.model.tree.action;

import com.ss.editor.Editor;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.node.ModelNode;

import javafx.scene.control.MenuItem;

/**
 * Базовая реализация действия над моделью.
 *
 * @author Ronn
 */
public abstract class AbstractNodeAction extends MenuItem {

    protected static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();
    protected static final Editor EDITOR = Editor.getInstance();

    /**
     * Компонент структуры модели.
     */
    private final ModelNodeTree nodeTree;

    /**
     * Узел модели.
     */
    private final ModelNode<?> node;

    public AbstractNodeAction(final ModelNodeTree nodeTree, final ModelNode<?> node) {
        this.nodeTree = nodeTree;
        this.node = node;
        setOnAction(event -> process());
        setText(getName());
    }

    /**
     * @return название действия.
     */
    protected abstract String getName();

    /**
     * Выполнение действий.
     */
    protected abstract void process();

    /**
     * @return компонент структуры модели.
     */
    protected ModelNodeTree getNodeTree() {
        return nodeTree;
    }

    /**
     * @return узел модели.
     */
    protected ModelNode<?> getNode() {
        return node;
    }
}
