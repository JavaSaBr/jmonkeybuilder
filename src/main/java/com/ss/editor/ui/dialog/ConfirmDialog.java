package com.ss.editor.ui.dialog;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.Messages;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.function.Consumer;

/**
 * The implementation of a dialog to ask questions.
 *
 * @author JavaSaBr
 */
public class ConfirmDialog extends AbstractSimpleEditorDialog {

    @NotNull
    private static final Point DIALOG_SIZE = new Point(600, -1);

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
        return notNull(questionLabel);
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
        questionLabel.minWidthProperty().bind(widthProperty().multiply(0.9));

        FXUtils.addToPane(questionLabel, root);
        FXUtils.addClassTo(root, CSSClasses.CONFIRM_DIALOG);
    }

    @NotNull
    @Override
    protected String getButtonOkText() {
        return Messages.QUESTION_DIALOG_BUTTON_OK;
    }

    @NotNull
    @Override
    protected String getButtonCloseText() {
        return Messages.QUESTION_DIALOG_BUTTON_CANCEL;
    }

    @Override
    protected void processKey(@NotNull final KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            processClose();
        }
    }

    @Override
    protected void processOk() {
        super.processOk();
        handler.accept(Boolean.TRUE);
    }

    @Override
    protected void processClose() {
        super.processClose();
        handler.accept(Boolean.FALSE);
    }

    @NotNull
    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
