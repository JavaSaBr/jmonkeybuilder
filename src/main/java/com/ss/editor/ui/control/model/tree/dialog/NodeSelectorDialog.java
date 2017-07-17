package com.ss.editor.ui.control.model.tree.dialog;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.scene.Spatial;
import com.ss.editor.Messages;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.dialog.AbstractSimpleEditorDialog;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.control.Button;
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

    /**
     * Instantiates a new Node selector dialog.
     *
     * @param model   the model
     * @param type    the type
     * @param handler the handler
     */
    public NodeSelectorDialog(@NotNull final Spatial model, @NotNull final Class<T> type,
                              @NotNull final Consumer<T> handler) {
        this.model = model;
        this.type = type;
        this.handler = handler;

        final ModelNodeTree nodeTree = getNodeTree();
        nodeTree.fill(getModel());

        final Button okButton = getOkButton();
        okButton.setDisable(true);
    }

    /**
     * Gets node tree.
     *
     * @return the model tree component.
     */
    @NotNull
    protected ModelNodeTree getNodeTree() {
        return notNull(nodeTree);
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return Messages.NODE_SELECTOR_DIALOG_TITLE;
    }

    @Override
    protected void createContent(@NotNull final GridPane root) {
        super.createContent(root);

        nodeTree = new ModelNodeTree(this::processSelect, null);
        nodeTree.prefHeightProperty().bind(heightProperty());
        nodeTree.prefWidthProperty().bind(widthProperty());

        root.add(nodeTree, 0, 0);

        FXUtils.addClassTo(root, CSSClasses.NODE_SELECTOR_DIALOG);
    }

    @Override
    protected boolean isGridStructure() {
        return true;
    }

    /**
     * Gets model.
     *
     * @return the loaded model.
     */
    @NotNull
    protected Spatial getModel() {
        return model;
    }

    /**
     * Gets type.
     *
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

    @NotNull
    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
