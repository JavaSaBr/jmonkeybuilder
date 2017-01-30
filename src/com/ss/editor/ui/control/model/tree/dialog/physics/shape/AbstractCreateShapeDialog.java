package com.ss.editor.ui.control.model.tree.dialog.physics.shape;

import static javafx.collections.FXCollections.observableArrayList;
import com.jme3.animation.LoopMode;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.node.control.anim.AnimationControlModelNode;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.dialog.AbstractSimpleEditorDialog;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import rlib.ui.control.input.FloatTextField;
import rlib.ui.util.FXUtils;

import java.awt.*;

/**
 * The implementation of a dialog to create a collision shape.
 *
 * @author JavaSaBr
 */
public abstract class AbstractCreateShapeDialog extends AbstractSimpleEditorDialog {

    private static final Point DIALOG_SIZE = new Point(400, 154);

    private static final Insets FIELD_OFFSET = new Insets(6, CANCEL_BUTTON_OFFSET.getRight(), 0, 0);
    private static final Insets LAST_FIELD_OFFSET = new Insets(FIELD_OFFSET.getTop(),
            CANCEL_BUTTON_OFFSET.getRight(), 20, 0);

    /**
     * The node tree component.
     */
    @NotNull
    private final AbstractNodeTree<?> nodeTree;

    /**
     * The collision object.
     */
    @NotNull
    private final PhysicsCollisionObject collisionObject;

    public AbstractCreateShapeDialog(@NotNull final AbstractNodeTree<ModelChangeConsumer> nodeTree,
                                     @NotNull final PhysicsCollisionObject collisionObject) {
        this.nodeTree = nodeTree;
        this.collisionObject = collisionObject;
    }

    /**
     * @return the node tree component.
     */
    @NotNull
    protected AbstractNodeTree<?> getNodeTree() {
        return nodeTree;
    }

    /**
     * @return the collision object.
     */
    @NotNull
    public PhysicsCollisionObject getCollisionObject() {
        return collisionObject;
    }

    @Override
    protected void createContent(@NotNull final VBox root) {
        super.createContent(root);
        root.setAlignment(Pos.CENTER_LEFT);
    }

    @Override
    protected void processOk() {
        super.processOk();
    }

    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
