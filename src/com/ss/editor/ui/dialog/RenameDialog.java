package com.ss.editor.ui.dialog;

import com.ss.editor.Messages;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Function;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import rlib.ui.util.FXUtils;

/**
 * Реализация диалога для ввода нового имени.
 *
 * @author Ronn
 */
public class RenameDialog extends EditorDialog {

    private static final Insets OK_BUTTON_OFFSET = new Insets(0, 4, 0, 0);
    private static final Insets CANCEL_BUTTON_OFFSET = new Insets(0, 15, 0, 0);

    private static final Point DIALOG_SIZE = new Point(400, 140);

    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    /**
     * Функция валидирования имени.
     */
    private Function<String, Boolean> validator;

    /**
     * Функция обработки введеного имени.
     */
    private Consumer<String> handler;

    /**
     * Поле для ввода имени.
     */
    private TextField nameField;

    /**
     * Кнопка приминения.
     */
    private Button okButton;

    @Override
    protected void createContent(final VBox root) {
        super.createContent(root);

        final HBox nameContainer = new HBox();
        nameContainer.setAlignment(Pos.CENTER_LEFT);

        final Label nameLabel = new Label(Messages.RENAME_DIALOG_NEW_NAME_LABEL + ":");
        nameLabel.setId(CSSIds.RENAME_DIALOG_LABEL);

        nameField = new TextField();
        nameField.setId(CSSIds.RENAME_DIALOG_TEXT_FIELD);
        nameField.prefWidthProperty().bind(root.widthProperty());
        nameField.textProperty().addListener((observable, oldValue, newValue) -> validateName(newValue));

        FXUtils.addToPane(nameLabel, nameContainer);
        FXUtils.addToPane(nameField, nameContainer);
        FXUtils.addToPane(nameContainer, root);

        FXUtils.addClassTo(nameLabel, CSSClasses.MAIN_FONT_13);
        FXUtils.addClassTo(nameField, CSSClasses.MAIN_FONT_13);

        VBox.setMargin(nameContainer, new Insets(20, CANCEL_BUTTON_OFFSET.getRight(), 20, 0));

        root.setOnKeyReleased(event -> {

            final Button okButton = getOkButton();

            if(event.getCode() == KeyCode.ENTER && !okButton.isDisable()) {
                processOk();
            }
        });
    }

    @Override
    public void show(Window owner) {
        super.show(owner);
        EXECUTOR_MANAGER.addFXTask(() -> getNameField().requestFocus());
    }

    @Override
    protected String getTitleText() {
        return Messages.RENAME_DIALOG_TITLE;
    }

    /**
     * @param initName изначальное имя.
     */
    public void setInitName(final String initName) {
        final TextField nameField = getNameField();
        nameField.setText(initName);
    }

    /**
     * @return поле для ввода имени.
     */
    private TextField getNameField() {
        return nameField;
    }

    /**
     * @return кнопка приминения.
     */
    private Button getOkButton() {
        return okButton;
    }

    /**
     * @return функция валидирования имени.
     */
    public Function<String, Boolean> getValidator() {
        return validator;
    }

    /**
     * @param validator функция валидирования имени.
     */
    public void setValidator(final Function<String, Boolean> validator) {
        this.validator = validator;
    }

    /**
     * @return функция обработки введеного имени.
     */
    private Consumer<String> getHandler() {
        return handler;
    }

    /**
     * @param handler функция обработки введеного имени.
     */
    public void setHandler(final Consumer<String> handler) {
        this.handler = handler;
    }

    /**
     * Валидация нового имени.
     */
    private void validateName(final String name) {
        final Function<String, Boolean> validator = getValidator();
        final Button okButton = getOkButton();
        okButton.setDisable(!(validator == null || validator.apply(name)));
    }

    @Override
    protected void createActions(final VBox root) {
        super.createActions(root);

        final HBox container = new HBox();
        container.setId(CSSIds.ASSET_EDITOR_DIALOG_BUTTON_CONTAINER);

        okButton = new Button(Messages.RENAME_DIALOG_BUTTON_OK);
        okButton.setId(CSSIds.EDITOR_DIALOG_BUTTON_OK);
        okButton.setOnAction(event -> processOk());

        final Button cancelButton = new Button(Messages.RENAME_DIALOG_BUTTON_CANCEL);
        cancelButton.setId(CSSIds.EDITOR_DIALOG_BUTTON_CANCEL);
        cancelButton.setOnAction(event -> hide());

        FXUtils.addToPane(okButton, container);
        FXUtils.addToPane(cancelButton, container);
        FXUtils.addToPane(container, root);

        HBox.setMargin(okButton, OK_BUTTON_OFFSET);
        HBox.setMargin(cancelButton, CANCEL_BUTTON_OFFSET);
    }

    /**
     * Процесс принятия выборанного имени.
     */
    private void processOk() {

        final Consumer<String> handler = getHandler();

        if (handler == null) {
            hide();
            return;
        }

        final TextField nameField = getNameField();
        handler.accept(nameField.getText());

        hide();
    }

    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
