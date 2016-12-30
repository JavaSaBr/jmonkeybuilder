package com.ss.editor.ui.dialog.model.animation;

import static javafx.collections.FXCollections.observableArrayList;

import com.jme3.animation.LoopMode;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.node.control.anim.AnimationControlModelNode;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.dialog.EditorDialog;

import org.jetbrains.annotations.NotNull;

import java.awt.Point;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
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
public class PlayParametersDialog extends EditorDialog {

    private static final Insets OK_BUTTON_OFFSET = new Insets(0, 4, 0, 0);
    private static final Insets CANCEL_BUTTON_OFFSET = new Insets(0, 15, 0, 0);

    private static final Point DIALOG_SIZE = new Point(500, 150);

    private static final Insets FIELD_OFFSET = new Insets(2, 20, 0, 0);
    private static final Insets BUTTONS_OFFSET = new Insets(25, 0, 0, 0);

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

    /**
     * The ok button.
     */
    private Button okButton;

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
        return "Settings";
    }

    @Override
    protected void createContent(@NotNull final VBox root) {
        super.createContent(root);

        root.setAlignment(Pos.CENTER_LEFT);

        final HBox loopModeContainer = new HBox();

        final Label loopModeLabel = new Label("Loop mode:");
        loopModeLabel.setId(CSSIds.SETTINGS_DIALOG_LABEL);

        loopModeComboBox = new ComboBox<>(LOOP_MODES);
        loopModeComboBox.setId(CSSIds.SETTINGS_DIALOG_FIELD);
        loopModeComboBox.prefWidthProperty().bind(root.widthProperty());

        final AnimationControlModelNode node = getNode();

        FXUtils.addToPane(loopModeLabel, loopModeContainer);
        FXUtils.addToPane(loopModeComboBox, loopModeContainer);
        FXUtils.addToPane(loopModeContainer, root);

        final HBox speedContainer = new HBox();

        final Label speedLabel = new Label("Speed:");
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
        VBox.setMargin(speedContainer, FIELD_OFFSET);
    }

    @Override
    protected void processKey(@NotNull final KeyEvent event) {
        super.processKey(event);
        if (event.getCode() == KeyCode.ENTER) {
            processOk();
        }
    }

    @Override
    protected void createActions(@NotNull final VBox root) {
        super.createActions(root);

        final HBox container = new HBox();
        container.setId(CSSIds.ASSET_EDITOR_DIALOG_BUTTON_CONTAINER);

        okButton = new Button(Messages.GENERATE_TANGENTS_DIALOG_BUTTON_OK);
        okButton.setId(CSSIds.EDITOR_DIALOG_BUTTON_OK);
        okButton.setOnAction(event -> processOk());

        final Button cancelButton = new Button(Messages.GENERATE_TANGENTS_DIALOG_BUTTON_CANCEL);
        cancelButton.setId(CSSIds.EDITOR_DIALOG_BUTTON_CANCEL);
        cancelButton.setOnAction(event -> hide());

        FXUtils.addToPane(okButton, container);
        FXUtils.addToPane(cancelButton, container);
        FXUtils.addToPane(container, root);

        FXUtils.addClassTo(okButton, CSSClasses.SPECIAL_FONT_16);
        FXUtils.addClassTo(cancelButton, CSSClasses.SPECIAL_FONT_16);

        HBox.setMargin(okButton, OK_BUTTON_OFFSET);
        HBox.setMargin(cancelButton, CANCEL_BUTTON_OFFSET);

        VBox.setMargin(container, BUTTONS_OFFSET);
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

    /**
     * Handle changing.
     */
    private void processOk() {

        final ModelNodeTree nodeTree = getNodeTree();
        final ModelChangeConsumer modelChangeConsumer = nodeTree.getModelChangeConsumer();

        final ComboBox<LoopMode> loopModeComboBox = getLoopModeComboBox();
        final SingleSelectionModel<LoopMode> selectionModel = loopModeComboBox.getSelectionModel();

        final TextField speedField = getSpeedField();
        final float speed = Float.parseFloat(speedField.getText());

        final AnimationControlModelNode node = getNode();
        node.updateSettings(selectionModel.getSelectedItem(), speed);

        hide();
    }

    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
