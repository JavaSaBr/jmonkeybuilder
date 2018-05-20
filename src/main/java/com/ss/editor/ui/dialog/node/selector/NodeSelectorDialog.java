package com.ss.editor.ui.dialog.node.selector;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.scene.Spatial;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.control.model.ModelNodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.dialog.AbstractSimpleEditorDialog;
import com.ss.rlib.fx.util.FXUtils;
import com.ss.rlib.common.util.array.Array;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.function.Consumer;

/**
 * The implementation of a dialog to select an object from a model.
 *
 * @param <T> the type of a node.
 * @author JavaSaBr
 */
public class NodeSelectorDialog<T> extends AbstractSimpleEditorDialog {

    @NotNull
    private static final Point DIALOG_SIZE = new Point(600, -1);

    /**
     * The type of selectable objects.
     */
    @NotNull
    private final Class<T> type;

    /**
     * The handler of selected object.
     */
    @NotNull
    private final Consumer<T> handler;

    /**
     * The loaded model.
     */
    @NotNull
    private final Spatial model;

    /**
     * The model tree component.
     */
    @Nullable
    private ModelNodeTree nodeTree;

    /**
     * The selected object.
     */
    @Nullable
    private T selected;

    public NodeSelectorDialog(@NotNull final Spatial model, @NotNull final Class<T> type,
                              @NotNull final Consumer<T> handler) {
        this.model = model;
        this.type = type;
        this.handler = handler;

        final ModelNodeTree nodeTree = getNodeTree();
        nodeTree.fill(getModel());
        nodeTree.getTreeView().setEditable(false);

        final Button okButton = notNull(getOkButton());
        okButton.setDisable(true);
    }

    /**
     * Get the node tree.
     *
     * @return the model tree component.
     */
    @FxThread
    protected @NotNull ModelNodeTree getNodeTree() {
        return notNull(nodeTree);
    }

    @Override
    @FromAnyThread
    protected @NotNull String getTitleText() {
        return Messages.NODE_SELECTOR_DIALOG_TITLE;
    }

    @Override
    @FxThread
    protected void createContent(@NotNull final GridPane root) {
        super.createContent(root);

        nodeTree = new ModelNodeTree(this::processSelect, null, SelectionMode.SINGLE);
        nodeTree.prefHeightProperty().bind(heightProperty());
        nodeTree.prefWidthProperty().bind(widthProperty());

        root.add(nodeTree, 0, 0);

        FXUtils.addClassTo(root, CssClasses.NODE_SELECTOR_DIALOG);
    }

    @Override
    @FromAnyThread
    protected boolean isGridStructure() {
        return true;
    }

    /**
     * Get the loaded model.
     *
     * @return the loaded model.
     */
    @FxThread
    protected @NotNull Spatial getModel() {
        return model;
    }

    /**
     * Get the type of selectable objects.
     *
     * @return the type of selectable objects.
     */
    @FxThread
    protected @NotNull Class<T> getType() {
        return type;
    }

    /**
     * Handle the selected objects.
     */
    @FxThread
    private void processSelect(@Nullable final Array<Object> objects) {
        final Object object = objects.first();
        final Object result = object instanceof TreeNode ? ((TreeNode) object).getElement() : object;
        final Class<T> type = getType();
        final Button okButton = notNull(getOkButton());
        okButton.setDisable(!type.isInstance(result));
        selected = type.isInstance(result) ? type.cast(result) : null;
    }

    @Override
    @FxThread
    protected void processOk() {
        handler.accept(selected);
        super.processOk();
    }

    @Override
    @FromAnyThread
    protected @NotNull String getButtonOkText() {
        return Messages.SIMPLE_DIALOG_BUTTON_SELECT;
    }

    @Override
    @FromAnyThread
    protected @NotNull Point getSize() {
        return DIALOG_SIZE;
    }
}
