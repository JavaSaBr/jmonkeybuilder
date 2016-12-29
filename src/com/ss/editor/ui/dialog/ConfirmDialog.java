package com.ss.editor.ui.dialog;

import com.ss.editor.Messages;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;

import org.jetbrains.annotations.NotNull;

import java.awt.Point;
import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import rlib.ui.util.FXUtils;

/**
 * The implementation of a dialog for asking questions.
 *
 * @author JavaSaBr
 */
public class ConfirmDialog extends EditorDialog {

    private static final Insets OK_BUTTON_OFFSET = new Insets(0, 4, 0, 0);
    private static final Insets CANCEL_BUTTON_OFFSET = new Insets(0, 15, 0, 0);

    private static final Point DIALOG_SIZE = new Point(600, 110);

    /**
     * The handler pf an answer.
     */
    private final Consumer<Boolean> handler;

    /**
     * The ok button.
     */
    private Button okButton;

    /**
     * The label.
     */
    private Label questionLabel;

    public ConfirmDialog(final Consumer<Boolean> handler, final String question) {
        this.handler = handler;
        this.questionLabel.setText(question);
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return Messages.QUESTION_DIALOG_TITLE;
    }

    @Override
    protected void createContent(@NotNull final VBox root) {
        super.createContent(root);

        questionLabel = new Label();
        questionLabel.setAlignment(Pos.CENTER);
        questionLabel.setTextAlignment(TextAlignment.CENTER);
        questionLabel.setWrapText(true);
        questionLabel.prefWidthProperty().bind(root.widthProperty().multiply(0.9));
        questionLabel.setPadding(new Insets(10, 0, 10, 0));

        FXUtils.addToPane(questionLabel, root);
        FXUtils.addClassTo(questionLabel, CSSClasses.SPECIAL_FONT_15);
    }

    @Override
    protected void processKey(@NotNull final KeyEvent event) {
        super.processKey(event);
        if (event.getCode() == KeyCode.ENTER) {
            processCancel();
        }
    }

    @Override
    protected void createActions(@NotNull final VBox root) {
        super.createActions(root);

        final HBox container = new HBox();
        container.setId(CSSIds.ASSET_EDITOR_DIALOG_BUTTON_CONTAINER);

        okButton = new Button(Messages.QUESTION_DIALOG_BUTTON_OK);
        okButton.setId(CSSIds.EDITOR_DIALOG_BUTTON_OK);
        okButton.setOnAction(event -> processOk());

        final Button cancelButton = new Button(Messages.QUESTION_DIALOG_BUTTON_CANCEL);
        cancelButton.setId(CSSIds.EDITOR_DIALOG_BUTTON_CANCEL);
        cancelButton.setOnAction(event -> hide());

        FXUtils.addToPane(okButton, container);
        FXUtils.addToPane(cancelButton, container);
        FXUtils.addToPane(container, root);

        FXUtils.addClassTo(okButton, CSSClasses.SPECIAL_FONT_16);
        FXUtils.addClassTo(cancelButton, CSSClasses.SPECIAL_FONT_16);

        HBox.setMargin(okButton, OK_BUTTON_OFFSET);
        HBox.setMargin(cancelButton, CANCEL_BUTTON_OFFSET);
    }

    /**
     * Handle OK answer.
     */
    private void processOk() {
        handler.accept(Boolean.TRUE);
        hide();
    }

    /**
     * Handle reject answer.
     */
    private void processCancel() {
        handler.accept(Boolean.FALSE);
        hide();
    }

    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
