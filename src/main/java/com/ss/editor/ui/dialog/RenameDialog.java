package com.ss.editor.ui.dialog;

import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.fx.util.FxControlUtils;
import com.ss.rlib.fx.util.FxUtils;
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

    private static final Point DIALOG_SIZE = new Point(400, 0);

    /**
     * The text field.
     */
    @NotNull
    private final TextField nameField;

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

    public RenameDialog() {
        nameField = new TextField();
    }

    @Override
    @FxThread
    protected void createContent(@NotNull GridPane root) {
        super.createContent(root);

        var nameLabel = new Label(Messages.RENAME_DIALOG_NEW_NAME_LABEL + ":");
        nameLabel.prefWidthProperty()
                .bind(widthProperty().multiply(DEFAULT_LABEL_W_PERCENT));

       nameField.prefWidthProperty()
               .bind(widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));

        FxControlUtils.onTextChange(nameField, this::validateName);

        root.add(nameLabel, 0, 0);
        root.add(nameField, 1, 0);

        FxUtils.addClass(nameLabel, CssClasses.DIALOG_DYNAMIC_LABEL)
                .addClass(nameField, CssClasses.DIALOG_FIELD);
    }

    @Override
    @FromAnyThread
    protected boolean isGridStructure() {
        return true;
    }

    @Override
    @FxThread
    public void show(@NotNull Window owner) {
        super.show(owner);

        ExecutorManager.getInstance()
                .addFxTask(() -> getNameField().requestFocus());
    }

    @Override
    @FromAnyThread
    protected @NotNull String getTitleText() {
        return Messages.RENAME_DIALOG_TITLE;
    }

    /**
     * Set the initial name.
     *
     * @param initName the initial name.
     */
    @FxThread
    public void setInitName(@NotNull String initName) {
        getNameField().setText(initName);
    }

    /**
     * Get the text field.
     *
     * @return the text field.
     */
    @FxThread
    private @NotNull TextField getNameField() {
        return nameField;
    }

    /**
     * Get the function for validation name.
     *
     * @return the function for validation name.
     */
    @FxThread
    private @Nullable Function<String, Boolean> getValidator() {
        return validator;
    }

    /**
     * Set the function for validation name.
     *
     * @param validator the function for validation name.
     */
    @FxThread
    public void setValidator(@Nullable Function<String, Boolean> validator) {
        this.validator = validator;
    }

    /**
     * Get the function for handling a new name.
     *
     * @return the function for handling a new name.
     */
    @FxThread
    private @Nullable Consumer<String> getHandler() {
        return handler;
    }

    /**
     * Set the function for handling a new name.
     *
     * @param handler the function for handling a new name.
     */
    @FxThread
    public void setHandler(@Nullable Consumer<String> handler) {
        this.handler = handler;
    }

    /**
     * Validate a new name.
     */
    @FxThread
    private void validateName(@NotNull String name) {
        var validator = getValidator();
        getOkButton().setDisable(!(validator == null || validator.apply(name)));
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

    @Override
    @FxThread
    protected void processOk() {
        super.processOk();

        var handler = getHandler();

        if (handler == null) {
            return;
        }

        handler.accept(getNameField().getText());
    }

    @Override
    @FromAnyThread
    protected @NotNull Point getSize() {
        return DIALOG_SIZE;
    }
}
