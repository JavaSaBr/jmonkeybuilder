package com.ss.editor.ui.dialog;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
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
    private final Consumer<@Nullable Boolean> handler;

    /**
     * The label.
     */
    @Nullable
    private Label questionLabel;

    public ConfirmDialog(@NotNull final Consumer<@Nullable Boolean> handler, @NotNull final String question) {
        this.handler = handler;
        final Label questionLabel = getQuestionLabel();
        questionLabel.setText(question);
    }

    /**
     * @return the label.
     */
    @FromAnyThread
    private @NotNull Label getQuestionLabel() {
        return notNull(questionLabel);
    }

    @Override
    @FromAnyThread
    protected @NotNull String getTitleText() {
        return Messages.QUESTION_DIALOG_TITLE;
    }

    @Override
    @FxThread
    protected void createContent(@NotNull final VBox root) {
        super.createContent(root);

        questionLabel = new Label();
        questionLabel.minWidthProperty().bind(widthProperty().multiply(0.9));

        FXUtils.addToPane(questionLabel, root);
        FXUtils.addClassTo(root, CSSClasses.CONFIRM_DIALOG);
    }

    @Override
    @FromAnyThread
    protected @NotNull String getButtonOkText() {
        return Messages.SIMPLE_DIALOG_BUTTON_YES;
    }

    @Override
    @FromAnyThread
    protected @NotNull String getButtonCloseText() {
        return Messages.SIMPLE_DIALOG_BUTTON_NO;
    }

    @Override
    @FxThread
    protected void processKey(@NotNull final KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            processClose();
        }
    }

    @Override
    @FxThread
    protected void processOk() {
        super.processOk();
        handler.accept(Boolean.TRUE);
    }

    @Override
    @FxThread
    protected void processClose() {
        super.processClose();
        handler.accept(Boolean.FALSE);
    }

    /**
     * Process cancel the dialog.
     */
    protected void processCancel() {
        super.processClose();
        handler.accept(null);
    }

    @Override
    @FromAnyThread
    protected @NotNull Point getSize() {
        return DIALOG_SIZE;
    }

    @Override
    @FxThread
    protected void createAdditionalActions(@NotNull final HBox container) {
        super.createAdditionalActions(container);

        final Button closeButton = new Button(Messages.SIMPLE_DIALOG_BUTTON_CANCEL);
        closeButton.setOnAction(event -> processCancel());

        FXUtils.addClassTo(closeButton, CSSClasses.DIALOG_BUTTON);
        FXUtils.addToPane(closeButton, container);
    }
}
