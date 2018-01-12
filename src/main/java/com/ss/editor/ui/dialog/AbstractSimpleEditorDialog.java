package com.ss.editor.ui.dialog;

import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.ui.util.FXUtils;
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
    public static final double DEFAULT_LABEL_W_PERCENT = 0.4;

    /**
     * The constant DEFAULT_FIELD_W_PERCENT.
     */
    public static final double DEFAULT_FIELD_W_PERCENT = 0.6;

    /**
     * The constant DEFAULT_LABEL_W_PERCENT2.
     */
    public static final double DEFAULT_LABEL_W_PERCENT2 = 0.5;

    /**
     * The constant DEFAULT_FIELD_W_PERCENT2.
     */
    public static final double DEFAULT_FIELD_W_PERCENT2 = 0.5;

    /**
     * The constant DEFAULT_LABEL_W_PERCENT3.
     */
    public static final double DEFAULT_LABEL_W_PERCENT3 = 0.6;

    /**
     * The constant DEFAULT_FIELD_W_PERCENT3.
     */
    public static final double DEFAULT_FIELD_W_PERCENT3 = 0.4;

    /**
     * The constant DEFAULT_FIELD_W_PERCENT4.
     */
    public static final double DEFAULT_FIELD_W_PERCENT4 = 0.3;

    /**
     * The executing manager.
     */
    @NotNull
    protected static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    /**
     * The ok button.
     */
    @Nullable
    private Button okButton;

    /**
     * The close button.
     */
    @Nullable
    private Button closeButton;

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
    @FxThread
    protected void processKey(@NotNull final KeyEvent event) {
        super.processKey(event);
        final Button okButton = getOkButton();
        if (okButton != null && event.getCode() == KeyCode.ENTER && !okButton.isDisable()) {
            processOk();
        }
    }

    @Override
    @FxThread
    protected void createContent(@NotNull final GridPane root) {
        super.createContent(root);
    }

    /**
     * Gets the ok button.
     *
     * @return the ok button.
     */
    @FxThread
    protected @Nullable Button getOkButton() {
        return okButton;
    }

    /**
     * Gets the close button.
     *
     * @return the close button.
     */
    @FxThread
    protected @Nullable Button getCloseButton() {
        return closeButton;
    }

    @Override
    @FxThread
    protected void createActions(@NotNull final VBox root) {
        super.createActions(root);

        final HBox container = new HBox();

        createBeforeActions(container);

        if (needOkButton()) {
            okButton = new Button(getButtonOkText());
            okButton.setOnAction(event -> safeProcessOk());
            FXUtils.addClassTo(okButton, CSSClasses.DIALOG_BUTTON);
        }

        if (needCloseButton()) {
            closeButton = new Button(getButtonCloseText());
            closeButton.setOnAction(event -> processClose());
            FXUtils.addClassTo(closeButton, CSSClasses.DIALOG_BUTTON);
        }

        if (needOkButton()) {
            FXUtils.addToPane(okButton, container);
        }

        if (needCloseButton()) {
            FXUtils.addToPane(closeButton, container);
        }

        createAdditionalActions(container);

        if (!container.getChildren().isEmpty()) {
            FXUtils.addToPane(container, root);
            FXUtils.addClassTo(container, CSSClasses.DEF_HBOX);
        }
    }

    @FxThread
    protected void createBeforeActions(@NotNull final HBox container) {

    }

    @FxThread
    protected void createAdditionalActions(@NotNull final HBox container) {

    }

    /**
     * @return true if need to add an ok button here.
     */
    @FromAnyThread
    protected boolean needOkButton() {
        return true;
    }

    /**
     * @return true if need to add a close button here.
     */
    @FromAnyThread
    protected boolean needCloseButton() {
        return true;
    }

    @FxThread
    private void safeProcessOk() {
        try {
            processOk();
        } catch (final Exception e) {
            EditorUtil.handleException(LOGGER, this, e);
        }
    }

    /**
     * Gets the button's close text.
     *
     * @return the the button's close text.
     */
    @FromAnyThread
    protected @NotNull String getButtonCloseText() {
        return Messages.SIMPLE_DIALOG_BUTTON_CLOSE;
    }

    /**
     * Gets button's ok text.
     *
     * @return the button's ok text.
     */
    @FromAnyThread
    protected @NotNull String getButtonOkText() {
        return Messages.SIMPLE_DIALOG_BUTTON_OK;
    }

    /**
     * Handle ok button.
     */
    @FxThread
    protected void processOk() {
        hide();
    }

    /**
     * Handle cancel button.
     */
    @FxThread
    protected void processClose() {
        hide();
    }
}
