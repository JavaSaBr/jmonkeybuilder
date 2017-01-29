package com.ss.editor.ui.control.model.tree.dialog;

import static java.util.Objects.requireNonNull;
import com.jme3.scene.Spatial;
import com.ss.editor.Messages;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.dialog.AbstractSimpleEditorDialog;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Point;
import java.util.Objects;
import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;

/**
 * The implementation of a dialog to select an object from a model.
 *
 * @author JavaSaBr
 */
public class NodeSelectorDialog<T> extends AbstractSimpleEditorDialog {

    @NotNull
    private static final Point DIALOG_SIZE = new Point(600, 450);

    @NotNull
    private static final Insets TREE_OFFSET = new Insets(6, CANCEL_BUTTON_OFFSET.getRight(), 20, 0);

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
        this.nodeTree.fill(getModel());

        final Button okButton = getOkButton();
        okButton.setDisable(true);
    }

    /**
     * @return the model tree component.
     */
    @NotNull
    protected ModelNodeTree getNodeTree() {
        return requireNonNull(nodeTree);
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return Messages.NODE_SELECTOR_DIALOG_TITLE;
    }

    @Override
    protected void createContent(@NotNull final VBox root) {
        super.createContent(root);

        nodeTree = new ModelNodeTree(this::processSelect, null);
        nodeTree.setId(CSSIds.ABSTRACT_NODE_TREE_TRANSPARENT_CONTAINER);
        nodeTree.prefHeightProperty().bind(heightProperty());

        FXUtils.addToPane(nodeTree, root);
        VBox.setMargin(nodeTree, TREE_OFFSET);
    }

    /**
     * @return the loaded model.
     */
    @NotNull
    protected Spatial getModel() {
        return model;
    }

    /**
     * @return the type of selectable objects.
     */
    @NotNull
    protected Class<T> getType() {
        return type;
    }

    /**
     * Handle a selected object.
     */
    private void processSelect(@Nullable final Object object) {
        final Object result = object instanceof ModelNode ? ((ModelNode) object).getElement() : object;
        final Class<T> type = getType();
        final Button okButton = getOkButton();
        okButton.setDisable(!type.isInstance(result));
        selected = type.isInstance(result) ? type.cast(result) : null;
    }

    @Override
    protected void processOk() {
        handler.accept(selected);
        super.processOk();
    }

    @NotNull
    @Override
    protected String getButtonOkLabel() {
        return Messages.NODE_SELECTOR_DIALOG_BUTTON;
    }

    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
