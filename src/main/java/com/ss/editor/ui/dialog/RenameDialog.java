package com.ss.editor.ui.dialog;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.Messages;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The implementation of a dialog for renaming.
 *
 * @author JavaSaBr
 */
public class RenameDialog extends AbstractSimpleEditorDialog {

    @NotNull
    private static final Point DIALOG_SIZE = new Point(400, 0);

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
    protected void createContent(@NotNull final GridPane root) {
        super.createContent(root);

        final Label nameLabel = new Label(Messages.RENAME_DIALOG_NEW_NAME_LABEL + ":");
        nameLabel.prefWidthProperty().bind(widthProperty().multiply(DEFAULT_LABEL_W_PERCENT));

        nameField = new TextField();
        nameField.textProperty().addListener((observable, oldValue, newValue) -> validateName(newValue));
        nameField.prefWidthProperty().bind(widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));

        root.add(nameLabel, 0, 0);
        root.add(nameField, 1, 0);

        FXUtils.addClassTo(nameLabel, CSSClasses.DIALOG_DYNAMIC_LABEL);
        FXUtils.addClassTo(nameField, CSSClasses.DIALOG_FIELD);
    }

    @Override
    protected boolean isGridStructure() {
        return true;
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
     * Sets init name.
     *
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
        return notNull(nameField);
    }

    /**
     * @return the function for validation name.
     */
    @Nullable
    private Function<String, Boolean> getValidator() {
        return validator;
    }

    /**
     * Sets validator.
     *
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
     * Sets handler.
     *
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

    @NotNull
    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
