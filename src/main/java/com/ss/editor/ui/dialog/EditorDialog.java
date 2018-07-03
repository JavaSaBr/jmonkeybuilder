package com.ss.editor.ui.dialog;

import static javafx.geometry.Pos.CENTER;
import com.ss.editor.JmeApplication;
import com.ss.editor.analytics.google.GAEvent;
import com.ss.editor.analytics.google.GAnalytics;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.scene.EditorFxScene;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.fx.util.FXUtils;
import com.ss.rlib.fx.util.FxUtils;
import com.ss.rlib.fx.window.popup.dialog.AbstractPopupDialog;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
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

    protected static final Logger LOGGER = LoggerManager.getLogger(EditorDialog.class);

    /**
     * The default dialog size.
     */
    private static final Point DEFAULT_SIZE = new Point(0, 0);

    /**
     * The stage of this dialog.
     */
    @NotNull
    private final Stage dialog;

    /**
     * The time when this dialog was showed.
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
     * The flat about that this dialog was full constructed.
     */
    private volatile boolean ready;

    public EditorDialog() {
        this.showedTime = LocalTime.now();

        container = new VBox();
        container.setAlignment(CENTER);

        var mainScene = EditorUtil.getFxScene();
        var scene = new Scene(container);
        var stylesheets = scene.getStylesheets();
        stylesheets.addAll(mainScene.getStylesheets());

        dialog = new Stage();
        dialog.setTitle(getTitleText());
        dialog.initStyle(StageStyle.UTILITY);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setResizable(isResizable());
        dialog.setScene(scene);

        var fxStage = EditorUtil.getFxStage();
        var icons = dialog.getIcons();
        icons.addAll(fxStage.getIcons());
    }

    /**
     * Construct content of this dialog.
     */
    @FxThread
    public void construct() {

        if (ready) {
            return;
        }

        createControls(container);
        configureSize(container);

        ready = true;
    }

    /**
     * Returns true if this dialog should be resizable.
     *
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
    protected void createControls(@NotNull VBox root) {

        var actionsContainer = new VBox();

        if (isGridStructure()) {
            var container = new GridPane();
            FxUtils.addClass(container, CssClasses.DEF_GRID_PANE, CssClasses.DIALOG_CONTENT_ROOT);
            createContent(container);
            FxUtils.addChild(root, container);
        } else {
            var container = new VBox();
            FxUtils.addClass(container, CssClasses.DEF_VBOX, CssClasses.DIALOG_CONTENT_ROOT);
            createContent(container);
            FxUtils.addChild(root, container);
        }

        createActions(actionsContainer);

        FxUtils.addClass(actionsContainer, CssClasses.DIALOG_ACTIONS_ROOT)
                .addClass(root, CssClasses.DIALOG_ROOT);

        FxUtils.addChild(root, actionsContainer);

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
    protected void configureSize(@NotNull VBox container) {
        configureSize(container, getSize());
    }

    /**
     * Configure size of this dialog.
     *
     * @param size the size.
     */
    @FxThread
    public void configureSize(@NotNull Point size) {
        configureSize(container, size);
    }

    /**
     * Configure size of the root container.
     *
     * @param container the root container.
     * @param size      the size.
     */
    @FxThread
    private void configureSize(@NotNull VBox container, @NotNull Point size) {

        var dialog = getDialog();

        var width = size.getX();
        var height = size.getY();

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
     * Get the content container.
     *
     * @return the content container.
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
    public void updateSize(@NotNull Point size) {
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
    protected void processKey(@NotNull KeyEvent event) {
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
    public void show(@NotNull Node owner) {
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
    public void show(@NotNull Window owner) {
        construct();

        var scene = owner.getScene();

        if (scene instanceof EditorFxScene) {
            var editorFxScene = (EditorFxScene) scene;
            var container = editorFxScene.getContainer();
            container.setFocusTraversable(false);
        }

        focusOwner = scene == null ? null : scene.getFocusOwner();

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

        var duration = Duration.between(showedTime, LocalTime.now());
        var seconds = (int) duration.getSeconds();

        var window = dialog.getOwner();
        var scene = window.getScene();

        if (scene instanceof EditorFxScene) {
            var editorFxScene = (EditorFxScene) scene;
            var container = editorFxScene.getContainer();
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
    protected void createContent(@NotNull VBox root) {
    }

    /**
     * Create the content of this dialog.
     *
     * @param root the root
     */
    @FxThread
    protected void createContent(@NotNull GridPane root) {
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
    protected void createActions(@NotNull VBox root) {
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
    public void setTitleText(@NotNull String title) {
        dialog.setTitle(title);
    }
}
