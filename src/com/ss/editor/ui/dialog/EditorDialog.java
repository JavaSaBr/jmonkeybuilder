package com.ss.editor.ui.dialog;

import static javafx.geometry.Pos.BOTTOM_LEFT;
import static javafx.scene.text.TextAlignment.LEFT;

import com.ss.editor.Editor;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.WindowChangeFocusEvent;
import com.ss.editor.ui.scene.EditorFXScene;

import org.jetbrains.annotations.NotNull;

import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import rlib.ui.hanlder.WindowDragHandler;
import rlib.ui.util.FXUtils;
import rlib.ui.window.popup.dialog.AbstractPopupDialog;

/**
 * The base implementation of the {@link AbstractPopupDialog} for using dialogs in the {@link
 * Editor}.
 *
 * @author JavaSaBr
 */
public class EditorDialog extends AbstractPopupDialog {

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

    /**
     * The last focus owner.
     */
    private Node focusOwner;

    @Override
    protected void createControls(final VBox root) {
        super.createControls(root);
        root.setId(CSSIds.EDITOR_DIALOG_BACKGROUND);
        createHeader(root);
        createContent(root);
        createActions(root);
        addEventHandler(KeyEvent.KEY_RELEASED, this::processKey);
    }

    protected void processKey(@NotNull final KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            hide();
        }
    }

    @Override
    public void show(@NotNull final Window owner) {

        final EditorFXScene scene = (EditorFXScene) owner.getScene();
        final StackPane container = scene.getContainer();
        container.setDisable(true);

        focusOwner = scene.getFocusOwner();

        super.show(owner);

        if (isHideOnLostFocus()) {
            owner.focusedProperty().addListener(hideListener);
            FX_EVENT_MANAGER.addEventHandler(WindowChangeFocusEvent.EVENT_TYPE, hideEventHandler);
        }
    }

    @Override
    public void hide() {

        final Window window = getOwnerWindow();
        final EditorFXScene scene = (EditorFXScene) window.getScene();
        final StackPane container = scene.getContainer();
        container.setDisable(false);

        if (focusOwner != null) {
            focusOwner.requestFocus();
        }

        super.hide();

        if (isHideOnLostFocus()) {
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
    protected void createHeader(@NotNull final VBox root) {

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
        closeButton.setOnAction(event -> hide());

        FXUtils.addClassTo(titleLabel, CSSClasses.SPECIAL_FONT_16);
        FXUtils.addClassTo(closeButton, CSSClasses.EDITOR_BAR_BUTTON);

        FXUtils.addToPane(titleLabel, titleContainer);
        FXUtils.addToPane(closeButton, header);
        FXUtils.addToPane(titleContainer, header);

        WindowDragHandler.install(header);

        FXUtils.addToPane(header, root);
    }

    /**
     * Create the content of this dialog.
     */
    protected void createContent(@NotNull final VBox root) {
    }

    /**
     * Create the actions of this dialog.
     */
    protected void createActions(@NotNull final VBox root) {
    }

    /**
     * @return the title of this dialog.
     */
    @NotNull
    protected String getTitleText() {
        return "Title";
    }
}
