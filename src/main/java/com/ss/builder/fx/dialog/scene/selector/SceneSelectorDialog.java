package com.ss.builder.ui.dialog.scene.selector;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.scene.Spatial;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.ui.control.scene.SceneNodeTree;
import com.ss.builder.ui.control.tree.node.TreeNode;
import com.ss.builder.ui.css.CssClasses;
import com.ss.builder.ui.dialog.AbstractSimpleEditorDialog;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.extension.scene.SceneNode;
import com.ss.editor.ui.control.scene.SceneNodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.dialog.AbstractSimpleEditorDialog;
import com.ss.rlib.fx.util.FXUtils;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.function.Consumer;

/**
 * The implementation of a dialog to select an object from a scene.
 *
 * @param <T> the type of a node.
 * @author JavaSaBr
 */
public class SceneSelectorDialog<T> extends AbstractSimpleEditorDialog {

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
     * The scene node.
     */
    @NotNull
    private final SceneNode sceneNode;

    /**
     * The scene tree component.
     */
    @Nullable
    private SceneNodeTree nodeTree;

    /**
     * The selected object.
     */
    @Nullable
    private T selected;

    public SceneSelectorDialog(@NotNull final SceneNode sceneNode, @NotNull final Class<T> type,
                               @NotNull final Consumer<T> handler) {
        this.sceneNode = sceneNode;
        this.type = type;
        this.handler = handler;

        final SceneNodeTree nodeTree = getNodeTree();
        nodeTree.fill(getSceneNode());
        nodeTree.getTreeView().setEditable(false);

        final Button okButton = notNull(getOkButton());
        okButton.setDisable(true);
    }

    /**
     * Get the node tree.
     *
     * @return the node tree component.
     */
    @FxThread
    protected @NotNull SceneNodeTree getNodeTree() {
        return notNull(nodeTree);
    }

    @Override
    @FromAnyThread
    protected @NotNull String getTitleText() {
        return Messages.SCENE_ELEMENT_SELECTOR_DIALOG_TITLE;
    }

    @Override
    @FxThread
    protected void createContent(@NotNull final GridPane root) {
        super.createContent(root);

        nodeTree = createSceneNodeTree();
        nodeTree.prefHeightProperty().bind(heightProperty());
        nodeTree.prefWidthProperty().bind(widthProperty());

        root.add(nodeTree, 0, 0);

        FXUtils.addClassTo(root, CssClasses.NODE_SELECTOR_DIALOG);
    }

    /**
     * Create a new scene node tree.
     *
     * @return the new scene node tree.
     */
    @FxThread
    protected @NotNull SceneNodeTree createSceneNodeTree() {
        return new SceneNodeTree(this::processSelect, null);
    }

    @Override
    @FromAnyThread
    protected boolean isGridStructure() {
        return true;
    }

    /**
     * Get the scene node.
     *
     * @return the scene node.
     */
    @FxThread
    protected @NotNull Spatial getSceneNode() {
        return sceneNode;
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
     * Handle the selected object.
     */
    @FxThread
    private void processSelect(@Nullable final Object object) {
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
