package com.ss.editor.ui.dialog;

import static java.util.Objects.requireNonNull;
import com.ss.editor.Messages;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Point;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import rlib.ui.util.FXUtils;

/**
 * The implementation of a dialog for renaming.
 *
 * @author JavaSaBr
 */
public class RenameDialog extends AbstractSimpleEditorDialog {

    @NotNull
    private static final Insets NAME_OFFSET = new Insets(20, CANCEL_BUTTON_OFFSET.getRight(), 20, 0);

    @NotNull
    private static final Point DIALOG_SIZE = new Point(400, 140);

    /**
     * The function for validation name.
     */
    @Nullable
    private Function<String, Boolean> validator;

    /**
     * The function for handling a new name.
     */
    @Nullable
    private Consumer<String> handler;

    /**
     * The text field.
     */
    @Nullable
    private TextField nameField;

    @Override
    protected void createContent(@NotNull final VBox root) {
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

        FXUtils.addClassTo(nameLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(nameField, CSSClasses.SPECIAL_FONT_14);

        VBox.setMargin(nameContainer, NAME_OFFSET);
    }

    @Override
    public void show(@NotNull final Window owner) {
        super.show(owner);
        EXECUTOR_MANAGER.addFXTask(() -> getNameField().requestFocus());
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return Messages.RENAME_DIALOG_TITLE;
    }

    /**
     * @param initName the initial name.
     */
    public void setInitName(final String initName) {
        final TextField nameField = getNameField();
        nameField.setText(initName);
    }

    /**
     * @return the text field.
     */
    @NotNull
    private TextField getNameField() {
        return requireNonNull(nameField);
    }

    /**
     * @return the function for validation name.
     */
    @Nullable
    private Function<String, Boolean> getValidator() {
        return validator;
    }

    /**
     * @param validator the function for validation name.
     */
    public void setValidator(@Nullable final Function<String, Boolean> validator) {
        this.validator = validator;
    }

    /**
     * @return the function for handling a new name.
     */
    @Nullable
    private Consumer<String> getHandler() {
        return handler;
    }

    /**
     * @param handler the function for handling a new name.
     */
    public void setHandler(@Nullable final Consumer<String> handler) {
        this.handler = handler;
    }

    /**
     * Validate a new name.
     */
    private void validateName(@NotNull final String name) {
        final Function<String, Boolean> validator = getValidator();
        final Button okButton = getOkButton();
        okButton.setDisable(!(validator == null || validator.apply(name)));
    }

    @NotNull
    @Override
    protected String getButtonCancelLabel() {
        return Messages.RENAME_DIALOG_BUTTON_CANCEL;
    }

    @NotNull
    @Override
    protected String getButtonOkLabel() {
        return Messages.RENAME_DIALOG_BUTTON_OK;
    }

    /**
     * Finish this dialog.
     */
    @Override
    protected void processOk() {
        super.processOk();

        final Consumer<String> handler = getHandler();
        if (handler == null) return;

        final TextField nameField = getNameField();
        handler.accept(nameField.getText());
    }

    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
