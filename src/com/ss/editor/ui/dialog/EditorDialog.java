package com.ss.editor.ui.dialog;

import com.ss.editor.ui.Icons;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import rlib.ui.hanlder.WindowDragHandler;
import rlib.ui.util.FXUtils;
import rlib.ui.window.popup.dialog.AbstractPopupDialog;

import static com.ss.editor.ui.css.CSSClasses.MAIN_FONT_13;
import static com.ss.editor.ui.css.CSSClasses.TOOLBAR_BUTTON;
import static com.ss.editor.ui.css.CSSIds.EDITOR_DIALOG_BACKGROUND;
import static com.ss.editor.ui.css.CSSIds.EDITOR_DIALOG_HEADER;
import static com.ss.editor.ui.css.CSSIds.EDITOR_DIALOG_HEADER_BUTTON_CLOSE;
import static javafx.geometry.Pos.BOTTOM_LEFT;
import static javafx.geometry.Pos.CENTER_LEFT;
import static javafx.scene.Cursor.HAND;
import static javafx.scene.text.TextAlignment.LEFT;

/**
 * Базовая реализация диалога для редактора.
 *
 * @author Ronn
 */
public class EditorDialog extends AbstractPopupDialog {

    public static final Insets TITLE_LABEL_OFFSET = new Insets(0, 0, 0, 10);
    public static final Insets CLOSE_BUTTON_OFFSET = new Insets(0, 10, 0, 0);

    @Override
    protected void createControls(final VBox root) {
        super.createControls(root);
        root.setId(EDITOR_DIALOG_BACKGROUND);
        createHeader(root);
        createContent(root);
        createActions(root);
    }

    /**
     * Построение шапки диалога.
     */
    protected void createHeader(final VBox root) {

        final StackPane header = new StackPane();
        header.setId(EDITOR_DIALOG_HEADER);

        final HBox titleContainer = new HBox();
        titleContainer.setAlignment(CENTER_LEFT);
        titleContainer.setPickOnBounds(false);

        final Label titleLabel = new Label(getTitleText());
        titleLabel.setTextAlignment(LEFT);
        titleLabel.setAlignment(BOTTOM_LEFT);

        final Button closeButton = new Button();
        closeButton.setId(EDITOR_DIALOG_HEADER_BUTTON_CLOSE);
        closeButton.setGraphic(new ImageView(Icons.CLOSE_18));
        closeButton.setOnAction(event -> hide());
        closeButton.setCursor(HAND);

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
     * Создание основого содержимого диалога.
     */
    protected void createContent(final VBox root) {

    }

    /**
     * Создание кнопок диалога.
     */
    protected void createActions(final VBox root) {

    }

    /**
     * @return текст шапки диалога.
     */
    protected String getTitleText() {
        return "Title";
    }
}
