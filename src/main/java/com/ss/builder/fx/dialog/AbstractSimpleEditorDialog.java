package com.ss.builder.fx.dialog;

import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.css.CssClasses;
import com.ss.builder.util.EditorUtils;
import com.ss.rlib.fx.util.FxUtils;
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

    public static final double DEFAULT_LABEL_W_PERCENT = 0.4;
    public static final double DEFAULT_FIELD_W_PERCENT = 0.6;

    public static final double DEFAULT_LABEL_W_PERCENT2 = 0.5;
    public static final double DEFAULT_FIELD_W_PERCENT2 = 0.5;

    public static final double DEFAULT_LABEL_W_PERCENT3 = 0.6;
    public static final double DEFAULT_FIELD_W_PERCENT3 = 0.4;

    public static final double DEFAULT_FIELD_W_PERCENT4 = 0.3;

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

    @Override
    @FxThread
    protected void processKey(@NotNull KeyEvent event) {
        super.processKey(event);
        var okButton = getOkButton();
        if (okButton != null && event.getCode() == KeyCode.ENTER && !okButton.isDisable()) {
            processOk();
        }
    }

    @Override
    @FxThread
    protected void createContent(@NotNull GridPane root) {
        super.createContent(root);
    }

    /**
     * Get the ok button.
     *
     * @return the ok button.
     */
    @FxThread
    protected @Nullable Button getOkButton() {
        return okButton;
    }

    /**
     * Get the close button.
     *
     * @return the close button.
     */
    @FxThread
    protected @Nullable Button getCloseButton() {
        return closeButton;
    }

    @Override
    @FxThread
    protected void createActions(@NotNull VBox root) {
        super.createActions(root);

        var container = new HBox();

        createBeforeActions(container);

        if (needOkButton()) {

            okButton = new Button(getButtonOkText());
            okButton.setOnAction(event -> safeProcessOk());

            FxUtils.addClass(okButton,
                    CssClasses.DIALOG_BUTTON);
        }

        if (needCloseButton()) {

            closeButton = new Button(getButtonCloseText());
            closeButton.setOnAction(event -> processClose());

            FxUtils.addClass(closeButton,
                    CssClasses.DIALOG_BUTTON);
        }

        if (needOkButton()) {
            FxUtils.addChild(container, okButton);
        }

        if (needCloseButton()) {
            FxUtils.addChild(container, closeButton);
        }

        createAdditionalActions(container);

        if (!container.getChildren().isEmpty()) {
            FxUtils.addClass(container, CssClasses.DEF_HBOX);
            FxUtils.addChild(root, container);
        }
    }

    @FxThread
    protected void createBeforeActions(@NotNull HBox container) {

    }

    @FxThread
    protected void createAdditionalActions(@NotNull HBox container) {

    }

    /**
     * Return true if need to add an ok button here.
     *
     * @return true if need to add an ok button here.
     */
    @FromAnyThread
    protected boolean needOkButton() {
        return true;
    }

    /**
     * Return true if need to add a close button here.
     *
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
        } catch (Exception e) {
            EditorUtils.handleException(LOGGER, this, e);
        }
    }

    /**
     * Get the button's close text.
     *
     * @return the the button's close text.
     */
    @FromAnyThread
    protected @NotNull String getButtonCloseText() {
        return Messages.SIMPLE_DIALOG_BUTTON_CLOSE;
    }

    /**
     * Get button's ok text.
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
