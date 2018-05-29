package com.ss.editor.ui.dialog;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.fx.util.FXUtils;
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
    @FxThread
    protected void createContent(@NotNull final GridPane root) {
        super.createContent(root);

        final Label nameLabel = new Label(Messages.RENAME_DIALOG_NEW_NAME_LABEL + ":");
        nameLabel.prefWidthProperty().bind(widthProperty().multiply(DEFAULT_LABEL_W_PERCENT));

        nameField = new TextField();
        nameField.textProperty().addListener((observable, oldValue, newValue) -> validateName(newValue));
        nameField.prefWidthProperty().bind(widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));

        root.add(nameLabel, 0, 0);
        root.add(nameField, 1, 0);

        FXUtils.addClassTo(nameLabel, CssClasses.DIALOG_DYNAMIC_LABEL);
        FXUtils.addClassTo(nameField, CssClasses.DIALOG_FIELD);
    }

    @Override
    @FromAnyThread
    protected boolean isGridStructure() {
        return true;
    }

    @Override
    @FxThread
    public void show(@NotNull final Window owner) {
        super.show(owner);
        EXECUTOR_MANAGER.addFxTask(() -> getNameField().requestFocus());
    }

    @Override
    @FromAnyThread
    protected @NotNull String getTitleText() {
        return Messages.RENAME_DIALOG_TITLE;
    }

    /**
     * Sets init name.
     *
     * @param initName the initial name.
     */
    @FxThread
    public void setInitName(@NotNull final String initName) {
        final TextField nameField = getNameField();
        nameField.setText(initName);
    }

    /**
     * @return the text field.
     */
    @FxThread
    private @NotNull TextField getNameField() {
        return notNull(nameField);
    }

    /**
     * @return the function for validation name.
     */
    @FxThread
    private @Nullable Function<String, Boolean> getValidator() {
        return validator;
    }

    /**
     * Sets validator.
     *
     * @param validator the function for validation name.
     */
    @FxThread
    public void setValidator(@Nullable final Function<String, Boolean> validator) {
        this.validator = validator;
    }

    /**
     * @return the function for handling a new name.
     */
    @FxThread
    private @Nullable Consumer<String> getHandler() {
        return handler;
    }

    /**
     * Sets handler.
     *
     * @param handler the function for handling a new name.
     */
    @FxThread
    public void setHandler(@Nullable final Consumer<String> handler) {
        this.handler = handler;
    }

    /**
     * Validate a new name.
     */
    @FxThread
    private void validateName(@NotNull final String name) {
        final Function<String, Boolean> validator = getValidator();
        final Button okButton = getOkButton();
        okButton.setDisable(!(validator == null || validator.apply(name)));
    }

    @Override
    @FromAnyThread
    protected @NotNull String getButtonCloseText() {
        return Messages.SIMPLE_DIALOG_BUTTON_CANCEL;
    }

    @Override
    @FromAnyThread
    protected @NotNull String getButtonOkText() {
        return Messages.RENAME_DIALOG_BUTTON_OK;
    }

    /**
     * Finish this dialog.
     */
    @Override
    @FxThread
    protected void processOk() {
        super.processOk();

        final Consumer<String> handler = getHandler();
        if (handler == null) return;

        final TextField nameField = getNameField();
        handler.accept(nameField.getText());
    }

    @Override
    @FromAnyThread
    protected @NotNull Point getSize() {
        return DIALOG_SIZE;
    }
}
