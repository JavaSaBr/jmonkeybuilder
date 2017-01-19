package com.ss.editor.ui.dialog;

import com.ss.editor.Messages;
import com.ss.editor.ui.css.CSSClasses;

import org.jetbrains.annotations.NotNull;

import java.awt.Point;
import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import rlib.ui.util.FXUtils;

/**
 * The implementation of a dialog for asking questions.
 *
 * @author JavaSaBr
 */
public class ConfirmDialog extends AbstractSimpleEditorDialog {

    private static final Insets QUESTION_PADDING = new Insets(10, 0, 10, 0);
    private static final Point DIALOG_SIZE = new Point(600, 110);

    /**
     * The handler pf an answer.
     */
    private final Consumer<Boolean> handler;

    /**
     * The label.
     */
    private Label questionLabel;

    public ConfirmDialog(@NotNull final Consumer<Boolean> handler, @NotNull final String question) {
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
        questionLabel.setPadding(QUESTION_PADDING);

        FXUtils.addToPane(questionLabel, root);
        FXUtils.addClassTo(questionLabel, CSSClasses.SPECIAL_FONT_15);
    }

    @NotNull
    @Override
    protected String getButtonOkLabel() {
        return Messages.QUESTION_DIALOG_BUTTON_OK;
    }

    @NotNull
    @Override
    protected String getButtonCancelLabel() {
        return Messages.QUESTION_DIALOG_BUTTON_CANCEL;
    }

    @Override
    protected void processKey(@NotNull final KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            processCancel();
        }
    }

    @Override
    protected void processOk() {
        super.processOk();
        handler.accept(Boolean.TRUE);
    }

    @Override
    protected void processCancel() {
        super.processCancel();
        handler.accept(Boolean.FALSE);
    }

    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
