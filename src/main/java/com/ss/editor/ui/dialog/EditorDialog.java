package com.ss.editor.ui.dialog;

import static com.ss.editor.config.DefaultSettingsProvider.Defaults.PREF_DEFAULT_THEME;
import static com.ss.editor.config.DefaultSettingsProvider.Preferences.PREF_UI_THEME;
import static javafx.geometry.Pos.CENTER;
import com.ss.editor.JmeApplication;
import com.ss.editor.analytics.google.GAEvent;
import com.ss.editor.analytics.google.GAnalytics;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.css.CssColorTheme;
import com.ss.editor.ui.css.CssRegistry;
import com.ss.editor.ui.event.FxEventManager;
import com.ss.editor.ui.scene.EditorFxScene;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.logging.Logger;
import com.ss.rlib.logging.LoggerManager;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.ui.window.popup.dialog.AbstractPopupDialog;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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
 * The base implementation of the {@link AbstractPopupDialog} for using dialogs in the {@link JmeApplication}.
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
    protected static final FxEventManager FX_EVENT_MANAGER = FxEventManager.getInstance();

    /**
     * The CSS registry.
     */
    @NotNull
    private static final CssRegistry CSS_REGISTRY = CssRegistry.getInstance();

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

    public EditorDialog() {
        this.showedTime = LocalTime.now();

        container = new VBox();
        container.setAlignment(CENTER);

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final CssColorTheme theme = editorConfig.getEnum(PREF_UI_THEME, PREF_DEFAULT_THEME);

        final Scene scene = new Scene(container);
        final ObservableList<String> stylesheets = scene.getStylesheets();
        stylesheets.addAll(CSS_REGISTRY.getAvailableCssFiles());
        stylesheets.add(theme.getCssFile());

        createControls(container);

        dialog = new Stage();
        dialog.setTitle(getTitleText());
        dialog.initStyle(StageStyle.UTILITY);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setResizable(isResizable());
        dialog.setScene(scene);

        final Stage fxStage = EditorUtil.getFxStage();
        final ObservableList<Image> icons = dialog.getIcons();
        icons.addAll(fxStage.getIcons());

        configureSize(container);
    }

    /**
     * @return true if this dialog should be resizable.
     */
    @FromAnyThread
    protected boolean isResizable() {
        return true;
    }

    /**
     * Create controls of this dialog.
     *
     * @param root the root container.
     */
    @FxThread
    protected void createControls(@NotNull final VBox root) {

        final VBox actionsContainer = new VBox();

        FXUtils.addClassTo(actionsContainer, CssClasses.DIALOG_ACTIONS_ROOT);

        if (isGridStructure()) {
            final GridPane container = new GridPane();
            FXUtils.addClassesTo(container, CssClasses.DEF_GRID_PANE, CssClasses.DIALOG_CONTENT_ROOT);
            createContent(container);
            FXUtils.addToPane(container, root);
        } else {
            final VBox container = new VBox();
            FXUtils.addClassesTo(container, CssClasses.DEF_VBOX, CssClasses.DIALOG_CONTENT_ROOT);
            createContent(container);
            FXUtils.addToPane(container, root);
        }

        createActions(actionsContainer);

        FXUtils.addToPane(actionsContainer, root);
        FXUtils.addClassTo(root, CssClasses.DIALOG_ROOT);

        root.addEventHandler(KeyEvent.KEY_RELEASED, this::processKey);
    }

    /**
     * Get the stage of this dialog.
     *
     * @return the stage of this dialog.
     */
    @FromAnyThread
    protected @NotNull Stage getDialog() {
        return dialog;
    }

    /**
     * Get the width property of this dialog.
     *
     * @return the width property of this dialog.
     */
    @FxThread
    protected @NotNull ReadOnlyDoubleProperty widthProperty() {
        return getContainer().widthProperty();
    }

    /**
     * Get the height property of this dialog.
     *
     * @return the height property of this dialog.
     */
    @FxThread
    protected @NotNull ReadOnlyDoubleProperty heightProperty() {
        return getContainer().heightProperty();
    }

    /**
     * Configure size of the root container.
     *
     * @param container the root container.
     */
    @FxThread
    protected void configureSize(@NotNull final VBox container) {
        configureSize(container, getSize());
    }

    /**
     * Configure size of this dialog.
     *
     * @param size the size.
     */
    @FxThread
    public void configureSize(@NotNull final Point size) {
        configureSize(container, size);
    }

    /**
     * Configure size of the root container.
     *
     * @param container the root container.
     * @param size      the size.
     */
    @FxThread
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
    @FromAnyThread
    protected @NotNull VBox getContainer() {
        return container;
    }

    /**
     * Updates a size of this dialog.
     *
     * @param size the size of the dialog.
     */
    @FxThread
    public void updateSize(@NotNull final Point size) {
        configureSize(getContainer(), size);
    }

    @FromAnyThread
    protected @NotNull Point getSize() {
        return DEFAULT_SIZE;
    }

    /**
     * Process key.
     *
     * @param event the event
     */
    @FxThread
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
    @FxThread
    public void show(@NotNull final Node owner) {
        show(owner.getScene().getWindow());
    }

    /**
     * Show this dialog.
     */
    @FxThread
    public void show() {
        show(EditorUtil.getFxLastWindow());
    }

    /**
     * Shows this dialog.
     *
     * @param owner the owner.
     */
    @FxThread
    public void show(@NotNull final Window owner) {

        final Scene scene = owner.getScene();

        if (scene instanceof EditorFxScene) {
            final EditorFxScene editorFxScene = (EditorFxScene) scene;
            final StackPane container = editorFxScene.getContainer();
            container.setFocusTraversable(false);
        }

        focusOwner = scene.getFocusOwner();

        dialog.initOwner(owner);
        dialog.show();
        dialog.requestFocus();
        dialog.toFront();
        dialog.setOnCloseRequest(event -> hide());

        GAnalytics.sendPageView(getDialogId(), null, "/dialog/" + getDialogId());
        GAnalytics.sendEvent(GAEvent.Category.DIALOG, GAEvent.Action.DIALOG_OPENED, getDialogId());

        EditorUtil.addFxWindow(dialog);

        Platform.runLater(dialog::sizeToScene);
    }

    /**
     * Gets dialog id.
     *
     * @return the dialog id
     */
    @FromAnyThread
    protected @NotNull String getDialogId() {
        return getClass().getSimpleName();
    }

    @FxThread
    public void hide() {

        final Duration duration = Duration.between(showedTime, LocalTime.now());
        final int seconds = (int) duration.getSeconds();

        final Window window = dialog.getOwner();
        final Scene scene = window.getScene();

        if (scene instanceof EditorFxScene) {
            final EditorFxScene editorFxScene = (EditorFxScene) scene;
            final StackPane container = editorFxScene.getContainer();
            container.setFocusTraversable(true);
        }

        if (focusOwner != null) {
            focusOwner.requestFocus();
        }

        dialog.hide();

        EditorUtil.removeFxWindow(window);

        GAnalytics.sendEvent(GAEvent.Category.DIALOG, GAEvent.Action.DIALOG_CLOSED, getDialogId());
        GAnalytics.sendTiming(GAEvent.Category.DIALOG, GAEvent.Label.SHOWING_A_DIALOG, seconds, getDialogId());
    }

    /**
     * Create the content of this dialog.
     *
     * @param root the root
     */
    @FxThread
    protected void createContent(@NotNull final VBox root) {
    }

    /**
     * Create the content of this dialog.
     *
     * @param root the root
     */
    @FxThread
    protected void createContent(@NotNull final GridPane root) {
    }

    /**
     * Is grid structure boolean.
     *
     * @return true if this dialog has grid structure.
     */
    @FromAnyThread
    protected boolean isGridStructure() {
        return false;
    }

    /**
     * Create the actions of this dialog.
     *
     * @param root the root
     */
    @FxThread
    protected void createActions(@NotNull final VBox root) {
    }

    /**
     * Gets title text.
     *
     * @return the title of this dialog.
     */
    @FromAnyThread
    protected @NotNull String getTitleText() {
        return "Title";
    }

    /**
     * Sets the new title.
     *
     * @param title the new title.
     */
    @FxThread
    public void setTitleText(@NotNull final String title) {
        dialog.setTitle(title);
    }
}
