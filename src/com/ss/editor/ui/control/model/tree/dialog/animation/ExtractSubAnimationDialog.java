package com.ss.editor.ui.control.model.tree.dialog.animation;

import static com.ss.editor.util.AnimationUtils.extractAnimation;

import com.jme3.animation.AnimControl;
import com.jme3.animation.Animation;
import com.ss.editor.Messages;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.fx.IntegerTextField;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.operation.animation.AddAnimationNodeOperation;
import com.ss.editor.ui.control.model.tree.dialog.AbstractNodeDialog;
import com.ss.editor.ui.control.model.tree.node.control.anim.AnimationModelNode;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.util.AnimationUtils;
import com.ss.editor.util.EditorUtil;

import org.jetbrains.annotations.NotNull;

import java.awt.Point;
import java.util.Objects;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;

/**
 * The implementation of a dialog to extract a sub animation.
 *
 * @author JavaSaBr
 */
public class ExtractSubAnimationDialog extends AbstractNodeDialog {

    private static final Point DIALOG_SIZE = new Point(350, 184);

    private static final Insets FIELD_OFFSET = new Insets(6, CANCEL_BUTTON_OFFSET.getRight(), 0, 0);
    private static final Insets LAST_FIELD_OFFSET = new Insets(FIELD_OFFSET.getTop(),
            CANCEL_BUTTON_OFFSET.getRight(), 20, 0);

    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    /**
     * The model tree component.
     */
    private final ModelNodeTree nodeTree;

    /**
     * The animation node.
     */
    private final AnimationModelNode node;

    /**
     * The field with a value of new animation name.
     */
    private TextField nameField;

    /**
     * The field with a value of start frame.
     */
    private IntegerTextField startFrameField;

    /**
     * The field with a value of end frame.
     */
    private IntegerTextField endFrameField;

    public ExtractSubAnimationDialog(@NotNull final ModelNodeTree nodeTree, @NotNull final AnimationModelNode node) {
        this.nodeTree = nodeTree;
        this.node = node;

        final Animation animation = node.getElement();
        final AnimControl control = Objects.requireNonNull(node.getControl());
        final int frameCount = AnimationUtils.getFrameCount(animation);

        final TextField nameField = getNameField();
        nameField.setText(AnimationUtils.findFreeName(control, "New Animation"));

        final IntegerTextField startFrameField = getStartFrameField();
        startFrameField.setMinMax(0, frameCount - 2);
        startFrameField.setValue(0);

        final IntegerTextField endFrameField = getEndFrameField();
        endFrameField.setMinMax(1, frameCount - 1);
        endFrameField.setValue(frameCount - 1);
    }

    /**
     * @return the model tree component.
     */
    protected ModelNodeTree getNodeTree() {
        return nodeTree;
    }

    /**
     * @return the animation node.
     */
    protected AnimationModelNode getNode() {
        return node;
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return Messages.PLAY_ANIMATION_SETTINDS_DIALOG_TITLE;
    }

    @Override
    protected void createContent(@NotNull final VBox root) {
        super.createContent(root);

        root.setAlignment(Pos.CENTER_LEFT);

        final HBox nameContainer = new HBox();

        final Label nameLabel = new Label("Name" + ":");
        nameLabel.setId(CSSIds.EDITOR_DIALOG_SHORT_LABEL);

        nameField = new TextField();
        nameField.setId(CSSIds.SETTINGS_DIALOG_FIELD);
        nameField.prefWidthProperty().bind(root.widthProperty());

        FXUtils.addToPane(nameLabel, nameContainer);
        FXUtils.addToPane(nameField, nameContainer);
        FXUtils.addToPane(nameContainer, root);

        final HBox startFrameContainer = new HBox();

        final Label startFrameLabel = new Label("Start frame" + ":");
        startFrameLabel.setId(CSSIds.EDITOR_DIALOG_SHORT_LABEL);

        startFrameField = new IntegerTextField();
        startFrameField.setId(CSSIds.SETTINGS_DIALOG_FIELD);
        startFrameField.prefWidthProperty().bind(root.widthProperty());

        FXUtils.addToPane(startFrameLabel, startFrameContainer);
        FXUtils.addToPane(startFrameField, startFrameContainer);
        FXUtils.addToPane(startFrameContainer, root);

        final HBox endFrameContainer = new HBox();

        final Label endFrameLabel = new Label("End frame" + ":");
        endFrameLabel.setId(CSSIds.EDITOR_DIALOG_SHORT_LABEL);

        endFrameField = new IntegerTextField();
        endFrameField.setId(CSSIds.SETTINGS_DIALOG_FIELD);
        endFrameField.prefWidthProperty().bind(root.widthProperty());

        FXUtils.addToPane(endFrameLabel, endFrameContainer);
        FXUtils.addToPane(endFrameField, endFrameContainer);
        FXUtils.addToPane(endFrameContainer, root);

        FXUtils.addClassTo(nameLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(nameField, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(startFrameLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(startFrameField, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(endFrameLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(endFrameField, CSSClasses.SPECIAL_FONT_14);

        VBox.setMargin(nameContainer, FIELD_OFFSET);
        VBox.setMargin(startFrameContainer, FIELD_OFFSET);
        VBox.setMargin(endFrameContainer, LAST_FIELD_OFFSET);
    }

    @Override
    protected void processKey(@NotNull final KeyEvent event) {
        super.processKey(event);
        if (event.getCode() == KeyCode.ENTER) {
            processOk();
        }
    }

    /**
     * @return the field with a value of new animation name.
     */
    private TextField getNameField() {
        return nameField;
    }

    /**
     * @return the field with a value of start frame.
     */
    private IntegerTextField getStartFrameField() {
        return startFrameField;
    }

    /**
     * @return the field with a value of end frame.
     */
    private IntegerTextField getEndFrameField() {
        return endFrameField;
    }

    @Override
    protected void processOk() {
        EditorUtil.incrementLoading();
        EXECUTOR_MANAGER.addBackgroundTask(this::processExtract);
        hide();
    }

    /**
     * Process of extraction a sub animation.
     */
    protected void processExtract() {

        final AnimationModelNode node = getNode();
        final AnimControl control = node.getControl();
        final Animation animation = node.getElement();

        final TextField nameField = getNameField();
        final IntegerTextField startFrameField = getStartFrameField();
        final IntegerTextField endFrameField = getEndFrameField();

        int startFrame = startFrameField.getValue();
        int endFrame = endFrameField.getValue();

        if (startFrame >= endFrame) {
            startFrame = endFrame - 1;
        }

        final Animation subAnimation = extractAnimation(animation, nameField.getText(), startFrame, endFrame);

        final ModelNodeTree nodeTree = getNodeTree();
        final ModelChangeConsumer modelChangeConsumer = nodeTree.getModelChangeConsumer();
        modelChangeConsumer.execute(new AddAnimationNodeOperation(subAnimation, control));

        EXECUTOR_MANAGER.addFXTask(EditorUtil::decrementLoading);
    }

    @Override
    protected String getButtonOkLabel() {
        return "Create";
    }

    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
