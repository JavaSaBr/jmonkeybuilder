package com.ss.editor.ui.dialog;

import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.fx.util.FxUtils;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

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

    @FxThread
    public static void ifOk(@NotNull String question, @NotNull Runnable handler) {

        var dialog = new ConfirmDialog(aBoolean -> {

            if (Boolean.TRUE.equals(aBoolean)) {
                handler.run();
            }

        }, question);

        dialog.postConstruct();
        dialog.show();
    }

    /**
     * The handler of an answer.
     */
    @NotNull
    private final Consumer<Boolean> handler;

    /**
     * The label.
     */
    @NotNull
    private final Label questionLabel;

    public ConfirmDialog(@NotNull Consumer<Boolean> handler, @NotNull String question) {
        this.handler = handler;
        this.questionLabel = new Label(question);
    }

    @Override
    @FromAnyThread
    protected @NotNull String getTitleText() {
        return Messages.QUESTION_DIALOG_TITLE;
    }

    @Override
    @FxThread
    protected void createContent(@NotNull VBox root) {
        super.createContent(root);

        questionLabel.minWidthProperty()
                .bind(widthProperty().multiply(0.9));

        FxUtils.addClass(root, CssClasses.CONFIRM_DIALOG);
        FxUtils.addChild(root, questionLabel);
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
    protected void processKey(@NotNull KeyEvent event) {
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
    protected void createAdditionalActions(@NotNull HBox container) {
        super.createAdditionalActions(container);

        var closeButton = new Button(Messages.SIMPLE_DIALOG_BUTTON_CANCEL);
        closeButton.setOnAction(event -> processCancel());

        FxUtils.addClass(closeButton, CssClasses.DIALOG_BUTTON);
        FxUtils.addChild(container, closeButton);
    }
}
