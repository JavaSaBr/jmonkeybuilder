package com.ss.editor.ui.dialog;

import com.ss.editor.Editor;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.WindowChangeFocusEvent;

import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import rlib.ui.hanlder.WindowDragHandler;
import rlib.ui.util.FXUtils;
import rlib.ui.window.popup.dialog.AbstractPopupDialog;

import static com.ss.editor.ui.css.CSSClasses.MAIN_FONT_13;
import static com.ss.editor.ui.css.CSSClasses.TOOLBAR_BUTTON;
import static javafx.geometry.Pos.BOTTOM_LEFT;
import static javafx.scene.text.TextAlignment.LEFT;

/**
 * The base implementation of the {@link AbstractPopupDialog} for using dialogs in the {@link
 * Editor}.
 *
 * @author JavaSaBr
 */
public class EditorDialog extends AbstractPopupDialog {

    public static final Insets TITLE_LABEL_OFFSET = new Insets(0, 0, 0, 10);
    public static final Insets CLOSE_BUTTON_OFFSET = new Insets(0, 10, 0, 0);

    private static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();

    /**
     * The handler for handling changing a focus of the window.
     */
    private final EventHandler<? super Event> hideEventHandler = event -> {
        final WindowChangeFocusEvent focusEvent = (WindowChangeFocusEvent) event;
        if (!focusEvent.isFocused()) hide();
    };

    /**
     * The handler for handling changing a focus of the window from JavaFX.
     */
    private final ChangeListener<Boolean> hideListener = (observable, oldValue, newValue) -> {
        if (newValue == Boolean.FALSE) hide();
    };

    @Override
    protected void createControls(final VBox root) {
        super.createControls(root);
        root.setId(CSSIds.EDITOR_DIALOG_BACKGROUND);
        createHeader(root);
        createContent(root);
        createActions(root);
    }

    @Override
    public void show(final Window owner) {
        super.show(owner);
        if (isHideOnLostFocus()) {
            owner.focusedProperty().addListener(hideListener);
            FX_EVENT_MANAGER.addEventHandler(WindowChangeFocusEvent.EVENT_TYPE, hideEventHandler);
        }
    }

    @Override
    public void hide() {
        super.hide();
        if (isHideOnLostFocus()) {
            final Window window = getOwnerWindow();
            window.focusedProperty().removeListener(hideListener);
            FX_EVENT_MANAGER.removeEventHandler(WindowChangeFocusEvent.EVENT_TYPE, hideEventHandler);
        }
    }

    /**
     * @return true if dialog will hide after losing a focus.
     */
    protected boolean isHideOnLostFocus() {
        return true;
    }

    /**
     * Create the header of this dialog.
     */
    protected void createHeader(final VBox root) {

        final StackPane header = new StackPane();
        header.setId(CSSIds.EDITOR_DIALOG_HEADER);

        final HBox titleContainer = new HBox();
        titleContainer.setAlignment(Pos.CENTER_LEFT);
        titleContainer.setPickOnBounds(false);

        final Label titleLabel = new Label(getTitleText());
        titleLabel.setTextAlignment(LEFT);
        titleLabel.setAlignment(BOTTOM_LEFT);

        final Button closeButton = new Button();
        closeButton.setId(CSSIds.EDITOR_DIALOG_HEADER_BUTTON_CLOSE);
        closeButton.setGraphic(new ImageView(Icons.CLOSE_18));
        closeButton.setOnAction(event -> hide());

        FXUtils.addClassTo(titleLabel, MAIN_FONT_13);
        FXUtils.addClassTo(closeButton, TOOLBAR_BUTTON);

        FXUtils.addToPane(titleLabel, titleContainer);
        FXUtils.addToPane(closeButton, header);
        FXUtils.addToPane(titleContainer, header);

        WindowDragHandler.install(header);

        HBox.setMargin(titleLabel, TITLE_LABEL_OFFSET);
        StackPane.setMargin(closeButton, CLOSE_BUTTON_OFFSET);

        FXUtils.addToPane(header, root);
    }

    /**
     * Create the content of this dialog.
     */
    protected void createContent(final VBox root) {
    }

    /**
     * Create the actions of this dialog.
     */
    protected void createActions(final VBox root) {
    }

    /**
     * @return the title of this dialog.
     */
    protected String getTitleText() {
        return "Title";
    }
}
