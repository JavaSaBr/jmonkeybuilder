package com.ss.editor.ui.dialog;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.Messages;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.ui.util.FXUtils;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The simple implementation of the dialog.
 *
 * @author JavaSaBr
 */
public abstract class AbstractSimpleEditorDialog extends EditorDialog {

    /**
     * The constant DEFAULT_LABEL_W_PERCENT.
     */
    protected static final double DEFAULT_LABEL_W_PERCENT = 0.4;

    /**
     * The constant DEFAULT_FIELD_W_PERCENT.
     */
    protected static final double DEFAULT_FIELD_W_PERCENT = 0.6;

    /**
     * The constant DEFAULT_LABEL_W_PERCENT2.
     */
    protected static final double DEFAULT_LABEL_W_PERCENT2 = 0.5;

    /**
     * The constant DEFAULT_FIELD_W_PERCENT2.
     */
    protected static final double DEFAULT_FIELD_W_PERCENT2 = 0.5;

    /**
     * The constant DEFAULT_LABEL_W_PERCENT3.
     */
    protected static final double DEFAULT_LABEL_W_PERCENT3 = 0.6;

    /**
     * The constant DEFAULT_FIELD_W_PERCENT3.
     */
    protected static final double DEFAULT_FIELD_W_PERCENT3 = 0.4;

    /**
     * The constant DEFAULT_FIELD_W_PERCENT4.
     */
    protected static final double DEFAULT_FIELD_W_PERCENT4 = 0.3;

    /**
     * The constant OK_BUTTON_OFFSET.
     */
    @NotNull
    protected static final Insets OK_BUTTON_OFFSET = new Insets(0, 4, 0, 0);

    /**
     * FIXME to remove
     * The constant CONTAINER_OFFSET.
     */
    @NotNull
    protected static final Insets CONTAINER_OFFSET = new Insets(10, 15, 20, 0);

    /**
     * The constant EXECUTOR_MANAGER.
     */
    @NotNull
    protected static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    /**
     * The ok button.
     */
    @Nullable
    private Button okButton;

    /**
     * True if this dialog is ready.
     */
    private boolean ready;

    /**
     * Instantiates a new Abstract simple editor dialog.
     */
    public AbstractSimpleEditorDialog() {
        ready = true;
    }

    /**
     * Is ready boolean.
     *
     * @return true if this dialog is ready.
     */
    protected boolean isReady() {
        return ready;
    }

    @Override
    protected void processKey(@NotNull final KeyEvent event) {
        super.processKey(event);
        final Button okButton = getOkButton();
        if (event.getCode() == KeyCode.ENTER && !okButton.isDisable()) {
            processOk();
        }
    }

    @Override
    protected void createContent(@NotNull final GridPane root) {
        super.createContent(root);
        root.setId(CSSIds.ABSTRACT_DIALOG_GRID_SETTINGS_CONTAINER);
    }

    /**
     * Gets ok button.
     *
     * @return the ok button.
     */
    @NotNull
    protected Button getOkButton() {
        return notNull(okButton);
    }

    @Override
    protected void createActions(@NotNull final VBox root) {
        super.createActions(root);

        final HBox container = new HBox();
        container.setId(CSSIds.ASSET_EDITOR_DIALOG_BUTTON_CONTAINER);

        okButton = new Button(getButtonOkLabel());
        okButton.setId(CSSIds.EDITOR_DIALOG_BUTTON_OK);
        okButton.setOnAction(event -> safeProcessOk());

        final Button cancelButton = new Button(getButtonCancelLabel());
        cancelButton.setId(CSSIds.EDITOR_DIALOG_BUTTON_CANCEL);
        cancelButton.setOnAction(event -> processCancel());

        FXUtils.addToPane(okButton, container);
        FXUtils.addToPane(cancelButton, container);
        FXUtils.addToPane(container, root);

        FXUtils.addClassTo(okButton, CSSClasses.SPECIAL_FONT_16);
        FXUtils.addClassTo(cancelButton, CSSClasses.SPECIAL_FONT_16);

        HBox.setMargin(okButton, OK_BUTTON_OFFSET);
    }

    private void safeProcessOk() {
        try {
            processOk();
        } catch (final Exception e) {
            EditorUtil.handleException(LOGGER, this, e);
        }
    }

    /**
     * Gets button cancel label.
     *
     * @return the button cancel label
     */
    @NotNull
    protected String getButtonCancelLabel() {
        return Messages.SIMPLE_DIALOG_BUTTON_CANCEL;
    }

    /**
     * Gets button ok label.
     *
     * @return the button ok label
     */
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
