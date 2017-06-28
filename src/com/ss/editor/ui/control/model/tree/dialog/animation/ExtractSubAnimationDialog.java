package com.ss.editor.ui.control.model.tree.dialog.animation;

import static com.ss.editor.util.AnimationUtils.extractAnimation;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.animation.AnimControl;
import com.jme3.animation.Animation;
import com.ss.editor.Messages;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.model.node.control.anim.AnimationModelNode;
import com.ss.editor.ui.control.model.tree.action.operation.animation.AddAnimationNodeOperation;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.dialog.AbstractSimpleEditorDialog;
import com.ss.editor.util.AnimationUtils;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.ui.control.input.IntegerTextField;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * The implementation of a dialog to extract a sub animation.
 *
 * @author JavaSaBr
 */
public class ExtractSubAnimationDialog extends AbstractSimpleEditorDialog {

    @NotNull
    private static final Point DIALOG_SIZE = new Point(390, 0);

    @NotNull
    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    /**
     * The node tree component.
     */
    @NotNull
    private final AbstractNodeTree<?> nodeTree;

    /**
     * The animation node.
     */
    @NotNull
    private final AnimationModelNode node;

    /**
     * The field with a value of new animation name.
     */
    @Nullable
    private TextField nameField;

    /**
     * The field with a value of start frame.
     */
    @Nullable
    private IntegerTextField startFrameField;

    /**
     * The field with a value of end frame.
     */
    @Nullable
    private IntegerTextField endFrameField;

    /**
     * Instantiates a new Extract sub animation dialog.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public ExtractSubAnimationDialog(@NotNull final AbstractNodeTree<?> nodeTree,
                                     @NotNull final AnimationModelNode node) {
        this.nodeTree = nodeTree;
        this.node = node;

        final Animation animation = node.getElement();
        final AnimControl control = notNull(node.getControl());
        final int frameCount = AnimationUtils.getFrameCount(animation);

        final TextField nameField = getNameField();
        nameField.setText(AnimationUtils.findFreeName(control, Messages.MANUAL_EXTRACT_ANIMATION_DIALOG_NAME_EXAMPLE));

        final IntegerTextField startFrameField = getStartFrameField();
        startFrameField.setMinMax(0, frameCount - 2);
        startFrameField.setValue(0);

        final IntegerTextField endFrameField = getEndFrameField();
        endFrameField.setMinMax(1, frameCount - 1);
        endFrameField.setValue(frameCount - 1);
    }

    /**
     * Gets node tree.
     *
     * @return the node tree component.
     */
    @NotNull
    protected AbstractNodeTree<?> getNodeTree() {
        return nodeTree;
    }

    /**
     * Gets node.
     *
     * @return the animation node.
     */
    @NotNull
    protected AnimationModelNode getNode() {
        return node;
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return Messages.MANUAL_EXTRACT_ANIMATION_DIALOG_TITLE;
    }

    @Override
    protected void createContent(@NotNull final GridPane root) {
        super.createContent(root);

        final Label nameLabel = new Label(Messages.MANUAL_EXTRACT_ANIMATION_DIALOG_NAME + ":");
        nameLabel.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        nameLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT));

        nameField = new TextField();
        nameField.setId(CSSIds.EDITOR_DIALOG_FIELD);
        nameField.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));

        final Label startFrameLabel = new Label(Messages.MANUAL_EXTRACT_ANIMATION_DIALOG_START_FRAME + ":");
        startFrameLabel.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        startFrameLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT));

        startFrameField = new IntegerTextField();
        startFrameField.setId(CSSIds.EDITOR_DIALOG_FIELD);
        startFrameField.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));

        final Label endFrameLabel = new Label(Messages.MANUAL_EXTRACT_ANIMATION_DIALOG_END_FRAME + ":");
        endFrameLabel.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        endFrameLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT));

        endFrameField = new IntegerTextField();
        endFrameField.setId(CSSIds.EDITOR_DIALOG_FIELD);
        endFrameField.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));

        root.add(nameLabel, 0, 0);
        root.add(nameField, 1, 0);
        root.add(startFrameLabel, 0, 1);
        root.add(startFrameField, 1, 1);
        root.add(endFrameLabel, 0, 2);
        root.add(endFrameField, 1, 2);

        FXUtils.addClassTo(nameLabel, nameField, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(startFrameLabel, startFrameField, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(endFrameLabel, endFrameField, CSSClasses.SPECIAL_FONT_14);
    }

    @Override
    protected boolean isGridStructure() {
        return true;
    }

    /**
     * @return the field with a value of new animation name.
     */
    @NotNull
    private TextField getNameField() {
        return notNull(nameField);
    }

    /**
     * @return the field with a value of start frame.
     */
    @NotNull
    private IntegerTextField getStartFrameField() {
        return notNull(startFrameField);
    }

    /**
     * @return the field with a value of end frame.
     */
    @NotNull
    private IntegerTextField getEndFrameField() {
        return notNull(endFrameField);
    }

    @Override
    protected void processOk() {
        EditorUtil.incrementLoading();
        EXECUTOR_MANAGER.addBackgroundTask(this::processExtract);
        super.processOk();
    }

    /**
     * Process of extraction a sub animation.
     */
    private void processExtract() {

        final AnimationModelNode node = getNode();
        final AnimControl control = notNull(node.getControl());
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

        final AbstractNodeTree<?> nodeTree = getNodeTree();
        final ChangeConsumer changeConsumer = notNull(nodeTree.getChangeConsumer());
        changeConsumer.execute(new AddAnimationNodeOperation(subAnimation, control));

        EXECUTOR_MANAGER.addFXTask(EditorUtil::decrementLoading);
    }

    @NotNull
    @Override
    protected String getButtonOkLabel() {
        return Messages.MANUAL_EXTRACT_ANIMATION_DIALOG_BUTTON_OK;
    }

    @NotNull
    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
