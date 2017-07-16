package com.ss.editor.ui.dialog;

import static com.ss.editor.ui.builder.EditorFXSceneBuilder.*;
import static javafx.geometry.Pos.CENTER;
import com.ss.editor.Editor;
import com.ss.editor.analytics.google.GAEvent;
import com.ss.editor.analytics.google.GAnalytics;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CssColorTheme;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.scene.EditorFXScene;
import com.ss.rlib.logging.Logger;
import com.ss.rlib.logging.LoggerManager;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.ui.window.popup.dialog.AbstractPopupDialog;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.time.Duration;
import java.time.LocalTime;

/**
 * The base implementation of the {@link AbstractPopupDialog} for using dialogs in the {@link Editor}.
 *
 * @author JavaSaBr
 */
public class EditorDialog {

    /**
     * The constant LOGGER.
     */
    @NotNull
    protected static final Logger LOGGER = LoggerManager.getLogger(EditorDialog.class);

    /**
     * The constant FX_EVENT_MANAGER.
     */
    @NotNull
    protected static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();

    /**
     * The default dialog size.
     */
    @NotNull
    private static final Point DEFAULT_SIZE = new Point(0, 0);

    /**
     * The stage of this dialog.
     */
    @NotNull
    private final Stage dialog;

    /**
     * The time when this DIALOG was showed.
     */
    @NotNull
    private volatile LocalTime showedTime;

    /**
     * The content container.
     */
    @NotNull
    private final VBox container;

    /**
     * The last focus owner.
     */
    @Nullable
    private Node focusOwner;

    /**
     * Instantiates a new Editor dialog.
     */
    public EditorDialog() {
        this.showedTime = LocalTime.now();

        container = new VBox();
        container.setAlignment(CENTER);

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final CssColorTheme theme = editorConfig.getTheme();

        final Scene scene = new Scene(container);
        final ObservableList<String> stylesheets = scene.getStylesheets();
        stylesheets.add(CSS_FILE_BASE);
        stylesheets.add(CSS_FILE_EXTERNAL);
        stylesheets.add(CSS_FILE_CUSTOM_IDS);
        stylesheets.add(CSS_FILE_CUSTOM_CLASSES);
        stylesheets.add(theme.getCssFile());

        createControls(container);

        dialog = new Stage();
        dialog.setTitle(getTitleText());
        dialog.initStyle(StageStyle.UTILITY);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setResizable(isResizable());
        dialog.setScene(scene);

        configureSize(container);
    }

    /**
     * @return true if this dialog should be resizable.
     */
    protected boolean isResizable() {
        return true;
    }

    /**
     * Create controls of this dialog.
     *
     * @param root the root container.
     */
    protected void createControls(@NotNull final VBox root) {

        final VBox actionsContainer = new VBox();

        FXUtils.addClassTo(actionsContainer, CSSClasses.DIALOG_ACTIONS_ROOT);

        if (isGridStructure()) {
            final GridPane container = new GridPane();
            FXUtils.addClassesTo(container, CSSClasses.DEF_GRID_PANE, CSSClasses.DIALOG_CONTENT_ROOT);
            createContent(container);
            FXUtils.addToPane(container, root);
        } else {
            final VBox container = new VBox();
            FXUtils.addClassesTo(container, CSSClasses.DEF_VBOX, CSSClasses.DIALOG_CONTENT_ROOT);
            createContent(container);
            FXUtils.addToPane(container, root);
        }

        createActions(actionsContainer);

        FXUtils.addToPane(actionsContainer, root);
        FXUtils.addClassTo(root, CSSClasses.DIALOG_ROOT);

        root.addEventHandler(KeyEvent.KEY_RELEASED, this::processKey);
    }

    /**
     * @return the stage of this dialog.
     */
    @NotNull
    protected Stage getDialog() {
        return dialog;
    }

    /**
     * @return the width property of this dialog.
     */
    @NotNull
    protected ReadOnlyDoubleProperty widthProperty() {
        return getContainer().widthProperty();
    }

    /**
     * @return the height property of this dialog.
     */
    @NotNull
    protected ReadOnlyDoubleProperty heightProperty() {
        return getContainer().heightProperty();
    }

    /**
     * Configure size of the root container.
     *
     * @param container the root container.
     */
    protected void configureSize(@NotNull final VBox container) {
        configureSize(container, getSize());
    }

    /**
     * Configure size of the root container.
     *
     * @param container the root container.
     * @param size      the size.
     */
    private void configureSize(@NotNull final VBox container, @NotNull final Point size) {

        final Stage dialog = getDialog();

        final double width = size.getX();
        final double height = size.getY();

        if (width >= 1D) {
            FXUtils.setFixedWidth(container, width);
            dialog.setMinWidth(width);
            dialog.setMaxWidth(width);
        }

        if (height >= 1D) {
            FXUtils.setFixedHeight(container, height);
            dialog.setMinHeight(height);
            dialog.setMaxHeight(height);
        }
    }

    /**
     * Gets container.
     *
     * @return The content container.
     */
    @NotNull
    protected VBox getContainer() {
        return container;
    }

    /**
     * Updates a size of this dialog.
     *
     * @param size the size of the dialog.
     */
    public void updateSize(@NotNull final Point size) {
        configureSize(getContainer(), size);
    }

    @NotNull
    protected Point getSize() {
        return DEFAULT_SIZE;
    }

    /**
     * Process key.
     *
     * @param event the event
     */
    protected void processKey(@NotNull final KeyEvent event) {
        event.consume();
        if (event.getCode() == KeyCode.ESCAPE) {
            hide();
        }
    }

    /**
     * Shows this dialog.
     *
     * @param owner the owner.
     */
    public void show(@NotNull final Node owner) {
        show(owner.getScene().getWindow());
    }

    /**
     * Shows this dialog.
     *
     * @param owner the owner.
     */
    public void show(@NotNull final Window owner) {

        final Scene scene = owner.getScene();

        if (scene instanceof EditorFXScene) {
            final EditorFXScene editorFXScene = (EditorFXScene) scene;
            final StackPane container = editorFXScene.getContainer();
            container.setFocusTraversable(false);
        }

        focusOwner = scene.getFocusOwner();

        dialog.initOwner(owner);
        dialog.show();
        dialog.requestFocus();

        GAnalytics.sendPageView(getDialogId(), null, "/dialog/" + getDialogId());
        GAnalytics.sendEvent(GAEvent.Category.DIALOG, GAEvent.Action.DIALOG_OPENED, getDialogId());

        Platform.runLater(dialog::sizeToScene);
    }

    /**
     * Gets dialog id.
     *
     * @return the dialog id
     */
    @NotNull
    protected String getDialogId() {
        return getClass().getSimpleName();
    }

    public void hide() {

        final Duration duration = Duration.between(showedTime, LocalTime.now());
        final int seconds = (int) duration.getSeconds();

        final Window window = dialog.getOwner();
        final Scene scene = window.getScene();

        if (scene instanceof EditorFXScene) {
            final EditorFXScene editorFXScene = (EditorFXScene) scene;
            final StackPane container = editorFXScene.getContainer();
            container.setFocusTraversable(true);
        }

        if (focusOwner != null) {
            focusOwner.requestFocus();
        }

        dialog.hide();

        GAnalytics.sendEvent(GAEvent.Category.DIALOG, GAEvent.Action.DIALOG_CLOSED, getDialogId());
        GAnalytics.sendTiming(GAEvent.Category.DIALOG, GAEvent.Label.SHOWING_A_DIALOG, seconds, getDialogId());
    }

    /**
     * Create the content of this dialog.
     *
     * @param root the root
     */
    protected void createContent(@NotNull final VBox root) {
    }

    /**
     * Create the content of this dialog.
     *
     * @param root the root
     */
    protected void createContent(@NotNull final GridPane root) {
    }

    /**
     * Is grid structure boolean.
     *
     * @return true if this dialog has grid structure.
     */
    protected boolean isGridStructure() {
        return false;
    }

    /**
     * Create the actions of this dialog.
     *
     * @param root the root
     */
    protected void createActions(@NotNull final VBox root) {
    }

    /**
     * Gets title text.
     *
     * @return the title of this dialog.
     */
    @NotNull
    protected String getTitleText() {
        return "Title";
    }
}
