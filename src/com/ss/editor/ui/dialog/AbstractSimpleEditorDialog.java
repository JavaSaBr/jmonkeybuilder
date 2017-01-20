package com.ss.editor.ui.dialog;

import com.ss.editor.Messages;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.util.EditorUtil;

import org.jetbrains.annotations.NotNull;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;

/**
 * The simple implementation of the dialog.
 *
 * @author JavaSaBr
 */
public abstract class AbstractSimpleEditorDialog extends EditorDialog {

    protected static final Insets OK_BUTTON_OFFSET = new Insets(0, 4, 0, 0);
    protected static final Insets CANCEL_BUTTON_OFFSET = new Insets(0, 15, 0, 0);

    protected static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    /**
     * The ok button.
     */
    private Button okButton;

    public AbstractSimpleEditorDialog() {
    }

    @Override
    protected void processKey(@NotNull final KeyEvent event) {
        super.processKey(event);
        if (event.getCode() == KeyCode.ENTER && !okButton.isDisable()) {
            processOk();
        }
    }

    /**
     * @return the ok button.
     */
    @NotNull
    protected Button getOkButton() {
        return okButton;
    }

    @Override
    protected void createActions(@NotNull final VBox root) {
        super.createActions(root);

        final HBox container = new HBox();
        container.setId(CSSIds.ASSET_EDITOR_DIALOG_BUTTON_CONTAINER);

        okButton = new Button(getButtonOkLabel());
        okButton.setId(CSSIds.EDITOR_DIALOG_BUTTON_OK);
        okButton.setOnAction(event -> {
            try {
                processOk();
            } catch (final Exception e) {
                EditorUtil.handleException(LOGGER, this, e);
            }
        });

        final Button cancelButton = new Button(getButtonCancelLabel());
        cancelButton.setId(CSSIds.EDITOR_DIALOG_BUTTON_CANCEL);
        cancelButton.setOnAction(event -> processCancel());

        FXUtils.addToPane(okButton, container);
        FXUtils.addToPane(cancelButton, container);
        FXUtils.addToPane(container, root);

        FXUtils.addClassTo(okButton, CSSClasses.SPECIAL_FONT_16);
        FXUtils.addClassTo(cancelButton, CSSClasses.SPECIAL_FONT_16);

        HBox.setMargin(okButton, OK_BUTTON_OFFSET);
        HBox.setMargin(cancelButton, CANCEL_BUTTON_OFFSET);
    }

    @NotNull
    protected String getButtonCancelLabel() {
        return Messages.SIMPLE_DIALOG_BUTTON_CANCEL;
    }

    @NotNull
    protected String getButtonOkLabel() {
        return Messages.SIMPLE_DIALOG_BUTTON_OK;
    }

    /**
     * Handle ok button.
     */
    protected void processOk() {
        hide();
    }

    /**
     * Handle cancel button.
     */
    protected void processCancel() {
        hide();
    }
}
