package com.ss.editor.ui.dialog;

import static java.util.Objects.requireNonNull;
import com.ss.editor.Messages;
import com.ss.editor.ui.css.CSSClasses;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.ss.rlib.ui.util.FXUtils;

import java.awt.*;
import java.util.function.Consumer;

/**
 * The implementation of a dialog to ask questions.
 *
 * @author JavaSaBr
 */
public class ConfirmDialog extends AbstractSimpleEditorDialog {

    private static final Insets QUESTION_PADDING = new Insets(10, 0, 10, 0);
    private static final Point DIALOG_SIZE = new Point(600, 131);

    /**
     * The handler of an answer.
     */
    @NotNull
    private final Consumer<Boolean> handler;

    /**
     * The label.
     */
    @Nullable
    private Label questionLabel;

    /**
     * Instantiates a new Confirm dialog.
     *
     * @param handler  the handler
     * @param question the question
     */
    public ConfirmDialog(@NotNull final Consumer<Boolean> handler, @NotNull final String question) {
        this.handler = handler;
        final Label questionLabel = getQuestionLabel();
        questionLabel.setText(question);
    }

    /**
     * @return the label.
     */
    @NotNull
    private Label getQuestionLabel() {
        return requireNonNull(questionLabel);
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
        questionLabel.setWrapText(true);
        questionLabel.setAlignment(Pos.CENTER);
        questionLabel.setPadding(QUESTION_PADDING);
        questionLabel.setTextAlignment(TextAlignment.CENTER);
        questionLabel.prefWidthProperty().bind(root.widthProperty().multiply(0.9));
        questionLabel.prefHeightProperty().bind(root.heightProperty());

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
