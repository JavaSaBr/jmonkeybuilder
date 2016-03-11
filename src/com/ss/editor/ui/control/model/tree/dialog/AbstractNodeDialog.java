package com.ss.editor.ui.control.model.tree.dialog;

import com.ss.editor.Messages;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.dialog.EditorDialog;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;

/**
 * Базовая реализация диалога для операций над узлами.
 *
 * @author Ronn
 */
public abstract class AbstractNodeDialog extends EditorDialog {

    protected static final Insets OK_BUTTON_OFFSET = new Insets(0, 4, 0, 0);
    protected static final Insets CANCEL_BUTTON_OFFSET = new Insets(0, 15, 0, 0);

    /**
     * Кнопка ок.
     */
    private Button okButton;

    public AbstractNodeDialog() {

    }

    /**
     * @return кнопка ок.
     */
    protected Button getOkButton() {
        return okButton;
    }

    @Override
    protected void createActions(final VBox root) {
        super.createActions(root);

        final HBox container = new HBox();
        container.setId(CSSIds.ASSET_EDITOR_DIALOG_BUTTON_CONTAINER);

        okButton = new Button(Messages.NODE_DIALOG_BUTTON_OK);
        okButton.setId(CSSIds.EDITOR_DIALOG_BUTTON_OK);
        okButton.setOnAction(event -> processOk());

        final Button cancelButton = new Button(Messages.NODE_DIALOG_BUTTON_CANCEL);
        cancelButton.setId(CSSIds.EDITOR_DIALOG_BUTTON_CANCEL);
        cancelButton.setOnAction(event -> hide());

        FXUtils.addToPane(okButton, container);
        FXUtils.addToPane(cancelButton, container);
        FXUtils.addToPane(container, root);

        HBox.setMargin(okButton, OK_BUTTON_OFFSET);
        HBox.setMargin(cancelButton, CANCEL_BUTTON_OFFSET);
    }

    protected void processOk() {

    }
}
