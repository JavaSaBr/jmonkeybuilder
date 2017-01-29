package com.ss.editor.ui.control.model.tree.dialog.animation;

import static java.util.Objects.requireNonNull;
import static javafx.collections.FXCollections.observableArrayList;
import com.jme3.animation.LoopMode;
import com.ss.editor.Messages;
import com.ss.editor.ui.control.model.node.control.anim.AnimationControlModelNode;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.dialog.AbstractSimpleEditorDialog;
import javafx.collections.ObservableList;
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
import org.jetbrains.annotations.Nullable;
import rlib.ui.control.input.FloatTextField;
import rlib.ui.util.FXUtils;

import java.awt.*;
import java.util.Objects;

/**
 * The implementation of a dialog with play animation parameters.
 *
 * @author JavaSaBr
 */
public class PlayParametersDialog extends AbstractSimpleEditorDialog {

    @NotNull
    private static final Point DIALOG_SIZE = new Point(400, 154);

    @NotNull
    private static final Insets FIELD_OFFSET = new Insets(6, CANCEL_BUTTON_OFFSET.getRight(), 0, 0);

    @NotNull
    private static final Insets LAST_FIELD_OFFSET = new Insets(FIELD_OFFSET.getTop(), CANCEL_BUTTON_OFFSET.getRight(), 20, 0);

    /**
     * The list of loop modes.
     */
    @NotNull
    private static final ObservableList<LoopMode> LOOP_MODES = observableArrayList(LoopMode.values());

    /**
     * The animation control node.
     */
    @NotNull
    private final AnimationControlModelNode node;

    /**
     * The list of loop modes.
     */
    @Nullable
    private ComboBox<LoopMode> loopModeComboBox;

    /**
     * The field with a value of speed.
     */
    @Nullable
    private FloatTextField speedField;

    public PlayParametersDialog(@NotNull final AnimationControlModelNode node) {
        this.node = node;

        final ComboBox<LoopMode> loopModeComboBox = getLoopModeComboBox();
        final SingleSelectionModel<LoopMode> selectionModel = loopModeComboBox.getSelectionModel();
        selectionModel.select(node.getLoopMode());

        final FloatTextField speedField = getSpeedField();
        speedField.setValue(node.getSpeed());
    }

    /**
     * @return the animation control node.
     */
    @NotNull
    protected AnimationControlModelNode getNode() {
        return node;
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return Messages.PLAY_ANIMATION_SETTINGS_DIALOG_TITLE;
    }

    @Override
    protected void createContent(@NotNull final VBox root) {
        super.createContent(root);

        root.setAlignment(Pos.CENTER_LEFT);

        final HBox loopModeContainer = new HBox();

        final Label loopModeLabel = new Label(Messages.PLAY_ANIMATION_SETTINGS_DIALOG_LOOP_MODE + ":");
        loopModeLabel.setId(CSSIds.EDITOR_DIALOG_SHORT_LABEL);

        loopModeComboBox = new ComboBox<>(LOOP_MODES);
        loopModeComboBox.setId(CSSIds.SETTINGS_DIALOG_FIELD);
        loopModeComboBox.prefWidthProperty().bind(root.widthProperty());

        FXUtils.addToPane(loopModeLabel, loopModeContainer);
        FXUtils.addToPane(loopModeComboBox, loopModeContainer);
        FXUtils.addToPane(loopModeContainer, root);

        final HBox speedContainer = new HBox();

        final Label speedLabel = new Label(Messages.PLAY_ANIMATION_SETTINGS_DIALOG_SPEED + ":");
        speedLabel.setId(CSSIds.EDITOR_DIALOG_SHORT_LABEL);

        speedField = new FloatTextField();
        speedField.setId(CSSIds.SETTINGS_DIALOG_FIELD);
        speedField.setMinMax(0.01F, 100F);
        speedField.setScrollPower(2F);
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

    /**
     * @return the field with a value of speed.
     */
    @NotNull
    private FloatTextField getSpeedField() {
        return requireNonNull(speedField);
    }

    /**
     * @return the list of loop modes.
     */
    @NotNull
    private ComboBox<LoopMode> getLoopModeComboBox() {
        return requireNonNull(loopModeComboBox);
    }

    @Override
    protected void processOk() {

        final ComboBox<LoopMode> loopModeComboBox = getLoopModeComboBox();
        final SingleSelectionModel<LoopMode> selectionModel = loopModeComboBox.getSelectionModel();

        final FloatTextField speedField = getSpeedField();
        final float speed = speedField.getValue();

        final AnimationControlModelNode node = getNode();
        node.updateSettings(selectionModel.getSelectedItem(), speed);

        super.processOk();
    }

    @NotNull
    @Override
    protected String getButtonOkLabel() {
        return Messages.PLAY_ANIMATION_SETTINGS_DIALOG_BUTTON_OK;
    }

    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
