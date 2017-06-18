package com.ss.editor.ui.control.model.tree.action;

import static com.ss.rlib.util.ClassUtils.unsafeCast;

import com.ss.editor.Editor;
import com.ss.editor.JFXApplication;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import com.ss.rlib.logging.Logger;
import com.ss.rlib.logging.LoggerManager;

/**
 * The base implementation of an action for an element in a node tree.
 *
 * @author JavaSaBr
 */
public abstract class AbstractNodeAction<C extends ChangeConsumer> extends MenuItem {

    protected static final Logger LOGGER = LoggerManager.getLogger(AbstractNodeAction.class);

    protected static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();
    protected static final JFXApplication JFX_APPLICATION = JFXApplication.getInstance();
    protected static final Editor EDITOR = Editor.getInstance();

    /**
     * The component of the node tree.
     */
    @NotNull
    private final AbstractNodeTree<C> nodeTree;

    /**
     * The node.
     */
    @NotNull
    private final ModelNode<?> node;

    public AbstractNodeAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
        this.nodeTree = unsafeCast(nodeTree);
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
    @FXThread
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
    protected AbstractNodeTree<C> getNodeTree() {
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
