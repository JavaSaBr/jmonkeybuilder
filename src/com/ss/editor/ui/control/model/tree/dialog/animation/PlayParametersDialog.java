package com.ss.editor.ui.control.model.tree.dialog.animation;

import static javafx.collections.FXCollections.observableArrayList;

import com.jme3.animation.LoopMode;
import com.ss.editor.Messages;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.dialog.AbstractNodeDialog;
import com.ss.editor.ui.control.model.tree.node.control.anim.AnimationControlModelNode;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;

import org.jetbrains.annotations.NotNull;

import java.awt.Point;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;

/**
 * The implementation of a dialog with play animation parameters.
 *
 * @author JavaSaBr
 */
public class PlayParametersDialog extends AbstractNodeDialog {

    private static final Point DIALOG_SIZE = new Point(450, 154);

    private static final Insets FIELD_OFFSET = new Insets(6, CANCEL_BUTTON_OFFSET.getRight(), 0, 0);
    private static final Insets LAST_FIELD_OFFSET = new Insets(FIELD_OFFSET.getTop(),
            CANCEL_BUTTON_OFFSET.getRight(), 20, 0);

    private static final ObservableList<LoopMode> LOOP_MODES = observableArrayList(LoopMode.values());

    /**
     * The model tree component.
     */
    private final ModelNodeTree nodeTree;

    /**
     * The animation control node.
     */
    private final AnimationControlModelNode node;

    /**
     * The list of loop modes.
     */
    private ComboBox<LoopMode> loopModeComboBox;

    /**
     * The field with a value of speed.
     */
    private TextField speedField;

    public PlayParametersDialog(final ModelNodeTree nodeTree, final AnimationControlModelNode node) {
        this.nodeTree = nodeTree;
        this.node = node;

        final SingleSelectionModel<LoopMode> selectionModel = loopModeComboBox.getSelectionModel();
        selectionModel.select(node.getLoopMode());

        speedField.setText(String.valueOf(node.getSpeed()));
    }

    /**
     * @return the model tree component.
     */
    protected ModelNodeTree getNodeTree() {
        return nodeTree;
    }

    /**
     * @return the animation control node.
     */
    protected AnimationControlModelNode getNode() {
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

        final HBox loopModeContainer = new HBox();

        final Label loopModeLabel = new Label(Messages.PLAY_ANIMATION_SETTINDS_DIALOG_LOOP_MODE + ":");
        loopModeLabel.setId(CSSIds.SETTINGS_DIALOG_LABEL);

        loopModeComboBox = new ComboBox<>(LOOP_MODES);
        loopModeComboBox.setId(CSSIds.SETTINGS_DIALOG_FIELD);
        loopModeComboBox.prefWidthProperty().bind(root.widthProperty());

        final AnimationControlModelNode node = getNode();

        FXUtils.addToPane(loopModeLabel, loopModeContainer);
        FXUtils.addToPane(loopModeComboBox, loopModeContainer);
        FXUtils.addToPane(loopModeContainer, root);

        final HBox speedContainer = new HBox();

        final Label speedLabel = new Label(Messages.PLAY_ANIMATION_SETTINDS_DIALOG_SPEED + ":");
        speedLabel.setId(CSSIds.SETTINGS_DIALOG_LABEL);

        speedField = new TextField();
        speedField.setId(CSSIds.SETTINGS_DIALOG_FIELD);
        speedField.prefWidthProperty().bind(root.widthProperty());

        FXUtils.addToPane(speedLabel, speedContainer);
        FXUtils.addToPane(speedField, speedContainer);
        FXUtils.addToPane(speedContainer, root);

        FXUtils.addClassTo(loopModeLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(loopModeComboBox, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(speedLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(speedField, CSSClasses.SPECIAL_FONT_14);

        VBox.setMargin(loopModeContainer, FIELD_OFFSET);
        VBox.setMargin(speedContainer, LAST_FIELD_OFFSET);
    }

    @Override
    protected void processKey(@NotNull final KeyEvent event) {
        super.processKey(event);
        if (event.getCode() == KeyCode.ENTER) {
            processOk();
        }
    }

    /**
     * @return the field with a value of speed.
     */
    private TextField getSpeedField() {
        return speedField;
    }

    /**
     * @return the list of loop modes.
     */
    private ComboBox<LoopMode> getLoopModeComboBox() {
        return loopModeComboBox;
    }

    @Override
    protected void processOk() {

        final ComboBox<LoopMode> loopModeComboBox = getLoopModeComboBox();
        final SingleSelectionModel<LoopMode> selectionModel = loopModeComboBox.getSelectionModel();

        final TextField speedField = getSpeedField();

        float speed;
        try {
            speed = Float.parseFloat(speedField.getText());
        } catch (final NumberFormatException e) {
            speed = 1F;
        }

        final AnimationControlModelNode node = getNode();
        node.updateSettings(selectionModel.getSelectedItem(), speed);

        hide();
    }

    @Override
    protected String getButtonOkLabel() {
        return Messages.PLAY_ANIMATION_SETTINDS_DIALOG_BUTTON_OK;
    }

    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
