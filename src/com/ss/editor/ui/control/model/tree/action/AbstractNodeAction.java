package com.ss.editor.ui.control.model.tree.action;

import com.ss.editor.Editor;
import com.ss.editor.JFXApplication;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import rlib.logging.Logger;
import rlib.logging.LoggerManager;

/**
 * The base implementation of the action for an element in a model tree.
 *
 * @author JavaSaBr
 */
public abstract class AbstractNodeAction extends MenuItem {

    protected static final Logger LOGGER = LoggerManager.getLogger(AbstractNodeAction.class);

    protected static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();
    protected static final JFXApplication JFX_APPLICATION = JFXApplication.getInstance();
    protected static final Editor EDITOR = Editor.getInstance();

    /**
     * The component of the model three.
     */
    private final ModelNodeTree nodeTree;

    /**
     * The node of the model.
     */
    private final ModelNode<?> node;

    public AbstractNodeAction(@NotNull final ModelNodeTree nodeTree, @NotNull final ModelNode<?> node) {
        this.nodeTree = nodeTree;
        this.node = node;
        setOnAction(event -> process());
        setText(getName());

        final Image icon = getIcon();
        if (icon != null) {
            setGraphic(new ImageView(icon));
        }
    }

    /**
     * @return the name of this action.
     */
    @NotNull
    protected abstract String getName();

    /**
     * Execute this action.
     */
    protected abstract void process();

    /**
     * The icon of this action.
     *
     * @return he icon or null.
     */
    @Nullable
    protected Image getIcon() {
        return null;
    }

    /**
     * @return the component of the model three.
     */
    @NotNull
    protected ModelNodeTree getNodeTree() {
        return nodeTree;
    }

    /**
     * @return the node of the model.
     */
    @NotNull
    protected ModelNode<?> getNode() {
        return node;
    }

    @Override
    public String toString() {
        return "AbstractNodeAction{name = " + getNode() + "} " + super.toString();
    }
}
