package com.ss.editor.ui.control.model.tree.dialog;

import com.ss.editor.Messages;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.dialog.EditorDialog;

import org.jetbrains.annotations.NotNull;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;

/**
 * The base implementation of the dialog for working with node elements.
 *
 * @author JavaSaBr
 */
public abstract class AbstractNodeDialog extends EditorDialog {

    protected static final Insets OK_BUTTON_OFFSET = new Insets(0, 4, 0, 0);
    protected static final Insets CANCEL_BUTTON_OFFSET = new Insets(0, 15, 0, 0);

    /**
     * The ok button.
     */
    private Button okButton;

    public AbstractNodeDialog() {
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
        okButton.setOnAction(event -> processOk());

        final Button cancelButton = new Button(Messages.NODE_DIALOG_BUTTON_CANCEL);
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

    @NotNull
    protected String getButtonOkLabel() {
        return Messages.NODE_DIALOG_BUTTON_OK;
    }

    protected void processOk() {
    }
}
