package com.ss.editor.ui.control.tree.action;

import static com.ss.rlib.util.ClassUtils.unsafeCast;
import com.ss.editor.JmeApplication;
import com.ss.editor.JfxApplication;
import com.ss.editor.analytics.google.GAEvent;
import com.ss.editor.analytics.google.GAnalytics;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.rlib.logging.Logger;
import com.ss.rlib.logging.LoggerManager;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The base implementation of an action for an element in a node tree.
 *
 * @param <C> the type parameter
 * @author JavaSaBr
 */
public abstract class AbstractNodeAction<C extends ChangeConsumer> extends MenuItem {

    /**
     * The logger.
     */
    @NotNull
    protected static final Logger LOGGER = LoggerManager.getLogger(AbstractNodeAction.class);

    /**
     * The executor manager.
     */
    @NotNull
    protected static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    /**
     * The FX application of this Editor.
     */
    @NotNull
    protected static final JfxApplication JFX_APPLICATION = JfxApplication.getInstance();

    /**
     * The Editor.
     */
    @NotNull
    protected static final JmeApplication JME_APPLICATION = JmeApplication.getInstance();

    /**
     * The component of the node tree.
     */
    @NotNull
    private final NodeTree<C> nodeTree;

    /**
     * The node.
     */
    @NotNull
    private final TreeNode<?> node;

    public AbstractNodeAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
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
     * Gets name.
     *
     * @return the name of this action.
     */
    @FXThread
    protected abstract @NotNull String getName();

    /**
     * Execute this action.
     */
    @FXThread
    protected void process() {
        GAnalytics.sendEvent(GAEvent.Category.EDITOR, GAEvent.Action.EXECUTE_NODE_ACTION, getClass().getSimpleName());
    }

    /**
     * The icon of this action.
     *
     * @return he icon or null.
     */
    @FXThread
    protected @Nullable Image getIcon() {
        return null;
    }

    /**
     * Gets node tree.
     *
     * @return the component of the model three.
     */
    @FXThread
    protected @NotNull NodeTree<C> getNodeTree() {
        return nodeTree;
    }

    /**
     * Gets node.
     *
     * @return the node of the model.
     */
    @FXThread
    protected @NotNull TreeNode<?> getNode() {
        return node;
    }

    @Override
    public String toString() {
        return "AbstractNodeAction{name = " + getNode() + "} " + super.toString();
    }
}
